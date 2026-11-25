package modelo;

import java.util.Objects;

public class Alumno {
    private int id_alumno;
    private String nombre;
    private String apellido;
    private String dni;
    private String correo; // puede ser null

    public  Alumno(String nombre, String apellido, String dni, String correo){
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.correo = (correo != null && correo.isBlank()) ? null : correo;
    }

    public Alumno(String nombre, String apellido, String dni){
        this(nombre, apellido, dni, null);
    }

    public Alumno(int id_alumno, String nombre, String apellido, String dni, String correo){
        this(nombre, apellido, dni, correo);
        this.id_alumno = id_alumno;
    }

    public int getId_alumno() { 
        return id_alumno; 
    }
    public void setId_alumno(int id_alumno) { 
        this.id_alumno = id_alumno; 
    }
    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    public String getApellido() { 
        return apellido; 
    }
    public void setApellido(String apellido) { 
        this.apellido = apellido; 
    }
    public String getDni() { 
        return dni; 
    }
    public void setDni(String dni) { 
        this.dni = dni; 
    }
    public String getCorreo() { 
        return correo; 
    }
    public void setCorreo(String correo) {this.correo = (correo != null && correo.isBlank())? null : correo;}

    //La identidad de un alumno se define por su DNI, que es el unico en el instituto
    @Override
    public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj==null|| getClass() != obj.getClass()) return false;
        Alumno otro = (Alumno) obj;
        return Objects.equals(dni,otro.dni);
    }

    @Override
    public int hashCode(){
        return Objects.hash(dni);
    }

    @Override
    public String toString() {
        return apellido + ", " + nombre + " (DNI " + dni + ")";
    }
}
