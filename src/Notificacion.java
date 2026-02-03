import java.util.Date;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587"; // Puerto 587 con STARTTLS
    private static final String EMAIL_FROM = "estalyn2casa@gmail.com";
    private static final String EMAIL_PASSWORD = "zdclehrpxuvmjjte";
    // Notificador automático
    private static Timer timerNotificador;
    private static long INTERVALO_MILISEGUNDOS = 2 * 60 * 1000; // 2 minutos
    private static final String ASUNTO_AUTOMATICO = "Recordatorio: Registrar Ayudantes";
    private static final String MENSAJE_AUTOMATICO = "El Sistema de Gestión de Ayudantes le recuerda que debe registrar sus ayudantes, " +
                                                      "para que al final del semestre su firma en el avance del proyecto sea válida por Jefatura.";
    
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
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.connectiontimeout", "20000");
            props.put("mail.smtp.timeout", "20000");
            props.put("mail.smtp.writetimeout", "20000");   
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
    
    // ==================== MÉTODOS DE NOTIFICADOR AUTOMÁTICO ====================
    
    /**
     * Inicia el envío periódico de notificaciones a todos los directores
     */
    public static void iniciarNotificadorAutomatico() {
        if (timerNotificador != null) {
            System.out.println("⚠ Notificador automático ya está en ejecución");
            return;
        }
        
        timerNotificador = new Timer("NotificadorAutomatico", true);
        System.out.println("✓ Notificador automático iniciado. Se enviarán notificaciones cada 2 minutos.");
        
        timerNotificador.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                enviarNotificacionesADirectores();
            }
        }, 0, INTERVALO_MILISEGUNDOS);
    }
    
    /**
     * Detiene el envío periódico de notificaciones
     */
    public static void detenerNotificadorAutomatico() {
        if (timerNotificador != null) {
            timerNotificador.cancel();
            timerNotificador = null;
            System.out.println("✓ Notificador automático detenido.");
        }
    }
    
    /**
     * Cambia el intervalo de envío de notificaciones
     * @param minutos Minutos entre cada envío
     */
    public static void cambiarIntervaloNotificador(long minutos) {
        detenerNotificadorAutomatico();
        INTERVALO_MILISEGUNDOS = minutos * 60 * 1000;
        iniciarNotificadorAutomatico();
        System.out.println("✓ Intervalo actualizado a " + minutos + " minuto(s).");
    }
    
    /**
     * Envía notificaciones a todos los directores
     */
    private static void enviarNotificacionesADirectores() {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            NotificacionDAO notificacionDAO = new NotificacionDAO();
            
            List<Usuario> directores = usuarioDAO.obtenerDirectores();
            
            if (directores == null || directores.isEmpty()) {
                System.out.println("[" + new Date() + "] No hay directores registrados.");
                return;
            }
            
            System.out.println("[" + new Date() + "] Enviando notificaciones a " + directores.size() + " director(es)...");
            
            for (Usuario usuario : directores) {
                try {
                    // Crear y guardar notificación en BD
                    Notificacion notificacion = new Notificacion(MENSAJE_AUTOMATICO, usuario.getId());
                    
                    // Guardar en base de datos
                    if (notificacionDAO.insertar(notificacion)) {
                        System.out.println("   ✓ Notificación guardada para: " + usuario.getNombre() + " " + usuario.getApellido());
                    } else {
                        System.out.println("   ✗ Error al guardar notificación para: " + usuario.getNombre());
                    }
                    
                    // Intentar enviar por correo
                    if (!usuario.getCorreo().trim().isEmpty()) {
                        boolean enviado = enviarNotificacionAUsuario(usuario, ASUNTO_AUTOMATICO, MENSAJE_AUTOMATICO);
                        if (enviado) {
                            System.out.println("   ✓ Correo enviado a: " + usuario.getCorreo());
                        } else {
                            System.out.println("   ⚠ No se pudo enviar correo a: " + usuario.getCorreo());
                        }
                    }
                    
                } catch (Exception e) {
                    System.err.println("   ✗ Error procesando director " + usuario.getNombre() + ": " + e.getMessage());
                }
            }
            
            System.out.println("[" + new Date() + "] Ciclo de notificaciones completado.");
            
        } catch (Exception e) {
            System.err.println("[" + new Date() + "] Error en el ciclo de notificaciones: " + e.getMessage());
            e.printStackTrace();
        }
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
