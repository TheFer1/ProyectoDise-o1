import java.util.Date;

/**
 * Clase Notificacion para gestionar notificaciones del sistema
 * Encapsula el comportamiento de las notificaciones
 */
public class Notificacion {
    private int id;
    private Date fecha;
    private String informacion;
    private int idUsuario;
    private boolean leida; // Para marcar si fue leída
    
    // Constructores
    public Notificacion() {
    }
    
    public Notificacion(String informacion, int idUsuario) {
        this.informacion = informacion;
        this.idUsuario = idUsuario;
        this.fecha = new Date();
        this.leida = false;
    }
    
    public Notificacion(int id, Date fecha, String informacion, int idUsuario) {
        this.id = id;
        this.fecha = fecha;
        this.informacion = informacion;
        this.idUsuario = idUsuario;
        this.leida = false;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public String getInformacion() {
        return informacion;
    }
    
    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public boolean isLeida() {
        return leida;
    }
    
    public void setLeida(boolean leida) {
        this.leida = leida;
    }
    
    // ==================== MÉTODOS DE LÓGICA ====================
    
    /**
     * Marca la notificación como leída
     */
    public void marcarComoLeida() {
        this.leida = true;
    }
    
    /**
     * Marca la notificación como no leída
     */
    public void marcarComoNoLeida() {
        this.leida = false;
    }
    
    /**
     * Obtiene el estado de lectura en texto
     */
    public String obtenerEstadoLectura() {
        return leida ? "Leída" : "No leída";
    }
    
    /**
     * Valida si la notificación es válida
     */
    public boolean esValida() {
        return informacion != null && !informacion.trim().isEmpty() &&
               idUsuario > 0 && fecha != null;
    }
    
    /**
     * Obtiene una vista previa de la notificación
     */
    public String obtenerVista() {
        return String.format("[%s] %s - %s", 
            obtenerEstadoLectura(), 
            fecha, 
            informacion.substring(0, Math.min(50, informacion.length())));
    }
    
    @Override
    public String toString() {
        return "Notificacion{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", informacion='" + informacion + '\'' +
                ", idUsuario=" + idUsuario +
                ", leida=" + leida +
                '}';
    }
}
