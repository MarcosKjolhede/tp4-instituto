package modelo;

import java.time.LocalDate;

public class Inscripcion {
    
    private int id_alumno;
    private int id_curso;
    private LocalDate fecha;
    private boolean cuotaPaga;

    public Inscripcion(int id_alumno, int id_curso, LocalDate fecha, boolean cuotaPaga) {
        
        this.id_alumno = id_alumno;
        this.id_curso = id_curso;
        this.fecha = fecha;
        this.cuotaPaga = cuotaPaga;
    }

    public int getId_alumno() { 
        return id_alumno; 
    }
    public void setId_alumno(int id_alumno) { 
        this.id_alumno = id_alumno; 
    }
    public int getId_curso() { 
        return id_curso; 
    }
    public void setId_curso(int id_curso) { 
        this.id_curso = id_curso; 
    }
    public LocalDate getFecha() {
        return fecha; 
    }
    public void setFecha(LocalDate fecha) { 
        this.fecha = fecha; 
    }
    public boolean isCuotaPaga() { 
        return cuotaPaga; 
    }
    public void setCuotaPaga(boolean cuotaPaga) { 
        this.cuotaPaga = cuotaPaga; 
    }
}
