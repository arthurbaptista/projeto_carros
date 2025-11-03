package dao;

import model.Cliente;
import model.Locacao;
import model.Veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LocacaoDAO {

    public void salvar(Locacao locacao, Veiculo veiculo) {
        String sql = "INSERT INTO locacoes (veiculo_placa, cliente_cpf, data_locacao, dias, valor_total) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, locacao.getCliente().getCpf());
            stmt.setDate(3, new Date(locacao.getData().getTimeInMillis()));
            stmt.setInt(4, locacao.getDias());
            stmt.setDouble(5, locacao.getValor());

            stmt.executeUpdate();
            System.out.println("Locação salva para o veículo: " + veiculo.getPlaca());

        } catch (SQLException e) {
            System.out.println("Erro ao salvar locação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void excluirPorPlaca(String placa) {
        String sql = "DELETE FROM locacoes WHERE veiculo_placa = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, placa);
            stmt.executeUpdate();
            System.out.println("Locação removida (devolução) para a placa: " + placa);

        } catch (SQLException e) {
            System.out.println("Erro ao excluir locação: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Locacao> buscarAtivasPorCPF(String cpf) {
        List<Locacao> locacoes = new ArrayList<>();

        String sql = "SELECT * FROM locacoes WHERE cliente_cpf = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Calendar data = Calendar.getInstance();
                data.setTime(rs.getDate("data_locacao"));

                locacoes.add(new Locacao(
                        rs.getInt("dias"),
                        rs.getDouble("valor_total"),
                        data,
                        null
                ));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar locações por CPF: " + e.getMessage());
        }

        return locacoes;
    }
    public Locacao buscarLocacaoPorPlaca(String placa) {
        String sql = "SELECT * FROM locacoes WHERE veiculo_placa = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Calendar data = Calendar.getInstance();
                data.setTime(rs.getDate("data_locacao"));
                return new Locacao(
                        rs.getInt("dias"),
                        rs.getDouble("valor_total"),
                        data,
                        new Cliente(null, null, null, rs.getString("cliente_cpf"), null)
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar locação por placa: " + e.getMessage());
        }
        return null;
    }
}