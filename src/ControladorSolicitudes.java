import java.util.List;

/**
 * Controlador para gestionar operaciones de solicitudes del Director
 * Separa la lógica de negocio de la interfaz gráfica
 * Aplica el principio de Single Responsibility
 */
public class ControladorSolicitudes {
    private Director director;
    private FormularioDAO formularioDAO;
    private SolicitudDAO solicitudDAO;
    private ProyectoDAO proyectoDAO;
    
    /**
     * Constructor que recibe el director actual
     */
    public ControladorSolicitudes(Director director) {
        this.director = director;
        this.formularioDAO = new FormularioDAO();
        this.solicitudDAO = new SolicitudDAO();
        this.proyectoDAO = new ProyectoDAO();
    }
    
    /**
     * Valida y envía una solicitud
     * @param asunto Asunto de la solicitud
     * @param descripcion Descripción de la solicitud
     * @return Resultado de la operación con mensaje apropiado
     */
    public ResultadoOperacion enviarSolicitud(String asunto, String descripcion) {
        // Validar campos vacíos
        if (asunto == null || asunto.trim().isEmpty()) {
            return ResultadoOperacion.error("El asunto de la solicitud no puede estar vacío");
        }
        
        // Crear la solicitud
        Solicitud solicitud = new Solicitud(asunto, director.getId());
        
        // Intentar enviar usando la lógica del Director
        boolean enviado = director.enviarSolicitud(solicitud);
        
        if (enviado) {
            return ResultadoOperacion.exito("✓ Solicitud enviada exitosamente a Jefatura");
        } else {
            return ResultadoOperacion.advertencia(
                "⚠ No puede enviar la solicitud.\n\n" +
                "Debe completar al menos un formulario de ayudantes en uno de sus proyectos.\n" +
                "Por favor, llene el formulario en el Paso 1."
            );
        }
    }
    
    /**
     * Registra un formulario de ayudantes
     * @param idProyecto ID del proyecto
     * @param numAyudantes Número de ayudantes
     * @param nombre Nombre del ayudante
     * @param apellido Apellido del ayudante
     * @param cedula Cédula del ayudante
     * @param facultad Facultad del ayudante
     * @return Resultado de la operación
     */
    public ResultadoOperacion registrarFormulario(int idProyecto, int numAyudantes, 
                                                  String nombre, String apellido, 
                                                  String cedula, String facultad) {
        // Validar campos
        if (nombre == null || nombre.trim().isEmpty()) {
            return ResultadoOperacion.error("El nombre del ayudante es requerido");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            return ResultadoOperacion.error("El apellido del ayudante es requerido");
        }
        if (cedula == null || cedula.trim().isEmpty()) {
            return ResultadoOperacion.error("La cédula del ayudante es requerida");
        }
        if (facultad == null || facultad.trim().isEmpty()) {
            return ResultadoOperacion.error("La facultad del ayudante es requerida");
        }
        
        // Validar que el proyecto pertenezca al director
        Proyecto proyecto = proyectoDAO.obtenerPorId(idProyecto);
        if (proyecto == null) {
            return ResultadoOperacion.error("El proyecto seleccionado no existe");
        }
        if (proyecto.getIdDirector() != director.getId()) {
            return ResultadoOperacion.error("No tiene permiso para agregar formularios a este proyecto");
        }
        
        // Crear y registrar el formulario
        Formulario formulario = new Formulario(numAyudantes, nombre, apellido, cedula, facultad);
        formulario.setIdProyecto(idProyecto);
        
        boolean registrado = director.registrarFormulario(formulario);
        
        if (registrado) {
            return ResultadoOperacion.exito("✓ Formulario guardado exitosamente. Ahora puede enviar su solicitud.");
        } else {
            return ResultadoOperacion.error("Error al guardar el formulario. Intente nuevamente.");
        }
    }
    
    /**
     * Obtiene las solicitudes del director
     * @return Lista de solicitudes
     */
    public List<Solicitud> obtenerSolicitudes() {
        return director.consultarMisSolicitudes();
    }
    
    /**
     * Obtiene los proyectos del director
     * @return Lista de proyectos
     */
    public List<Proyecto> obtenerProyectos() {
        return director.consultarMisProyectos();
    }
    
    /**
     * Verifica si el director tiene proyectos
     * @return true si tiene al menos un proyecto
     */
    public boolean tieneProyectos() {
        List<Proyecto> proyectos = obtenerProyectos();
        return proyectos != null && !proyectos.isEmpty();
    }
    
    /**
     * Verifica si el director tiene formularios en sus proyectos
     * @return true si tiene al menos un formulario
     */
    public boolean tieneFormularios() {
        List<Proyecto> proyectos = obtenerProyectos();
        if (proyectos == null || proyectos.isEmpty()) {
            return false;
        }
        
        for (Proyecto proyecto : proyectos) {
            List<Formulario> formulariosProyecto = formularioDAO.buscarPorProyecto(proyecto.getId());
            if (formulariosProyecto != null && !formulariosProyecto.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Obtiene información del estado actual del director
     * @return Mensaje informativo sobre el estado
     */
    public String obtenerEstadoInformativo() {
        StringBuilder info = new StringBuilder();
        
        List<Proyecto> proyectos = obtenerProyectos();
        int cantProyectos = proyectos != null ? proyectos.size() : 0;
        
        List<Solicitud> solicitudes = obtenerSolicitudes();
        int cantSolicitudes = solicitudes != null ? solicitudes.size() : 0;
        
        boolean tieneForm = tieneFormularios();
        
        info.append("Estado: ");
        info.append(cantProyectos).append(" proyecto(s), ");
        info.append(cantSolicitudes).append(" solicitud(es) enviada(s)");
        
        if (!tieneForm) {
            info.append("\n⚠ Sin formularios de ayudantes");
        }
        
        return info.toString();
    }
}
