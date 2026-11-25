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

/**
 * La tabla inscripcion tiene clave primaria COMPUESTA (id_alumno, id_curso):
 * una inscripción se identifica siempre con los dos valores juntos.
 */
public class InscripcionDAO {

    /** Inscripciones de un curso, con el nombre del alumno (JOIN), para la tabla de la ventana. */
    public List<FilaInscripcion> listarPorCurso(int idCurso) throws InstitutoException {
        String sql = "SELECT i.id_alumno, i.id_curso, a.apellido, a.nombre, a.dni, i.fecha, i.cuota_paga "
                + "FROM inscripcion i JOIN alumno a ON a.id_alumno = i.id_alumno "
                + "WHERE i.id_curso = ? ORDER BY a.apellido, a.nombre";
        List<FilaInscripcion> lista = new ArrayList<>();
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new FilaInscripcion(
                            rs.getInt("id_alumno"),
                            rs.getInt("id_curso"),
                            rs.getString("apellido") + ", " + rs.getString("nombre"),
                            rs.getString("dni"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getBoolean("cuota_paga")));
                }
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
        return lista;
    }

    /** Devuelve null si no existe. La clave es el par (alumno, curso). */
    public Inscripcion buscarPorId(int idAlumno, int idCurso) throws InstitutoException {
        String sql = "SELECT id_alumno, id_curso, fecha, cuota_paga FROM inscripcion "
                + "WHERE id_alumno = ? AND id_curso = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAlumno);
            ps.setInt(2, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Inscripcion(rs.getInt("id_alumno"), rs.getInt("id_curso"),
                        rs.getDate("fecha").toLocalDate(), rs.getBoolean("cuota_paga"));
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
     *  2) Insertar la inscripción (puede fallar por la clave primaria: alumno ya inscripto).
     * Si cualquier paso falla se hace rollback y no queda nada a medias.
     */
    public void inscribir(int idAlumno, int idCurso) throws InstitutoException {
        String sqlDescontar = "UPDATE curso SET vacantes = vacantes - 1 WHERE id_curso = ? AND vacantes > 0";
        String sqlInsertar = "INSERT INTO inscripcion (id_alumno, id_curso, fecha, cuota_paga) VALUES (?, ?, ?, FALSE)";
        try (Connection con = Conexion.abrir()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlDescontar)) {
                    ps.setInt(1, idCurso);
                    if (ps.executeUpdate() == 0) {
                        // 0 filas: o el curso está completo, o el alumno ya estaba inscripto en uno completo.
                        // Se distingue para dar el mensaje correcto.
                        if (yaInscripto(con, idAlumno, idCurso)) {
                            throw new InstitutoException("El alumno ya está inscripto en este curso.");
                        }
                        throw new InstitutoException("El curso está completo: no quedan vacantes.");
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(sqlInsertar)) {
                    ps.setInt(1, idAlumno);
                    ps.setInt(2, idCurso);
                    ps.setDate(3, Date.valueOf(LocalDate.now()));
                    ps.executeUpdate(); // si ya estaba inscripto, falla (clave primaria) y se hace rollback
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

    private boolean yaInscripto(Connection con, int idAlumno, int idCurso) throws SQLException {
        String sql = "SELECT 1 FROM inscripcion WHERE id_alumno = ? AND id_curso = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idAlumno);
            ps.setInt(2, idCurso);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Cambia la fecha y/o el estado de la cuota. No toca vacantes. */
    public void modificar(Inscripcion i) throws InstitutoException {
        String sql = "UPDATE inscripcion SET fecha = ?, cuota_paga = ? WHERE id_alumno = ? AND id_curso = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(i.getFecha()));
            ps.setBoolean(2, i.isCuotaPaga());
            ps.setInt(3, i.getId_alumno());
            ps.setInt(4, i.getId_curso());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
    }

    /**
     * BAJA: borra la inscripción y devuelve la vacante, en una sola transacción.
     * Si el DELETE no afecta ninguna fila es que otra persona ya la dio de baja: en ese caso
     * NO se devuelve la vacante (si no, el curso quedaría con un lugar de más).
     */
    public void borrar(int idAlumno, int idCurso) throws InstitutoException {
        String sqlBorrar = "DELETE FROM inscripcion WHERE id_alumno = ? AND id_curso = ?";
        String sqlDevolver = "UPDATE curso SET vacantes = vacantes + 1 WHERE id_curso = ?";
        try (Connection con = Conexion.abrir()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlBorrar)) {
                    ps.setInt(1, idAlumno);
                    ps.setInt(2, idCurso);
                    if (ps.executeUpdate() == 0) {
                        throw new InstitutoException("La inscripción ya no existe (otra persona pudo haberla dado de baja).");
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(sqlDevolver)) {
                    ps.setInt(1, idCurso);
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

    /** D.1: el filtro (cuota impaga) lo hace la base con WHERE */
    public List<Deudor> listarDeudores() throws InstitutoException {
        String sql = "SELECT a.apellido, a.nombre, a.dni, c.codigo, c.cuota "
                + "FROM inscripcion i "
                + "JOIN alumno a ON a.id_alumno = i.id_alumno "
                + "JOIN curso c ON c.id_curso = i.id_curso "
                + "WHERE i.cuota_paga = FALSE "
                + "ORDER BY a.apellido, a.nombre, c.codigo";
        List<Deudor> lista = new ArrayList<>();
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Deudor(
                        rs.getString("apellido") + ", " + rs.getString("nombre"),
                        rs.getString("dni"),
                        rs.getString("codigo"),
                        rs.getBigDecimal("cuota")));
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
        return lista;
    }
}
