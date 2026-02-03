import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Ventana principal del Sistema de Gestión Académica
 * Con control de acceso basado en roles (RBAC)
 */
public class SistemaGestionWindow extends JFrame {
    private JTabbedPane tabbedPane;
    
    // Usuario autenticado
    private Usuario usuarioActual;
    private String rolActual; // "Director", "Jefatura", "Usuario"
    
    // DAOs
    private UsuarioDAO usuarioDAO;
    private ProyectoDAO proyectoDAO;
    private FormularioDAO formularioDAO;
    private NotificacionDAO notificacionDAO;
    private AvanceDAO avanceDAO;
    
    // Constructor original para compatibilidad
    public SistemaGestionWindow() {
        this(null, "Usuario");
    }
    
    // Constructor con usuario y rol autenticados
    public SistemaGestionWindow(Usuario usuarioActual, String rolActual) {
        this.usuarioActual = usuarioActual;
        this.rolActual = rolActual;
        inicializarDAOs();
        inicializarComponentes();
    }
    
    private void inicializarDAOs() {
        usuarioDAO = new UsuarioDAO();
        proyectoDAO = new ProyectoDAO();
        formularioDAO = new FormularioDAO();
        notificacionDAO = new NotificacionDAO();
        avanceDAO = new AvanceDAO();
    }
    
    private void inicializarComponentes() {
        String titulo = "Sistema de Gestión Académica";
        if (usuarioActual != null) {
            titulo += " - " + usuarioActual.getNombre() + " (" + rolActual + ")";
        }
        
        setTitle(titulo);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Panel superior con información del usuario
        JPanel topPanel = crearPanelUsuarioActual();
        
        // Crear panel con pestañas
        tabbedPane = new JTabbedPane();
        
        // Agregar pestañas según permisos del rol
        // Jefatura ve proyectos de todos los directores
        if ("Jefatura".equals(rolActual)) {
            tabbedPane.addTab("📁 Proyectos y Directores", crearPanelProyectosJefatura());
            tabbedPane.addTab("👥 Usuarios", crearPanelUsuarios());
            tabbedPane.addTab("📋 Avances", crearPanelAvancesJefatura());
        }
        
        // Solo Director puede ver/editar sus proyectos
        if (puedeManejarProyectos()) {
            tabbedPane.addTab("📁 Mis Proyectos", crearPanelProyectos());
            tabbedPane.addTab("� Enviar Avance", crearPanelGuardarAvance());
        }
        
        // Solo Jefatura puede ver formularios
        if (puedeManejarFormularios()) {
            tabbedPane.addTab("📝 Formularios", crearPanelFormularios());
        }
        
        tabbedPane.addTab("🔔 Notificaciones", crearPanelNotificaciones());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    /**
     * Panel con información del usuario autenticado
     */
    private JPanel crearPanelUsuarioActual() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        if (usuarioActual != null) {
            JLabel lblUsuario = new JLabel("👤 " + usuarioActual.getNombre() + " | Rol: " + rolActual);
            lblUsuario.setFont(new Font("Arial", Font.BOLD, 13));
            lblUsuario.setForeground(Color.WHITE);
            panel.add(lblUsuario, BorderLayout.WEST);
            
            JButton btnSalir = new JButton("Cerrar Sesión");
            btnSalir.setFont(new Font("Arial", Font.PLAIN, 11));
            btnSalir.setFocusPainted(false);
            btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnSalir.addActionListener(e -> {
                dispose();
                LoginWindow login = new LoginWindow();
                login.setVisible(true);
            });
            panel.add(btnSalir, BorderLayout.EAST);
        }
        
        return panel;
    }
    
    /**
     * Verifica si el rol actual puede manejar proyectos (Solo Director)
     */
    private boolean puedeManejarProyectos() {
        return "Director".equals(rolActual);
    }
    
    /**
     * Verifica si el rol actual puede manejar formularios (Director y Jefatura)
     */
    private boolean puedeManejarFormularios() {
        return "Jefatura".equals(rolActual) || "Director".equals(rolActual);
    }
    
    /**
     * Verifica si el rol actual puede manejar usuarios (Solo Jefatura)
     */
    private boolean puedeManejarUsuarios() {
        return "Jefatura".equals(rolActual);
    }
    
    // ======================== PANEL USUARIOS ========================
    
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de formulario (solo habilitado para Director)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos de Usuario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtNombre = new JTextField(20);
        JTextField txtApellido = new JTextField(20);
        JTextField txtCorreo = new JTextField(20);
        JPasswordField txtPassword = new JPasswordField(20);
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Director", "Jefatura"});
        
