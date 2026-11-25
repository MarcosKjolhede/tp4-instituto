package app;

import modelo.FilaInscripcion;

import javax.swing.table.AbstractTableModel;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InscripcionesTableModel extends AbstractTableModel {

    private static final String[] COLUMNAS = {"Alumno", "DNI", "Fecha de inscripción", "Cuota paga"};

    private List<FilaInscripcion> filas = new ArrayList<>();

    public void setFilas(List<FilaInscripcion> filas) {
        this.filas = filas;
        fireTableDataChanged();
    }

    public FilaInscripcion getFila(int fila) { return filas.get(fila); }

    @Override 
    public int getRowCount() { return filas.size(); }
    @Override 
    public int getColumnCount() { return COLUMNAS.length; }
    @Override 
    public String getColumnName(int c) { return COLUMNAS[c]; }

    @Override
    public Class<?> getColumnClass(int c) {
        switch (c) {
            case 2: return LocalDate.class;
            case 3: return Boolean.class; // se dibuja como casilla
            default: return String.class;
        }
    }

    @Override
    public Object getValueAt(int fila, int col) {
        FilaInscripcion f = filas.get(fila);
        switch (col) {
            case 0: return f.alumno();
            case 1: return f.dni();
            case 2: return f.fecha();
            default: return f.cuotaPaga();
        }
    }
}
