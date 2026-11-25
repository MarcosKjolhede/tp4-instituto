package modelo;

import java.time.LocalDate;

//Una inscripcion con el nombre del alumno, lista para mostrar en la tabla
public record FilaInscripcion(int id_alumno, String alumno, String dni, LocalDate fecha, boolean cuotaPaga){
}
