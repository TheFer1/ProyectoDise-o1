import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginWindow  extends JFrame{
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioActual;
    private String rolActual;
    private JTextField txtCorreo ; 
    private JPasswordField txtPassword;

    public LoginWindow() {
        usuarioDAO = new UsuarioDAO();
        inicializarComponentes();
    }
  private void inicializarComponentes() {
        setTitle("Sistema de Gestión De Solicitudes - Login");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel principal con fondo
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        
        // Panel superior con título
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(41, 128, 185)); // Azul
        JLabel titleLabel = new JLabel("Sistema de Gestión De Solicitudes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Correo
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblCorreo, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        txtCorreo = new JTextField(20);
        txtCorreo.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(txtCorreo, gbc);
        
        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 12));
        formPanel.add(lblPassword, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        formPanel.add(txtPassword, gbc);
        
        // Mensaje de estado
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel lblEstado = new JLabel(" ");
        lblEstado.setForeground(Color.RED);
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 11));
        formPanel.add(lblEstado, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 12));
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(150, 40));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 12));
        btnRegistrar.setBackground(new Color(46, 204, 113));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setPreferredSize(new Dimension(150, 40));
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnDemo = new JButton("Ver Demo");
        btnDemo.setFont(new Font("Arial", Font.PLAIN, 11));
        btnDemo.setPreferredSize(new Dimension(100, 35));
        btnDemo.setFocusPainted(false);
        btnDemo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegistrar);
        buttonPanel.add(btnDemo);
        
        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(236, 240, 241));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextArea infoText = new JTextArea(3, 35);
        infoText.setEditable(false);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoText.setFont(new Font("Monospaced", Font.PLAIN, 10));
        infoText.setBackground(new Color(236, 240, 241));
        infoText.setText("Usuarios de prueba:\n" +
                        "Director: carlos@uni.edu / dir123\n" +
                        "Jefatura: maria@uni.edu / jef123\n" +
                        "Solo Jefatura crea usuarios");
        infoPanel.add(infoText);
        
        // Acciones
        btnLogin.addActionListener(e -> {
            String correo = txtCorreo.getText().trim();
            String password = new String(txtPassword.getPassword());
            
            if (correo.isEmpty() || password.isEmpty()) {
                lblEstado.setText("⚠ Ingrese correo y contraseña");
                return;
            }
            
            if (autenticar(correo, password)) {
                // Validar que solo Director y Jefatura pueden acceder
                if (!"Director".equals(rolActual) && !"Jefatura".equals(rolActual)) {
                    lblEstado.setText("✗ Acceso restringido a Director y Jefatura únicamente");
                    lblEstado.setForeground(Color.RED);
                    txtPassword.setText("");
                    return;
                }
                
                lblEstado.setText("✓ Autenticación exitosa");
                lblEstado.setForeground(new Color(46, 204, 113));
                
                // Abrir sistema con rol validado
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    SistemaGestionWindow sistema = new SistemaGestionWindow(usuarioActual, rolActual);
                    sistema.setVisible(true);
                });
            } else {
                lblEstado.setText("✗ Correo o contraseña inválidos");
                lblEstado.setForeground(Color.RED);
                txtPassword.setText("");
            }
        });
        
        btnRegistrar.addActionListener(e -> mostrarFormularioRegistro());
        
        btnDemo.addActionListener(e -> {
            // Cargar usuario demo (Director)
            List<Usuario> usuarios = usuarioDAO.obtenerTodos();
            if (!usuarios.isEmpty()) {
                usuarioActual = usuarios.get(0);
                rolActual = "Director";
                
                dispose();
                SistemaGestionWindow sistema = new SistemaGestionWindow(usuarioActual, rolActual);
                sistema.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No hay usuarios en la base de datos.\n" +
                    "Cree un usuario primero.",
                    "Sin datos",
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Ensamblar
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Panel con botones
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(Color.WHITE);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private boolean autenticar(String correo, String password) {
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equals(correo) && usuario.getPassword().equals(password)) {
                usuarioActual = usuario;
                
                // Determinar el rol del usuario
                if (usuario instanceof Director) {
                    rolActual = "Director";
                } else if (usuario instanceof Jefatura) {
                    rolActual = "Jefatura";
                } else {
                    rolActual = "Usuario";
                }
                
                return true;
            }
        }
        
        return false;
    }
      private void mostrarFormularioRegistro() {
        JDialog dialog = new JDialog(this, "Registrar Director", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);
        
        // Título
        JLabel lblTitulo = new JLabel("Registro de Director");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitulo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel lblSubtitulo = new JLabel("Complete los datos para registrarse");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSubtitulo.setForeground(Color.GRAY);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblSubtitulo);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Campos
        JTextField txtNombre = new JTextField(20);
        JTextField txtApellido = new JTextField(20);
        JTextField txtCorreoReg = new JTextField(20);
        JPasswordField txtPasswordReg = new JPasswordField(20);
        JPasswordField txtPasswordConfirm = new JPasswordField(20);
        
        mainPanel.add(crearCampo("Nombre:", txtNombre));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(crearCampo("Apellido:", txtApellido));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(crearCampo("Correo:", txtCorreoReg));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(crearCampo("Contraseña:", txtPasswordReg));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(crearCampo("Confirmar:", txtPasswordConfirm));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Panel de botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        
        JButton btnRegistrarDialog = new JButton("Registrar");
        btnRegistrarDialog.setBackground(new Color(46, 204, 113));
        btnRegistrarDialog.setForeground(Color.WHITE);
        btnRegistrarDialog.setFont(new Font("Arial", Font.BOLD, 12));
        btnRegistrarDialog.setPreferredSize(new Dimension(120, 35));
        btnRegistrarDialog.setFocusPainted(false);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setFocusPainted(false);
        
        btnRegistrarDialog.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String correo = txtCorreoReg.getText().trim();
            String password = new String(txtPasswordReg.getPassword());
            String passwordConfirm = new String(txtPasswordConfirm.getPassword());
            
            // Validaciones
            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Complete todos los campos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(passwordConfirm)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Las contraseñas no coinciden", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.length() < 4) {
                JOptionPane.showMessageDialog(dialog, 
                    "La contraseña debe tener al menos 4 caracteres", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Crear Director automáticamente
            Director nuevoDirector = new Director(0, nombre, apellido, correo, password);
            
            try {
                usuarioDAO.insertar(nuevoDirector, "Director");
                
                JOptionPane.showMessageDialog(dialog,
                    "✓ Director registrado exitosamente\n" +
                    "Ya puede iniciar sesión",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                
                // Prellenar el correo en el login
                txtCorreo.setText(correo);
                txtPassword.setText("");
                txtPassword.requestFocus();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error al registrar: " + ex.getMessage() +
                    "\nVerifique que el correo no esté en uso",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(btnRegistrarDialog);
        btnPanel.add(btnCancelar);
        
        mainPanel.add(btnPanel);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Crea un panel con etiqueta y campo de texto
     */
    private JPanel crearCampo(String etiqueta, JTextField campo) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(400, 25));
        
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setPreferredSize(new Dimension(90, 25));
        
        panel.add(lbl, BorderLayout.WEST);
        panel.add(campo, BorderLayout.CENTER);
        
        return panel;
    }


}


    
