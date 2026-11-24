package app;

import io.github.cdimascio.dotenv.Dotenv;

public class Instituto {
    public static void main(String[] args){
        System.out.println(System.getProperty("user.dir"));

        Dotenv dotenv = Dotenv.configure().directory("C:\\Users\\ceped\\Desktop\\tp4-instituto").ignoreIfMissing().load();
        System.out.println(dotenv.get("DB_URL", "Valor por defecto"));
        System.out.println(dotenv.get("DB_USER", "Valor por defecto"));
        System.out.println(dotenv.get("DB_PASS", "Valor por defecto"));

    }
}
