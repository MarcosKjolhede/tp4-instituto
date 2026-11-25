package modelo;

import java.time.LocalDate;

// Una inscripción con el nombre del alumno, lista para mostrar en la tabla.
// La inscripción no tiene un id propio: su clave primaria es compuesta (id_alumno, id_curso),
// por eso la fila guarda las dos partes de la clave.
public record FilaInscripcion(int idAlumno, int idCurso, String alumno, String dni, LocalDate fecha, boolean cuotaPaga) {
}

