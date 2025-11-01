package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConnection {
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("database/database.properties");

            if (input == null) {
                System.out.println("⚠️ Arquivo database.properties não encontrado!");
                return null;
            }

            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            connection = DriverManager.getConnection(url, user, password);
            //System.out.println("✅ Conexão com banco estabelecida!");
        } catch (Exception e) {
            System.out.println("❌ Erro ao conectar com banco: " + e.getMessage());
        }

        return connection;
    }
}
