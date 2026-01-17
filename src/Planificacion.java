import java.util.List;

/**
 * Clase Planificacion para gestionar la planificación de ayudantes en proyectos
 */
public class Planificacion {
    private int id;
    private int idProyecto;
    private int maximoDeAyudantes;
    private int ayudantesRegistrados;
    
    // DAOs
    private FormularioDAO formularioDAO;
    private ProyectoDAO proyectoDAO;
    
    // Constructores
    public Planificacion() {
        inicializarDAOs();
    }
    
    public Planificacion(int maximoDeAyudantes, int ayudantesRegistrados) {
        this.maximoDeAyudantes = maximoDeAyudantes;
        this.ayudantesRegistrados = ayudantesRegistrados;
        inicializarDAOs();
    }
    
    public Planificacion(int id, int idProyecto, int maximoDeAyudantes, int ayudantesRegistrados) {
        this.id = id;
        this.idProyecto = idProyecto;
        this.maximoDeAyudantes = maximoDeAyudantes;
        this.ayudantesRegistrados = ayudantesRegistrados;
        inicializarDAOs();
    }
    
    /**
     * Inicializa los DAOs
     */
    private void inicializarDAOs() {
        this.formularioDAO = new FormularioDAO();
        this.proyectoDAO = new ProyectoDAO();
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdProyecto() {
        return idProyecto;
    }
    
    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }
    
    public int getMaximoDeAyudantes() {
        return maximoDeAyudantes;
    }
    
    public void setMaximoDeAyudantes(int maximoDeAyudantes) {
        this.maximoDeAyudantes = maximoDeAyudantes;
    }
    
    public int getAyudantesRegistrados() {
        return ayudantesRegistrados;
    }
    
    public void setAyudantesRegistrados(int ayudantesRegistrados) {
        this.ayudantesRegistrados = ayudantesRegistrados;
    }
    
    /**
     * Calcula cuántos ayudantes faltan por registrar
     */
    public int calcularFaltantes() {
        return maximoDeAyudantes - ayudantesRegistrados;
    }
    
    /**
     * Verifica si el número de ayudantes está dentro del límite
     */
    public boolean verificarNumeroDeAyudantes() {
        return ayudantesRegistrados <= maximoDeAyudantes;
    }
    
    /**
     * Verifica si se puede registrar más ayudantes
     */
    public boolean puedeRegistrarMasAyudantes() {
        return ayudantesRegistrados < maximoDeAyudantes;
    }
    
    /**
     * Obtiene los formularios aprobados para este proyecto
     */
    public List<Formulario> obtenerAyudantesAprobados() {
        return formularioDAO.buscarPorEstado("Aprobado");
    }
    
    /**
     * Obtiene los formularios pendientes para este proyecto
     */
    public List<Formulario> obtenerAyudantesPendientes() {
        return formularioDAO.buscarPorEstado("Pendiente");
    }
    
    /**
     * Obtiene el proyecto asociado a esta planificación
     */
    public Proyecto obtenerProyecto() {
        if (idProyecto > 0) {
            return proyectoDAO.obtenerPorId(idProyecto);
        }
        return null;
    }
    
    /**
     * Actualiza el número de ayudantes registrados basado en los formularios aprobados
     */
    public void actualizarAyudantesRegistrados() {
        List<Formulario> aprobados = obtenerAyudantesAprobados();
        if (aprobados != null) {
            this.ayudantesRegistrados = aprobados.size();
        }
    }
    
    @Override
    public String toString() {
        return "Planificacion{" +
                "id=" + id +
                ", idProyecto=" + idProyecto +
                ", maximoDeAyudantes=" + maximoDeAyudantes +
                ", ayudantesRegistrados=" + ayudantesRegistrados +
                ", faltantes=" + calcularFaltantes() +
                '}';
    }
}
