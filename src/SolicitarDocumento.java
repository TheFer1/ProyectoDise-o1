import java.util.Date;

/**
 * Clase SolicitarDocumento que extiende de Solicitud
 * Especialización para solicitudes de documentos
 */
public class SolicitarDocumento extends Solicitud {
    
    // Constructores
    public SolicitarDocumento() {
        super();
        this.setTipo("Documento");
    }
    
    public SolicitarDocumento(String asunto, int idUsuario, String tipoDeDocumento) {
        super(asunto, idUsuario);
        this.setTipo("Documento");
        this.setTipoDocumento(tipoDeDocumento);
    }
    
    public SolicitarDocumento(int idSolicitud, Date fecha, String asunto, String estadoEmisionDest, 
                             int idUsuario, String tipoDeDocumento) {
        super(idSolicitud, fecha, asunto, estadoEmisionDest, idUsuario);
        this.setTipo("Documento");
        this.setTipoDocumento(tipoDeDocumento);
    }
    
    /**
     * Valida que el tipo de documento sea válido
     */
    public boolean esTipoDocumentoValido() {
        String tipo = this.getTipoDocumento();
        return tipo != null && !tipo.trim().isEmpty();
    }
    
    /**
     * Obtiene el tipo de documento solicitado
     */
    public String obtenerTipoDocumento() {
        return this.getTipoDocumento();
    }
    
    /**
     * Verifica si es un tipo de documento común
     */
    public boolean esDocumentoComun() {
        String tipo = this.getTipoDocumento();
        if (tipo == null) return false;
        
        tipo = tipo.toLowerCase();
        return tipo.contains("certificado") || 
               tipo.contains("constancia") || 
               tipo.contains("comprobante") ||
               tipo.contains("acta");
    }
    
    /**
     * Valida si el documento puede ser procesado
     */
    public boolean puedeSerProcesado() {
        return esTipoDocumentoValido() && 
               this.getAsunto() != null && 
               !this.getAsunto().trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "SolicitarDocumento{" +
                "tipoDocumento='" + this.getTipoDocumento() + '\'' +
                ", asunto='" + this.getAsunto() + '\'' +
                ", estado='" + this.getEstadoEmisionDest() + '\'' +
                '}';
    }
}
