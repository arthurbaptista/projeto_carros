package database;

import dao.DatabaseConnection;
import java.sql.Statement;
import java.sql.Connection;

public class DatabaseInitializer {
    public static void inicializar() {
        String sqlClientes = """
            CREATE TABLE IF NOT EXISTS clientes (
                id INT AUTO_INCREMENT PRIMARY KEY,
                nome VARCHAR(100) NOT NULL,
                sobrenome VARCHAR(100) NOT NULL,
                rg VARCHAR(20) UNIQUE NOT NULL,
                cpf VARCHAR(14) UNIQUE NOT NULL,
                endereco TEXT NOT NULL
            )
            """;

        String sqlVeiculos = """
            CREATE TABLE IF NOT EXISTS veiculos (
                id INT AUTO_INCREMENT PRIMARY KEY,
                tipo VARCHAR(20) NOT NULL,
                marca VARCHAR(50) NOT NULL,
                estado VARCHAR(20) NOT NULL,
                categoria VARCHAR(20) NOT NULL,
                valor_compra DECIMAL(10,2) NOT NULL,
                placa VARCHAR(10) UNIQUE NOT NULL,
                ano INT NOT NULL,
                modelo VARCHAR(50) NOT NULL
            )
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlClientes);
            stmt.execute(sqlVeiculos);
            System.out.println("✅ Tabelas criadas com sucesso!");

        } catch (Exception e) {
            System.out.println("❌ Erro ao criar tabelas: " + e.getMessage());
        }
    }
}