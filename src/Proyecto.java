import java.util.Date;

/**
 * Clase Proyecto que representa un proyecto académico/investigación
 */
public class Proyecto {
    private int id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private String tipo;
    private Date fechaInicio;
    private Date fechaFin;
    private int numeroDeAyudantesDelProyecto;
    private int idUsuario; // Usuario que creó el proyecto
    
    // Constructores
    public Proyecto() {
    }
    
    public Proyecto(String nombre, String codigo, String descripcion, String tipo) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }
    
    public Proyecto(int id, String nombre, String codigo, String descripcion, String tipo, Date fechaInicio, Date fechaFin, int numeroDeAyudantesDelProyecto) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numeroDeAyudantesDelProyecto = numeroDeAyudantesDelProyecto;
    }
    
    public Proyecto(int id, String nombre, String codigo, String descripcion, String tipo, Date fechaInicio, Date fechaFin, int numeroDeAyudantesDelProyecto, int idUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numeroDeAyudantesDelProyecto = numeroDeAyudantesDelProyecto;
        this.idUsuario = idUsuario;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public Date getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public Date getFechaFin() {
        return fechaFin;
    }
    
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public int getNumeroDeDayudantesDelProyecto() {
        return numeroDeAyudantesDelProyecto;
    }
    
    public void setNumeroDeDayudantesDelProyecto(int numeroDeAyudantesDelProyecto) {
        this.numeroDeAyudantesDelProyecto = numeroDeAyudantesDelProyecto;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    /**
     * Alias para setIdUsuario (idDirector = idUsuario en este contexto)
     */
    public void setIdDirector(int idDirector) {
        this.idUsuario = idDirector;
    }
    
    /**
     * Alias para getIdUsuario
     */
    public int getIdDirector() {
        return this.idUsuario;
    }

    @Override
    public String toString() {
        return "Proyecto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", codigo='" + codigo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", ayudantes=" + numeroDeAyudantesDelProyecto +
                '}';
    }
}
