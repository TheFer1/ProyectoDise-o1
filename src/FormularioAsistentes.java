/**
 * Clase FormularioAsistentes
 * Representa un formulario específico para solicitar asistentes de investigación
 */
public class FormularioAsistentes extends Formulario {
    
    // Constructor vacío
    public FormularioAsistentes() {
        super();
        this.setTipoPersonal("Asistente de Investigación");
    }
    
    // Constructor con parámetros
    public FormularioAsistentes(int numeroDeAyudantes, String nombreDelAyudante, 
                                String apellidoDelAyudante, String cedula, String facultad) {
        super(numeroDeAyudantes, nombreDelAyudante, apellidoDelAyudante, cedula, facultad);
        this.setTipoPersonal("Asistente de Investigación");
    }
    
    // Constructor completo
    public FormularioAsistentes(int id, int numeroDeAyudantes, String nombreDelAyudante, 
                                String apellidoDelAyudante, String cedula, String facultad, String estado) {
        super(id, numeroDeAyudantes, nombreDelAyudante, apellidoDelAyudante, cedula, facultad, estado);
        this.setTipoPersonal("Asistente de Investigación");
    }
    
    /**
     * Verifica si el proyecto cumple con los requisitos para asistentes de investigación
     * @return ResultadoOperacion con el resultado de la verificación
     */
    public ResultadoOperacion verificarProyecto() {
        ProyectoDAO proyectoDAO = new ProyectoDAO();
        Proyecto proyecto = proyectoDAO.obtenerPorId(this.getIdProyecto());
        
        if (proyecto == null) {
            return new ResultadoOperacion(false, "El proyecto no existe");
        }
        
        // Obtener el número actual de asistentes del proyecto
        FormularioDAO formularioDAO = new FormularioDAO();
        int ayudantesActuales = formularioDAO.contarAyudantesPorProyecto(this.getIdProyecto());
        
        // Validar contra el proyecto
        ResultadoOperacion validacion = this.validarContraProyecto(proyecto, ayudantesActuales);
        
        if (!validacion.isExitoso()) {
            return validacion;
        }
        
        return new ResultadoOperacion(true, 
            "Proyecto verificado correctamente para asistentes. " + validacion.getMensaje());
    }
    
    /**
     * Calcula la vigencia del contrato para asistentes (normalmente 2 años)
     * @return String con la vigencia del contrato
     */
    public String calcularVigenciaContrato() {
        return "2 Años";
    }
    
    /**
     * Notifica al director sobre el nuevo asistente registrado
     * @return boolean indicando si se envió la notificación
     */
    public boolean notificar() {
        ProyectoDAO proyectoDAO = new ProyectoDAO();
        Proyecto proyecto = proyectoDAO.obtenerPorId(this.getIdProyecto());
        
        if (proyecto == null) {
            return false;
        }
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario director = usuarioDAO.obtenerPorId(proyecto.getIdUsuario());
        
        if (director == null) {
            return false;
        }
        
        String mensaje = String.format(
            "Se ha registrado un nuevo asistente de investigación para su proyecto '%s':\n" +
            "Nombre: %s %s\n" +
            "Cédula: %s\n" +
            "Facultad: %s\n" +
            "Estado: %s\n" +
            "Vigencia del contrato: %s\n\n" +
            "Este es un rol de alto nivel que requiere seguimiento cercano.",
            proyecto.getNombre(),
            this.getNombreDelAyudante(),
            this.getApellidoDelAyudante(),
            this.getCedula(),
            this.getFacultad(),
            this.getEstado(),
            calcularVigenciaContrato()
        );
        
        return Notificacion.enviarNotificacionAUsuario(
            director, 
            "Nuevo Asistente de Investigación Registrado", 
            mensaje
        );
    }
    
    @Override
    public String toString() {
        return "FormularioAsistentes{" +
                "id=" + getId() +
                ", nombreCompleto='" + obtenerNombreCompleto() + '\'' +
                ", cedula='" + getCedula() + '\'' +
                ", facultad='" + getFacultad() + '\'' +
                ", estado='" + getEstado() + '\'' +
                ", tipoPersonal='" + getTipoPersonal() + '\'' +
                ", vigenciaContrato='" + calcularVigenciaContrato() + '\'' +
                '}';
    }
}
