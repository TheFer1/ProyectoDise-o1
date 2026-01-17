import java.util.List;

/**
 * Clase Jefatura que extiende de Usuario
 * Representa a un miembro de la jefatura con funciones administrativas
 */
public class Jefatura extends Usuario {
    
    // DAOs
    private ProyectoDAO proyectoDAO;
    private FormularioDAO formularioDAO;
    private SolicitudDAO solicitudDAO;
    private UsuarioDAO usuarioDAO;
    
    // Constructor
    public Jefatura() {
        super();
        inicializarDAOs();
    }
    
    public Jefatura(String nombre, String apellido, String correo, String contraseña) {
        super(nombre, apellido, correo, contraseña);
        inicializarDAOs();
    }
    
    public Jefatura(int id, String nombre, String apellido, String correo, String contraseña) {
        super(id, nombre, apellido, correo, contraseña);
        inicializarDAOs();
    }
    
    /**
     * Inicializa los DAOs
     */
    private void inicializarDAOs() {
        this.proyectoDAO = new ProyectoDAO();
        this.formularioDAO = new FormularioDAO();
        this.solicitudDAO = new SolicitudDAO();
        this.usuarioDAO = new UsuarioDAO();
    }
    
    /**
     * Obtiene todos los proyectos registrados
     */
    public List<Proyecto> consultarProyectos() {
        return proyectoDAO.obtenerTodos();
    }
    
    /**
     * Obtiene todos los formularios registrados
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
     * Obtiene todas las solicitudes
     */
    public List<Solicitud> consultarSolicitudes() {
        return solicitudDAO.obtenerTodas();
    }
    
    /**
     * Obtiene solicitudes por estado
     */
    public List<Solicitud> consultarSolicitudesPorEstado(String estado) {
        return solicitudDAO.buscarPorEstado(estado);
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
     * Rechaza un formulario
     */
    public boolean rechazarFormulario(int idFormulario) {
        Formulario f = formularioDAO.obtenerPorId(idFormulario);
        if (f == null) {
            return false;
        }
        f.setEstado("Rechazado");
        return formularioDAO.actualizar(f);
    }
    
    /**
     * Asesora sobre una solicitud
     */
    public boolean asesorarSolicitud(int idSolicitud, String asesoramiento) {
        Solicitud s = solicitudDAO.obtenerPorId(idSolicitud);
        if (s == null) {
            return false;
        }
        s.setEstadoEmisionDest("Asesorada");
        // Nota: Agregar campo de asesoramiento en Solicitud si no existe
        return solicitudDAO.actualizar(s);
    }
    
    /**
     * Aprueba una solicitud
     */
    public boolean aprobarSolicitud(int idSolicitud) {
        Solicitud s = solicitudDAO.obtenerPorId(idSolicitud);
        if (s == null) {
            return false;
        }
        s.setEstadoEmisionDest("Aprobada");
        return solicitudDAO.actualizar(s);
    }
    
    /**
     * Rechaza una solicitud
     */
    public boolean rechazarSolicitud(int idSolicitud) {
        Solicitud s = solicitudDAO.obtenerPorId(idSolicitud);
        if (s == null) {
            return false;
        }
        s.setEstadoEmisionDest("Rechazada");
        return solicitudDAO.actualizar(s);
    }
    
    /**
     * Obtiene información de un director específico
     */
    public Usuario obtenerDirector(int idDirector) {
        return usuarioDAO.obtenerPorId(idDirector);
    }
    
    /**
     * Obtiene todos los directores registrados
     */
    public List<Usuario> consultarDirectores() {
        return usuarioDAO.obtenerTodos();
    }
    
    @Override
    public String toString() {
        return "Jefatura{" + super.toString() + "}";
    }
}
