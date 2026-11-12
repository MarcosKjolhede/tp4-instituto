package modelo;

import java.time.LocalDate;

public class Inscripcion {
    private int id;
    private int alumnoId;
    private String cursoCodigo;
    private LocalDate fecha;
    private boolean cuotaPaga;

    public Inscripcion(int id, int alumnoId, String cursoCodigo, LocalDate fecha, boolean cuotaPaga) {
        this.id = id;
        this.alumnoId = alumnoId;
        this.cursoCodigo = cursoCodigo;
        this.fecha = fecha;
        this.cuotaPaga = cuotaPaga;
    }

    public int getId() { 
        return id; 
    }
    public void setId(int id) { 
        this.id = id; 
    }
    public int getAlumnoId() { 
        return alumnoId; 
    }
    public void setAlumnoId(int alumnoId) { 
        this.alumnoId = alumnoId; 
    }
    public String getCursoCodigo() { 
        return cursoCodigo; 
    }
    public void setCursoCodigo(String cursoCodigo) { 
        this.cursoCodigo = cursoCodigo; 
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
