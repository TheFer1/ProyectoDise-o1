import java.util.List;

/**
 * Clase Jefatura que extiende de Usuario
 * Representa a un miembro de la jefatura con funciones administrativas
 */
public class Jefatura extends Usuario {
    
    // DAOs
    private ProyectoDAO proyectoDAO;
    private FormularioDAO formularioDAO;
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
