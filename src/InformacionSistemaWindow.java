import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * Ventana de información y orientación del sistema - VERSIÓN MEJORADA
 * Se muestra después de iniciar sesión para explicar el funcionamiento
 */
public class InformacionSistemaWindow extends JFrame {
    private Usuario usuarioActual;
    private String rolActual;
    
    // Colores personalizados
    private static final Color COLOR_PRINCIPAL = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    private static final Color COLOR_ADVERTENCIA = new Color(230, 126, 34);
    private static final Color COLOR_EXITO = new Color(46, 204, 113);
    private static final Color COLOR_INFO = new Color(52, 73, 94);
    private static final Color COLOR_FONDO = new Color(236, 240, 241);
    
    public InformacionSistemaWindow(Usuario usuarioActual, String rolActual) {
        this.usuarioActual = usuarioActual;
        this.rolActual = rolActual;
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        setTitle("Información del Sistema - Bienvenida");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_FONDO);
        
        // Panel superior - Header mejorado
        mainPanel.add(crearHeaderMejorado(), BorderLayout.NORTH);
        
        // Panel central con scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(COLOR_FONDO);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        // Secciones mejoradas
        contentPanel.add(crearSeccionAdvertenciaMejorada());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        contentPanel.add(crearSeccionPasosMejorada());
        
        // ScrollPane con estilo
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(COLOR_FONDO);
        
