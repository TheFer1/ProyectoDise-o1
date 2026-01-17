import java.util.Date;

/**
 * Clase base Solicitud
 */
public class Solicitud {
    private int idSolicitud;
    private Date fecha;
    private String asunto;
    private String estadoEmisionDest; // Estado de emisión y destino
    private int idUsuario; // ID del usuario que solicita (Jefatura)
    private int idDirector; // ID del director asociado a la solicitud
    private String tipo; // Permiso o Documento
    private String codigoPermiso; // Código del permiso si es tipo Permiso
    private String tipoDocumento; // Tipo de documento si es tipo Documento
    
    // Constructores
    public Solicitud() {
    }
    
    public Solicitud(String asunto, int idUsuario) {
        this.asunto = asunto;
        this.idUsuario = idUsuario;
        this.fecha = new Date();
        this.estadoEmisionDest = "Pendiente";
    }
    
    public Solicitud(int idSolicitud, Date fecha, String asunto, String estadoEmisionDest, int idUsuario) {
        this.idSolicitud = idSolicitud;
        this.fecha = fecha;
        this.asunto = asunto;
        this.estadoEmisionDest = estadoEmisionDest;
        this.idUsuario = idUsuario;
    }
    
    public Solicitud(int idSolicitud, Date fecha, String asunto, String estadoEmisionDest, int idUsuario, int idDirector) {
        this.idSolicitud = idSolicitud;
        this.fecha = fecha;
        this.asunto = asunto;
        this.estadoEmisionDest = estadoEmisionDest;
        this.idUsuario = idUsuario;
        this.idDirector = idDirector;
        this.tipo = null;
        this.codigoPermiso = null;
        this.tipoDocumento = null;
    }
    
    public Solicitud(int idSolicitud, Date fecha, String asunto, String estadoEmisionDest, int idUsuario, int idDirector, String tipo, String codigoPermiso, String tipoDocumento) {
        this.idSolicitud = idSolicitud;
        this.fecha = fecha;
        this.asunto = asunto;
        this.estadoEmisionDest = estadoEmisionDest;
        this.idUsuario = idUsuario;
        this.idDirector = idDirector;
        this.tipo = tipo;
        this.codigoPermiso = codigoPermiso;
        this.tipoDocumento = tipoDocumento;
    }
    // Getters y Setters
    public int getIdSolicitud() {
        return idSolicitud;
    }
    
    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public String getAsunto() {
        return asunto;
    }
    
    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }
    
    public String getEstadoEmisionDest() {
        return estadoEmisionDest;
    }
    
    public void setEstadoEmisionDest(String estadoEmisionDest) {
        this.estadoEmisionDest = estadoEmisionDest;
    }
    
    /**
     * Alias para setEstadoEmisionDest para compatibilidad
     */
    public void setEstado(String estado) {
        this.estadoEmisionDest = estado;
    }
    
    /**
     * Alias para getEstadoEmisionDest para compatibilidad
     */
    public String getEstado() {
        return this.estadoEmisionDest;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public int getIdDirector() {
        return idDirector;
    }
    
    public void setIdDirector(int idDirector) {
        this.idDirector = idDirector;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getCodigoPermiso() {
        return codigoPermiso;
    }
    
    public void setCodigoPermiso(String codigoPermiso) {
        this.codigoPermiso = codigoPermiso;
    }
    
    public String getTipoDocumento() {
        return tipoDocumento;
    }
    
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    
    @Override
    public String toString() {
        return "Solicitud{" +
                "idSolicitud=" + idSolicitud +
                ", fecha=" + fecha +
                ", asunto='" + asunto + '\'' +
                ", estado='" + estadoEmisionDest + '\'' +
                '}';
    }
}
