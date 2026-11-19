package datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion { 
    private static final String URL= "jdbc:mysql://localhost:3306/institutos";
    private static final String USUARIO= "root";
    private static final String CLAVE= " ";

    public static Connection abrir() throws SQLException{
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    } 
    
}
