import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase Director que extiende de Usuario
 * Representa un director de proyecto con funcionalidades específicas
 */
public class Director extends Usuario {
    
    // DAOs
    private FormularioDAO formularioDAO;
    private ProyectoDAO proyectoDAO;
    private NotificacionDAO notificacionDAO;
    
    // Constructor
    public Director() {
        super();
        inicializarDAOs();
    }
    
    public Director(String nombre, String apellido, String correo, String contraseña) {
        super(nombre, apellido, correo, contraseña);
        inicializarDAOs();
    }
    
    public Director(int id, String nombre, String apellido, String correo, String contraseña) {
        super(id, nombre, apellido, correo, contraseña);
        inicializarDAOs();
    }
    
    /**
     * Inicializa los DAOs
     */
    private void inicializarDAOs() {
        this.formularioDAO = new FormularioDAO();
        this.proyectoDAO = new ProyectoDAO();
        this.notificacionDAO = new NotificacionDAO();
    }
    
    /**
     * Registra un nuevo formulario (ayudante) para un proyecto
     */
    public boolean registrarFormulario(Formulario formulario) {
        if (formulario == null) {
            return false;
        }
        return formularioDAO.insertar(formulario);
    }
    
    /**
     * Obtiene todos los formularios registrados por este director
     */
    public List<Formulario> consultarFormularios() {
        return formularioDAO.obtenerTodos();
    }
    
    /**
     * Obtiene formularios por estado
     */
    public List<Formulario> consultarFormulariosPorEstado(String estado) {
        return formularioDAO.buscarPorEstado(estado);
    }
    
    /**
     * Obtiene los proyectos de este director
     */
    public List<Proyecto> consultarMisProyectos() {
        return proyectoDAO.obtenerPorDirector(this.id);
    }
    

    
    /**
     * Obtiene las notificaciones del director
     */
    public List<Notificacion> consultarMisNotificaciones() {
        return notificacionDAO.obtenerPorUsuario(this.id);
    }
    
    /**
     * Obtiene las notificaciones no leídas del director
     */
    public List<Notificacion> consultarNotificacionesNoLeidas() {
        List<Notificacion> todasNotificaciones = notificacionDAO.obtenerPorUsuario(this.id);
        List<Notificacion> noLeidas = new ArrayList<>();
        for (Notificacion n : todasNotificaciones) {
            if (!n.isLeida()) {
                noLeidas.add(n);
            }
        }
        return noLeidas;
    }
    
    /**
     * Registra un rechazo de formulario
     */
    public boolean rechazarFormulario(int idFormulario, String motivo) {
        Formulario f = formularioDAO.obtenerPorId(idFormulario);
        if (f == null) {
            return false;
        }
        f.setEstado("Rechazado");
        return formularioDAO.actualizar(f);
    }
    
    /**
     * Aprueba un formulario
     */
    public boolean aprobarFormulario(int idFormulario) {
        Formulario f = formularioDAO.obtenerPorId(idFormulario);
        if (f == null) {
            return false;
        }
        f.setEstado("Aprobado");
        return formularioDAO.actualizar(f);
    }
    
    /**
     * Crea un nuevo proyecto
     */
    public boolean crearProyecto(Proyecto proyecto) {
        if (proyecto == null) {
            return false;
        }
        proyecto.setIdDirector(this.id);
        return proyectoDAO.insertar(proyecto);
    }
    
    /**
     * Actualiza un proyecto existente
     */
    public boolean actualizarProyecto(Proyecto proyecto) {
        if (proyecto == null) {
            return false;
        }
        return proyectoDAO.actualizar(proyecto);
    }
    
    @Override
    public String toString() {
        return "Director{" + super.toString() + "}";
    }
}
