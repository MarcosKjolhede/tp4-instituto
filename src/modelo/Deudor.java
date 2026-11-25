package modelo;

import java.math.BigDecimal;
//informar los deudores 
public record Deudor(String alumno, String dni, String curso, BigDecimal importe) {
}