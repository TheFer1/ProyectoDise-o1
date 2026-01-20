import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre Formularios
 */
public class FormularioDAO {
    private Connection connection;
    
    public FormularioDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Insertar un nuevo formulario
     */
    public boolean insertar(Formulario formulario) {
        String sql = "INSERT INTO formularios(numero_ayudantes, nombre_ayudante, apellido_ayudante, cedula, facultad, estado, id_proyecto) VALUES(?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, formulario.getNumeroDeAyudantes());
            pstmt.setString(2, formulario.getNombreDelAyudante());
            pstmt.setString(3, formulario.getApellidoDelAyudante());
            pstmt.setString(4, formulario.getCedula());
            pstmt.setString(5, formulario.getFacultad());
            pstmt.setString(6, formulario.getEstado());
            pstmt.setInt(7, formulario.getIdProyecto());
            pstmt.executeUpdate();
            System.out.println("Formulario insertado para: " + formulario.getNombreDelAyudante());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar formulario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener todos los formularios
     */
    public List<Formulario> obtenerTodos() {
        List<Formulario> formularios = new ArrayList<>();
        String sql = "SELECT * FROM formularios ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Formulario formulario = new Formulario(
                    rs.getInt("id"),
                    rs.getInt("numero_ayudantes"),
                    rs.getString("nombre_ayudante"),
                    rs.getString("apellido_ayudante"),
                    rs.getString("cedula"),
                    rs.getString("facultad"),
                    rs.getString("estado")
                );
                formulario.setIdProyecto(rs.getInt("id_proyecto"));
                formularios.add(formulario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener formularios: " + e.getMessage());
        }
        
        return formularios;
    }
    
    /**
     * Obtener formulario por ID
     */
    public Formulario obtenerPorId(int id) {
        String sql = "SELECT * FROM formularios WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Formulario formulario = new Formulario(
                    rs.getInt("id"),
                    rs.getInt("numero_ayudantes"),
                    rs.getString("nombre_ayudante"),
                    rs.getString("apellido_ayudante"),
                    rs.getString("cedula"),
                    rs.getString("facultad"),
                    rs.getString("estado")
                );
                formulario.setIdProyecto(rs.getInt("id_proyecto"));
                return formulario;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener formulario: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Actualizar formulario
     */
    public boolean actualizar(Formulario formulario) {
        String sql = "UPDATE formularios SET numero_ayudantes = ?, nombre_ayudante = ?, apellido_ayudante = ?, cedula = ?, facultad = ?, estado = ?, id_proyecto = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, formulario.getNumeroDeAyudantes());
            pstmt.setString(2, formulario.getNombreDelAyudante());
            pstmt.setString(3, formulario.getApellidoDelAyudante());
            pstmt.setString(4, formulario.getCedula());
            pstmt.setString(5, formulario.getFacultad());
            pstmt.setString(6, formulario.getEstado());
            pstmt.setInt(7, formulario.getIdProyecto());
            pstmt.setInt(8, formulario.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar formulario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Eliminar formulario
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM formularios WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar formulario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Buscar formularios por estado
     */
    public List<Formulario> buscarPorEstado(String estado) {
        List<Formulario> formularios = new ArrayList<>();
        String sql = "SELECT * FROM formularios WHERE estado = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, estado);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Formulario formulario = new Formulario(
                    rs.getInt("id"),
                    rs.getInt("numero_ayudantes"),
                    rs.getString("nombre_ayudante"),
                    rs.getString("apellido_ayudante"),
                    rs.getString("cedula"),
                    rs.getString("facultad"),
                    rs.getString("estado")
                );
                formulario.setIdProyecto(rs.getInt("id_proyecto"));
                formularios.add(formulario);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar formularios: " + e.getMessage());
        }
        
        return formularios;
    }
    
    /**
     * Obtener formularios de un usuario específico (por sus proyectos)
     */
    public List<Formulario> obtenerFormulariosPorUsuario(int idUsuario) {
        List<Formulario> formularios = new ArrayList<>();
        String sql = "SELECT f.* FROM formularios f " +
                     "INNER JOIN proyectos p ON f.id_proyecto = p.id " +
                     "WHERE p.id_usuario = ? " +
                     "ORDER BY f.id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Formulario formulario = new Formulario(
                    rs.getInt("id"),
                    rs.getInt("numero_ayudantes"),
                    rs.getString("nombre_ayudante"),
                    rs.getString("apellido_ayudante"),
                    rs.getString("cedula"),
                    rs.getString("facultad"),
                    rs.getString("estado")
                );
                formulario.setIdProyecto(rs.getInt("id_proyecto"));
                formularios.add(formulario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener formularios del usuario: " + e.getMessage());
        }
        
        return formularios;
    }
    public List<Formulario> buscarPorProyecto(int idProyecto) {
    List<Formulario> formularios = new ArrayList<>();
    String sql = "SELECT * FROM formularios WHERE id_proyecto = ? ORDER BY id";
    
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, idProyecto);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Formulario formulario = new Formulario(
                rs.getInt("id"),
                rs.getInt("numero_ayudantes"),
                rs.getString("nombre_ayudante"),
                rs.getString("apellido_ayudante"),
                rs.getString("cedula"),
                rs.getString("facultad"),
                rs.getString("estado")
            );
            formulario.setIdProyecto(rs.getInt("id_proyecto"));
            formularios.add(formulario);
        }
    } catch (SQLException e) {
        System.err.println("Error al buscar formularios por proyecto: " + e.getMessage());
    }
    
    return formularios;
}
}
