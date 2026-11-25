package datos;

import java.sql.SQLException;

// Convierte un error de la base (SQLException) en un mensaje que el usuario entienda (D.2 y D.3)
public class TraductorDeErrores {

    private TraductorDeErrores() {
    }

    public static InstitutoException traducir(SQLException e) {
        // una línea para nosotros (consola), no para el usuario; nunca el rastro de pila
        System.err.println("(bd) codigo " + e.getErrorCode() + " / estado " + e.getSQLState() + ": " + e.getMessage());

        int codigo = e.getErrorCode();
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        String msg = e.getMessage() == null ? "" : e.getMessage();

        // 1062 = clave duplicada. El mensaje de MySQL dice cuál fue la restricción
        if (codigo == 1062) {
            if (msg.contains("uq_curso_codigo")) {
                return new InstitutoException("Ya existe un curso con ese código.", e);
            }
            if (msg.contains("uq_alumno_dni")) {
                return new InstitutoException("Ya existe un alumno con ese DNI.", e);
            }

            if (msg.contains("PRIMARY") || msg.contains("pk_inscripcion")) {
                return new InstitutoException("El alumno ya está inscripto en este curso.", e);
            }
            return new InstitutoException("Ya existe un registro igual.", e);
        }

        // 1451 = no se puede borrar/modificar un registro "padre" que tiene hijos
        if (codigo == 1451) {
            if (msg.contains("fk_inscripcion_alumno")) {
                return new InstitutoException("No se puede borrar el alumno porque tiene una inscripción registrada.", e);
            }
            if (msg.contains("fk_inscripcion_curso")) {
                return new InstitutoException("No se puede borrar el curso porque ya tiene alumnos inscriptos.", e);
            }
            return new InstitutoException("No se puede eliminar o modificar el registro porque está en uso.", e);
        }

        // 1452 = se intentó guardar un registro hijo que apunta a un padre que no existe
        if (codigo == 1452) {
            return new InstitutoException("El alumno o el curso indicado no existen.", e);
        }

        // 3819 (MySQL) / 4025 (MariaDB) = no se cumplió un CHECK (ej: cupo menor a los inscriptos)
        if (codigo == 3819 || codigo == 4025) {
            return new InstitutoException("Los datos no cumplen las reglas del curso (por ejemplo, el cupo no puede ser menor a los inscriptos).", e);
        }

        // 1406 = texto demasiado largo para la columna
        if (codigo == 1406) {
            return new InstitutoException("Hay un dato demasiado largo para guardarlo.", e);
        }

        // 1049 = la base de datos no existe en ese servidor
        if (codigo == 1049) {
            return new InstitutoException("La base 'instituto' no existe en el servidor. Ejecutá instituto.sql.", e);
        }

        // 1045 = usuario o clave incorrectos
        if (codigo == 1045) {
            return new InstitutoException("Usuario o clave de la base de datos incorrectos. Revisá el archivo .env.", e);
        }

        // SQLState 08xxx = errores de conexión (base apagada, servidor inalcanzable...)
        if (estado.startsWith("08")) {
            return new InstitutoException("No se pudo conectar con la base de datos. Verificá que el servidor esté encendido.", e);
        }

        return new InstitutoException("Ocurrió un error inesperado en la base de datos.", e);
    }
}
