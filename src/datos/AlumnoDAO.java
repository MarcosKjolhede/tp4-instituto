package datos;

import modelo.Alumno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {
  
    public List<Alumno> listar() throws InstitutoException{
        String sql ="SELET id_alumno, nombre, apellido, dni, correo FROM alumno ORDER BY apellido, nombre";
        List<Alumno> lista = new ArrayList<>();
        try(Connection con = Conexion.abrir();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            while (rs.next()) {
                lista.add(armar(rs));
            }
        } catch (SQLException e) {
            throw TraductorDeErrores.traducir(e);
        }
        return lista;
    }

    //Devuelve null si no exite
    public Alumno buscarPorId(int id_alumno) throws InstitutoException{
        String sql = "SELECT id_alumno, nombre, apellido, dni, correo FROM alumno where id = ?";
        try(Connection con = Conexion.abrir();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setInt(1,id_alumno);
                try(ResultSet rs = ps.executeQuery()){
                    return rs.next() ? armar(rs) : null;
                }
            }catch(SQLException e){
                throw TraductorDeErrores.traducir(e);
            }
    }

    public void insertar(Alumno a) throws InstitutoException{
        String sql = "INSERT INTO alumno (nombre, apellido, dni, correo) VALUES (?, ?, ?, ?)";
        try(Connection con = Conexion.abrir();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
                ps.setString(1, a.getNombre());
                ps.setString(2, a.getApellido());
                ps.setString(3, a.getDni());
                ps.setString(4, a.getCorreo());//null entonces null en la base
                ps.executeUpdate();
                try(ResultSet claves = ps.getGeneratedKeys()){
                    if (claves.next()){
                        a.setId_alumno(claves.getInt(1));
                    }
                }
            }catch(SQLException e){
                throw TraductorDeErrores.traducir(e);
            }
    }

    public void modificar(Alumno a) throws InstitutoException{
        String sql = "UPDATE alumno SET nombre = ?, apellido = ?, dni = ?, correo = ? WHERE id_alumno = ?";
        try(Connection con = Conexion.abrir();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, a.getNombre());
                ps.setString(2, a.getApellido());
                ps.setString(3, a.getDni());
                ps.setString(4, a.getCorreo());
                ps.setInt(5, a.getId_alumno());
                ps.executeUpdate();
        }catch(SQLException e){
            throw TraductorDeErrores.traducir(e);
        }
    }

    public void borrar(int id_alumno) throws InstitutoException{
        String sql = "DELETE FROM alumno WHERE id_alumno = ?";
        try(Connection con = Conexion.abrir();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setInt(1, id_alumno);
                ps.executeUpdate();
            }catch(SQLException e){
                throw TraductorDeErrores.traducir(e); //si tiene inscripciones: error 1451
            }
    }

    private Alumno armar(ResultSet rs) throws SQLException{
        return new Alumno(rs.getInt("id_alumno"), rs.getString("nombre"), rs.getString("apellido"), rs.getString("dni"), rs.getString("correo"));
    }
}
