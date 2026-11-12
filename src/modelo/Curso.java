package modelo;

import java.math.BigDecimal;

public class Curso {
    private String codigo;
    private String idioma;
    private String nivel;
    private String turno;
    private int cupo;
    private BigDecimal cuota;
    private int vacantes;

    public Curso(String codigo, String idioma, String nivel, String turno, int cupo, BigDecimal cuota, int vacantes) {
        this.codigo = codigo;
        this.idioma = idioma;
        this.nivel = nivel;
        this.turno = turno;
        this.cupo = cupo;
        this.cuota = cuota;
        this.vacantes = vacantes;
    }

    public String getCodigo() { 
        return codigo; 
    }
    public void setCodigo(String codigo) { 
        this.codigo = codigo; 
    }
    public String getIdioma() { 
        return idioma; 
    }
    public void setIdioma(String idioma) { 
        this.idioma = idioma; 
    }
    public String getNivel() { 
        return nivel; 
    }
    public void setNivel(String nivel) { 
        this.nivel = nivel; 
    }
    public String getTurno() { 
        return turno; 
    }
    public void setTurno(String turno) { 
        this.turno = turno; 
    }
    public int getCupo() { 
        return cupo; 
    }
    public void setCupo(int cupo) { 
        this.cupo = cupo; 
    }
    public BigDecimal getCuota() { 
        return cuota; 
    }
    public void setCuota(BigDecimal cuota) { 
        this.cuota = cuota; 
    }
    public int getVacantes() { 
        return vacantes; 
    }
    public void setVacantes(int vacantes) { 
        this.vacantes = vacantes; 
    }

    // no se guarda, se calcula
    public int getInscriptos() { return cupo - vacantes; }

    @Override
    public String toString() { return codigo; }
}
