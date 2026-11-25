package datos;

import java.sql.Statement;
import java.nio.file.Path;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class Conexion {
    //System.out.println(System.getProperty("user.dir"));
    private static final Dotenv dotenv = Dotenv.configure().directory("C:\\Users\\ceped\\Desktop\\tp4-instituto").ignoreIfMissing().load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");



    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
    private static boolean creada = false;

    public static Connection abrir() throws SQLException {
        // System.out.println("URL: " + URL);
        // System.out.println("USER: " + USER);
        // System.out.println("PASS cargada: " + (PASS != null && !PASS.isEmpty()));
        
        if (!creada) {
            ejecutarScript("src/instituto.sql");
            creada = true;
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static void ejecutarScript(String rutaArchivo) throws SQLException {
        try {
            String contenido = Files.readString(Path.of(rutaArchivo));
            try (Connection conn = DriverManager.getConnection(SERVER_URL, USER, PASS);
                 Statement st = conn.createStatement()) {

                for (String sentencia : contenido.split(";")) {
                    String limpia = sentencia.trim();
                    if (!limpia.isEmpty()) {
                        st.execute(limpia);
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLException("No se pudo ejecutar el script SQL: " + e.getMessage(), e);
        }
    }
}
