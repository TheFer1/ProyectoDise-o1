import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestionar operaciones de Avances
 */
public class AvanceDAO {
    private Connection conexion;
    
    public AvanceDAO() {
        this.conexion = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Inserta un nuevo avance
     */
    public boolean insertar(Avance avance) {
        String sql = "INSERT INTO avances (idProyecto, idDirector, descripcion, nombreArchivo, rutaArchivo, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, avance.getIdProyecto());
            pstmt.setInt(2, avance.getIdDirector());
            pstmt.setString(3, avance.getDescripcion());
            pstmt.setString(4, avance.getNombreArchivo());
            pstmt.setString(5, avance.getRutaArchivo());
            pstmt.setString(6, avance.getEstado());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar avance: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene un avance por ID
     */
    public Avance obtenerPorId(int idAvance) {
        String sql = "SELECT * FROM avances WHERE idAvance = ?";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idAvance);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return construirAvance(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener avance: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene todos los avances
     */
    public List<Avance> obtenerTodos() {
        List<Avance> avances = new ArrayList<>();
        String sql = "SELECT * FROM avances ORDER BY fechaCarga DESC";
        
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                avances.add(construirAvance(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener avances: " + e.getMessage());
        }
        
        return avances;
    }
    
    /**
     * Obtiene avances por proyecto
     */
    public List<Avance> obtenerPorProyecto(int idProyecto) {
        List<Avance> avances = new ArrayList<>();
        String sql = "SELECT * FROM avances WHERE idProyecto = ? ORDER BY fechaCarga DESC";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idProyecto);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    avances.add(construirAvance(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener avances del proyecto: " + e.getMessage());
        }
        
        return avances;
    }
    
    /**
     * Obtiene avances por director
     */
    public List<Avance> obtenerPorDirector(int idDirector) {
        List<Avance> avances = new ArrayList<>();
        String sql = "SELECT * FROM avances WHERE idDirector = ? ORDER BY fechaCarga DESC";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idDirector);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    avances.add(construirAvance(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener avances del director: " + e.getMessage());
        }
        
        return avances;
    }
    
    /**
     * Actualiza el estado de un avance
     */
    public boolean actualizarEstado(int idAvance, String nuevoEstado) {
        String sql = "UPDATE avances SET estado = ? WHERE idAvance = ?";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, idAvance);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un avance
     */
    public boolean eliminar(int idAvance) {
        String sql = "DELETE FROM avances WHERE idAvance = ?";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setInt(1, idAvance);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar avance: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene avances por estado
     */
    public List<Avance> obtenerPorEstado(String estado) {
        List<Avance> avances = new ArrayList<>();
        String sql = "SELECT * FROM avances WHERE estado = ? ORDER BY fechaCarga DESC";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, estado);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    avances.add(construirAvance(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener avances por estado: " + e.getMessage());
        }
        
        return avances;
    }
    
    /**
     * Actualiza el archivo firmado de un avance
     */
    public boolean actualizarArchivoFirmado(int idAvance, String archivoFirmado) {
        String sql = "UPDATE avances SET archivoFirmado = ?, estado = 'Revisado' WHERE idAvance = ?";
        
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, archivoFirmado);
            pstmt.setInt(2, idAvance);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar archivo firmado: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Método auxiliar para construir un objeto Avance desde un ResultSet
     */
    private Avance construirAvance(ResultSet rs) throws SQLException {
        Avance avance = new Avance(
            rs.getInt("idAvance"),
            rs.getInt("idProyecto"),
            rs.getInt("idDirector"),
            rs.getString("descripcion"),
            rs.getString("nombreArchivo"),
            rs.getString("rutaArchivo"),
            rs.getTimestamp("fechaCarga"),
            rs.getString("estado")
        );
        
        // Obtener archivo firmado si existe
        String archivoFirmado = rs.getString("archivoFirmado");
        if (archivoFirmado != null) {
            avance.setArchivoFirmado(archivoFirmado);
        }
        
        return avance;
    }
}
