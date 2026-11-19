package datos;

public class InstitutoException extends Exception {
    public InstitutoException(String mensaje){
        super(mensaje);
    }
    public InstitutoException(String mensaje, Throwable causa){
        super(mensaje, causa);
    }
    
}
