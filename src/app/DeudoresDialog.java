package app;

import modelo.Deudor;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.math.BigDecimal;
import java.util.List;

// D.1: muestra el informe de deudores (la lista ya viene filtrada desde la base)
public class DeudoresDialog extends JDialog {

    public DeudoresDialog(Frame padre, List<Deudor> deudores) {
        super(padre, "Informe de deudores", true);

        String[] columnas = {"Alumno", "DNI", "Curso", "Importe adeudado"};
        AbstractTableModel modelo = new AbstractTableModel() {
            @Override
            public int getRowCount() { return deudores.size(); }
            @Override 
            public int getColumnCount() { return columnas.length; }
            @Override 
            public String getColumnName(int c) { return columnas[c]; }
            @Override 
            public Class<?> getColumnClass(int c) { return c == 3 ? BigDecimal.class : String.class; }
            @Override 
            public Object getValueAt(int f, int c) {
                Deudor d = deudores.get(f);
                switch (c) {
                    case 0: return d.alumno();
                    case 1: return d.dni();
                    case 2: return d.curso();
                    default: return d.importe();
                }
            }
        };
        JTable tabla = new JTable(modelo);
        tabla.setAutoCreateRowSorter(true);

        String resumen = deudores.isEmpty()
                ? "Nadie debe la cuota."
                : deudores.size() + " cuota(s) impaga(s).";
        JLabel lblResumen = new JLabel(resumen);
        lblResumen.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.add(btnCerrar);

        add(lblResumen, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(pie, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(620, 340));
        pack();
        setLocationRelativeTo(padre);
    }
}
