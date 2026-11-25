package modelo;

import java.time.LocalDate;
import java.util.Objects;

public class Inscripcion {
    private int idAlumno;
    private int idCurso;
    private LocalDate fecha;
    private boolean cuotaPaga;

    public Inscripcion(int idAlumno, int idCurso, LocalDate fecha, boolean cuotaPaga) {
        this.idAlumno= idAlumno;
        this.idCurso = idCurso;
        this.fecha = fecha;
        this.cuotaPaga = cuotaPaga;
    }
    public Inscripcion(int idAlumno, int idCurso){
        this(idAlumno, idCurso, LocalDate.now(),false);
    }

    public int getIdAlumno() {
        return idAlumno;
    }
    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }
    public int getIdCurso() {
        return idCurso;
    }
    public void setIdCurso(int idCurso) {this.idCurso = idCurso;}
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

    //La identidad de una inscripcion es el par de idAlumno y idCurso que es la clave primaria compuestas en la BD
    @Override
    public boolean equals(Object obj){
        if(this==obj)return true;
        if(obj==null || getClass()!=obj.getClass())return false;
        Inscripcion otra = (Inscripcion) obj;
        return idAlumno== otra.idAlumno && idCurso == otra.idCurso;
    }
    @Override
    public int hashCode(){
        return Objects.hash(idAlumno,idCurso);
    }
    @Override
    public String toString(){
        return "Inscripcion Alumno: "+idAlumno+", Curso: "+idCurso+", Fecha: "+fecha+", cuotapaga: "+cuotaPaga;
    }
}
