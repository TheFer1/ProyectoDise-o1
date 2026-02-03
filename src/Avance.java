import java.util.Date;

/**
 * Clase modelo para Avance
 */
public class Avance {
    private int idAvance;
    private int idProyecto;
    private int idDirector;
    private String descripcion;
    private String nombreArchivo;
    private String rutaArchivo;
    private Date fechaCarga;
    private String estado; // Pendiente, Revisado, Aprobado
    private String archivoFirmado; // Ruta del archivo firmado por Jefatura
    
    // Constructores
    public Avance() {
    }
    
    public Avance(int idProyecto, int idDirector, String descripcion, String nombreArchivo, String rutaArchivo) {
        this.idProyecto = idProyecto;
        this.idDirector = idDirector;
        this.descripcion = descripcion;
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.fechaCarga = new Date();
        this.estado = "Pendiente";
    }
    
    public Avance(int idAvance, int idProyecto, int idDirector, String descripcion, String nombreArchivo, 
                  String rutaArchivo, Date fechaCarga, String estado) {
        this.idAvance = idAvance;
        this.idProyecto = idProyecto;
        this.idDirector = idDirector;
        this.descripcion = descripcion;
        this.nombreArchivo = nombreArchivo;
        this.rutaArchivo = rutaArchivo;
        this.fechaCarga = fechaCarga;
        this.estado = estado;
    }
    
    // Getters y Setters
    public int getIdAvance() {
        return idAvance;
    }
    
    public void setIdAvance(int idAvance) {
        this.idAvance = idAvance;
    }
    
    public int getIdProyecto() {
        return idProyecto;
    }
    
    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }
    
    public int getIdDirector() {
        return idDirector;
    }
    
    public void setIdDirector(int idDirector) {
        this.idDirector = idDirector;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }
    
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
    public String getRutaArchivo() {
        return rutaArchivo;
    }
    
    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
    
    public Date getFechaCarga() {
        return fechaCarga;
    }
    
    public void setFechaCarga(Date fechaCarga) {
        this.fechaCarga = fechaCarga;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public String getArchivoFirmado() {
        return archivoFirmado;
    }
    
    public void setArchivoFirmado(String archivoFirmado) {
        this.archivoFirmado = archivoFirmado;
    }
    
    


    @Override
    public String toString() {
        return "Avance{" +
                "idAvance=" + idAvance +
                ", idProyecto=" + idProyecto +
                ", idDirector=" + idDirector +
                ", descripcion='" + descripcion + '\'' +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", rutaArchivo='" + rutaArchivo + '\'' +
                ", fechaCarga=" + fechaCarga +
                ", estado='" + estado + '\'' +
                ", archivoFirmado='" + archivoFirmado + '\'' +
                '}';
    }
}
