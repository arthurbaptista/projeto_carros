package dao;

import model.*;
import enums.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

    public void salvar(Veiculo veiculo) {
        String sql = "INSERT INTO veiculos (tipo, marca, estado, categoria, valor_compra, placa, ano, modelo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Determinar o tipo e modelo
            String tipo = "";
            String modelo = "";

            if (veiculo instanceof Automovel) {
                tipo = "AUTOMOVEL";
                modelo = ((Automovel) veiculo).getModelo().name();
                System.out.println("  Salvando AUTOMOVEL - Modelo: " + modelo);
            } else if (veiculo instanceof Motocicleta) {
                tipo = "MOTOCICLETA";
                modelo = ((Motocicleta) veiculo).getModelo().name();
                System.out.println(" Salvando MOTOCICLETA - Modelo: " + modelo);
            } else if (veiculo instanceof Van) {
                tipo = "VAN";
                modelo = ((Van) veiculo).getModelo().name();
                System.out.println("🚐 Salvando VAN - Modelo: " + modelo);
            }

            // Debug: mostrar todos os valores
            System.out.println("  Valores a serem salvos:");
            System.out.println("  Tipo: " + tipo);
            System.out.println("  Marca: " + veiculo.getMarca().name());
            System.out.println("  Estado: " + veiculo.getEstado().name());
            System.out.println("  Categoria: " + veiculo.getCategoria().name());
            System.out.println("  Valor Compra: " + veiculo.getValorDeCompra());
            System.out.println("  Placa: " + veiculo.getPlaca());
            System.out.println("  Ano: " + veiculo.getAno());
            System.out.println("  Modelo: " + modelo);

            stmt.setString(1, tipo);
            stmt.setString(2, veiculo.getMarca().name());
            stmt.setString(3, veiculo.getEstado().name());
            stmt.setString(4, veiculo.getCategoria().name());
            stmt.setDouble(5, veiculo.getValorDeCompra());
            stmt.setString(6, veiculo.getPlaca());
            stmt.setInt(7, veiculo.getAno());
            stmt.setString(8, modelo);

            int linhasAfetadas = stmt.executeUpdate();
            System.out.println("✅ Veículo salvo! Linhas afetadas: " + linhasAfetadas);

        } catch (SQLException e) {
            System.out.println("❌ Erro ao salvar veículo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Veiculo buscarPorPlaca(String placa) {
        String sql = "SELECT * FROM veiculos WHERE placa = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return criarVeiculoFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao buscar veículo por placa: " + e.getMessage());
        }

        return null;
    }

    public List<Veiculo> listarTodos() {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM veiculos";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Veiculo veiculo = criarVeiculoFromResultSet(rs);
                if (veiculo != null) {
                    veiculos.add(veiculo);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao listar veículos: " + e.getMessage());
        }

        return veiculos;
    }

    private Veiculo criarVeiculoFromResultSet(ResultSet rs) throws SQLException {
        String tipo = rs.getString("tipo");
        Marca marca = Marca.valueOf(rs.getString("marca"));
        Estado estado = Estado.valueOf(rs.getString("estado"));
        Categoria categoria = Categoria.valueOf(rs.getString("categoria"));
        double valorCompra = rs.getDouble("valor_compra");
        String placa = rs.getString("placa");
        int ano = rs.getInt("ano");
        String modeloStr = rs.getString("modelo");

        switch (tipo) {
            case "AUTOMOVEL":
                ModeloAutomovel modeloAuto = ModeloAutomovel.valueOf(modeloStr);
                return new Automovel(marca, estado, categoria, valorCompra, placa, ano, modeloAuto);

            case "MOTOCICLETA":
                ModeloMotocicleta modeloMoto = ModeloMotocicleta.valueOf(modeloStr);
                return new Motocicleta(marca, estado, categoria, valorCompra, placa, ano, modeloMoto);

            case "VAN":
                ModeloVan modeloVan = ModeloVan.valueOf(modeloStr);
                return new Van(marca, estado, categoria, valorCompra, placa, ano, modeloVan);

            default:
                return null;
        }
    }

    // Métodos para buscar veículos por estado (serão úteis depois)
    public List<Veiculo> listarDisponiveis() {
        return listarPorEstado(Estado.DISPONIVEL);
    }

    public List<Veiculo> listarLocados() {
        return listarPorEstado(Estado.LOCADO);
    }

    private List<Veiculo> listarPorEstado(Estado estado) {
        List<Veiculo> veiculos = new ArrayList<>();
        String sql = "SELECT * FROM veiculos WHERE estado = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, estado.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Veiculo veiculo = criarVeiculoFromResultSet(rs);
                if (veiculo != null) {
                    veiculos.add(veiculo);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Erro ao listar veículos por estado: " + e.getMessage());
        }

        return veiculos;
    }

    public void atualizarEstado(String placa, Estado novoEstado) {
        String sql = "UPDATE veiculos SET estado = ? WHERE placa = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoEstado.name());
            stmt.setString(2, placa);
            stmt.executeUpdate();

            System.out.println("✅ Estado do veículo " + placa + " atualizado para: " + novoEstado);

        } catch (SQLException e) {
            System.out.println("❌ Erro ao atualizar estado do veículo: " + e.getMessage());
        }
    }
}