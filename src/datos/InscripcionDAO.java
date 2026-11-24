package datos;

import modelo.Deudor;
import modelo.FilaInscripcion;
import modelo.Inscripcion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InscripcionDAO {

    /** Inscripciones de un curso, con el nombre del alumno (JOIN), para la tabla de la ventana. */
    public List<FilaInscripcion> listarPorCurso(String cursoCodigo) throws InstitutoException {
        String sql = "SELECT i.id, a.apellido, a.nombre, a.dni, i.fecha, i.cuota_paga "
                + "FROM inscripcion i JOIN alumno a ON a.id = i.alumno_id "
                + "WHERE i.curso_codigo = ? ORDER BY a.apellido, a.nombre";
        List<FilaInscripcion> lista = new ArrayList<>();
        try(Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cursoCodigo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new FilaInscripcion(rs.getInt("id"), rs.getString("apellido") + ", " + rs.getString("nombre"), rs.getString("dni"), rs.getDate("fecha").toLocalDate(), rs.getBoolean("cuota_paga")));
                }
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
        return lista;
    }

    /** Devuelve null si no existe. */
    public Inscripcion buscarPorId(int id) throws InstitutoException {
        String sql = "SELECT id, alumno_id, curso_codigo, fecha, cuota_paga FROM inscripcion WHERE id = ?";
        try(Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Inscripcion(rs.getInt("id"), rs.getInt("alumno_id"), rs.getString("curso_codigo"), rs.getDate("fecha").toLocalDate(), rs.getBoolean("cuota_paga"));
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
    }

    /**
     * INSCRIBIR (B.6): una sola transacción con dos pasos.
     *  1) Descontar la vacante con un UPDATE condicional. Si no quedan vacantes, afecta 0 filas.
     *     Además toma el bloqueo de la fila del curso: dos personas inscribiéndose al último lugar
     *     quedan en fila y la segunda ve vacantes = 0.
     *  2) Insertar la inscripción (puede fallar por UNIQUE: alumno ya inscripto).
     * Si cualquier paso falla se hace rollback y no queda nada a medias.
     */
    public void inscribir(int alumnoId, String cursoCodigo) throws InstitutoException {
        String sqlDescontar = "UPDATE curso SET vacantes = vacantes - 1 WHERE codigo = ? AND vacantes > 0";
        String sqlInsertar = "INSERT INTO inscripcion (alumno_id, curso_codigo, fecha, cuota_paga) "
                + "VALUES (?, ?, ?, FALSE)";
        try(Connection con = Conexion.abrir()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlDescontar)) {
                    ps.setString(1, cursoCodigo);
                    if (ps.executeUpdate() == 0) {
                        throw new InstitutoException("El curso está completo: no quedan vacantes.");
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(sqlInsertar)) {
                    ps.setInt(1, alumnoId);
                    ps.setString(2, cursoCodigo);
                    ps.setDate(3, Date.valueOf(LocalDate.now()));
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException | InstitutoException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
    }

    /** Cambia la fecha y/o el estado de la cuota. No toca vacantes. */
    public void modificar(Inscripcion i) throws InstitutoException {
        String sql = "UPDATE inscripcion SET fecha = ?, cuota_paga = ? WHERE id = ?";
        try(Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(i.getFecha()));
            ps.setBoolean(2, i.isCuotaPaga());
            ps.setInt(3, i.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
    }

    /** BAJA: borra la inscripción y devuelve la vacante, en una sola transacción. */
    public void borrar(int id) throws InstitutoException {
        String sqlBuscar = "SELECT curso_codigo FROM inscripcion WHERE id = ? FOR UPDATE";
        String sqlBorrar = "DELETE FROM inscripcion WHERE id = ?";
        String sqlDevolver = "UPDATE curso SET vacantes = vacantes + 1 WHERE codigo = ?";
        try(Connection con = Conexion.abrir()) {
            con.setAutoCommit(false);
            try {
                String codigo;
                try (PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new InstitutoException("La inscripcion ya no existe (otra persona pudo haberla dado de baja).");
                        }
                        codigo = rs.getString(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(sqlBorrar)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(sqlDevolver)) {
                    ps.setString(1, codigo);
                    ps.executeUpdate();
                }
                con.commit();
            } catch (SQLException | InstitutoException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
    }

    /** D.1: el filtro (cuota impaga) lo hace la base con WHERE; Java solo muestra lo que llega. */
    public List<Deudor> listarDeudores() throws InstitutoException {
        String sql = "SELECT a.apellido, a.nombre, a.dni, c.codigo, c.cuota "
                + "FROM inscripcion i "
                + "JOIN alumno a ON a.id = i.alumno_id "
                + "JOIN curso c ON c.codigo = i.curso_codigo "
                + "WHERE i.cuota_paga = FALSE "
                + "ORDER BY a.apellido, a.nombre, c.codigo";
        List<Deudor> lista = new ArrayList<>();
        try(Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Deudor(rs.getString("apellido") + ", " + rs.getString("nombre"),
                        rs.getString("dni"), rs.getString("codigo"), rs.getBigDecimal("cuota")));
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
        return lista;
    }
}
