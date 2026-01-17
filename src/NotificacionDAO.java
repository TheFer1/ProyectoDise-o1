import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre Notificaciones
 */
public class NotificacionDAO {
    private Connection connection;
    
    public NotificacionDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Insertar una nueva notificación
     */
    public boolean insertar(Notificacion notificacion) {
        String sql = "INSERT INTO notificaciones(fecha, informacion, id_usuario) VALUES(?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(notificacion.getFecha().getTime()));
            pstmt.setString(2, notificacion.getInformacion());
            pstmt.setInt(3, notificacion.getIdUsuario());
            pstmt.executeUpdate();
            System.out.println("Notificación insertada para usuario ID: " + notificacion.getIdUsuario());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar notificación: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener todas las notificaciones
     */
    public List<Notificacion> obtenerTodas() {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM notificaciones ORDER BY fecha DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Notificacion notificacion = new Notificacion(
                    rs.getInt("id"),
                    rs.getDate("fecha"),
                    rs.getString("informacion"),
                    rs.getInt("id_usuario")
                );
                notificaciones.add(notificacion);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener notificaciones: " + e.getMessage());
        }
        
        return notificaciones;
    }
    
    /**
     * Obtener notificaciones por usuario
     */
    public List<Notificacion> obtenerPorUsuario(int idUsuario) {
        List<Notificacion> notificaciones = new ArrayList<>();
        String sql = "SELECT * FROM notificaciones WHERE id_usuario = ? ORDER BY fecha DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Notificacion notificacion = new Notificacion(
                    rs.getInt("id"),
                    rs.getDate("fecha"),
                    rs.getString("informacion"),
                    rs.getInt("id_usuario")
                );
                notificaciones.add(notificacion);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener notificaciones: " + e.getMessage());
        }
        
        return notificaciones;
    }
    
    /**
     * Eliminar notificación
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM notificaciones WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar notificación: " + e.getMessage());
            return false;
        }
    }
}