        // Panel inferior - Botones mejorado
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));
        
        JButton btnEntender = crearBotonPrincipal("✓ Entendido, Continuar");
        btnEntender.addActionListener(e -> continuar());
        
        JButton btnMasInfo = crearBotonSecundario("? Más Información");
        btnMasInfo.addActionListener(e -> mostrarMasInfo());
        
        bottomPanel.add(btnEntender);
        bottomPanel.add(btnMasInfo);
        
        // Ensamblar
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel crearHeaderMejorado() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(COLOR_PRINCIPAL);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        headerPanel.setPreferredSize(new Dimension(0, 110));
        
        // Lado izquierdo - Texto
        JPanel headerTextPanel = new JPanel();
        headerTextPanel.setBackground(COLOR_PRINCIPAL);
        headerTextPanel.setLayout(new BoxLayout(headerTextPanel, BoxLayout.Y_AXIS));
        
        JLabel lblBienvenida = new JLabel("🎓 ¡Bienvenido al Sistema de Gestión de Ayudantes!");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBienvenida.setForeground(Color.WHITE);
        
        JLabel lblUsuario = new JLabel("👤 " + usuarioActual.getNombre() + " " + usuarioActual.getApellido() + 
                                      " | " + (rolActual != null ? rolActual : "Usuario"));
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setForeground(new Color(230, 240, 255));
        
        JLabel lblSubtitulo = new JLabel("Información importante sobre el uso del sistema");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubtitulo.setForeground(new Color(180, 220, 255));
        
        headerTextPanel.add(lblBienvenida);
        headerTextPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerTextPanel.add(lblUsuario);
        headerTextPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        headerTextPanel.add(lblSubtitulo);
        
        headerPanel.add(headerTextPanel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel crearSeccionAdvertenciaMejorada() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(255, 245, 230));
        panel.setBorder(new CompoundBorder(
            new LineBorder(COLOR_ADVERTENCIA, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        // Título con icono
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(255, 245, 230));
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        JLabel lblIcono = new JLabel("⚠️");
        lblIcono.setFont(new Font("Arial", Font.PLAIN, 24));
        
        JLabel lblTitulo = new JLabel("ADVERTENCIA IMPORTANTE");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(COLOR_ADVERTENCIA);
        
        titlePanel.add(lblIcono);
        titlePanel.add(lblTitulo);
        panel.add(titlePanel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Contenido
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textArea.setBackground(new Color(255, 245, 230));
        textArea.setBorder(BorderFactory.createEmptyBorder());
        textArea.setForeground(COLOR_INFO);
        
        textArea.setText(
            "Si NO registra a sus ayudantes o integrantes del proyecto en este sistema, " +
            "NO PODRÁ subir su avance semestral.\n\n" +
            
            "Sin el registro de sus ayudantes, Jefatura NO le firmará los documentos " +
            "de avance del proyecto.\n\n" +
            
            "Por lo tanto, es OBLIGATORIO registrar a todos sus ayudantes e integrantes " +
            "ANTES de presentar su avance semestral."
        );
        
        panel.add(textArea);
        return panel;
    }
    
    private JPanel crearSeccionPasosMejorada() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(230, 245, 255));
        panel.setBorder(new CompoundBorder(
            new LineBorder(COLOR_SECUNDARIO, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Título con icono
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(230, 245, 255));
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        
        JLabel lblIcono = new JLabel("📝");
        lblIcono.setFont(new Font("Arial", Font.PLAIN, 24));
        
        JLabel lblTitulo = new JLabel("Pasos para Registrar Participantes  en el Sistema");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(COLOR_SECUNDARIO);
        
        titlePanel.add(lblIcono);
        titlePanel.add(lblTitulo);
        panel.add(titlePanel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Pasos numerados
        JPanel stepsPanel = new JPanel();
        stepsPanel.setLayout(new BoxLayout(stepsPanel, BoxLayout.Y_AXIS));
        stepsPanel.setBackground(new Color(230, 245, 255));
        
        String[][] pasos = {
            {"1", "Inicie sesión con sus credenciales de director"},
            {"2", "Haga clic en la pestaña \"Mis Proyectos\""},
            {"3", "Seleccione el proyecto en el que desea registrar Participantes"},
            {"4", "Haga clic en el botón \"Registrar Nuevo Participante\""},
            {"5", "Complete el formulario con los datos del participante"},
            {"6", "Haga clic en \"Guardar y Registrar\""},
            {"7", "El sistema le mostrará un mensaje de confirmación"}
        };
        
        for (String[] paso : pasos) {
            JPanel pasoPanel = crearPasoPanel(paso[0], paso[1]);
            stepsPanel.add(pasoPanel);
            stepsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        
        panel.add(stepsPanel);
        
        // Nota importante
        JPanel notaPanel = new JPanel();
        notaPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        notaPanel.setBackground(new Color(255, 250, 200));
        notaPanel.setBorder(new LineBorder(new Color(255, 200, 0), 1));
        
        JLabel lblNota = new JLabel("⚠️ Asegúrese de registrar todos sus ayudantes ANTES de la fecha límite");
        lblNota.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblNota.setForeground(COLOR_INFO);
        notaPanel.add(lblNota);
        
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(notaPanel);
        
        return panel;
    }
    
    private JPanel crearPasoPanel(String numero, String descripcion) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 8));
        panel.setBackground(new Color(230, 245, 255));
        
        // Número circular
        JLabel lblNumero = new JLabel(numero);
        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNumero.setForeground(Color.WHITE);
        lblNumero.setBackground(COLOR_SECUNDARIO);
        lblNumero.setOpaque(true);
        lblNumero.setHorizontalAlignment(JLabel.CENTER);
        lblNumero.setVerticalAlignment(JLabel.CENTER);
        lblNumero.setPreferredSize(new Dimension(35, 35));
        lblNumero.setBorder(BorderFactory.createEmptyBorder());
        
        // Descripción
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(COLOR_INFO);
        
        panel.add(lblNumero);
        panel.add(lblDesc);
        
        return panel;
    }
    
    private JButton crearBotonPrincipal(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setBackground(COLOR_EXITO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_EXITO);
            }
        });
        
        return btn;
    }
    
    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setBackground(COLOR_PRINCIPAL);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(30, 100, 150));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_PRINCIPAL);
            }
        });
        
        return btn;
    }
    
    private void continuar() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            SistemaGestionWindow sistema = new SistemaGestionWindow(usuarioActual, rolActual);
            sistema.setVisible(true);
        });
    }
    
    private void mostrarMasInfo() {
        String info = "INFORMACIÓN DETALLADA\n\n" +
                     "🎓 OBJETIVOS DEL SISTEMA:\n" +
                     "• Centralizar la gestión de ayudantes académicos\n" +
                     "• Facilitar la validación y firma de documentos\n" +
                     "• Mantener registro oficial de todo el equipo de trabajo\n\n" +
                     
                     "📋 CAMPOS DEL FORMULARIO:\n" +
                     "• Nombre completo del ayudante\n" +
                     "• Apellido completo\n" +
                     "• Número de cédula de identidad\n" +
                     "• Facultad de procedencia\n" +
                     "• Tipo de rol (Ayudante, Técnico, Investigador, etc.)\n\n" +
                     
                     "⏰ FECHAS IMPORTANTES:\n" +
                     "• Registro debe completarse antes del avance semestral\n" +
                     "• La firma de Jefatura se realiza una vez registrados todos los ayudantes\n\n" +
                     
                     "📧 ¿NECESITA AYUDA?\n" +
                     "Contacte directamente a la Jefatura de su departamento.\n";
        
        JOptionPane.showMessageDialog(this, info, "Información Detallada", 
                                     JOptionPane.INFORMATION_MESSAGE);
    }
}
