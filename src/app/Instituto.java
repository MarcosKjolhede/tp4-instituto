package app;

import io.github.cdimascio.dotenv.Dotenv;
import javax.swing.*;

public class Instituto {
    public static void main(String[] args){
        System.out.println(System.getProperty("user.dir"));

        Dotenv dotenv = Dotenv.configure().directory("C:\\Users\\marco\\OneDrive\\Documentos\\UTN_Tup\\ProgII\\Ejercicios\\tp4-instituto").ignoreIfMissing().load();
        System.out.println(dotenv.get("DB_URL", "Valor por defecto"));
        System.out.println(dotenv.get("DB_USER", "Valor por defecto"));
        System.out.println(dotenv.get("DB_PASS", "Valor por defecto"));

        // D.2: red de seguridad. Si se escapara algo inesperado
        Thread.setDefaultUncaughtExceptionHandler((hilo, error) ->
                JOptionPane.showMessageDialog(null, "Ocurrio un error inesperado. La aplicación sigue funcionando " , "Error", JOptionPane.ERROR_MESSAGE));

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                // si no hay look & feel del sistema, se usa el de Java
            }
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
            ventana.refrescar(); // carga inicial; si la base está apagada, avisa con un diálogo
        });

    }
}





