package app;

import modelo.Curso;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class CursosTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS =
            {"Código", "Idioma", "Nivel", "Turno", "Cupo", "Inscriptos", "Vacantes", "Cuota mensual"};

    private List<Curso> cursos = new ArrayList<>();

    public void setCursos(List<Curso> cursos) {
        this.cursos = cursos;
        fireTableDataChanged();
    }

    public Curso getCurso(int fila) { return cursos.get(fila); }

    /** Fila (del modelo) del curso con ese id, o -1. */
    public int indiceDe(int idCurso) {
        for (int i = 0; i < cursos.size(); i++) {
            if (cursos.get(i).getIdCurso() == idCurso) {
                return i;
            }
        }
        return -1;
    }

    @Override public int getRowCount() { 
        return cursos.size(); 
    }
    @Override public int getColumnCount() { 
        return COLUMNAS.length; 
    }
    @Override public String getColumnName(int c) { 
        return COLUMNAS[c]; 
    }

    @Override
    public Class<?> getColumnClass(int c) {
        switch (c) {
            case 4: case 5: case 6: return Integer.class;
            case 7: return BigDecimal.class;
            default: return String.class;
        }
    }

    @Override
    public Object getValueAt(int fila, int col) {
        Curso c = cursos.get(fila);
        switch (col) {
            case 0: return c.getCodigo();
            case 1: return c.getIdioma();
            case 2: return c.getNivel();
            case 3: return c.getTurno();
            case 4: return c.getCupo();
            case 5: return c.getInscriptos();
            case 6: return c.getVacantes();
            default: return c.getCuota();
        }
    }
}
