
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:postgresql://ep-cool-poetry-a4m4n9lu-pooler.us-east-1.aws.neon.tech/memorama?sslmode=require";
    private static final String USER = "memorama_owner";
    private static final String PASSWORD = "npg_Qcdiz7hEgG2M";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
