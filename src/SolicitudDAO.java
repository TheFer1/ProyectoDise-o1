import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre Solicitudes
 */
public class SolicitudDAO {
    private Connection connection;
    
    public SolicitudDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Insertar una nueva solicitud (sobrecargado)
     */
    public boolean insertar(Solicitud solicitud) {
        return insertar(solicitud, solicitud.getTipo(), solicitud.getCodigoPermiso(), solicitud.getTipoDocumento());
    }
    
    /**
     * Insertar una nueva solicitud con parámetros específicos
     */
    public boolean insertar(Solicitud solicitud, String tipo, String codigoPermiso, String tipoDocumento) {
        String sql = "INSERT INTO solicitudes(fecha, asunto, estado, id_usuario, id_director, tipo, codigo_permiso, tipo_documento) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(solicitud.getFecha().getTime()));
            pstmt.setString(2, solicitud.getAsunto());
            pstmt.setString(3, solicitud.getEstadoEmisionDest());
            pstmt.setInt(4, solicitud.getIdUsuario());
            pstmt.setInt(5, solicitud.getIdDirector());
            pstmt.setString(6, tipo);
            pstmt.setString(7, codigoPermiso);
            pstmt.setString(8, tipoDocumento);
            pstmt.executeUpdate();
            System.out.println("Solicitud insertada: " + solicitud.getAsunto());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar solicitud: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener todas las solicitudes
     */
    public List<Solicitud> obtenerTodos() {
        List<Solicitud> solicitudes = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Solicitud solicitud = new Solicitud(
                    rs.getInt("id"),
                    rs.getDate("fecha"),
                    rs.getString("asunto"),
                    rs.getString("estado"),
                    rs.getInt("id_usuario")
                );
                solicitudes.add(solicitud);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitudes: " + e.getMessage());
        }
        
        return solicitudes;
    }
    
    /**
     * Obtener todas las solicitudes (alias para compatibilidad)
     */
    public List<Solicitud> obtenerTodas() {
        return obtenerTodos();
    }
    
    /**
     * Obtener solicitud por ID
     */
    public Solicitud obtenerPorId(int id) {
        String sql = "SELECT * FROM solicitudes WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Solicitud(
                    rs.getInt("id"),
                    rs.getDate("fecha"),
                    rs.getString("asunto"),
                    rs.getString("estado"),
                    rs.getInt("id_usuario"),
                    rs.getInt("id_director"),
                    rs.getString("tipo"),
                    rs.getString("codigo_permiso"),
                    rs.getString("tipo_documento")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitud: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Actualizar solicitud completa
     */
    public boolean actualizar(Solicitud solicitud) {
        String sql = "UPDATE solicitudes SET fecha = ?, asunto = ?, estado = ?, id_usuario = ?, id_director = ?, tipo = ?, codigo_permiso = ?, tipo_documento = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(solicitud.getFecha().getTime()));
            pstmt.setString(2, solicitud.getAsunto());
            pstmt.setString(3, solicitud.getEstadoEmisionDest());
            pstmt.setInt(4, solicitud.getIdUsuario());
            pstmt.setInt(5, solicitud.getIdDirector());
            pstmt.setString(6, solicitud.getTipo());
            pstmt.setString(7, solicitud.getCodigoPermiso());
            pstmt.setString(8, solicitud.getTipoDocumento());
            pstmt.setInt(9, solicitud.getIdSolicitud());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar solicitud: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualizar estado de solicitud
     */
    public boolean actualizarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE solicitudes SET estado = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Eliminar solicitud
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM solicitudes WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar solicitud: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Buscar solicitudes por estado
     */
    public List<Solicitud> buscarPorEstado(String estado) {
        List<Solicitud> solicitudes = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes WHERE estado = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, estado);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Solicitud solicitud = new Solicitud(
                    rs.getInt("id"),
                    rs.getDate("fecha"),
                    rs.getString("asunto"),
                    rs.getString("estado"),
                    rs.getInt("id_usuario"),
                    rs.getInt("id_director"),
                    rs.getString("tipo"),
                    rs.getString("codigo_permiso"),
                    rs.getString("tipo_documento")
                );
                solicitudes.add(solicitud);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar solicitudes por estado: " + e.getMessage());
        }
        
        return solicitudes;
    }
    
    /**
     * Buscar solicitudes por usuario
     */
    public List<Solicitud> obtenerPorUsuario(int idUsuario) {
        List<Solicitud> solicitudes = new ArrayList<>();
        String sql = "SELECT * FROM solicitudes WHERE id_usuario = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Solicitud solicitud = new Solicitud(
                    rs.getInt("id"),
                    rs.getDate("fecha"),
                    rs.getString("asunto"),
                    rs.getString("estado"),
                    rs.getInt("id_usuario"),
                    rs.getInt("id_director"),
                    rs.getString("tipo"),
                    rs.getString("codigo_permiso"),
                    rs.getString("tipo_documento")
                );
                solicitudes.add(solicitud);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar solicitudes por usuario: " + e.getMessage());
        }
        
        return solicitudes;
    }
    
    /**
     * Buscar solicitudes por usuario (alias anterior)
     */
    public List<Solicitud> buscarPorUsuario(int idUsuario) {
        return obtenerPorUsuario(idUsuario);
    }
}
