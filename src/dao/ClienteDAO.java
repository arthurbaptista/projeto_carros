package dao;

import model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO implements Dao<Cliente> {

    @Override
    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, sobrenome, rg, cpf, endereco) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getSobrenome());
            stmt.setString(3, cliente.getRg());
            stmt.setString(4, cliente.getCpf());
            stmt.setString(5, cliente.getEndereco());

            stmt.executeUpdate();
            System.out.println(" Cliente salvo: " + cliente.getNome());

        } catch (SQLException e) {
            System.out.println(" Erro ao salvar cliente: " + e.getMessage());
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getString("nome"),
                        rs.getString("sobrenome"),
                        rs.getString("rg"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                );
                clientes.add(cliente);
            }

        } catch (SQLException e) {
            System.out.println(" Erro ao listar clientes: " + e.getMessage());
        }

        return clientes;
    }

    // Métodos simplificados por enquanto
    @Override
    public void atualizar(Cliente cliente) {
        System.out.println("Método atualizar - implementar depois");
    }

    @Override
    public void excluir(Cliente cliente) {
        System.out.println("Método excluir - implementar depois");
    }

    @Override
    public Cliente buscarPorId(int id) {
        System.out.println("Método buscarPorId - implementar depois");
        return null;
    }
}