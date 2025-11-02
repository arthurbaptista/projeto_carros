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

        // SQL PARA NOVA TABELA
        String sqlLocacoes = """
            CREATE TABLE IF NOT EXISTS locacoes (
                id INT AUTO_INCREMENT PRIMARY KEY,
                veiculo_placa VARCHAR(10) NOT NULL,
                cliente_cpf VARCHAR(14) NOT NULL,
                data_locacao DATE NOT NULL,
                dias INT NOT NULL,
                valor_total DECIMAL(10,2) NOT NULL,
                FOREIGN KEY (veiculo_placa) REFERENCES veiculos(placa),
                FOREIGN KEY (cliente_cpf) REFERENCES clientes(cpf)
            )
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlClientes);
            stmt.execute(sqlVeiculos);
            stmt.execute(sqlLocacoes); // Executa a criação da nova tabela
            System.out.println("✅ Tabelas criadas com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }
}