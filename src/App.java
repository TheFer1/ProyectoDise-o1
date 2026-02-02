import javax.swing.SwingUtilities;

public class App {
    
    public static void main(String[] args) {
        // Iniciar notificador automático
       // Notificacion.iniciarNotificadorAutomatico();
        
        // Iniciar interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            LoginWindow login = new LoginWindow();
            login.setVisible(true);
        });
        
        // Hook para detener el notificador al cerrar la aplicación
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Notificacion.detenerNotificadorAutomatico();
        }));
    }
}



