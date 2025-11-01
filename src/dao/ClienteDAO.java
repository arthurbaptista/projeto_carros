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
            stmt.setString(3, formatarRG(cliente.getRg()));
            stmt.setString(4, formatarCPF(cliente.getCpf()));
            stmt.setString(5, cliente.getEndereco());

            stmt.executeUpdate();
            System.out.println("✅ Cliente salvo: " + cliente.getNome());

        } catch (SQLException e) {
            System.out.println("❌ Erro ao salvar cliente: " + e.getMessage());
        }
    }

    @Override
    public void atualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, sobrenome = ?, endereco = ? WHERE cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getSobrenome());
            stmt.setString(3, cliente.getEndereco());
            stmt.setString(4, formatarCPF(cliente.getCpf()));

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("✅ Cliente atualizado: " + cliente.getNome());
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    @Override
    public void excluir(Cliente cliente) {
        // Primeiro verificar se o cliente tem veículos locados
        if (clienteTemVeiculosLocados(cliente)) {
            throw new RuntimeException("Cliente não pode ser excluído pois possui veículos locados!");
        }

        String sql = "DELETE FROM clientes WHERE cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, formatarCPF(cliente.getCpf()));

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("✅ Cliente excluído: " + cliente.getNome());
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao excluir cliente: " + e.getMessage());
            throw new RuntimeException("Erro ao excluir cliente: " + e.getMessage());
        }
    }

    private boolean clienteTemVeiculosLocados(Cliente cliente) {
        // TODO: Implementar verificação quando tivermos a tabela de locações
        // Por enquanto, retornamos false para permitir exclusão
        return false;
    }

    @Override
    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Cliente(
                        rs.getString("nome"),
                        rs.getString("sobrenome"),
                        rs.getString("rg"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                );
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao buscar cliente por ID: " + e.getMessage());
        }

        return null;
    }

    public Cliente buscarPorCPF(String cpf) {
        String sql = "SELECT * FROM clientes WHERE cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, formatarCPF(cpf));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Cliente(
                        rs.getString("nome"),
                        rs.getString("sobrenome"),
                        rs.getString("rg"),
                        rs.getString("cpf"),
                        rs.getString("endereco")
                );
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao buscar cliente por CPF: " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nome, sobrenome";

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
            System.out.println("❌ Erro ao listar clientes: " + e.getMessage());
        }

        return clientes;
    }

    // Métodos para formatação automática
    private String formatarCPF(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", ""); // Remove não números
        if (cpf.length() == 11) {
            return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
        }
        return cpf;
    }

    private String formatarRG(String rg) {
        rg = rg.replaceAll("[^0-9]", ""); // Remove não números

        // Formatação segura que não quebra com RGs curtos
        if (rg.length() >= 9) {
            return rg.substring(0, 2) + "." + rg.substring(2, 5) + "." + rg.substring(5, 8) + "-" + rg.substring(8);
        } else if (rg.length() >= 8) {
            return rg.substring(0, 2) + "." + rg.substring(2, 5) + "." + rg.substring(5, 8);
        } else if (rg.length() >= 5) {
            return rg.substring(0, 2) + "." + rg.substring(2, 5);
        } else if (rg.length() >= 2) {
            return rg.substring(0, 2);
        } else {
            return rg; // Retorna como está se for muito curto
        }
    }

    // Métodos para formatação para exibição
    public String formatarCPFExibicao(String cpf) {
        return formatarCPF(cpf);
    }

    public String formatarRGExibicao(String rg) {
        return formatarRG(rg);
    }
}