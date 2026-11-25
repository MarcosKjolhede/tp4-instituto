package app;

import datos.AlumnoDAO;
import datos.CursoDAO;
import datos.InscripcionDAO;
import datos.InstitutoException;
import modelo.Alumno;
import modelo.Curso;
import modelo.Deudor;
import modelo.FilaInscripcion;
import modelo.Inscripcion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

public class VentanaPrincipal extends JFrame{

    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final CursoDAO cursoDAO = new CursoDAO();
    private final InscripcionDAO inscripcionDAO = new InscripcionDAO();

    //para dar de alta un alumno
    private JTextField txtNombreAlumno;
    private JTextField txtApellidoAlumno;
    private JTextField txtDniAlumno;
    private JTextField txtCorreoAlumno;
    private JButton btnGuardarAlumno;

    //componentes de inscripcion
    private JComboBox<Alumno> comboAlumnos;
    private JComboBox<Curso> comboCursos;
    private JButton btnInscribir;

    // tabla de cursos y tabla de inscriptos del curso elegido
    private CursosTableModel modeloTablaCursos;
    private JTable tablaCursos;
    private InscripcionesTableModel modeloTablaInscriptos;
    private JTable tablaInscriptos;

    // acciones
    private JButton btnBajaInscripcion;
    private JButton btnCambiarCuota;
    private JButton btnRefrescar;
    private JButton btnInformeDeudores;

    /** Código del curso elegido: lo comparten la tabla y el combo para estar siempre sincronizados. */
    private String codigoElegido = null;
    /** Si se acaba de crear un alumno, el combo lo deja seleccionado después de refrescar. */
    private int alumnoAElegir = -1;
    /** Evita que los listeners se disparen cuando es el programa (y no el usuario) quien cambia la selección. */
    private boolean actualizando = false;

    public VentanaPrincipal() {
        super("Instituto de Idiomas");
        setSize(980, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearPanelAlta(), BorderLayout.NORTH);
        add(crearPanelTablas(), BorderLayout.CENTER);
        add(crearPanelInferior(), BorderLayout.SOUTH);
        conectarEventos();
    }

    // ------------------------------------------------------------------
    // Pantalla
    // ------------------------------------------------------------------
    private JPanel crearPanelAlta() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Alta de alumno nuevo"));

