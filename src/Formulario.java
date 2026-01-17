/**
 * Clase Formulario que representa un formulario de solicitud de ayudantes
 * Encapsula la lógica de validación y comportamiento de formularios
 */
public class Formulario {
    private int id;
    private int numeroDeAyudantes;
    private String nombreDelAyudante;
    private String apellidoDelAyudante;
    private String cedula;
    private String facultad;
    private String estado; // Pendiente, Aprobado, Rechazado
    private int idProyecto; // ID del proyecto asociado
    
    // Constructores
    public Formulario() {
    }
    
    public Formulario(int numeroDeAyudantes, String nombreDelAyudante, String apellidoDelAyudante, 
                      String cedula, String facultad) {
        this.numeroDeAyudantes = numeroDeAyudantes;
        this.nombreDelAyudante = nombreDelAyudante;
        this.apellidoDelAyudante = apellidoDelAyudante;
        this.cedula = cedula;
        this.facultad = facultad;
        this.estado = "Pendiente";
    }
    
    public Formulario(int id, int numeroDeAyudantes, String nombreDelAyudante, String apellidoDelAyudante, 
                      String cedula, String facultad, String estado) {
        this.id = id;
        this.numeroDeAyudantes = numeroDeAyudantes;
        this.nombreDelAyudante = nombreDelAyudante;
        this.apellidoDelAyudante = apellidoDelAyudante;
        this.cedula = cedula;
        this.facultad = facultad;
        this.estado = estado;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getNumeroDeAyudantes() {
        return numeroDeAyudantes;
    }
    
    public void setNumeroDeAyudantes(int numeroDeAyudantes) {
        this.numeroDeAyudantes = numeroDeAyudantes;
    }
    
    public String getNombreDelAyudante() {
        return nombreDelAyudante;
    }
    
    public void setNombreDelAyudante(String nombreDelAyudante) {
        this.nombreDelAyudante = nombreDelAyudante;
    }
    
    public String getApellidoDelAyudante() {
        return apellidoDelAyudante;
    }
    
    public void setApellidoDelAyudante(String apellidoDelAyudante) {
        this.apellidoDelAyudante = apellidoDelAyudante;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public String getFacultad() {
        return facultad;
    }
    
    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public int getIdProyecto() {
        return idProyecto;
    }
    
    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }
    
    // ==================== MÉTODOS DE LÓGICA ====================
    
    /**
     * Valida si el formulario tiene todos los campos obligatorios
     */
    public boolean esValido() {
        return nombreDelAyudante != null && !nombreDelAyudante.trim().isEmpty() &&
               apellidoDelAyudante != null && !apellidoDelAyudante.trim().isEmpty() &&
               cedula != null && !cedula.trim().isEmpty() &&
               facultad != null && !facultad.trim().isEmpty() &&
               numeroDeAyudantes > 0 &&
               idProyecto > 0;
    }
    
    /**
     * Obtiene el nombre completo del ayudante
     */
    public String obtenerNombreCompleto() {
        return nombreDelAyudante + " " + apellidoDelAyudante;
    }
    
    /**
     * Valida que la cédula tenga un formato válido
     */
    public boolean esCedulaValida() {
        return cedula != null && cedula.matches("[0-9]{7,10}");
    }
    
    /**
     * Cambia el estado a Aprobado
     */
    public void aprobar() {
        this.estado = "Aprobado";
    }
    
    /**
     * Cambia el estado a Rechazado
     */
    public void rechazar() {
        this.estado = "Rechazado";
    }
    
    /**
     * Verifica si el formulario está pendiente
     */
    public boolean estaPendiente() {
        return "Pendiente".equals(this.estado);
    }
    
    /**
     * Verifica si el formulario fue aprobado
     */
    public boolean estaAprobado() {
        return "Aprobado".equals(this.estado);
    }
    
    /**
     * Verifica si el formulario fue rechazado
     */
    public boolean estaRechazado() {
        return "Rechazado".equals(this.estado);
    }
    
    @Override
    public String toString() {
        return "Formulario{" +
                "id=" + id +
                ", numeroDeAyudantes=" + numeroDeAyudantes +
                ", nombreDelAyudante='" + nombreDelAyudante + '\'' +
                ", apellidoDelAyudante='" + apellidoDelAyudante + '\'' +
                ", cedula='" + cedula + '\'' +
                ", facultad='" + facultad + '\'' +
                ", estado='" + estado + '\'' +
                ", idProyecto=" + idProyecto +
                '}';
    }
}
