import java.util.Date;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

/**
 * Clase Notificacion para gestionar notificaciones del sistema
 * Encapsula el comportamiento de las notificaciones y envío de correos
 */
public class Notificacion {
    private int id;
    private Date fecha;
    private String informacion;
    private int idUsuario;
    private boolean leida; // Para marcar si fue leída
    
    // Configuración del servidor SMTP
    private static final String SMTP_HOST = "smtp.gmail.com"; // Cambiar según tu proveedor
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "estalyn2casa@gmail.com"; // Cambiar por tu email
    private static final String EMAIL_PASSWORD = "jzganmvechyphaqe"; // Cambiar por tu contraseña de aplicación
    
    // Constructores
    public Notificacion() {
    }
    
    public Notificacion(String informacion, int idUsuario) {
        this.informacion = informacion;
        this.idUsuario = idUsuario;
        this.fecha = new Date();
        this.leida = false;
    }
    
    public Notificacion(int id, Date fecha, String informacion, int idUsuario) {
        this.id = id;
        this.fecha = fecha;
        this.informacion = informacion;
        this.idUsuario = idUsuario;
        this.leida = false;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public String getInformacion() {
        return informacion;
    }
    
    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public boolean isLeida() {
        return leida;
    }
    
    public void setLeida(boolean leida) {
        this.leida = leida;
    }
    
    // ==================== MÉTODOS DE LÓGICA ====================
    
    /**
     * Marca la notificación como leída
     */
    public void marcarComoLeida() {
        this.leida = true;
    }
    
    /**
     * Marca la notificación como no leída
     */
    public void marcarComoNoLeida() {
        this.leida = false;
    }
    
    /**
     * Obtiene el estado de lectura en texto
     */
    public String obtenerEstadoLectura() {
        return leida ? "Leída" : "No leída";
    }
    
    /**
     * Valida si la notificación es válida
     */
    public boolean esValida() {
        return informacion != null && !informacion.trim().isEmpty() &&
               idUsuario > 0 && fecha != null;
    }
    
    /**
     * Obtiene una vista previa de la notificación
     */
    public String obtenerVista() {
        return String.format("[%s] %s - %s", 
            obtenerEstadoLectura(), 
            fecha, 
            informacion.substring(0, Math.min(50, informacion.length())));
    }
    
    // ==================== MÉTODOS DE ENVÍO DE CORREO ====================
    
    /**
     * Envía un correo electrónico
     * @param destinatario Correo del destinatario
     * @param asunto Asunto del correo
     * @param mensaje Cuerpo del mensaje
     * @return true si se envió correctamente, false si hubo error
     */
    public static boolean enviarCorreo(String destinatario, String asunto, String mensaje) {
        // Validar que el destinatario no esté vacío
        if (destinatario == null || destinatario.trim().isEmpty()) {
            System.err.println("El correo del destinatario está vacío");
            return false;
        }
        
        try {
            // Configuración SMTP
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            // Crear sesión
            Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
                    }
                }
            );
            
            // Crear mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destinatario)
            );
            message.setSubject(asunto);
            message.setText(mensaje);
            
            // Enviar
            Transport.send(message);
            
            System.out.println("✓ Correo enviado correctamente a: " + destinatario);
            return true;
            
        } catch (NoClassDefFoundError e) {
            System.err.println("⚠ Librería Jakarta Activation no disponible. Notificación guardada en BD pero no se envió por correo.");
            System.err.println("Para enviar correos, instala jakarta.activation-2.0.jar completo en la carpeta lib/");
            return false;
        } catch (Exception e) {
            System.err.println("⚠ Error al enviar correo: " + e.getMessage());
            System.err.println("Notificación guardada en BD pero no se pudo enviar por correo.");
            return false;
        }
    }
    
    /**
     * Envía una notificación por correo a un usuario
     * @param usuario Usuario al que se enviará el correo
     * @param asunto Asunto del correo
     * @param mensaje Cuerpo del mensaje
     * @return true si se envió correctamente
     */
    public static boolean enviarNotificacionAUsuario(Usuario usuario, String asunto, String mensaje) {
        if (usuario == null || usuario.getCorreo() == null) {
            System.err.println("Usuario o correo inválido");
            return false;
        }
        
        String mensajeCompleto = String.format(
            "Hola %s %s,\n\n%s\n\n" +
            "---\n" +
            "Este es un mensaje automático del Sistema de Gestión Académica.\n" +
            "Por favor no responder a este correo.",
            usuario.getNombre(),
            usuario.getApellido(),
            mensaje
        );
        
        return enviarCorreo(usuario.getCorreo(), asunto, mensajeCompleto);
    }
    
    @Override
    public String toString() {
        return "Notificacion{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", informacion='" + informacion + '\'' +
                ", idUsuario=" + idUsuario +
                ", leida=" + leida +
                '}';
    }
}
