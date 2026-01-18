import javax.swing.SwingUtilities;

public class App {
           public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWindow login = new LoginWindow();
            login.setVisible(true);
        });
    } 
}
