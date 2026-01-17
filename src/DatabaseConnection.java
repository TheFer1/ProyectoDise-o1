import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase para manejar la conexión a la base de datos SQLite
 */
public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:data/data.sqlite";
    private static DatabaseConnection instance;
    private Connection connection;
    
    // Bloque estático para cargar el driver
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver: " + e.getMessage());
        }
    }
    
    // Constructor privado (Singleton)
    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Conexión a la base de datos establecida.");
            inicializarTablas();
        } catch (SQLException e) {
            System.err.println("Error al conectar: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la instancia única de la conexión (Patrón Singleton)
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Obtiene la conexión activa
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión: " + e.getMessage());
        }
        return connection;
    }
    
    /**
     * Inicializa las tablas de la base de datos
     */
    private void inicializarTablas() {
        try (Statement stmt = connection.createStatement()) {
            // Tabla de Personas (legacy)
            stmt.execute("CREATE TABLE IF NOT EXISTS name (" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " first_name TEXT NOT NULL," +
                    " last_name TEXT NOT NULL" +
                    ");");
            
            // Tabla de Usuarios
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " nombre TEXT NOT NULL," +
                    " apellido TEXT NOT NULL," +
                    " correo TEXT NOT NULL UNIQUE," +
                    " contraseña TEXT NOT NULL," +
                    " tipo TEXT NOT NULL DEFAULT 'Usuario'" + // Usuario, Director, Jefatura
                    ");");
            
            // Tabla de Proyectos
            stmt.execute("CREATE TABLE IF NOT EXISTS proyectos (" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " nombre TEXT NOT NULL," +
                    " codigo TEXT NOT NULL UNIQUE," +
                    " descripcion TEXT," +
                    " tipo TEXT," +
                    " fecha_inicio DATE," +
                    " fecha_fin DATE," +
                    " num_ayudantes INTEGER DEFAULT 0," +
                    " id_usuario INTEGER," +
                    " FOREIGN KEY(id_usuario) REFERENCES usuarios(id)" +
                    ");");;
            
            // Tabla de Formularios
            stmt.execute("CREATE TABLE IF NOT EXISTS formularios (" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " numero_ayudantes INTEGER," +
                    " nombre_ayudante TEXT NOT NULL," +
                    " apellido_ayudante TEXT NOT NULL," +
                    " cedula TEXT NOT NULL," +
                    " facultad TEXT," +
                    " estado TEXT DEFAULT 'Pendiente'" +
                    ");");
            
            // Tabla de Solicitudes
            stmt.execute("CREATE TABLE IF NOT EXISTS solicitudes (" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " fecha DATE NOT NULL," +
                    " asunto TEXT NOT NULL," +
                    " estado TEXT DEFAULT 'Pendiente'," +
                    " id_usuario INTEGER," +
                    " id_director INTEGER," +
                    " tipo TEXT," + // Permiso, Documento
                    " codigo_permiso TEXT," +
                    " tipo_documento TEXT," +
                    " FOREIGN KEY (id_usuario) REFERENCES usuarios(id)," +
                    " FOREIGN KEY (id_director) REFERENCES usuarios(id)" +
                    ");");
            
            // Tabla de Notificaciones
            stmt.execute("CREATE TABLE IF NOT EXISTS notificaciones (" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " fecha DATE NOT NULL," +
                    " informacion TEXT NOT NULL," +
                    " id_usuario INTEGER NOT NULL," +
                    " FOREIGN KEY (id_usuario) REFERENCES usuarios(id)" +
                    ");");
            
            // Tabla de Planificación
            stmt.execute("CREATE TABLE IF NOT EXISTS planificacion (" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " maximo_ayudantes INTEGER NOT NULL," +
                    " ayudantes_registrados INTEGER DEFAULT 0" +
                    ");");
            
            System.out.println("Todas las tablas han sido inicializadas.");
            
            // Migrar esquema: agregar columna id_director si no existe
            migrarEsquemaSolicitudes();
            migrarEsquemaNotificaciones();
            
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }
    
    /**
     * Migra el esquema de solicitudes para agregar id_director si no existe
     */
    private void migrarEsquemaSolicitudes() {
        try (Statement stmt = connection.createStatement()) {
            // Verificar si la columna id_director existe
            var rs = stmt.executeQuery("PRAGMA table_info(solicitudes)");
            boolean tieneIdDirector = false;
            
            while (rs.next()) {
                if ("id_director".equals(rs.getString("name"))) {
                    tieneIdDirector = true;
                    break;
                }
            }
            
            // Si no existe, agregarla
            if (!tieneIdDirector) {
                stmt.execute("ALTER TABLE solicitudes ADD COLUMN id_director INTEGER");
                System.out.println("✓ Columna id_director agregada a solicitudes");
            }
        } catch (SQLException e) {
            System.err.println("Error al migrar esquema: " + e.getMessage());
        }
    }
    
    /**
     * Migra el esquema de notificaciones para cambiar destinatario por id_usuario
     */
    private void migrarEsquemaNotificaciones() {
        try (Statement stmt = connection.createStatement()) {
            // Verificar estructura de tabla notificaciones
            var rs = stmt.executeQuery("PRAGMA table_info(notificaciones)");
            boolean tieneIdUsuario = false;
            boolean tieneDestinatario = false;
            
            while (rs.next()) {
                String columnName = rs.getString("name");
                if ("id_usuario".equals(columnName)) {
                    tieneIdUsuario = true;
                }
                if ("destinatario".equals(columnName)) {
                    tieneDestinatario = true;
                }
            }
            
            // Si tiene destinatario pero no id_usuario, necesita migración completa
            if (tieneDestinatario && !tieneIdUsuario) {
                // SQLite no permite DROP COLUMN, necesitamos recrear la tabla
                stmt.execute("ALTER TABLE notificaciones RENAME TO notificaciones_old");
                
                stmt.execute("CREATE TABLE notificaciones (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " fecha DATE NOT NULL," +
                        " informacion TEXT NOT NULL," +
                        " id_usuario INTEGER NOT NULL," +
                        " FOREIGN KEY (id_usuario) REFERENCES usuarios(id)" +
                        ")");
                
                // No copiamos datos antiguos porque destinatario era TEXT y ahora necesitamos INTEGER
                stmt.execute("DROP TABLE notificaciones_old");
                System.out.println("✓ Tabla notificaciones migrada a id_usuario");
            } else if (!tieneIdUsuario && !tieneDestinatario) {
                // Tabla nueva, agregar id_usuario
                stmt.execute("ALTER TABLE notificaciones ADD COLUMN id_usuario INTEGER NOT NULL DEFAULT 0");
                System.out.println("✓ Columna id_usuario agregada a notificaciones");
            }
        } catch (SQLException e) {
            System.err.println("Error al migrar notificaciones: " + e.getMessage());
        }
    }
    
    /**
     * Cierra la conexión
     */
    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}
