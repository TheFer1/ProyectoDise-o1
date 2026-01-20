/**
 * Clase para encapsular el resultado de una operación
 * Separa la lógica de negocio de la presentación
 */
public class ResultadoOperacion {
    private boolean exitoso;
    private String mensaje;
    private String tipoMensaje; // "exito", "error", "advertencia", "info"
    
    // Constructores
    public ResultadoOperacion() {
    }
    
    public ResultadoOperacion(boolean exitoso, String mensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.tipoMensaje = exitoso ? "exito" : "error";
    }
    
    public ResultadoOperacion(boolean exitoso, String mensaje, String tipoMensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.tipoMensaje = tipoMensaje;
    }
    
    // Factory methods para crear resultados comunes
    public static ResultadoOperacion exito(String mensaje) {
        return new ResultadoOperacion(true, mensaje, "exito");
    }
    
    public static ResultadoOperacion error(String mensaje) {
        return new ResultadoOperacion(false, mensaje, "error");
    }
    
    public static ResultadoOperacion advertencia(String mensaje) {
        return new ResultadoOperacion(false, mensaje, "advertencia");
    }
    
    public static ResultadoOperacion info(String mensaje) {
        return new ResultadoOperacion(true, mensaje, "info");
    }
    
    // Getters y Setters
    public boolean isExitoso() {
        return exitoso;
    }
    
    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public String getTipoMensaje() {
        return tipoMensaje;
    }
    
    public void setTipoMensaje(String tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }
    
    /**
     * Verifica si el resultado es de tipo error
     */
    public boolean esError() {
        return "error".equals(tipoMensaje);
    }
    
    /**
     * Verifica si el resultado es de tipo advertencia
     */
    public boolean esAdvertencia() {
        return "advertencia".equals(tipoMensaje);
    }
    
    /**
     * Verifica si el resultado es de tipo éxito
     */
    public boolean esExito() {
        return "exito".equals(tipoMensaje);
    }
    
    @Override
    public String toString() {
        return "ResultadoOperacion{" +
                "exitoso=" + exitoso +
                ", mensaje='" + mensaje + '\'' +
                ", tipoMensaje='" + tipoMensaje + '\'' +
                '}';
    }
}
