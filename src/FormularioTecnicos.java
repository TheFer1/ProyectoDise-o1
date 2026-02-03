/**
 * Clase FormularioTecnicos
 * Representa un formulario específico para solicitar técnicos en investigación
 */
public class FormularioTecnicos extends Formulario {
    
    // Constructor vacío
    public FormularioTecnicos() {
        super();
        this.setTipoPersonal("Técnico en Investigación");
    }
    
    // Constructor con parámetros
    public FormularioTecnicos(int numeroDeAyudantes, String nombreDelAyudante, 
                              String apellidoDelAyudante, String cedula, String facultad) {
        super(numeroDeAyudantes, nombreDelAyudante, apellidoDelAyudante, cedula, facultad);
        this.setTipoPersonal("Técnico en Investigación");
    }
    
    // Constructor completo
    public FormularioTecnicos(int id, int numeroDeAyudantes, String nombreDelAyudante, 
                              String apellidoDelAyudante, String cedula, String facultad, String estado) {
        super(id, numeroDeAyudantes, nombreDelAyudante, apellidoDelAyudante, cedula, facultad, estado);
        this.setTipoPersonal("Técnico en Investigación");
    }
    
    /**
     * Verifica si el proyecto cumple con los requisitos para técnicos
     * @return ResultadoOperacion con el resultado de la verificación
     */
    public ResultadoOperacion verificarProyecto() {
        ProyectoDAO proyectoDAO = new ProyectoDAO();
        Proyecto proyecto = proyectoDAO.obtenerPorId(this.getIdProyecto());
        
        if (proyecto == null) {
            return new ResultadoOperacion(false, "El proyecto no existe");
        }
        
        // Obtener el número actual de técnicos del proyecto
        FormularioDAO formularioDAO = new FormularioDAO();
        int ayudantesActuales = formularioDAO.contarAyudantesPorProyecto(this.getIdProyecto());
        
        // Validar contra el proyecto
        ResultadoOperacion validacion = this.validarContraProyecto(proyecto, ayudantesActuales);
        
        if (!validacion.isExitoso()) {
            return validacion;
        }
        
        return new ResultadoOperacion(true, 
            "Proyecto verificado correctamente para técnicos. " + validacion.getMensaje());
    }
    
    /**
     * Calcula la vigencia del contrato para técnicos (normalmente 1 año)
     * @return String con la vigencia del contrato
     */
    public String calcularVigenciaContrato() {
        return "1 Año";
    }
    
    /**
     * Notifica al director sobre el nuevo técnico registrado
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
            "Se ha registrado un nuevo técnico en investigación para su proyecto '%s':\n" +
            "Nombre: %s %s\n" +
            "Cédula: %s\n" +
            "Facultad: %s\n" +
            "Estado: %s\n" +
            "Vigencia del contrato: %s",
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
            "Nuevo Técnico en Investigación Registrado", 
            mensaje
        );
    }
    
    @Override
    public String toString() {
        return "FormularioTecnicos{" +
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
