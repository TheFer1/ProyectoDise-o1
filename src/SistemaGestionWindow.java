import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    private SolicitudDAO solicitudDAO;
    private NotificacionDAO notificacionDAO;
    
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
        solicitudDAO = new SolicitudDAO();
        notificacionDAO = new NotificacionDAO();
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
            tabbedPane.addTab("📋 Solicitudes", crearPanelSolicitudesDirector());
        }
        
        // Solo Director puede ver/editar sus proyectos
        if (puedeManejarProyectos()) {
            tabbedPane.addTab("📁 Mis Proyectos", crearPanelProyectos());
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
        
        searchPanel.add(new JLabel("Buscar proyecto:"));
        searchPanel.add(txtBuscar);
        searchPanel.add(btnBuscar);
        searchPanel.add(btnMostrarTodos);
        
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
        JTextField txtTipo = new JTextField(20);
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
        formPanel.add(txtTipo, gbc);
        
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
            String tipo = txtTipo.getText().trim();
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
                limpiarCamposProyecto(txtNombre, txtCodigo, txtDescripcion, txtTipo, txtFechaInicio, txtFechaFin);
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
            limpiarCamposProyecto(txtNombre, txtCodigo, txtDescripcion, txtTipo, txtFechaInicio, txtFechaFin);
        });
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        cargarProyectos(modelo);
        
        return panel;
    }
    
    private void limpiarCamposProyecto(JTextField txtNombre, JTextField txtCodigo, 
                                       JTextField txtDescripcion, JTextField txtTipo,
                                       JTextField txtFechaInicio, JTextField txtFechaFin) {
        txtNombre.setText("");
        txtCodigo.setText("");
        txtDescripcion.setText("");
        txtTipo.setText("");
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
        
        // Variables para los campos (usadas en listeners)
        JTextField txtNumAyudantes = new JTextField(15);
        JTextField txtNombre = new JTextField(15);
        JTextField txtApellido = new JTextField(15);
        JTextField txtCedula = new JTextField(15);
        JTextField txtFacultad = new JTextField(15);
        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Pendiente", "Aprobado", "Rechazado"});
        JComboBox<String> cbProyecto = new JComboBox<>(); // ComboBox para proyectos
        
        // Variable para rastrear ayudantes pendientes
        final int[] ayudantesPendientes = {0};
        final int[] proyectoSeleccionadoId = {0}; // ID del proyecto seleccionado
        
        // Solo Director ve el panel de formulario de entrada
        if (!puedeManejarUsuarios()) {
            // Panel de formulario
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Formularios"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            int filaActual = 0;
            
            // Fila 0: Proyecto
            gbc.gridx = 0; gbc.gridy = filaActual;
            formPanel.add(new JLabel("Proyecto:"), gbc);
            gbc.gridx = 1;
            formPanel.add(cbProyecto, gbc);
            filaActual++;
            
            // Fila 1: Número de Ayudantes
            gbc.gridx = 0; gbc.gridy = filaActual;
            formPanel.add(new JLabel("N° Ayudantes:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtNumAyudantes, gbc);
            filaActual++;
            
            // Fila 2: Nombre
            gbc.gridx = 0; gbc.gridy = filaActual;
            formPanel.add(new JLabel("Nombre:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtNombre, gbc);
            filaActual++;
            
            // Fila 2: Apellido
            gbc.gridx = 0; gbc.gridy = filaActual;
            formPanel.add(new JLabel("Apellido:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtApellido, gbc);
            filaActual++;
            
            // Fila 3: Cédula
            gbc.gridx = 0; gbc.gridy = filaActual;
            formPanel.add(new JLabel("Cédula:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtCedula, gbc);
            filaActual++;
            
            // Fila 4: Facultad
            gbc.gridx = 0; gbc.gridy = filaActual;
            formPanel.add(new JLabel("Facultad:"), gbc);
            gbc.gridx = 1;
            formPanel.add(txtFacultad, gbc);
            filaActual++;
            
            // Botones
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton btnAgregar = new JButton("Agregar");
            JButton btnLimpiar = new JButton("Limpiar");
            
            btnPanel.add(btnAgregar);
            btnPanel.add(btnLimpiar);
            
            gbc.gridx = 0; gbc.gridy = filaActual;
            gbc.gridwidth = 2;
            formPanel.add(btnPanel, gbc);
            
            panel.add(formPanel, BorderLayout.NORTH);
            
            // Acciones de botones para Director
            btnAgregar.addActionListener(e -> {
                try {
                    int numAyudantes = Integer.parseInt(txtNumAyudantes.getText().trim());
                    String nombre = txtNombre.getText().trim();
                    String apellido = txtApellido.getText().trim();
                    String cedula = txtCedula.getText().trim();
                    String facultad = txtFacultad.getText().trim();
                    
                    if (numAyudantes <= 0) {
                        JOptionPane.showMessageDialog(panel, "El número de ayudantes debe ser mayor a 0", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (nombre.isEmpty() || apellido.isEmpty() || cedula.isEmpty() || facultad.isEmpty()) {
                        JOptionPane.showMessageDialog(panel, "Complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Si no hay pendientes, iniciar nuevo registro múltiple
                    if (ayudantesPendientes[0] == 0) {
                        ayudantesPendientes[0] = numAyudantes;
                    }
                    
                    // Registrar un ayudante
                    Formulario formulario = new Formulario(1, nombre, apellido, cedula, facultad);
                    formulario.setEstado("Pendiente");
                    formulario.setIdProyecto(proyectoSeleccionadoId[0]); // Asignar proyecto al formulario
                    
                    if (formularioDAO.insertar(formulario)) {
                        ayudantesPendientes[0]--;
                        
                        if (ayudantesPendientes[0] > 0) {
                            JOptionPane.showMessageDialog(panel, 
                                "Ayudante registrado exitosamente.\n" +
                                "Faltan " + ayudantesPendientes[0] + " ayudante(s) por registrar.\n" +
                                "Ingrese los datos del siguiente ayudante.", 
                                "Registro Parcial", 
                                JOptionPane.INFORMATION_MESSAGE);
                            // Limpiar solo los campos de datos personales
                            txtNombre.setText("");
                            txtApellido.setText("");
                            txtCedula.setText("");
                            txtNombre.requestFocus();
                        } else {
                            JOptionPane.showMessageDialog(panel, 
                                "¡Todos los ayudantes han sido registrados exitosamente!", 
                                "Registro Completo", 
                                JOptionPane.INFORMATION_MESSAGE);
                            limpiarCamposFormulario(txtNumAyudantes, txtNombre, txtApellido, txtCedula, txtFacultad, cbEstado);
                        }
                        
                        cargarFormulariosEnTabla(panel);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "El número de ayudantes debe ser un valor numérico", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            btnLimpiar.addActionListener(e -> {
                limpiarCamposFormulario(txtNumAyudantes, txtNombre, txtApellido, txtCedula, txtFacultad, cbEstado);
                ayudantesPendientes[0] = 0;
            });
            
            // Cargar proyectos del Director en el ComboBox
            cargarProyectosEnCombo(cbProyecto, proyectoSeleccionadoId);
        }
        
        // Panel de gestión de estado para Jefatura
        if (puedeManejarUsuarios()) {
            JPanel estadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            estadoPanel.setBorder(BorderFactory.createTitledBorder("Gestión de Estados"));
            
            estadoPanel.add(new JLabel("Estado:"));
            estadoPanel.add(cbEstado);
            
            JButton btnActualizarEstado = new JButton("Actualizar Estado");
            estadoPanel.add(btnActualizarEstado);
            
            panel.add(estadoPanel, BorderLayout.NORTH);
            
            // Acción para actualizar estado
            btnActualizarEstado.addActionListener(e -> {
                // Obtener tabla del scrollPane
                Component[] components = panel.getComponents();
                JTable tabla = null;
                for (Component comp : components) {
                    if (comp instanceof JScrollPane) {
                        JViewport viewport = ((JScrollPane) comp).getViewport();
                        if (viewport.getView() instanceof JTable) {
                            tabla = (JTable) viewport.getView();
                            break;
                        }
                    }
                }
                
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
                        JOptionPane.showMessageDialog(panel, "Estado actualizado exitosamente");
                        cargarFormulariosEnTabla(panel);
                    }
                }
            });
        }
        
        // Tabla - siempre con todas las columnas; ocultamos algunas en la vista
        String[] columnas = new String[]{"ID", "N° Ayudantes", "Nombre", "Apellido", "Cédula", "Facultad", "Estado", "IdProyecto", "Proyecto", "Director"};
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
        ocultarColumna(tabla, 7); // Ocultar IdProyecto
        if (!puedeManejarUsuarios()) { // Director no ve Estado
            ocultarColumna(tabla, 6);
        }
        
        // Listener para selección (solo para Jefatura)
        if (puedeManejarUsuarios()) {
            tabla.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                    int fila = tabla.getSelectedRow();
                    cbEstado.setSelectedItem(modelo.getValueAt(fila, 6));
                }
            });
        }
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        cargarFormularios(modelo);
        
        return panel;
    }
    
    private void cargarFormulariosEnTabla(JPanel panel) {
        // Buscar la tabla en el panel
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JViewport viewport = ((JScrollPane) comp).getViewport();
                if (viewport.getView() instanceof JTable) {
                    JTable tabla = (JTable) viewport.getView();
                    DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
                    cargarFormularios(modelo);
                    return;
                }
            }
        }
    }
    
    private void limpiarCamposFormulario(JTextField txtNumAyudantes, JTextField txtNombre, 
                                         JTextField txtApellido, JTextField txtCedula, 
                                         JTextField txtFacultad, JComboBox<String> cbEstado) {
        txtNumAyudantes.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtCedula.setText("");
        txtFacultad.setText("");
        cbEstado.setSelectedIndex(0);
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
                f.getEstado(),
                f.getIdProyecto(),
                nombreProyecto,
                nombreDirector
            });
        }
    }
    
    private void cargarProyectosEnCombo(JComboBox<String> cbProyecto, int[] proyectoSeleccionadoId) {
        cbProyecto.removeAllItems();
        
        if (usuarioActual == null) {
            cbProyecto.addItem("No hay usuario autenticado");
            return;
        }
        
        // Cargar SOLO los proyectos del Director actual
        List<Proyecto> proyectos = proyectoDAO.obtenerPorUsuario(usuarioActual.getId());
        
        for (Proyecto p : proyectos) {
            cbProyecto.addItem(p.getNombre());
        }
        
        // Event listener para capturar el proyecto seleccionado
        cbProyecto.addActionListener(e -> {
            String proyectoSeleccionado = (String) cbProyecto.getSelectedItem();
            
            // Buscar el ID del proyecto seleccionado
            for (Proyecto p : proyectos) {
                if (p.getNombre().equals(proyectoSeleccionado)) {
                    proyectoSeleccionadoId[0] = p.getId();
                    break;
                }
            }
        });
        
        if (cbProyecto.getItemCount() > 0) {
            cbProyecto.setSelectedIndex(0);
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
    
    // ======================== PANEL SOLICITUDES ========================
    
    /**
     * Panel de Solicitudes para Director (solo sus solicitudes)
     */
    private JPanel crearPanelSolicitudesDirector() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de formulario para crear nueva solicitud
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Nueva Solicitud"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Permiso", "Documento"});
        JComboBox<String> cbDirector = new JComboBox<>();
        JTextField txtAsunto = new JTextField(20);
        JTextField txtDetalle = new JTextField(20);
        
        // Cargar lista de Directores
        List<Usuario> directores = usuarioDAO.obtenerTodos().stream()
            .filter(u -> "Director".equals(u.getTipo()))
            .toList();
        
        for (Usuario director : directores) {
            cbDirector.addItem(director.getId() + " - " + director.getNombre() + " " + director.getApellido());
        }
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbTipo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Director:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbDirector, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Asunto:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtAsunto, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Detalle (Permiso/Documento):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDetalle, gbc);
        
        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("✓ Registrar Solicitud");
        JButton btnLimpiar = new JButton("Limpiar");
        
        btnPanel.add(btnAgregar);
        btnPanel.add(btnLimpiar);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);
        
        // Tabla de solicitudes del Director
        String[] columnas = {"ID", "Fecha", "Asunto", "Tipo", "Detalle", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Solicitudes"));
        
        // Eventos
        btnAgregar.addActionListener(e -> {
            String asunto = txtAsunto.getText().trim();
            String detalle = txtDetalle.getText().trim();
            String tipo = (String) cbTipo.getSelectedItem();
            String directorSeleccionado = (String) cbDirector.getSelectedItem();
            
            if (asunto.isEmpty() || detalle.isEmpty() || directorSeleccionado == null) {
                JOptionPane.showMessageDialog(panel, "Complete todos los campos", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Extraer ID del director (formato: "ID - Nombre Apellido")
            int idDirector = Integer.parseInt(directorSeleccionado.split(" - ")[0]);
            
            Solicitud solicitud = new Solicitud(asunto, usuarioActual.getId());
            solicitud.setIdDirector(idDirector);
            
            String codigoPermiso = tipo.equals("Permiso") ? detalle : null;
            String tipoDocumento = tipo.equals("Documento") ? detalle : null;
            
            if (solicitudDAO.insertar(solicitud, tipo, codigoPermiso, tipoDocumento)) {
                // Verificar si hay directores con formularios de ayudantes incompletos
                verificarFormulariosDirectores();
                
                JOptionPane.showMessageDialog(panel, "✓ Solicitud enviada exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtAsunto.setText("");
                txtDetalle.setText("");
                cargarSolicitudesDirector(modelo);
            }
        });
        
        btnLimpiar.addActionListener(e -> {
            txtAsunto.setText("");
            txtDetalle.setText("");
        });
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        cargarSolicitudesDirector(modelo);
        
        return panel;
    }
    
    private void cargarSolicitudesDirector(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        
        if (usuarioActual != null) {
            List<Solicitud> solicitudes = solicitudDAO.buscarPorUsuario(usuarioActual.getId());
            for (Solicitud s : solicitudes) {
                // Determinar detalle según tipo
                String detalle = "";
                if ("Permiso".equals(s.getTipo()) && s.getCodigoPermiso() != null) {
                    detalle = s.getCodigoPermiso();
                } else if ("Documento".equals(s.getTipo()) && s.getTipoDocumento() != null) {
                    detalle = s.getTipoDocumento();
                }
                
                modelo.addRow(new Object[]{
                    s.getIdSolicitud(), 
                    s.getFecha(), 
                    s.getAsunto(), 
                    s.getTipo(),
                    detalle,
                    s.getEstadoEmisionDest()
                });
            }
        }
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
            // Obtener director del proyecto
            Usuario director = usuarioDAO.obtenerPorId(proyecto.getIdUsuario());
            
            if (director != null && "Director".equals(director.getTipo())) {
                // Contar formularios asociados a este proyecto
                List<Formulario> formularios = formularioDAO.obtenerFormulariosPorUsuario(director.getId());
                
                int ayudantesRequeridos = proyecto.getNumeroDeDayudantesDelProyecto();
                int ayudantesRegistrados = formularios.size();
                
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
                    System.out.println("Notificación creada para director: " + director.getNombre());
                }
            }
        }
    }
    

}
