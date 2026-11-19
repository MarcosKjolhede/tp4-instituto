package datos;

import java.sql.SQLException;

//Convertimos un error de (SQLExcepion) en un mensaje que se entienda
public class TraductorDeErrores {

    private TraductorDeErrores(){}

    public static InstitutoException traducir(SQLException e){
        //una linea para nosotros, no para el usuario
        System.err.println("(bd) codigo " + e.getErrorCode() + " / estado " + e.getSQLState() + ": " + e.getMessage());

        int codigo = e.getErrorCode();
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        String msg = e.getMessage() == null ? "" : e.getMessage();

        //1062 = clave duplicada. La retriccion dice cual fue
        if (codigo == 1062) {
            if (msg.contains("uq_curso_codigo")){
                return new InstitutoException("Ya existe un curso con ese codigo", e);
            }
            if (msg.contains("uq_alumno_dni")){
                return new InstitutoException("Ya existe un alumno con ese DNI", e);
            }
            if (msg.contains("pk_inscripcion")){
                return new InstitutoException("El alumno ya esta inscripto en este curso", e);
            }
            return new InstitutoException("Ya existe un registro igual", e);
            }

        //1451 = resttricciones de borrado y modificacion
        if(codigo == 1451){
            if (msg.contains("fk_inscripcion_alumno")) {
                return  new InstitutoException("No se puede borrar el alumno porque tiene una inscripcion registrada", e);
            }
            if (msg.contains("fk_inscripcion_curso")) {
                return  new InstitutoException("No se puede borrar el curso porque ya tiene alumnos registrados", e);
            }
            return new InstitutoException("No se puede eliminar o modificar el registro porque esta en uso", e);
        }

        //1452 = error al insertar o actualizar un registro hijo apuntando a un padre que no existe
        if(codigo == 1452){
            return new InstitutoException("El alumno o el curso indicado no existen", e);
        }

        //1049 = la base de datos a la que intentas conectarte o seleccionar no existe en ese servidor
        if (codigo == 1049) {
            return new InstitutoException("La base 'instituto' que intentas conectarte no existe", e);
        }
        
        // SQLState 08xxx = errores de conexion
        if (estado.startsWith("08")) {
            return new InstitutoException("No se pudo conectar con la base de datos", e);
        }

        return new InstitutoException("Ocurrio un error inesperado en la base de datos", e);
    }
}


