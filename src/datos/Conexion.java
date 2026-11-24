package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class Conexion {
    // System.out.println(System.getProperty("user.dir"));
    private static final Dotenv dotenv = Dotenv.configure().directory("C:\\Users\\marco\\OneDrive\\Documentos\\UTN_Tup\\ProgII\\Ejercicios\\tp4-instituto\\src\\sources\\").ignoreIfMissing().load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASS = dotenv.get("DB_PASS");

    public static Connection abrir() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
