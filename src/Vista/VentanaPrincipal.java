package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaPrincipal extends JFrame{
    //para dar de alta un alumno
    private JTextField txtNombreAlumno;
    private JTextField txtApellidoAlumno;
    private JTextField txtDniAlumno;
    private JTextField txtCorreoAlumno;
    private JButton btnGuardarAlumno;

    //componentes de inscripcion
    private JComboBox<String> comboAlumnos;
    private JComboBox<String> comboCursos;
    private JButton btnInscribir;

    //componentes de la tabla con sus acciones por definir
    private JTable tablaCursos;
    private DefaultTableModel modeloTablaCursos;
    private JButton btnBajaInscripcion;
    private JButton btnRefrescar;

    //Boton para el informe de deudores
    private JButton btnInformeDeudores;

    public VentanaPrincipal(){
        super("Instituto de Idiomas");
        setSize(900, 650);
        setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));
        setLocationRelativeTo((null));
        setLayout(new BorderLayout());

        //Panel para dar de alta alumnos
        JPanel panelFormularioAlumno= new JPanel(new GridLayout(5,2,5,5));
        panelFormularioAlumno.setBorder(BorderFactory.createTitledBorder("Alta de Alumno Nuevo(DNI UNICO)"));

        panelFormularioAlumno.add(new JLabel("Nombre:"));
        txtNombreAlumno = new JTextField();
        panelFormularioAlumno.add(txtNombreAlumno);

        panelFormularioAlumno.add(new JLabel("Apellido:"));
        txtApellidoAlumno = new JTextField();
        panelFormularioAlumno.add(txtApellidoAlumno);

        panelFormularioAlumno.add(new JLabel("DNI:"));
        txtDniAlumno = new JTextField();
        panelFormularioAlumno.add(txtDniAlumno);

        panelFormularioAlumno.add(new JLabel("Correo (opcional): "));
        txtCorreoAlumno = new JTextField();
        panelFormularioAlumno.add(txtCorreoAlumno);

        btnGuardarAlumno = new JButton("Guardar Alumno");
        panelFormularioAlumno.add(new JLabel());
        panelFormularioAlumno.add(btnGuardarAlumno);

        add(panelFormularioAlumno, BorderLayout.NORTH);

        String[] columnas = {"Codigo", " Idioma", "Nivel", "Turno", "Cupo", "Inscriptos", "Vacante"};

        //Sobreescritura de getColumnClass para ordenar numeros por numero y no por texto
        modeloTablaCursos = new DefaultTableModel(columnas, 0){
            //de la columna 4 en adelante indicamos que son tipo integer
            @Override
            public Class<?> getColumnClass(int columna){
                if(columna >=4){
                    return Integer.class;
                }
            return String.class;
            }
            //no permite la edicion de las celdas de alumnos conviritendolas solo en lectura
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        tablaCursos = new JTable(modeloTablaCursos);
        tablaCursos.setAutoCreateRowSorter(true);

        JScrollPane scrollTabla = new JScrollPane(tablaCursos);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Cursos, Inscriptos y Vacantes"));
        add(scrollTabla, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new GridLayout(2,1));

        JPanel panelInscripcion = new JPanel(new FlowLayout());
        panelInscripcion.setBorder(BorderFactory.createTitledBorder("Nueva Inscripcion:"));

        comboAlumnos = new JComboBox<>();
        comboCursos = new JComboBox<>();
        btnInscribir = new JButton("Inscribir Alumno");

        panelInscripcion.add(new JLabel("Alumno: "));
        panelInscripcion.add(comboAlumnos);
        panelInscripcion.add(new JLabel("Curso: "));
        panelInscripcion.add(comboCursos);
        panelInscripcion.add(btnInscribir);

        JPanel panelBotonesAccion = new JPanel(new FlowLayout());
        btnBajaInscripcion = new JButton("Dar de Baja Inscripcion Seleccionada");
        btnInformeDeudores = new JButton("Ver informe de Deudores");
        btnRefrescar = new JButton("Refrescar tabla");

        panelBotonesAccion.add(btnBajaInscripcion);
        panelBotonesAccion.add(btnInformeDeudores);
        panelBotonesAccion.add(btnRefrescar);

        panelInferior.add(panelInscripcion);
        panelInferior.add(panelBotonesAccion);

        add(panelInferior, BorderLayout.SOUTH);
    }
    public  DefaultTableModel getModeloTablaCursos(){return modeloTablaCursos;}
    public JComboBox<String> getComboAlumnos(){return comboAlumnos;}
    public  JComboBox<String> getComboCursos(){return comboCursos;}
}
