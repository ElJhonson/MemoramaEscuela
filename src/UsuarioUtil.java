import java.sql.Connection;
import java.sql.*;


public class UsuarioUtil {

    public static boolean registrar(String nombre, String email, String tel, String username, String password) {
        String sql = "INSERT INTO usuarios (nombre, email, telefono, username, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            stmt.setString(2, email);
            stmt.setString(3, tel);
            stmt.setString(4, username);
            stmt.setString(5, password);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            return false; // Puede fallar si ya existe el usuario
        }
    }

    public static boolean iniciarSesion(String username, String password) {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
        try (Connection conn = Conexion.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // si existe, login válido
        } catch (SQLException e) {
            return false;
        }
    }

}