        txtNombreAlumno = new JTextField();
        txtApellidoAlumno = new JTextField();
        txtDniAlumno = new JTextField();
        txtCorreoAlumno = new JTextField();
        btnGuardarAlumno = new JButton("Guardar alumno");

        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombreAlumno);
        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellidoAlumno);
        panel.add(new JLabel("DNI (sin puntos):"));
        panel.add(txtDniAlumno);
        panel.add(new JLabel("Correo (opcional):"));
        panel.add(txtCorreoAlumno);
        panel.add(new JLabel());
        panel.add(btnGuardarAlumno);
        return panel;
    }

    private JSplitPane crearPanelTablas() {
        // Modelos propios: getColumnClass devuelve Integer/BigDecimal, así los números ordenan como números
        modeloTablaCursos = new CursosTableModel();
        tablaCursos = new JTable(modeloTablaCursos);
        tablaCursos.setAutoCreateRowSorter(true);
        tablaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollCursos = new JScrollPane(tablaCursos);
        scrollCursos.setBorder(BorderFactory.createTitledBorder("Cursos, inscriptos y vacantes"));

        modeloTablaInscriptos = new InscripcionesTableModel();
        tablaInscriptos = new JTable(modeloTablaInscriptos);
        tablaInscriptos.setAutoCreateRowSorter(true);
        tablaInscriptos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollInscriptos = new JScrollPane(tablaInscriptos);
        scrollInscriptos.setBorder(BorderFactory.createTitledBorder("Inscriptos del curso elegido (elegí uno para dar de baja)"));

        JSplitPane division = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollCursos, scrollInscriptos);
        division.setResizeWeight(0.5);
        return division;
    }

    private JPanel crearPanelInferior() {
        JPanel panelInferior = new JPanel(new GridLayout(2, 1));

        JPanel panelInscripcion = new JPanel(new FlowLayout());
        panelInscripcion.setBorder(BorderFactory.createTitledBorder("Nueva inscripción"));
        comboAlumnos = new JComboBox<>();
        comboCursos = new JComboBox<>();
        btnInscribir = new JButton("Inscribir alumno");
        panelInscripcion.add(new JLabel("Alumno: "));
        panelInscripcion.add(comboAlumnos);
        panelInscripcion.add(new JLabel("Curso: "));
        panelInscripcion.add(comboCursos);
        panelInscripcion.add(btnInscribir);

        JPanel panelBotonesAccion = new JPanel(new FlowLayout());
        btnBajaInscripcion = new JButton("Dar de baja inscripción seleccionada");
        btnCambiarCuota = new JButton("Cambiar estado de cuota");
        btnInformeDeudores = new JButton("Ver informe de deudores");
        btnRefrescar = new JButton("Refrescar");
        panelBotonesAccion.add(btnBajaInscripcion);
        panelBotonesAccion.add(btnCambiarCuota);
        panelBotonesAccion.add(btnInformeDeudores);
        panelBotonesAccion.add(btnRefrescar);

        panelInferior.add(panelInscripcion);
        panelInferior.add(panelBotonesAccion);
        return panelInferior;
    }

    private void conectarEventos() {
        btnGuardarAlumno.addActionListener(e -> guardarAlumno());
        btnInscribir.addActionListener(e -> inscribir());
        btnBajaInscripcion.addActionListener(e -> darDeBaja());
        btnCambiarCuota.addActionListener(e -> cambiarCuota());
        btnInformeDeudores.addActionListener(e -> verDeudores());
        btnRefrescar.addActionListener(e -> refrescar());

        // Si el usuario toca una fila de la tabla, el combo de cursos la sigue (y se cargan sus inscriptos)
        tablaCursos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !actualizando) {
                int vista = tablaCursos.getSelectedRow();
                if (vista >= 0) {
                    Curso curso = modeloTablaCursos.getCurso(tablaCursos.convertRowIndexToModel(vista));
                    elegirCurso(curso.getCodigo());
                }
            }
        });
        // ...y si el usuario cambia el combo, la tabla lo sigue
        comboCursos.addActionListener(e -> {
            Curso curso = (Curso) comboCursos.getSelectedItem();
            if (!actualizando && curso != null) {
                elegirCurso(curso.getCodigo());
            }
        });
    }

    // ------------------------------------------------------------------
    // C.5: una sola operación que deja la pantalla igual a la base
    // ------------------------------------------------------------------
    public void refrescar() {
        List<Curso> cursos;
        List<Alumno> alumnos;
        try {
            cursos = cursoDAO.listar();
            alumnos = alumnoDAO.listar();
        } catch (InstitutoException e) {
            limpiarPantalla(); // no dejamos datos viejos a la vista si no se pudo leer la base
            mostrarError(e);
            return;
        }

        Alumno actual = (Alumno) comboAlumnos.getSelectedItem();
        int idAlumno = alumnoAElegir >= 0 ? alumnoAElegir : (actual == null ? -1 : actual.getId_alumno());
        alumnoAElegir = -1;

        actualizando = true;
        try {
            modeloTablaCursos.setCursos(cursos);
            comboCursos.removeAllItems();
            for (Curso c : cursos) {
                comboCursos.addItem(c);
            }
            comboAlumnos.removeAllItems();
            for (Alumno a : alumnos) {
                comboAlumnos.addItem(a);
            }
            for (int i = 0; i < comboAlumnos.getItemCount(); i++) {
                if (comboAlumnos.getItemAt(i).getId_alumno() == idAlumno) {
                    comboAlumnos.setSelectedIndex(i);
                }
            }
        } finally {
            actualizando = false;
        }

        // Se conserva el curso elegido; si ya no existe, se elige el primero
        if (modeloTablaCursos.indiceDe(codigoElegido) >= 0) {
            elegirCurso(codigoElegido);
        } else if (!cursos.isEmpty()) {
            elegirCurso(cursos.get(0).getCodigo());
        } else {
            codigoElegido = null;
            cargarInscriptos();
        }
    }

    /** Deja elegido el mismo curso en la tabla y en el combo, y carga sus inscriptos. */
    private void elegirCurso(String codigo) {
        codigoElegido = codigo;
        boolean anterior = actualizando;
        actualizando = true;
        try {
            int fila = modeloTablaCursos.indiceDe(codigo);
            if (fila >= 0) {
                int vista = tablaCursos.convertRowIndexToView(fila);
                tablaCursos.setRowSelectionInterval(vista, vista);
            }
            for (int i = 0; i < comboCursos.getItemCount(); i++) {
                if (comboCursos.getItemAt(i).getCodigo().equals(codigo)) {
                    comboCursos.setSelectedIndex(i);
                    break;
                }
            }
        } finally {
            actualizando = anterior;
        }
        cargarInscriptos();
    }

    private void cargarInscriptos() {
        if (codigoElegido == null) {
            modeloTablaInscriptos.setFilas(List.of());
            return;
        }
        try {
            modeloTablaInscriptos.setFilas(inscripcionDAO.listarPorCurso(codigoElegido));
        } catch (InstitutoException e) {
            modeloTablaInscriptos.setFilas(List.of());
            mostrarError(e);
        }
    }

    private void limpiarPantalla() {
        actualizando = true;
        try {
            modeloTablaCursos.setCursos(List.of());
            comboCursos.removeAllItems();
            comboAlumnos.removeAllItems();
        } finally {
            actualizando = false;
        }
        codigoElegido = null;
        modeloTablaInscriptos.setFilas(List.of());
    }

    
    // Acciones
    

    //C.2: valida ANTES de guardar y avisa con un diálogo
    private void guardarAlumno() {
        String nombre = txtNombreAlumno.getText().trim();
        String apellido = txtApellidoAlumno.getText().trim();
        String dni = txtDniAlumno.getText().trim();
        String correo = txtCorreoAlumno.getText().trim();

        String problema = validarAlumno(nombre, apellido, dni, correo);
        if (problema != null) {
            JOptionPane.showMessageDialog(this, problema, "Revisá los datos", JOptionPane.WARNING_MESSAGE);
            return; // los campos quedan como estaban para poder corregir
        }
        Alumno nuevo = new Alumno(0, nombre, apellido, dni, correo.isEmpty() ? null : correo);
        try {
            alumnoDAO.insertar(nuevo);
            txtNombreAlumno.setText("");
            txtApellidoAlumno.setText("");
            txtDniAlumno.setText("");
            txtCorreoAlumno.setText("");
            JOptionPane.showMessageDialog(this, "El alumno se guardó correctamente.", "Listo",
                    JOptionPane.INFORMATION_MESSAGE);
            alumnoAElegir = nuevo.getId_alumno(); // queda elegido en el combo, listo para inscribirlo
            refrescar();
        } catch (InstitutoException e) {
            mostrarError(e); // por ejemplo: DNI repetido
        }
    }

    /** Devuelve el texto del problema, o null si todo está bien. */
    private String validarAlumno(String nombre, String apellido, String dni, String correo) {
        if (nombre.isEmpty()) return "El nombre es obligatorio.";
        if (apellido.isEmpty()) return "El apellido es obligatorio.";
        if (!dni.matches("\\d{7,8}")) return "El DNI debe tener 7 u 8 números, sin puntos ni espacios.";
        if (!correo.isEmpty() && !correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            return "El correo no tiene un formato válido (ejemplo: nombre@dominio.com). Si no tiene, dejalo vacío.";
        }
        return null;
    }

    /** C.3: alumno y curso salen de los combos (objetos), nada se escribe a mano. */
    private void inscribir() {
        Alumno alumno = (Alumno) comboAlumnos.getSelectedItem();
        Curso curso = (Curso) comboCursos.getSelectedItem();
        if (alumno == null) {
            aviso("No hay alumnos cargados. Dá de alta uno primero.");
            return;
        }
        if (curso == null) {
            aviso("No hay cursos cargados.");
            return;
        }
        try {
            inscripcionDAO.inscribir(alumno.getId_alumno(), curso.getCodigo());
            JOptionPane.showMessageDialog(this,
                    "Se inscribió a " + alumno.getApellido() + ", " + alumno.getNombre() + " en " + curso.getCodigo() + ".",
                    "Listo", JOptionPane.INFORMATION_MESSAGE);
        } catch (InstitutoException e) {
            mostrarError(e); // curso completo, ya inscripto, base apagada...
        }
        refrescar(); // también si falló: así se ve lo que realmente hay en la base
    }

    /** C.4: guarda (hay fila elegida) + confirmación antes de borrar. */
    private void darDeBaja() {
        FilaInscripcion fila = inscripcionElegida();
        if (fila == null) {
            aviso("Elegí una inscripción en la tabla de abajo (inscriptos del curso).");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Dar de baja la inscripción de " + fila.alumno() + "?\nSe libera una vacante en el curso.",
                "Confirmar baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            inscripcionDAO.borrar(fila.id_inscipcion());
        } catch (InstitutoException e) {
            mostrarError(e);
        }
        refrescar();
    }

    private void cambiarCuota() {
        FilaInscripcion fila = inscripcionElegida();
        if (fila == null) {
            aviso("Elegí una inscripción en la tabla de abajo (inscriptos del curso).");
            return;
        }
        try {
            Inscripcion inscripcion = inscripcionDAO.buscarPorId(fila.id_inscipcion());
            if (inscripcion == null) {
                aviso("La inscripción ya no existe.");
            } else {
                inscripcion.setCuotaPaga(!inscripcion.isCuotaPaga());
                inscripcionDAO.modificar(inscripcion);
            }
        } catch (InstitutoException e) {
            mostrarError(e);
        }
        refrescar();
    }

    // D.1: el informe sale de una consulta a la base
    private void verDeudores() {
        try {
            List<Deudor> deudores = inscripcionDAO.listarDeudores();
            new DeudoresDialog(this, deudores).setVisible(true);
        } catch (InstitutoException e) {
            mostrarError(e);
        }
    }

    
    // Auxiliares
   
    private FilaInscripcion inscripcionElegida() {
        int vista = tablaInscriptos.getSelectedRow();
        // con la tabla ordenada, la fila que se ve no es la fila del modelo: hay que convertir
        return vista < 0 ? null : modeloTablaInscriptos.getFila(tablaInscriptos.convertRowIndexToModel(vista));
    }

    private void aviso(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(InstitutoException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo completar la operación",
                JOptionPane.ERROR_MESSAGE);
    }
}
