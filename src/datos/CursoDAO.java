package datos;

import modelo.Curso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    public List<Curso> listar() throws InstitutoException {
        String sql = "SELECT id_curso, codigo, idioma, nivel, turno, cupo, cuota, vacantes FROM curso ORDER BY codigo";
        List<Curso> lista = new ArrayList<>();
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(armar(rs));
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
        return lista;
    }

    //Un curso nuevo empieza con todo vacante, esta liberado (vacantes = cupo)
    public void insertar(Curso c) throws InstitutoException{
        String sql = "INSERT INTO curso (codigo, idioma, nivel, turno, cupo, cuota, vacantes)" + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection con = Conexion.abrir();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, c.getCodigo());
                ps.setString(2, c.getIdioma());
                ps.setString(3, c.getNivel());
                ps.setString(4, c.getTurno());
                ps.setInt(5, c.getCupo());
                ps.setBigDecimal(6, c.getCuota());
                ps.setInt(7, c.getCupo());
                ps.executeUpdate();
            }catch(SQLException e){
                throw TraductorDeErrores.traducir(e);
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
    }

    // Un curso nuevo empieza con todo vacante (vacantes = cupo).
    // El id_curso lo genera la base (AUTO_INCREMENT), por eso no se manda en el INSERT.
    public void insertar(Curso c) throws InstitutoException {
        String sql = "INSERT INTO curso (codigo, idioma, nivel, turno, cupo, cuota, vacantes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCodigo());
            ps.setString(2, c.getIdioma());
            ps.setString(3, c.getNivel());
            ps.setString(4, c.getTurno());
            ps.setInt(5, c.getCupo());
            ps.setBigDecimal(6, c.getCuota());
            ps.setInt(7, c.getCupo());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    c.setIdCurso(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
    }

    /*
     * Si cambia el cupo, las vacantes se ajustan por la diferencia. En MySQL las asignaciones del SET
     * se evalúan de izquierda a derecha, por eso "vacantes" va ANTES que "cupo" (usa el cupo viejo).
     * Si el nuevo cupo queda por debajo de los inscriptos, el CHECK de la base lo rechaza.
     */
    public void modificar(Curso c) throws InstitutoException {
        String sql = "UPDATE curso SET codigo = ?, idioma = ?, nivel = ?, turno = ?, "
                + "vacantes = vacantes + (? - cupo), cupo = ?, cuota = ? WHERE id_curso = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCodigo());
            ps.setString(2, c.getIdioma());
            ps.setString(3, c.getNivel());
            ps.setString(4, c.getTurno());
            ps.setInt(5, c.getCupo());
            ps.setInt(6, c.getCupo());
            ps.setBigDecimal(7, c.getCuota());
            ps.setInt(8, c.getIdCurso());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
    }

    public void borrar(int idCurso) throws InstitutoException {
        String sql = "DELETE FROM curso WHERE id_curso = ?";
        try (Connection con = Conexion.abrir();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e); // si tiene inscripciones: error 1451
        }
    }

    private Curso armar(ResultSet rs) throws SQLException{
        return new Curso(rs.getInt("id_curso"), rs.getString("codigo"), rs.getString("idioma"), rs.getString("nivel"), rs.getString("turno"), rs.getInt("cupo"), rs.getBigDecimal("cuota"), rs.getInt("vacantes"));
    }
}