        // Deshabilitar si no es Director
        boolean puedeEditar = puedeManejarUsuarios();
        txtNombre.setEnabled(puedeEditar);
        txtApellido.setEnabled(puedeEditar);
        txtCorreo.setEnabled(puedeEditar);
        txtPassword.setEnabled(puedeEditar);
        cmbTipo.setEnabled(puedeEditar);
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtApellido, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Correo:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCorreo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbTipo, gbc);
        
        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        
        // Deshabilitar botones si no es Director
        btnAgregar.setEnabled(puedeEditar);
        btnEliminar.setEnabled(puedeEditar);
        
        // Mostrar ícono de restricción si no es Jefatura
        if (!puedeEditar) {
            JLabel lblRestriccion = new JLabel("⛔ Solo Jefatura puede crear/editar usuarios");
            lblRestriccion.setForeground(new Color(231, 76, 60));
            lblRestriccion.setFont(new Font("Arial", Font.BOLD, 11));
            formPanel.add(lblRestriccion);
        }
        
        btnPanel.add(btnAgregar);
        btnPanel.add(btnEliminar);
        btnPanel.add(btnLimpiar);
        
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        
        // Tabla
        String[] columnas = {"ID", "Nombre", "Apellido", "Correo"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Usuarios"));
        
        // Eventos
        btnAgregar.addActionListener(e -> {
            if (!puedeManejarUsuarios()) {
                JOptionPane.showMessageDialog(panel,
                    "❌ No tiene permiso para crear usuarios.\nSolo Jefatura puede hacerlo.",
                    "Acceso Denegado",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String correo = txtCorreo.getText().trim();
            String password = new String(txtPassword.getPassword());
            String tipo = (String) cmbTipo.getSelectedItem();
            
            if (nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Complete todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Usuario usuario = new Usuario(nombre, apellido, correo, password);
            if (usuarioDAO.insertar(usuario, tipo)) {
                JOptionPane.showMessageDialog(panel, "✓ Usuario agregado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarUsuarios(modelo);
                txtNombre.setText("");
                txtApellido.setText("");
                txtCorreo.setText("");
                txtPassword.setText("");
            }
        });
        
        btnEliminar.addActionListener(e -> {
            if (!puedeManejarUsuarios()) {
                JOptionPane.showMessageDialog(panel,
                    "❌ No tiene permiso para eliminar usuarios.\nSolo Jefatura puede hacerlo.",
                    "Acceso Denegado",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int row = tabla.getSelectedRow();
            if (row != -1) {
                int id = (int) modelo.getValueAt(row, 0);
                if (usuarioDAO.eliminar(id)) {
                    JOptionPane.showMessageDialog(panel, "Usuario eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarUsuarios(modelo);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Seleccione un usuario", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnLimpiar.addActionListener(e -> {
            txtNombre.setText("");
            txtApellido.setText("");
            txtCorreo.setText("");
            txtPassword.setText("");
        });
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Cargar datos iniciales
        cargarUsuarios(modelo);
        
        return panel;
    }
    
    private void cargarUsuarios(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Usuario> usuarios = usuarioDAO.obtenerTodos();
        for (Usuario u : usuarios) {
            modelo.addRow(new Object[]{u.getId(), u.getNombre(), u.getApellido(), u.getCorreo()});
        }
    }
    
    //  PANEL PROYECTOS JEFATURA 
    
    private JPanel crearPanelProyectosJefatura() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla de proyectos con directores
        String[] columnas = {"ID", "Nombre Proyecto", "Código", "Descripción", "Tipo", "Inicio", "Fin", "Ayudantes", "Director Responsable"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Todos los Proyectos y sus Directores"));
        
        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Filtrar"));
        
        JTextField txtBuscar = new JTextField(20);
        JButton btnBuscar = new JButton("🔍 Buscar");
        JButton btnMostrarTodos = new JButton("📋 Mostrar Todos");
        JButton btnActualizar = new JButton("🔄 Actualizar");
        
        searchPanel.add(new JLabel("Buscar proyecto:"));
        searchPanel.add(txtBuscar);
        searchPanel.add(btnBuscar);
        searchPanel.add(btnMostrarTodos);
        searchPanel.add(btnActualizar);
        
        // Eventos
        btnBuscar.addActionListener(e -> {
            String texto = txtBuscar.getText().trim();
            if (!texto.isEmpty()) {
                modelo.setRowCount(0);
                List<Proyecto> proyectos = proyectoDAO.buscar(texto);
                for (Proyecto p : proyectos) {
                    String fechaInicioStr = p.getFechaInicio() != null ? p.getFechaInicio().toString() : "";
                    String fechaFinStr = p.getFechaFin() != null ? p.getFechaFin().toString() : "";
                    String nombreUsuario = proyectoDAO.obtenerNombreUsuario(p.getIdUsuario());
                    modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getCodigo(), p.getDescripcion(), 
                        p.getTipo(), fechaInicioStr, fechaFinStr, p.getNumeroDeDayudantesDelProyecto(), nombreUsuario});
                }
            }
        });
        
        btnMostrarTodos.addActionListener(e -> {
            txtBuscar.setText("");
            cargarTodosProyectosJefatura(modelo);
        });
        
        btnActualizar.addActionListener(e -> cargarTodosProyectosJefatura(modelo));
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Cargar datos iniciales
        cargarTodosProyectosJefatura(modelo);
        
        return panel;
    }
    
    private void cargarTodosProyectosJefatura(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Proyecto> proyectos = proyectoDAO.obtenerTodos();
        for (Proyecto p : proyectos) {
            String fechaInicioStr = p.getFechaInicio() != null ? p.getFechaInicio().toString() : "";
            String fechaFinStr = p.getFechaFin() != null ? p.getFechaFin().toString() : "";
            String nombreUsuario = proyectoDAO.obtenerNombreUsuario(p.getIdUsuario());
            modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getCodigo(), p.getDescripcion(), 
                p.getTipo(), fechaInicioStr, fechaFinStr, p.getNumeroDeDayudantesDelProyecto(), nombreUsuario});
        }
    }
    
    // ======================== PANEL PROYECTOS ========================
    
    private JPanel crearPanelProyectos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos de Proyecto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtNombre = new JTextField(20);
        JTextField txtCodigo = new JTextField(20);
        JTextField txtDescripcion = new JTextField(20);
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{
            "Semilla",
            "Interno",
            "Grupales",
            "Vinculación",
            "Confinanciamiento",
            "Transferencia Tecnológica"
        });
        JTextField txtFechaInicio = new JTextField(20);
        JTextField txtFechaFin = new JTextField(20);
        JSpinner spnAyudantes = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        spnAyudantes.setVisible(false); // Campo oculto, se llena desde PDF
        
        // Placeholder para fechas
        txtFechaInicio.setText("YYYY-MM-DD");
        txtFechaFin.setText("YYYY-MM-DD");
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtCodigo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDescripcion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbTipo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Fecha Inicio (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtFechaInicio, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Fecha Fin (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtFechaFin, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("N° Ayudantes:"), gbc);
        gbc.gridx = 1;
        JButton btnSubirPDF = new JButton("📄 Extraer de PDF");
        btnSubirPDF.setFont(new Font("Arial", Font.PLAIN, 11));
        btnSubirPDF.setFocusPainted(false);
        btnSubirPDF.setCursor(new Cursor(Cursor.HAND_CURSOR));
        formPanel.add(btnSubirPDF, gbc);
        
        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        
        btnPanel.add(btnAgregar);
        btnPanel.add(btnEliminar);
        btnPanel.add(btnLimpiar);
        
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        
        // Tabla
        String[] columnas = {"ID", "Nombre", "Código", "Descripción", "Tipo", "Inicio", "Fin", "Ayudantes", "Creado por"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Proyectos"));
        
        // Evento para botón de subir PDF
        btnSubirPDF.addActionListener(e -> {
         int numeroAyudantes = ExtractorPDF.extraerNumeroDeAyudanteDePDF(this);
             if (numeroAyudantes > 0) {
                 spnAyudantes.setValue(numeroAyudantes);
                 JOptionPane.showMessageDialog(panel, "✓ Número de ayudantes extraído: " + numeroAyudantes, "Éxito", JOptionPane.INFORMATION_MESSAGE);
             } else {
                 JOptionPane.showMessageDialog(panel, "No se pudo extraer el número de ayudantes del PDF", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Eventos
        btnAgregar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String codigo = txtCodigo.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String tipo = (String) cmbTipo.getSelectedItem();
            String fechaInicioStr = txtFechaInicio.getText().trim();
            String fechaFinStr = txtFechaFin.getText().trim();
            int ayudantes = (int) spnAyudantes.getValue();
            
            if (nombre.isEmpty() || codigo.isEmpty() || tipo.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Complete los campos obligatorios (Nombre, Código, Tipo)", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Convertir fechas
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaInicio = null;
            Date fechaFin = null;
            
            try {
                if (!fechaInicioStr.isEmpty() && !fechaInicioStr.equals("YYYY-MM-DD")) {
                    fechaInicio = sdf.parse(fechaInicioStr);
                }
                if (!fechaFinStr.isEmpty() && !fechaFinStr.equals("YYYY-MM-DD")) {
                    fechaFin = sdf.parse(fechaFinStr);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Formato de fecha inválido. Use YYYY-MM-DD", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Proyecto proyecto = new Proyecto(nombre, codigo, descripcion, tipo);
            proyecto.setFechaInicio(fechaInicio);
            proyecto.setFechaFin(fechaFin);
            proyecto.setNumeroDeDayudantesDelProyecto(ayudantes);
            
            // Asociar el proyecto al usuario autenticado
            if (usuarioActual != null) {
                proyecto.setIdUsuario(usuarioActual.getId());
            }
            
            if (proyectoDAO.insertar(proyecto)) {
                JOptionPane.showMessageDialog(panel, "✓ Proyecto agregado exitosamente y asociado a " + usuarioActual.getNombre(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarProyectos(modelo);
                limpiarCamposProyecto(txtNombre, txtCodigo, txtDescripcion, cmbTipo, txtFechaInicio, txtFechaFin);
            }
        });
        
        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row != -1) {
                int id = (int) modelo.getValueAt(row, 0);
                if (proyectoDAO.eliminar(id)) {
                    JOptionPane.showMessageDialog(panel, "✓ Proyecto eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    cargarProyectos(modelo);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Seleccione un proyecto", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        btnLimpiar.addActionListener(e -> {
            limpiarCamposProyecto(txtNombre, txtCodigo, txtDescripcion, cmbTipo, txtFechaInicio, txtFechaFin);
        });
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        cargarProyectos(modelo);
        
        return panel;
    }
    
    private void limpiarCamposProyecto(JTextField txtNombre, JTextField txtCodigo, 
                                       JTextField txtDescripcion, JComboBox<String> cmbTipo,
                                       JTextField txtFechaInicio, JTextField txtFechaFin) {
        txtNombre.setText("");
        txtCodigo.setText("");
        txtDescripcion.setText("");
        cmbTipo.setSelectedIndex(0);
        txtFechaInicio.setText("YYYY-MM-DD");
        txtFechaFin.setText("YYYY-MM-DD");
    }
    
    private void cargarProyectos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Proyecto> proyectos;
        
        // Si es Director, mostrar solo sus proyectos
        if ("Director".equals(rolActual) && usuarioActual != null) {
            proyectos = proyectoDAO.obtenerPorUsuario(usuarioActual.getId());
        } else {
            // Para otros roles (o sin autenticación), mostrar todos
            proyectos = proyectoDAO.obtenerTodos();
        }
        
        for (Proyecto p : proyectos) {
            String fechaInicioStr = p.getFechaInicio() != null ? p.getFechaInicio().toString() : "";
            String fechaFinStr = p.getFechaFin() != null ? p.getFechaFin().toString() : "";
            System.out.println("Proyecto: " + p.getNombre() + " - ID Usuario: " + p.getIdUsuario());
            String nombreUsuario = proyectoDAO.obtenerNombreUsuario(p.getIdUsuario());
            System.out.println("Nombre obtenido: " + nombreUsuario);
            modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getCodigo(), p.getDescripcion(), p.getTipo(), fechaInicioStr, fechaFinStr, p.getNumeroDeDayudantesDelProyecto(), nombreUsuario});
        }
    }
    
    //  PANEL FORMULARIOS 
    
    private JPanel crearPanelFormularios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // ===== SECCIÓN 1: FORMULARIO PARA REGISTRAR AYUDANTES (solo para Director) =====
        JPanel formularioAyudantesPanel = null;
        
        if ("Director".equals(rolActual)) {
            formularioAyudantesPanel = new JPanel(new BorderLayout(5, 5));
            formularioAyudantesPanel.setBorder(BorderFactory.createTitledBorder("Registrar Ayudantes"));
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            Director director = new Director(usuarioActual.getId(), usuarioActual.getNombre(),
                usuarioActual.getApellido(), usuarioActual.getCorreo(), usuarioActual.getContraseña());
            
            JComboBox<String> cbProyectoAyudantes = new JComboBox<>();
            JComboBox<String> cmbTipoPersonal = new JComboBox<>(new String[]{
                "Ayudante de Investigación",
                "Técnico en Investigación",
                "Asistente de Investigación"
            });
            JTextField txtNombreAyudante = new JTextField(15);
            JTextField txtApellidoAyudante = new JTextField(15);
            JTextField txtCedulaAyudante = new JTextField(15);
            JTextField txtFacultadAyudante = new JTextField(15);
            JSpinner spnNumAyudantes = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
            
            // Cargar proyectos del director
            List<Proyecto> proyectosDirector = proyectoDAO.obtenerPorDirector(usuarioActual.getId());
            for (Proyecto p : proyectosDirector) {
                cbProyectoAyudantes.addItem(p.getId() + " - " + p.getNombre());
            }
            
            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(new JLabel("Proyecto:"), gbc);
            gbc.gridx = 1;
            formPanel.add(cbProyectoAyudantes, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(new JLabel("Tipo de Personal:"), gbc);
            gbc.gridx = 1;
            formPanel.add(cmbTipoPersonal, gbc);
            
            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(new JLabel("N° de Ayudantes:"), gbc);
            gbc.gridx = 1;
            formPanel.add(spnNumAyudantes, gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            formPanel.add(new JLabel("Nombre:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtNombreAyudante, gbc);
            
            gbc.gridx = 0; gbc.gridy = 4;
            formPanel.add(new JLabel("Apellido:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtApellidoAyudante, gbc);
            
            gbc.gridx = 0; gbc.gridy = 5;
            formPanel.add(new JLabel("Cédula:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtCedulaAyudante, gbc);
            
            gbc.gridx = 0; gbc.gridy = 6;
            formPanel.add(new JLabel("Facultad:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtFacultadAyudante, gbc);
            
            JPanel btnAyudantesPanel = new JPanel(new FlowLayout());
            JButton btnGuardarAyudante = new JButton("✓ Guardar");
            JButton btnLimpiarAyudante = new JButton("🔄 Limpiar");
            
            btnAyudantesPanel.add(btnGuardarAyudante);
            btnAyudantesPanel.add(btnLimpiarAyudante);
            
            gbc.gridx = 0; gbc.gridy = 7;
            gbc.gridwidth = 2;
            formPanel.add(btnAyudantesPanel, gbc);
            
            // Eventos
            btnGuardarAyudante.addActionListener(e -> {
                if (cbProyectoAyudantes.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(panel, "Debe seleccionar un proyecto", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String proyectoSel = (String) cbProyectoAyudantes.getSelectedItem();
                int idProyecto = Integer.parseInt(proyectoSel.split(" - ")[0]);
                int numAyudantes = (int) spnNumAyudantes.getValue();
                String nombre = txtNombreAyudante.getText().trim();
                String apellido = txtApellidoAyudante.getText().trim();
                String cedula = txtCedulaAyudante.getText().trim();
                String facultad = txtFacultadAyudante.getText().trim();
                String tipoPersonal = (String) cmbTipoPersonal.getSelectedItem();

                if (nombre.isEmpty() || apellido.isEmpty() || cedula.isEmpty() || facultad.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Complete todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Crear formulario con tipo de personal
                Formulario formulario = new Formulario(numAyudantes, nombre, apellido, cedula, facultad);
                formulario.setIdProyecto(idProyecto);
                formulario.setTipoPersonal(tipoPersonal);
                
                // Guardar en BD
                if (formularioDAO.insertar(formulario)) {
                    // Obtener información del proyecto para la notificación
                    Proyecto proyecto = proyectoDAO.obtenerPorId(idProyecto);
                    String nombreProyecto = proyecto != null ? proyecto.getNombre() : "Proyecto ID: " + idProyecto;
                    
                    // Crear notificación dependiendo del tipo de personal
                    String mensajeNotificacion;
                    String asuntoEmail;
                    
                    if ("Ayudante de Investigación".equals(tipoPersonal) || 
                        tipoPersonal.toLowerCase().contains("ayudante")) {
                        // Para ayudantes: contar cuántos hay registrados vs. requeridos
                        int ayudantesRequeridos = proyecto != null ? proyecto.getNumeroDeDayudantesDelProyecto() : 0;
                        List<Formulario> formulariosProyecto = formularioDAO.buscarPorProyecto(idProyecto);
                        int ayudantesRegistrados = formulariosProyecto != null ? formulariosProyecto.size() : 0;
                        
                        mensajeNotificacion = String.format(
                            "✓ Nuevo ayudante registrado: %s %s\n" +
                            "Proyecto: %s\n" +
                            "Ayudantes registrados: %d de %d requeridos",
                            nombre, apellido,
                            nombreProyecto,
                            ayudantesRegistrados,
                            ayudantesRequeridos
                        );
                        
                        asuntoEmail = "Nuevo Ayudante Registrado - " + nombreProyecto;
                        
                        Notificacion notificacion = new Notificacion(mensajeNotificacion, usuarioActual.getId());
                        notificacionDAO.insertar(notificacion);
                    } else {
                        // Para otros tipos de personal: notificación simple
                        mensajeNotificacion = String.format(
                            "✓ Nuevo personal registrado: %s %s\n" +
                            "Tipo: %s\n" +
                            "Proyecto: %s",
                            nombre, apellido,
                            tipoPersonal,
                            nombreProyecto
                        );
                        
                        asuntoEmail = "Nuevo Personal Registrado - " + tipoPersonal;
                        
                        Notificacion notificacion = new Notificacion(mensajeNotificacion, usuarioActual.getId());
                        notificacionDAO.insertar(notificacion);
                    }
                    
                    // Enviar correo electrónico al director
                    try {
                        boolean correoEnviado = Notificacion.enviarNotificacionAUsuario(
                            usuarioActual, 
                            asuntoEmail, 
                            mensajeNotificacion
                        );
                        
                        if (correoEnviado) {
                            System.out.println("Correo de notificación enviado a: " + usuarioActual.getCorreo());
                        }
                    } catch (Exception ex) {
                        System.err.println("Error al enviar correo: " + ex.getMessage());
                        // No detenemos el flujo si falla el email
                    }
                    
                    JOptionPane.showMessageDialog(panel, "✓ Personal registrado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Limpiar campos
                    txtNombreAyudante.setText("");
                    txtApellidoAyudante.setText("");
                    txtCedulaAyudante.setText("");
                    txtFacultadAyudante.setText("");
                    spnNumAyudantes.setValue(1);
                    cmbTipoPersonal.setSelectedIndex(0);
                    
                    // Recargar tabla de formularios
                    cargarFormulariosEnTabla(panel);
                } else {
                    JOptionPane.showMessageDialog(panel, "Error al guardar el personal", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnLimpiarAyudante.addActionListener(e -> {
                txtNombreAyudante.setText("");
                txtApellidoAyudante.setText("");
                txtCedulaAyudante.setText("");
                txtFacultadAyudante.setText("");
                spnNumAyudantes.setValue(1);
            });
            
            formularioAyudantesPanel.add(formPanel, BorderLayout.CENTER);
        }
        
        // Variables para tabla y gestión de estado
        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Pendiente", "Aprobado", "Rechazado"});
        
        // Panel de gestión de estado solo para Jefatura
        if (puedeManejarUsuarios()) {
            JPanel estadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            estadoPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Estados"));

            estadoPanel.add(new JLabel("Estado:"));
            estadoPanel.add(cbEstado);

            JButton btnActualizarEstado = new JButton("Actualizar Estado");
            estadoPanel.add(btnActualizarEstado);
            
            JButton btnActualizarLista = new JButton("🔄 Actualizar Lista");
        btnActualizarLista.addActionListener(e -> cargarFormulariosEnTabla(panel));
            btnActualizarEstado.addActionListener(e -> {
                JTable tabla = obtenerTablaDesdePanel(panel);
                if (tabla == null) return;

                int filaSeleccionada = tabla.getSelectedRow();
                if (filaSeleccionada == -1) {
                    JOptionPane.showMessageDialog(panel, "Seleccione un formulario de la tabla", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
                int id = (int) modelo.getValueAt(filaSeleccionada, 0);
                String nuevoEstado = (String) cbEstado.getSelectedItem();

                Formulario formulario = formularioDAO.obtenerPorId(id);
                if (formulario != null) {
                    formulario.setEstado(nuevoEstado);
                    if (formularioDAO.actualizar(formulario)) {
                        // Verificar y notificar si faltan ayudantes
                        verificarFormulariosDirectores();
                        JOptionPane.showMessageDialog(panel, "Estado actualizado exitosamente");
                        cargarFormulariosEnTabla(panel);
                    }
                }
            });
        }
        
        // Tabla - siempre con todas las columnas; ocultamos algunas en la vista
        String[] columnas = new String[]{"ID", "N° Ayudantes", "Nombre", "Apellido", "Cédula", "Facultad", "Tipo Personal", "Estado", "IdProyecto", "Proyecto", "Director"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Formularios"));
        
        // Ocultar columnas en la vista
        ocultarColumna(tabla, 0); // Ocultar ID
        ocultarColumna(tabla, 1); // Ocultar N° Ayudantes
        ocultarColumna(tabla, 8); // Ocultar IdProyecto
        if (!puedeManejarUsuarios()) { // Director no ve Estado
            ocultarColumna(tabla, 7);
        }
        
        // Listener para selección (solo para Jefatura)
        if (puedeManejarUsuarios()) {
            tabla.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                    int fila = tabla.getSelectedRow();
                    cbEstado.setSelectedItem(modelo.getValueAt(fila, 7));
                }
            });
        }
        
        // Panel central con formulario de ayudantes (si es Director) y tabla
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        if (formularioAyudantesPanel != null) {
            centerPanel.add(formularioAyudantesPanel, BorderLayout.NORTH);
        }
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de resumen de ayudantes (solo para Jefatura)
        JPanel resumenPanel = null;
        if (puedeManejarUsuarios()) {
            resumenPanel = crearPanelResumenAyudantesEnFormularios();
            panel.add(resumenPanel, BorderLayout.NORTH);
        }
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        cargarFormularios(modelo);
        
        return panel;
    }
    
    private void cargarFormulariosEnTabla(JPanel panel) {
        // Buscar la tabla en el panel
        JTable tabla = obtenerTablaDesdePanel(panel);
        if (tabla != null) {
            DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
            cargarFormularios(modelo);
        }
    }

    private JTable obtenerTablaDesdePanel(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JViewport viewport = ((JScrollPane) comp).getViewport();
                if (viewport.getView() instanceof JTable) {
                    return (JTable) viewport.getView();
                }
            }
        }
        return null;
    }

    private void cargarFormularios(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Formulario> formularios;
        
        // Si es Director, mostrar solo formularios de sus proyectos
        if ("Director".equals(rolActual) && usuarioActual != null) {
            formularios = formularioDAO.obtenerFormulariosPorUsuario(usuarioActual.getId());
        } else {
            // Para otros roles (Jefatura), mostrar todos los formularios
            formularios = formularioDAO.obtenerTodos();
        }
        
        for (Formulario f : formularios) {
            // Obtener nombre del proyecto y director
            String nombreProyecto = "";
            String nombreDirector = "";
            
            if (f.getIdProyecto() > 0) {
                Proyecto proyecto = proyectoDAO.obtenerPorId(f.getIdProyecto());
                if (proyecto != null) {
                    nombreProyecto = proyecto.getNombre();
                    // Obtener nombre del director
                    Usuario director = usuarioDAO.obtenerPorId(proyecto.getIdUsuario());
                    if (director != null) {
                        nombreDirector = director.getNombre();
                    }
                }
            }
            
            // Siempre cargamos todas las columnas; la vista oculta algunas
            modelo.addRow(new Object[]{
                f.getId(), 
                f.getNumeroDeAyudantes(), 
                f.getNombreDelAyudante(), 
                f.getApellidoDelAyudante(), 
                f.getCedula(), 
                f.getFacultad(), 
                f.getTipoPersonal(),
                f.getEstado(),
                f.getIdProyecto(),
                nombreProyecto,
                nombreDirector
            });
        }
    }
    


    private void ocultarColumna(JTable tabla, int indice) {
        if (indice < 0 || indice >= tabla.getColumnModel().getColumnCount()) return;
        TableColumn column = tabla.getColumnModel().getColumn(indice);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
        column.setResizable(false);
    }
    
    // ======================== PANEL NOTIFICACIONES ========================
    
    private JPanel crearPanelNotificaciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] columnas = {"ID", "Fecha", "Información"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Lista de Notificaciones"));
        
        // Panel con botón actualizar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.addActionListener(e -> cargarNotificaciones(modelo));
        topPanel.add(btnActualizar);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        cargarNotificaciones(modelo);
        
        return panel;
    }
    
    private void cargarNotificaciones(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Notificacion> notificaciones = notificacionDAO.obtenerPorUsuario(usuarioActual.getId());
        for (Notificacion n : notificaciones) {
            modelo.addRow(new Object[]{n.getId(), n.getFecha(), n.getInformacion()});
        }
    }
    
    /**
     * Verifica si los directores tienen formularios de ayudantes completos
     * Si no los tienen, crea una notificación
     */
    private void verificarFormulariosDirectores() {
        // Obtener todos los proyectos
        List<Proyecto> proyectos = proyectoDAO.obtenerTodos();
        
        for (Proyecto proyecto : proyectos) {
            verificarFormulariosProyecto(proyecto.getId());
        }
    }
    
    /**
     * Verifica si un proyecto específico tiene formularios de ayudantes completos
     * Si no, crea una notificación para su director
     */
    private void verificarFormulariosProyecto(int idProyecto) {
        // Obtener el proyecto
        Proyecto proyecto = proyectoDAO.obtenerPorId(idProyecto);
        if (proyecto == null) return;
        
        // Obtener director del proyecto
        Usuario director = usuarioDAO.obtenerPorId(proyecto.getIdUsuario());
        
        if (director != null && "Director".equals(director.getTipo())) {
            // Contar formularios asociados SOLO a este proyecto
            List<Formulario> todosFormularios = formularioDAO.obtenerTodos();
            int ayudantesRegistrados = 0;
            for (Formulario f : todosFormularios) {
                if (f.getIdProyecto() == idProyecto) {
                    ayudantesRegistrados++;
                }
            }
            
            int ayudantesRequeridos = proyecto.getNumeroDeDayudantesDelProyecto();
            
            // Si faltan ayudantes por registrar, crear notificación
            if (ayudantesRegistrados < ayudantesRequeridos) {
                String mensaje = String.format(
                    "El proyecto '%s' requiere %d ayudante(s) pero solo tiene %d registrado(s). " +
                    "Por favor, complete los formularios de ayudantes.",
                    proyecto.getNombre(),
                    ayudantesRequeridos,
                    ayudantesRegistrados
                );
                
                Notificacion notificacion = new Notificacion(
                    mensaje,
                    director.getId()
                );
                
                notificacionDAO.insertar(notificacion);
                System.out.println("Notificación creada para director: " + director.getNombre() + " del proyecto ID: " + idProyecto);
            }
        }
    }
    
    // ======================== PANEL GUARDAR AVANCE ========================
    
    private JPanel crearPanelGuardarAvance() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de carga de archivo
        JPanel uploadPanel = new JPanel(new GridBagLayout());
        uploadPanel.setBorder(BorderFactory.createTitledBorder("Cargar Avance"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JComboBox<String> cbProyectoAvance = new JComboBox<>();
        JTextField txtDescripcion = new JTextField(25);
        JLabel lblArchivo = new JLabel("Ningún archivo seleccionado");
        JButton btnSeleccionarArchivo = new JButton("📁 Seleccionar Archivo");
        JButton btnCargar = new JButton("📤 Cargar Avance");
        JButton btnLimpiar = new JButton("🔄 Limpiar");
        
        // Cargar proyectos del director
        List<Proyecto> proyectosDirector = proyectoDAO.obtenerPorDirector(usuarioActual.getId());
        
        for (Proyecto p : proyectosDirector) {
            cbProyectoAvance.addItem(p.getId() + " - " + p.getNombre());
        }
        
        // Variable para almacenar el archivo seleccionado
        final java.io.File[] archivoSeleccionado = {null};
        
        // Tabla de avances - DECLARAR PRIMERO
        String[] columnas = {"ID", "Proyecto", "Descripción", "Archivo", "Fecha", "Director"};
        DefaultTableModel modeloAvances = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tablaAvances = new JTable(modeloAvances);
        JScrollPane scrollPane = new JScrollPane(tablaAvances);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Avances Cargados"));
        
        gbc.gridx = 0; gbc.gridy = 0;
        uploadPanel.add(new JLabel("Proyecto:"), gbc);
        gbc.gridx = 1;
        uploadPanel.add(cbProyectoAvance, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        uploadPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        uploadPanel.add(txtDescripcion, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        uploadPanel.add(new JLabel("Archivo:"), gbc);
        gbc.gridx = 1;
        uploadPanel.add(lblArchivo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        uploadPanel.add(btnSeleccionarArchivo, gbc);
        
        // Evento para seleccionar archivo
        btnSeleccionarArchivo.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF", "pdf"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Word", "doc", "docx"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel", "xls", "xlsx"));
            fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "jpg", "png", "gif"));
            
            int resultado = fileChooser.showOpenDialog(panel);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                archivoSeleccionado[0] = fileChooser.getSelectedFile();
                lblArchivo.setText(archivoSeleccionado[0].getName());
            }
        });
        
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        uploadPanel.add(btnCargar, gbc);
        gbc.gridx = 1;
        uploadPanel.add(btnLimpiar, gbc);
        
        // Evento para cargar avance
        btnCargar.addActionListener(e -> {
            if (cbProyectoAvance.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(panel, "Seleccione un proyecto", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (archivoSeleccionado[0] == null) {
                JOptionPane.showMessageDialog(panel, "Seleccione un archivo", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String descripcion = txtDescripcion.getText().trim();
            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Ingrese una descripción del avance", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String proyectoSel = (String) cbProyectoAvance.getSelectedItem();
            int idProyecto = Integer.parseInt(proyectoSel.split(" - ")[0]);
            
            try {
                // Crear carpeta de avances si no existe
                java.io.File carpetaAvances = new java.io.File("avances");
                if (!carpetaAvances.exists()) {
                    carpetaAvances.mkdir();
                }
                
                // Generar nombre único para el archivo
                String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
                String nombreArchivo = timestamp + "_" + archivoSeleccionado[0].getName();
                String rutaDestino = "avances/" + nombreArchivo;
                
                // Copiar archivo
                java.nio.file.Files.copy(archivoSeleccionado[0].toPath(), 
                    new java.io.File(rutaDestino).toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                // Guardar información en BD
                Avance avance = new Avance(idProyecto, usuarioActual.getId(), descripcion, nombreArchivo, rutaDestino);
                
                if (avanceDAO.insertar(avance)) {
                    JOptionPane.showMessageDialog(panel, 
                        "✓ Avance cargado exitosamente\n" + 
                        "Archivo: " + nombreArchivo + "\n" +
                        "Descripción: " + descripcion,
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Limpiar campos
                    txtDescripcion.setText("");
                    lblArchivo.setText("Ningún archivo seleccionado");
                    archivoSeleccionado[0] = null;
                    
                    // Recargar tabla de avances
                    cargarAvances(modeloAvances);
                } else {
                    JOptionPane.showMessageDialog(panel, "Error al guardar el avance en la BD", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error al cargar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnLimpiar.addActionListener(e -> {
            txtDescripcion.setText("");
            lblArchivo.setText("Ningún archivo seleccionado");
            archivoSeleccionado[0] = null;
        });
        
        // Panel de acciones
        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnDescargar = new JButton("⬇️ Descargar");
        JButton btnEliminar = new JButton("🗑️ Eliminar");
        
        btnDescargar.addActionListener(e -> {
            int fila = tablaAvances.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(panel, "Seleccione un avance para descargar", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Obtener el avance completo de la BD para verificar si tiene archivo firmado
            int idAvance = (int) modeloAvances.getValueAt(fila, 0);
            Avance avance = avanceDAO.obtenerPorId(idAvance);
            
            if (avance == null) {
                JOptionPane.showMessageDialog(panel, "Error al obtener información del avance", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Priorizar archivo firmado si existe
            String rutaArchivo;
            String nombreDescarga;
            
            if (avance.getArchivoFirmado() != null && !avance.getArchivoFirmado().isEmpty()) {
                rutaArchivo = avance.getArchivoFirmado();
                nombreDescarga = new java.io.File(rutaArchivo).getName();
            } else {
                rutaArchivo = avance.getRutaArchivo();
                nombreDescarga = avance.getNombreArchivo();
            }
            
            java.io.File archivo = new java.io.File(rutaArchivo);
            
            if (archivo.exists()) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File(nombreDescarga));
                int resultado = fileChooser.showSaveDialog(panel);
                
                if (resultado == JFileChooser.APPROVE_OPTION) {
                    try {
                        java.nio.file.Files.copy(archivo.toPath(), 
                            fileChooser.getSelectedFile().toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(panel, "Archivo descargado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, "Error al descargar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel, "El archivo no existe", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnEliminar.addActionListener(e -> {
            int fila = tablaAvances.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(panel, "Seleccione un avance para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int respuesta = JOptionPane.showConfirmDialog(panel, "¿Está seguro de que desea eliminar este avance?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                int idAvance = (int) modeloAvances.getValueAt(fila, 0);
                String nombreArchivo = (String) modeloAvances.getValueAt(fila, 3);
                java.io.File archivo = new java.io.File("avances/" + nombreArchivo);
                
                // Eliminar archivo
                if (archivo.delete()) {
                    // Eliminar de la BD
                    if (avanceDAO.eliminar(idAvance)) {
                        JOptionPane.showMessageDialog(panel, "Avance eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarAvances(modeloAvances);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Error al eliminar de la BD", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "No se pudo eliminar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        accionesPanel.add(btnDescargar);
        accionesPanel.add(btnEliminar);
        
        // Ensamblar panel
        panel.add(uploadPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(accionesPanel, BorderLayout.SOUTH);
        
        cargarAvances(modeloAvances);
        
        return panel;
    }
    
    private void cargarAvances(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        
        // Obtener avances del director actual de la BD
        List<Avance> avances = avanceDAO.obtenerPorDirector(usuarioActual.getId());
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Avance a : avances) {
            // Obtener nombre del proyecto
            String nombreProyecto = "";
            Proyecto proyecto = proyectoDAO.obtenerPorId(a.getIdProyecto());
            if (proyecto != null) {
                nombreProyecto = proyecto.getNombre();
            }
            
            // Mostrar archivo firmado si existe, sino el original
            String archivoMostrar = a.getNombreArchivo();
            if (a.getArchivoFirmado() != null && !a.getArchivoFirmado().isEmpty()) {
                // Extraer solo el nombre del archivo firmado
                java.io.File archivoFirmado = new java.io.File(a.getArchivoFirmado());
                archivoMostrar = archivoFirmado.getName() + " (FIRMADO)";
            }
            
            modelo.addRow(new Object[]{
                a.getIdAvance(),
                nombreProyecto,
                a.getDescripcion(),
                archivoMostrar,
                sdf.format(a.getFechaCarga()),
                a.getEstado()
            });
        }
    }
    
    // ======================== PANEL AVANCES JEFATURA ========================
    
    private JPanel crearPanelAvancesJefatura() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabla de todos los avances
        String[] columnas = {"ID", "Proyecto", "Director", "Descripción", "Archivo", "Fecha", "Estado", "Firmado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Todos los Avances"));
        
        // Ocultar columna ID
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);
        
        // Panel de acciones
        JPanel accionesPanel = new JPanel(new GridBagLayout());
        accionesPanel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton btnDescargar = new JButton("⬇️ Descargar Avance");
        JButton btnSubirFirmado = new JButton("📤 Subir Archivo Firmado");
        JButton btnDescargarFirmado = new JButton("📥 Descargar Firmado");
        JButton btnActualizar = new JButton("🔄 Actualizar");
        JComboBox<String> cmbEstado = new JComboBox<>(new String[]{"Pendiente", "Revisado", "Aprobado"});
        JButton btnCambiarEstado = new JButton("Cambiar Estado");
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        accionesPanel.add(btnDescargar, gbc);
        
        gbc.gridy = 1;
        accionesPanel.add(btnSubirFirmado, gbc);
        
        gbc.gridy = 2;
        accionesPanel.add(btnDescargarFirmado, gbc);
        
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        accionesPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1;
        accionesPanel.add(cmbEstado, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        accionesPanel.add(btnCambiarEstado, gbc);
        
        gbc.gridy = 5;
        accionesPanel.add(btnActualizar, gbc);
        
        // Evento descargar avance
        btnDescargar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(panel, "Seleccione un avance", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String nombreArchivo = (String) modelo.getValueAt(fila, 4);
            java.io.File archivo = new java.io.File("avances/" + nombreArchivo);
            
            if (archivo.exists()) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File(nombreArchivo));
                int resultado = fileChooser.showSaveDialog(panel);
                
                if (resultado == JFileChooser.APPROVE_OPTION) {
                    try {
                        java.nio.file.Files.copy(archivo.toPath(), 
                            fileChooser.getSelectedFile().toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(panel, "Archivo descargado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, "Error al descargar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel, "El archivo no existe", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Evento subir archivo firmado
        btnSubirFirmado.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(panel, "Seleccione un avance", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int resultado = fileChooser.showOpenDialog(panel);
            
            if (resultado == JFileChooser.APPROVE_OPTION) {
                try {
                    java.io.File archivoSeleccionado = fileChooser.getSelectedFile();
                    int idAvance = (int) modelo.getValueAt(fila, 0);
                    
                    // Crear carpeta de firmados si no existe
                    java.io.File carpetaFirmados = new java.io.File("avances/firmados");
                    if (!carpetaFirmados.exists()) {
                        carpetaFirmados.mkdirs();
                    }
                    
                    // Generar nombre único
                    String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
                    String nombreFirmado = "FIRMADO_" + timestamp + "_" + archivoSeleccionado.getName();
                    String rutaFirmado = "avances/firmados/" + nombreFirmado;
                    
                    // Copiar archivo
                    java.nio.file.Files.copy(archivoSeleccionado.toPath(), 
                        new java.io.File(rutaFirmado).toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    
                    // Guardar en BD
                    if (avanceDAO.actualizarArchivoFirmado(idAvance, rutaFirmado)) {
                        JOptionPane.showMessageDialog(panel, "Archivo firmado subido exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        cargarAvancesJefatura(modelo);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Error al guardar en BD", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Evento descargar archivo firmado
        btnDescargarFirmado.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(panel, "Seleccione un avance", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String archivoFirmado = (String) modelo.getValueAt(fila, 7);
            if (archivoFirmado == null || archivoFirmado.equals("No")) {
                JOptionPane.showMessageDialog(panel, "Este avance no tiene archivo firmado", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int idAvance = (int) modelo.getValueAt(fila, 0);
            Avance avance = avanceDAO.obtenerPorId(idAvance);
            
            if (avance != null && avance.getArchivoFirmado() != null) {
                java.io.File archivo = new java.io.File(avance.getArchivoFirmado());
                
                if (archivo.exists()) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setSelectedFile(new java.io.File(archivo.getName()));
                    int resultado = fileChooser.showSaveDialog(panel);
                    
                    if (resultado == JFileChooser.APPROVE_OPTION) {
                        try {
                            java.nio.file.Files.copy(archivo.toPath(), 
                                fileChooser.getSelectedFile().toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            JOptionPane.showMessageDialog(panel, "Archivo firmado descargado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "El archivo no existe", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Evento cambiar estado
        btnCambiarEstado.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(panel, "Seleccione un avance", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int idAvance = (int) modelo.getValueAt(fila, 0);
            String nuevoEstado = (String) cmbEstado.getSelectedItem();
            
            if (avanceDAO.actualizarEstado(idAvance, nuevoEstado)) {
                JOptionPane.showMessageDialog(panel, "Estado actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarAvancesJefatura(modelo);
            } else {
                JOptionPane.showMessageDialog(panel, "Error al actualizar estado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Evento actualizar
        btnActualizar.addActionListener(e -> cargarAvancesJefatura(modelo));
        
        // Listener para actualizar el combo de estado
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                String estadoActual = (String) modelo.getValueAt(tabla.getSelectedRow(), 6);
                cmbEstado.setSelectedItem(estadoActual);
            }
        });
        
        panel.add(accionesPanel, BorderLayout.WEST);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        cargarAvancesJefatura(modelo);
        
        return panel;
    }
    
    private void cargarAvancesJefatura(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        
        List<Avance> avances = avanceDAO.obtenerTodos();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Avance a : avances) {
            // Obtener nombre del proyecto
            String nombreProyecto = "";
            Proyecto proyecto = proyectoDAO.obtenerPorId(a.getIdProyecto());
            if (proyecto != null) {
                nombreProyecto = proyecto.getNombre();
            }
            
            // Obtener nombre del director
            String nombreDirector = "";
            Usuario director = usuarioDAO.obtenerPorId(a.getIdDirector());
            if (director != null) {
                nombreDirector = director.getNombre() + " " + director.getApellido();
            }
            
            String tieneFirmado = (a.getArchivoFirmado() != null) ? "Sí" : "No";
            
            modelo.addRow(new Object[]{
                a.getIdAvance(),
                nombreProyecto,
                nombreDirector,
                a.getDescripcion(),
                a.getNombreArchivo(),
                sdf.format(a.getFechaCarga()),
                a.getEstado(),
                tieneFirmado
            });
        }
    }
    
    /**
     * Panel de Resumen de Ayudantes por Proyecto INTEGRADO EN FORMULARIOS
     * Vista exclusiva para Jefatura mostrando cuántos ayudantes hay registrados
     * desglosados por tipo de personal
     */
    private JPanel crearPanelResumenAyudantesEnFormularios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("📊 Resumen de Ayudantes por Proyecto"));
        
        // Panel superior con botón de actualización
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JLabel lblInfo = new JLabel("Estado de registros y desglose por tipo de personal");
        topPanel.add(lblInfo);
        
        JButton btnActualizarResumen = new JButton("🔄 Actualizar");
        topPanel.add(btnActualizarResumen);
        
        // Tabla con desglose detallado
        String[] columnas = {"Proyecto", "Código", "Director", 
                            "Req.", "Registrados", "Faltantes",
                            "👨‍🔬 Ayudantes", "🔧 Técnicos", "👨‍💼 Asistentes", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Arial", Font.PLAIN, 10));
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        
        // Panel inferior con estadísticas por tipo
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Totales"));
        
        JLabel lblTotalProyectos = new JLabel("Proyectos: 0");
        JLabel lblCompletados = new JLabel("✓ Completos: 0");
        JLabel lblIncompletos = new JLabel("⚠ Incompletos: 0");
        JLabel lblTotalAyudantes = new JLabel("👨‍🔬 Ayudantes: 0");
        JLabel lblTotalTecnicos = new JLabel("🔧 Técnicos: 0");
        JLabel lblTotalAsistentes = new JLabel("👨‍💼 Asistentes: 0");
        
        statsPanel.add(lblTotalProyectos);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(lblCompletados);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(lblIncompletos);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(lblTotalAyudantes);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(lblTotalTecnicos);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(lblTotalAsistentes);
        
        // Runnable para cargar datos
        Runnable cargarDatos = () -> {
            modelo.setRowCount(0);
            List<Object[]> resumen = formularioDAO.obtenerResumenAyudantesPorProyecto();
            
            int totalProyectos = 0;
            int completados = 0;
            int incompletos = 0;
            int totalAyudantesInv = 0;
            int totalTecnicos = 0;
            int totalAsistentes = 0;
            
            for (Object[] fila : resumen) {
                int idProyecto = (Integer) fila[0];
                int[] desglose = formularioDAO.obtenerDesglosePorTipoPersonal(idProyecto);
                
                Object[] filaCompleta = new Object[10];
                filaCompleta[0] = fila[1]; // Nombre proyecto
                filaCompleta[1] = fila[2]; // Código
                filaCompleta[2] = fila[6]; // Director
                filaCompleta[3] = fila[3]; // Requeridos
                filaCompleta[4] = fila[4]; // Registrados
                filaCompleta[5] = fila[5]; // Faltantes
                filaCompleta[6] = desglose[0]; // Ayudantes de Investigación
                filaCompleta[7] = desglose[1]; // Técnicos
                filaCompleta[8] = desglose[2]; // Asistentes
                filaCompleta[9] = fila[7]; // Estado
                
                modelo.addRow(filaCompleta);
                totalProyectos++;
                totalAyudantesInv += desglose[0];
                totalTecnicos += desglose[1];
                totalAsistentes += desglose[2];
                
                if ("✓ Completo".equals(fila[7])) {
                    completados++;
                } else {
                    incompletos++;
                }
            }
            
            // Actualizar estadísticas
            lblTotalProyectos.setText("Proyectos: " + totalProyectos);
            lblCompletados.setText("✓ Completos: " + completados);
            lblIncompletos.setText("⚠ Incompletos: " + incompletos);
            lblTotalAyudantes.setText("👨‍🔬 Ayudantes: " + totalAyudantesInv);
            lblTotalTecnicos.setText("🔧 Técnicos: " + totalTecnicos);
            lblTotalAsistentes.setText("👨‍💼 Asistentes: " + totalAsistentes);
        };
        
        // Evento del botón actualizar
        btnActualizarResumen.addActionListener(e -> cargarDatos.run());
        
        // Evento de doble clic en la tabla para ver detalles de ayudantes
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tabla.getSelectedRow();
                    if (row >= 0) {
                        String nombreProyecto = (String) modelo.getValueAt(row, 0);
                        // Obtener el ID del proyecto de la lista de resumen
                        List<Object[]> resumen = formularioDAO.obtenerResumenAyudantesPorProyecto();
                        for (Object[] fila : resumen) {
                            if (nombreProyecto.equals(fila[1])) {
                                mostrarDetallesAyudantesProyecto((Integer) fila[0], nombreProyecto);
                                break;
                            }
                        }
                    }
                }
            }
        });
        
        // Cargar datos al crear el panel
        SwingUtilities.invokeLater(cargarDatos);
        
        // Armar el panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Muestra una ventana con los detalles de todos los ayudantes de un proyecto
     * Desglosados por tipo de personal
     */
    private void mostrarDetallesAyudantesProyecto(int idProyecto, String nombreProyecto) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Detalles de Ayudantes - " + nombreProyecto, true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel superior con información del proyecto
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Proyecto proyecto = proyectoDAO.obtenerPorId(idProyecto);
        String infoText = "Proyecto: " + nombreProyecto;
        if (proyecto != null) {
            infoText += " | Código: " + proyecto.getCodigo() + 
                       " | Director: " + proyectoDAO.obtenerNombreUsuario(proyecto.getIdUsuario()) +
                       " | Ayudantes Requeridos: " + proyecto.getNumeroDeDayudantesDelProyecto();
        }
        infoPanel.add(new JLabel(infoText));
        infoPanel.setBackground(new Color(240, 240, 240));
        infoPanel.setBorder(BorderFactory.createEtchedBorder());
        
        // Panel de pestañas para cada tipo de personal
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Obtener formularios del proyecto
        List<Formulario> formularios = formularioDAO.buscarPorProyecto(idProyecto);
        
        // Separar por tipo
        List<Formulario> ayudantesInv = new ArrayList<>();
        List<Formulario> tecnicos = new ArrayList<>();
        List<Formulario> asistentes = new ArrayList<>();
        
        for (Formulario f : formularios) {
            String tipo = f.getTipoPersonal();
            if (tipo != null) {
                if (tipo.contains("Ayudante de Investigación")) {
                    ayudantesInv.add(f);
                } else if (tipo.contains("Técnico")) {
                    tecnicos.add(f);
                } else if (tipo.contains("Asistente")) {
                    asistentes.add(f);
                }
            }
        }
        
        // Tab 1: Ayudantes de Investigación
        JPanel tabAyudantes = crearTablaAyudantes(ayudantesInv, "👨‍🔬 Ayudantes de Investigación (" + ayudantesInv.size() + ")");
        tabbedPane.addTab("👨‍🔬 Ayudantes (" + ayudantesInv.size() + ")", tabAyudantes);
        
        // Tab 2: Técnicos
        JPanel tabTecnicos = crearTablaAyudantes(tecnicos, "🔧 Técnicos en Investigación (" + tecnicos.size() + ")");
        tabbedPane.addTab("🔧 Técnicos (" + tecnicos.size() + ")", tabTecnicos);
        
        // Tab 3: Asistentes
        JPanel tabAsistentes = crearTablaAyudantes(asistentes, "👨‍💼 Asistentes de Investigación (" + asistentes.size() + ")");
        tabbedPane.addTab("👨‍💼 Asistentes (" + asistentes.size() + ")", tabAsistentes);
        
        // Panel inferior con botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExportar = new JButton("📊 Exportar");
        JButton btnCerrar = new JButton("Cerrar");
        
        btnCerrar.addActionListener(e -> dialog.dispose());
        btnExportar.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Funcionalidad de exportación en desarrollo", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnPanel.add(btnExportar);
        btnPanel.add(btnCerrar);
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Crea un panel con tabla de ayudantes
     */
    private JPanel crearTablaAyudantes(List<Formulario> formularios, String titulo) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] columnas = {"#", "Nombre Completo", "Cédula", "Facultad", "Estado", "Fecha Registro"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setRowHeight(25);
        tabla.setFont(new Font("Arial", Font.PLAIN, 11));
        
        int contador = 1;
        for (Formulario f : formularios) {
            modelo.addRow(new Object[]{
                contador++,
                f.getNombreDelAyudante() + " " + f.getApellidoDelAyudante(),
                f.getCedula(),
                f.getFacultad(),
                f.getEstado(),
                "Sin fecha"
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(tabla);
        
        // Panel inferior con información
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTotal = new JLabel("Total registrados: " + formularios.size());
        lblTotal.setFont(new Font("Arial", Font.BOLD, 11));
        
        long aprobados = formularios.stream().filter(f -> "Aprobado".equals(f.getEstado())).count();
        JLabel lblAprobados = new JLabel("✓ Aprobados: " + aprobados);
        
        long pendientes = formularios.stream().filter(f -> "Pendiente".equals(f.getEstado())).count();
        JLabel lblPendientes = new JLabel("⏳ Pendientes: " + pendientes);
        
        long rechazados = formularios.stream().filter(f -> "Rechazado".equals(f.getEstado())).count();
        JLabel lblRechazados = new JLabel("✗ Rechazados: " + rechazados);
        
        statsPanel.add(lblTotal);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(lblAprobados);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(lblPendientes);
        statsPanel.add(new JSeparator(JSeparator.VERTICAL));
        statsPanel.add(lblRechazados);
        statsPanel.setBorder(BorderFactory.createEtchedBorder());
        
        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}