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
        String titulo = "Sistema de Gestión De Solicitudes";
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
            tabbedPane.addTab("📋 Solicitudes", crearPanelSolicitudesJefatura());
        }
        
        // Solo Director puede ver/editar sus proyectos
        if (puedeManejarProyectos()) {
            tabbedPane.addTab("📁 Mis Proyectos", crearPanelProyectos());
            tabbedPane.addTab("📨 Enviar Solicitud", crearPanelEnviarSolicitudDirector());
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
    
    // ======================== PANEL SOLICITUDES ========================

    /**
     * Panel de Solicitudes para Jefatura: cambio de estado y notificación
     */
    private JPanel crearPanelSolicitudesJefatura() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Fecha", "Asunto", "Tipo", "Detalle", "Estado", "Solicitante", "IdUsuario"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Solicitudes"));

        ocultarColumna(tabla, 7); // IdUsuario oculto

        JPanel accionesPanel = new JPanel(new GridBagLayout());
        accionesPanel.setBorder(BorderFactory.createTitledBorder("Acciones"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Pendiente", "Aprobado", "Rechazado"});
        JButton btnActualizarEstado = new JButton("Actualizar Estado");

        gbc.gridx = 0; gbc.gridy = 0;
        accionesPanel.add(new JLabel("Estado:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        accionesPanel.add(cbEstado, gbc);
        gbc.gridx = 2; gbc.gridy = 0;
        accionesPanel.add(btnActualizarEstado, gbc);

        JLabel lblMensaje = new JLabel("Notificación personalizada:");
        JTextArea txtMensaje = new JTextArea(3, 25);
        txtMensaje.setLineWrap(true);
        txtMensaje.setWrapStyleWord(true);
        JScrollPane scrollMensaje = new JScrollPane(txtMensaje);
        JButton btnEnviarNotificacion = new JButton("Enviar Notificación");
        JButton btnActualizarSolicitudes = new JButton("🔄 Actualizar");

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 3;
        accionesPanel.add(lblMensaje, gbc);
        gbc.gridy = 2;
        accionesPanel.add(scrollMensaje, gbc);
        gbc.gridy = 3;
        accionesPanel.add(btnEnviarNotificacion, gbc);
        gbc.gridy = 4;
        accionesPanel.add(btnActualizarSolicitudes, gbc);

        btnActualizarEstado.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(panel, "Seleccione una solicitud", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = (int) modelo.getValueAt(fila, 0);
            String nuevoEstado = (String) cbEstado.getSelectedItem();

            if (solicitudDAO.actualizarEstado(id, nuevoEstado)) {
                JOptionPane.showMessageDialog(panel, "Estado actualizado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarSolicitudesJefatura(modelo);
            } else {
                JOptionPane.showMessageDialog(panel, "No se pudo actualizar el estado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEnviarNotificacion.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(panel, "Seleccione una solicitud", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String mensaje = txtMensaje.getText().trim();
            if (mensaje.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Escriba el mensaje de la notificación", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idUsuario = (int) modelo.getValueAt(fila, 7);
            Notificacion notificacion = new Notificacion(mensaje, idUsuario);
            if (notificacionDAO.insertar(notificacion)) {
                JOptionPane.showMessageDialog(panel, "Notificación enviada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtMensaje.setText("");
            } else {
                JOptionPane.showMessageDialog(panel, "No se pudo enviar la notificación", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                String estadoActual = (String) modelo.getValueAt(tabla.getSelectedRow(), 5);
                cbEstado.setSelectedItem(estadoActual);
            }
        });
        
        btnActualizarSolicitudes.addActionListener(e -> cargarSolicitudesJefatura(modelo));

        panel.add(accionesPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        cargarSolicitudesJefatura(modelo);
        return panel;
    }

    private void cargarSolicitudesJefatura(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Solicitud> solicitudes = solicitudDAO.obtenerTodos();

        for (Solicitud s : solicitudes) {
            String detalle = "";
            if ("Permiso".equals(s.getTipo()) && s.getCodigoPermiso() != null) {
                detalle = s.getCodigoPermiso();
            } else if ("Documento".equals(s.getTipo()) && s.getTipoDocumento() != null) {
                detalle = s.getTipoDocumento();
            }

            String solicitante = "";
            if (s.getIdUsuario() > 0) {
                Usuario u = usuarioDAO.obtenerPorId(s.getIdUsuario());
                if (u != null) {
                    solicitante = u.getNombre() + " " + u.getApellido();
                }
            }

            modelo.addRow(new Object[]{
                s.getIdSolicitud(),
                s.getFecha(),
                s.getAsunto(),
                s.getTipo(),
                detalle,
                s.getEstadoEmisionDest(),
                solicitante,
                s.getIdUsuario()
            });
        }
    }
    
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
            
            // Crear solicitud especializada según el tipo
            Solicitud solicitud;
            if (tipo.equals("Permiso")) {
                solicitud = new SolicitarPermiso(asunto, usuarioActual.getId(), detalle);
            } else {
                solicitud = new SolicitarDocumento(asunto, usuarioActual.getId(), detalle);
            }
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
    
    // ======================== PANEL ENVIAR SOLICITUD (DIRECTOR) ========================
    
    /**
     * Panel para que el Director envíe solicitudes
     * Usa el patrón Controlador para separar lógica de UI
     */
    private JPanel crearPanelEnviarSolicitudDirector() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Crear controlador (separación de responsabilidades)
        Director director = new Director(usuarioActual.getId(), usuarioActual.getNombre(),
            usuarioActual.getApellido(), usuarioActual.getCorreo(), usuarioActual.getContraseña());
        ControladorSolicitudes controlador = new ControladorSolicitudes(director);
        
        // Panel de instrucciones
        JPanel instruccionesPanel = new JPanel(new BorderLayout());
        instruccionesPanel.setBorder(BorderFactory.createTitledBorder("📋 Instrucciones"));
        JLabel lblInstrucciones = new JLabel(
            "<html><b>Para enviar una solicitud a Jefatura, primero complete el formulario de ayudantes.</b><br>" +
            "Luego podrá enviar su solicitud con la información del asunto y detalles.</html>"
        );
        lblInstrucciones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        instruccionesPanel.add(lblInstrucciones, BorderLayout.CENTER);
        
        // Panel principal con dos secciones
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // ===== SECCIÓN 1: FORMULARIO DE AYUDANTES =====
        JPanel formularioPanel = new JPanel(new BorderLayout(5, 5));
        formularioPanel.setBorder(BorderFactory.createTitledBorder("Paso 1: Formulario de Ayudantes"));
        
        JPanel formAyudantesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JSpinner spnNumAyudantes = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JTextField txtNombreAyudante = new JTextField(15);
        JTextField txtApellidoAyudante = new JTextField(15);
        JTextField txtCedula = new JTextField(15);
        JTextField txtFacultad = new JTextField(15);
        JComboBox<String> cbProyecto = new JComboBox<>();
        
        // Cargar proyectos del director usando controlador
        List<Proyecto> proyectosDirector = controlador.obtenerProyectos();
        for (Proyecto p : proyectosDirector) {
            cbProyecto.addItem(p.getId() + " - " + p.getNombre());
        }
        
        gbc.gridx = 0; gbc.gridy = 0;
        formAyudantesPanel.add(new JLabel("Proyecto:"), gbc);
        gbc.gridx = 1;
        formAyudantesPanel.add(cbProyecto, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formAyudantesPanel.add(new JLabel("N° de Ayudantes:"), gbc);
        gbc.gridx = 1;
        formAyudantesPanel.add(spnNumAyudantes, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formAyudantesPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        formAyudantesPanel.add(txtNombreAyudante, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formAyudantesPanel.add(new JLabel("Apellido:"), gbc);
        gbc.gridx = 1;
        formAyudantesPanel.add(txtApellidoAyudante, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formAyudantesPanel.add(new JLabel("Cédula:"), gbc);
        gbc.gridx = 1;
        formAyudantesPanel.add(txtCedula, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formAyudantesPanel.add(new JLabel("Facultad:"), gbc);
        gbc.gridx = 1;
        formAyudantesPanel.add(txtFacultad, gbc);
        
        JButton btnGuardar = new JButton("✓ Guardar");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        formAyudantesPanel.add(btnGuardar, gbc);
        
        formularioPanel.add(formAyudantesPanel, BorderLayout.CENTER);
        
        // ===== SECCIÓN 2: ENVIAR SOLICITUD =====
        JPanel solicitudPanel = new JPanel(new BorderLayout(5, 5));
        solicitudPanel.setBorder(BorderFactory.createTitledBorder("Paso 2: Enviar Solicitud a Jefatura"));
        
        JPanel formSolicitudPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        
        JComboBox<String> cbTipoSolicitud = new JComboBox<>(new String[]{"Formulario de Ayudantes", "Permiso", "Documento"});
        JTextField txtAsunto = new JTextField(20);
        JTextField txtDetalle = new JTextField(20);
        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        
        gbc2.gridx = 0; gbc2.gridy = 0;
        formSolicitudPanel.add(new JLabel("Tipo de Solicitud:"), gbc2);
        gbc2.gridx = 1;
        formSolicitudPanel.add(cbTipoSolicitud, gbc2);
        
        gbc2.gridx = 0; gbc2.gridy = 1;
        formSolicitudPanel.add(new JLabel("Asunto:"), gbc2);
        gbc2.gridx = 1;
        formSolicitudPanel.add(txtAsunto, gbc2);
        
        gbc2.gridx = 0; gbc2.gridy = 2;
        formSolicitudPanel.add(new JLabel("Detalle (Código/Tipo):"), gbc2);
        gbc2.gridx = 1;
        formSolicitudPanel.add(txtDetalle, gbc2);
        
        gbc2.gridx = 0; gbc2.gridy = 3;
        gbc2.anchor = GridBagConstraints.NORTH;
        formSolicitudPanel.add(new JLabel("Descripción:"), gbc2);
        gbc2.gridx = 1;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.weighty = 1.0;
        formSolicitudPanel.add(scrollDescripcion, gbc2);
        
        // El envío se controla con el mismo botón "Guardar" (Paso 1 obligatorio)
        gbc2.gridx = 0; gbc2.gridy = 4;
        gbc2.gridwidth = 2;
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.weighty = 0;
        formSolicitudPanel.add(new JLabel(" "), gbc2); // Espaciador
        
        solicitudPanel.add(formSolicitudPanel, BorderLayout.CENTER);
        
        // Agregar ambas secciones al panel principal
        mainPanel.add(formularioPanel);
        mainPanel.add(solicitudPanel);
        
        // ===== TABLA DE SOLICITUDES =====
        // Panel contenedor para la tabla
        JPanel tablaPanel = new JPanel(new BorderLayout());
        tablaPanel.setPreferredSize(new Dimension(0, 200)); // Altura fija de 200px
        
        // Tabla de solicitudes
        String[] columnasSol = {"ID", "Fecha", "Asunto", "Estado"};
        DefaultTableModel modeloSol = new DefaultTableModel(columnasSol, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaSolicitudes = new JTable(modeloSol);
        tablaSolicitudes.setRowHeight(25);
        tablaSolicitudes.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollSol = new JScrollPane(tablaSolicitudes);
        scrollSol.setBorder(BorderFactory.createTitledBorder("📋 Solicitudes Enviadas"));
        tablaPanel.add(scrollSol, BorderLayout.CENTER);
        
        // Método para cargar solicitudes usando controlador
        Runnable cargarSolicitudes = () -> {
            modeloSol.setRowCount(0);
            List<Solicitud> solicitudes = controlador.obtenerSolicitudes();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Solicitud s : solicitudes) {
                modeloSol.addRow(new Object[]{
                    s.getIdSolicitud(),
                    s.getFecha() != null ? sdf.format(s.getFecha()) : "",
                    s.getAsunto(),
                    s.getEstadoEmisionDest()
                });
            }
        };
        
        // Eventos
        // Evento para guardar formulario usando controlador
        btnGuardar.addActionListener(e -> {
            if (cbProyecto.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(panel, "Debe seleccionar un proyecto", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String proyectoSel = (String) cbProyecto.getSelectedItem();
            int idProyecto = Integer.parseInt(proyectoSel.split(" - ")[0]);
            int numAyudantes = (int) spnNumAyudantes.getValue();
            String nombre = txtNombreAyudante.getText().trim();
            String apellido = txtApellidoAyudante.getText().trim();
            String cedula = txtCedula.getText().trim();
            String facultad = txtFacultad.getText().trim();

            // Usar controlador para registrar formulario
            ResultadoOperacion resultado = controlador.registrarFormulario(
                idProyecto, numAyudantes, nombre, apellido, cedula, facultad
            );

            if (!resultado.isExitoso()) {
                int tipoMensaje = resultado.esError() ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE;
                JOptionPane.showMessageDialog(panel, resultado.getMensaje(), "Error", tipoMensaje);
                return;
            }

            // Limpiar campos tras guardar
            txtNombreAyudante.setText("");
            txtApellidoAyudante.setText("");
            txtCedula.setText("");
            txtFacultad.setText("");
            spnNumAyudantes.setValue(1);

            // Verificar y notificar si faltan ayudantes solo para este proyecto
            verificarFormulariosProyecto(idProyecto);

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(panel, resultado.getMensaje(), "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Paso 2 opcional: envío de solicitud
            String tipoSolicitud = (String) cbTipoSolicitud.getSelectedItem();
            String asunto = txtAsunto.getText().trim();
            String detalle = txtDetalle.getText().trim();
            String descripcion = txtDescripcion.getText().trim();

            if (asunto.isEmpty()) {
                return; // Ya se guardó el formulario, no enviar solicitud
            }

            if ("Formulario de Ayudantes".equals(tipoSolicitud)) {
                // Usar controlador para enviar solicitud
                ResultadoOperacion resultadoEnvio = controlador.enviarSolicitud(asunto, descripcion);
                
                int tipoMensaje = resultadoEnvio.isExitoso() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
                JOptionPane.showMessageDialog(panel, resultadoEnvio.getMensaje(), 
                    resultadoEnvio.isExitoso() ? "Éxito" : "Advertencia", tipoMensaje);
                
                if (resultadoEnvio.isExitoso()) {
                    txtAsunto.setText("");
                    txtDetalle.setText("");
                    txtDescripcion.setText("");
                    cargarSolicitudes.run();
                }
            } else if ("Permiso".equals(tipoSolicitud)) {
                if (detalle.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "No se envió la solicitud de permiso por faltar el código.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                SolicitarPermiso solicitudPermiso = new SolicitarPermiso(asunto, usuarioActual.getId(), detalle);

                if (solicitudPermiso.puedeSerProcesado() && solicitudDAO.insertar(solicitudPermiso)) {
                    JOptionPane.showMessageDialog(panel,
                        "✓ Solicitud de permiso enviada",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                    txtAsunto.setText("");
                    txtDetalle.setText("");
                    txtDescripcion.setText("");
                    cargarSolicitudes.run();
                } else {
                    JOptionPane.showMessageDialog(panel, "No se pudo enviar la solicitud de permiso", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if ("Documento".equals(tipoSolicitud)) {
                if (detalle.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "No se envió la solicitud de documento por faltar el tipo de documento.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                SolicitarDocumento solicitudDocumento = new SolicitarDocumento(asunto, usuarioActual.getId(), detalle);

                if (solicitudDocumento.puedeSerProcesado() && solicitudDAO.insertar(solicitudDocumento)) {
                    JOptionPane.showMessageDialog(panel,
                        "✓ Solicitud de documento enviada",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                    txtAsunto.setText("");
                    txtDetalle.setText("");
                    txtDescripcion.setText("");
                    cargarSolicitudes.run();
                } else {
                    JOptionPane.showMessageDialog(panel, "No se pudo enviar la solicitud de documento", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Cargar datos iniciales
        cargarSolicitudes.run();
        
        // Ensamblar panel final
        panel.add(instruccionesPanel, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);
        panel.add(tablaPanel, BorderLayout.SOUTH);
        
        return panel;
    }

}
