/**
 * Clase FormularioAyudantes
 * Representa un formulario específico para solicitar ayudantes de investigación
 */
public class FormularioAyudantes extends Formulario {
    
    // Constructor vacío
    public FormularioAyudantes() {
        super();
        this.setTipoPersonal("Ayudante");
    }
    
    // Constructor con parámetros
    public FormularioAyudantes(int numeroDeAyudantes, String nombreDelAyudante, 
                               String apellidoDelAyudante, String cedula, String facultad) {
        super(numeroDeAyudantes, nombreDelAyudante, apellidoDelAyudante, cedula, facultad);
        this.setTipoPersonal("Ayudante");
    }
    
    // Constructor completo
    public FormularioAyudantes(int id, int numeroDeAyudantes, String nombreDelAyudante, 
                               String apellidoDelAyudante, String cedula, String facultad, String estado) {
        super(id, numeroDeAyudantes, nombreDelAyudante, apellidoDelAyudante, cedula, facultad, estado);
        this.setTipoPersonal("Ayudante");
    }
    
    /**
     * Verifica si el proyecto cumple con los requisitos para ayudantes
     * @return ResultadoOperacion con el resultado de la verificación
     */
    public ResultadoOperacion verificarProyecto() {
        ProyectoDAO proyectoDAO = new ProyectoDAO();
        Proyecto proyecto = proyectoDAO.obtenerPorId(this.getIdProyecto());
        
        if (proyecto == null) {
            return new ResultadoOperacion(false, "El proyecto no existe");
        }
        
        // Obtener el número actual de ayudantes del proyecto
        FormularioDAO formularioDAO = new FormularioDAO();
        int ayudantesActuales = formularioDAO.contarAyudantesPorProyecto(this.getIdProyecto());
        
        // Validar contra el proyecto
        ResultadoOperacion validacion = this.validarContraProyecto(proyecto, ayudantesActuales);
        
        if (!validacion.isExitoso()) {
            return validacion;
        }
        
        return new ResultadoOperacion(true, 
            "Proyecto verificado correctamente para ayudantes. " + validacion.getMensaje());
    }
    
    /**
     * Calcula la vigencia del contrato para ayudantes (normalmente 1 semestre)
     * @return String con la vigencia del contrato
     */
    public String calcularVigenciaContrato() {
        return "1 Semestre";
    }
    
    /**
     * Notifica al director sobre el nuevo ayudante registrado
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
            "Se ha registrado un nuevo ayudante para su proyecto '%s':\n" +
            "Nombre: %s %s\n" +
            "Cédula: %s\n" +
            "Facultad: %s\n" +
            "Estado: %s",
            proyecto.getNombre(),
            this.getNombreDelAyudante(),
            this.getApellidoDelAyudante(),
            this.getCedula(),
            this.getFacultad(),
            this.getEstado()
        );
        
        return Notificacion.enviarNotificacionAUsuario(
            director, 
            "Nuevo Ayudante Registrado", 
            mensaje
        );
    }
    
    @Override
    public String toString() {
        return "FormularioAyudantes{" +
                "id=" + getId() +
                ", nombreCompleto='" + obtenerNombreCompleto() + '\'' +
                ", cedula='" + getCedula() + '\'' +
                ", facultad='" + getFacultad() + '\'' +
                ", estado='" + getEstado() + '\'' +
                ", tipoPersonal='" + getTipoPersonal() + '\'' +
                '}';
    }
}
