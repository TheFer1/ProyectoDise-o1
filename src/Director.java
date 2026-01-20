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
    private SolicitudDAO solicitudDAO;
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
        this.solicitudDAO = new SolicitudDAO();
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
     * Envía una solicitud
     * Verifica que exista al menos un formulario antes de enviar
     * Si no hay formularios, crea una notificación recordando llenar el formulario
     */
    public boolean enviarSolicitud(Solicitud solicitud) {
        if (solicitud == null) {
            return false;
        }
        
        // Verificar si el director tiene formularios en SUS proyectos
        boolean tieneFormularios = false;
        List<Proyecto> misProyectos = proyectoDAO.obtenerPorDirector(this.id);
        
        if (misProyectos != null && !misProyectos.isEmpty()) {
            for (Proyecto proyecto : misProyectos) {
                List<Formulario> formulariosProyecto = formularioDAO.buscarPorProyecto(proyecto.getId());
                if (formulariosProyecto != null && !formulariosProyecto.isEmpty()) {
                    tieneFormularios = true;
                    break;
                }
            }
        }
        
        if (!tieneFormularios) {
            // No tiene formularios en sus proyectos, crear notificación
            Notificacion notificacion = new Notificacion(
                "ATENCIÓN: Debe llenar un formulario de ayudantes en uno de sus proyectos antes de enviar una solicitud a Jefatura. Por favor, complete el formulario requerido.",
                this.id
            );
            notificacionDAO.insertar(notificacion);
            System.out.println("No se puede enviar la solicitud. Debe llenar un formulario en sus proyectos primero.");
            return false;
        }
        
        // Si tiene formularios en sus proyectos, proceder con el envío
        solicitud.setIdUsuario(this.id);
        solicitud.setFecha(new Date());
        solicitud.setEstadoEmisionDest("Pendiente");
        return solicitudDAO.insertar(solicitud);
    }
    
    /**
     * Obtiene las solicitudes enviadas por este director
     */
    public List<Solicitud> consultarMisSolicitudes() {
        return solicitudDAO.obtenerPorUsuario(this.id);
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
