import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre Proyectos
 */
public class ProyectoDAO {
    private Connection connection;
    
    public ProyectoDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Insertar un nuevo proyecto
     */
    public boolean insertar(Proyecto proyecto) {
        String sql = "INSERT INTO proyectos(nombre, codigo, descripcion, tipo, fecha_inicio, fecha_fin, num_ayudantes, id_usuario) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, proyecto.getNombre());
            pstmt.setString(2, proyecto.getCodigo());
            pstmt.setString(3, proyecto.getDescripcion());
            pstmt.setString(4, proyecto.getTipo());
            pstmt.setDate(5, proyecto.getFechaInicio() != null ? new java.sql.Date(proyecto.getFechaInicio().getTime()) : null);
            pstmt.setDate(6, proyecto.getFechaFin() != null ? new java.sql.Date(proyecto.getFechaFin().getTime()) : null);
            pstmt.setInt(7, proyecto.getNumeroDeDayudantesDelProyecto());
            pstmt.setInt(8, proyecto.getIdUsuario());
            pstmt.executeUpdate();
            System.out.println("Proyecto insertado: " + proyecto.getNombre());
            return true;
        } catch (SQLException e) {
            System.err.println("Error al insertar proyecto: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener todos los proyectos
     */
    public List<Proyecto> obtenerTodos() {
        List<Proyecto> proyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos ORDER BY id";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Proyecto proyecto = new Proyecto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("codigo"),
                    rs.getString("descripcion"),
                    rs.getString("tipo"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getInt("num_ayudantes"),
                    rs.getInt("id_usuario")
                );
                proyectos.add(proyecto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proyectos: " + e.getMessage());
        }
        
        return proyectos;
    }
    
    /**
     * Obtener proyecto por ID
     */
    public Proyecto obtenerPorId(int id) {
        String sql = "SELECT * FROM proyectos WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Proyecto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("codigo"),
                    rs.getString("descripcion"),
                    rs.getString("tipo"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getInt("num_ayudantes"),
                    rs.getInt("id_usuario")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proyecto: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtener proyectos de un usuario específico
     */
    public List<Proyecto> obtenerPorUsuario(int idUsuario) {
        List<Proyecto> proyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos WHERE id_usuario = ? ORDER BY id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Proyecto proyecto = new Proyecto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("codigo"),
                    rs.getString("descripcion"),
                    rs.getString("tipo"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getInt("num_ayudantes"),
                    rs.getInt("id_usuario")
                );
                proyectos.add(proyecto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener proyectos del usuario: " + e.getMessage());
        }
        
        return proyectos;
    }
    
    /**
     * Actualizar proyecto
     */
    public boolean actualizar(Proyecto proyecto) {
        String sql = "UPDATE proyectos SET nombre = ?, codigo = ?, descripcion = ?, tipo = ?, fecha_inicio = ?, fecha_fin = ?, num_ayudantes = ?, id_usuario = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, proyecto.getNombre());
            pstmt.setString(2, proyecto.getCodigo());
            pstmt.setString(3, proyecto.getDescripcion());
            pstmt.setString(4, proyecto.getTipo());
            pstmt.setDate(5, proyecto.getFechaInicio() != null ? new java.sql.Date(proyecto.getFechaInicio().getTime()) : null);
            pstmt.setDate(6, proyecto.getFechaFin() != null ? new java.sql.Date(proyecto.getFechaFin().getTime()) : null);
            pstmt.setInt(7, proyecto.getNumeroDeDayudantesDelProyecto());
            pstmt.setInt(8, proyecto.getIdUsuario());
            pstmt.setInt(9, proyecto.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar proyecto: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Eliminar proyecto
     */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM proyectos WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar proyecto: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Buscar proyectos por nombre o código
     */
    public List<Proyecto> buscar(String texto) {
        List<Proyecto> proyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos WHERE nombre LIKE ? OR codigo LIKE ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String pattern = "%" + texto + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Proyecto proyecto = new Proyecto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("codigo"),
                    rs.getString("descripcion"),
                    rs.getString("tipo"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getInt("num_ayudantes"),
                    rs.getInt("id_usuario")
                );
                proyectos.add(proyecto);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar proyectos: " + e.getMessage());
        }
        
        return proyectos;
    }
    
    /**
     * Buscar proyectos por director
     */
    public List<Proyecto> obtenerPorDirector(int idDirector) {
        List<Proyecto> proyectos = new ArrayList<>();
        String sql = "SELECT * FROM proyectos WHERE id_usuario = ? ORDER BY nombre";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idDirector);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Proyecto proyecto = new Proyecto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("codigo"),
                    rs.getString("descripcion"),
                    rs.getString("tipo"),
                    rs.getDate("fecha_inicio"),
                    rs.getDate("fecha_fin"),
                    rs.getInt("num_ayudantes"),
                    rs.getInt("id_usuario")
                );
                proyectos.add(proyecto);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar proyectos por director: " + e.getMessage());
        }
        
        return proyectos;
    }
    
    /**
     * Obtener nombre completo del usuario que creó el proyecto
     */
    public String obtenerNombreUsuario(int idUsuario) {
        if (idUsuario == 0) {
            return "Sin asignar";
        }
        
        String sql = "SELECT nombre, apellido FROM usuarios WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String nombreCompleto = rs.getString("nombre") + " " + rs.getString("apellido");
                System.out.println("Usuario encontrado: ID=" + idUsuario + " -> " + nombreCompleto);
                return nombreCompleto;
            } else {
                System.out.println("No se encontró usuario con ID=" + idUsuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return "Sin asignar";
    }
}
