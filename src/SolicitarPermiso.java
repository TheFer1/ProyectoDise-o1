import java.util.Date;

/**
 * Clase SolicitarPermiso que extiende de Solicitud
 * Especialización para solicitudes de permisos
 */
public class SolicitarPermiso extends Solicitud {
    
    // Constructores
    public SolicitarPermiso() {
        super();
        this.setTipo("Permiso");
    }
    
    public SolicitarPermiso(String asunto, int idUsuario, String codigoPermiso) {
        super(asunto, idUsuario);
        this.setTipo("Permiso");
        this.setCodigoPermiso(codigoPermiso);
    }
    
    public SolicitarPermiso(int idSolicitud, Date fecha, String asunto, String estadoEmisionDest, 
                           int idUsuario, String codigoPermiso) {
        super(idSolicitud, fecha, asunto, estadoEmisionDest, idUsuario);
        this.setTipo("Permiso");
        this.setCodigoPermiso(codigoPermiso);
    }
    
    /**
     * Valida que el código de permiso sea válido
     */
    public boolean esCodigoPermisoValido() {
        String codigo = this.getCodigoPermiso();
        return codigo != null && !codigo.trim().isEmpty() && codigo.length() >= 3;
    }
    
    /**
     * Obtiene el código del permiso
     */
    public String obtenerCodigoPermiso() {
        return this.getCodigoPermiso();
    }
    
    /**
     * Valida si el permiso puede ser procesado
     */
    public boolean puedeSerProcesado() {
        return esCodigoPermisoValido() && 
               this.getAsunto() != null && 
               !this.getAsunto().trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "SolicitarPermiso{" +
                "codigoPermiso='" + this.getCodigoPermiso() + '\'' +
                ", asunto='" + this.getAsunto() + '\'' +
                ", estado='" + this.getEstadoEmisionDest() + '\'' +
                '}';
    }
}
