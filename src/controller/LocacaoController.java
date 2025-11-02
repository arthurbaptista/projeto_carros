package controller;

import dao.ClienteDAO;
import dao.LocacaoDAO;
import dao.VeiculoDAO;
import enums.Estado;
import model.Cliente;
import model.Veiculo;

import java.util.Calendar;
import java.util.List;

public class LocacaoController {

    private ClienteDAO clienteDAO;
    private VeiculoDAO veiculoDAO;
    private LocacaoDAO locacaoDAO;

    public LocacaoController() {
        this.clienteDAO = new ClienteDAO();
        this.veiculoDAO = new VeiculoDAO();
        this.locacaoDAO = new LocacaoDAO();
    }

    public Cliente buscarCliente(String busca) {
        if (busca.isEmpty()) {
            throw new RuntimeException("Digite um nome ou CPF para buscar!");
        }

        List<Cliente> clientes = clienteDAO.listarTodos();
        for (Cliente cliente : clientes) {
            if (cliente.getNome().toLowerCase().contains(busca.toLowerCase()) ||
                    cliente.getCpf().contains(busca)) {
                return cliente;
            }
        }
        // Se não encontrar, lança exceção
        throw new RuntimeException("Cliente não encontrado!");
    }

    public void locarVeiculo(Cliente cliente, Veiculo veiculo, String diasStr, String dataStr) {
        if (cliente == null) {
            throw new RuntimeException("Selecione um cliente primeiro!");
        }
        if (veiculo == null) {
            throw new RuntimeException("Selecione um veículo da tabela!");
        }

        int dias;
        try {
            dias = Integer.parseInt(diasStr.trim());
            if (dias <= 0) {
                throw new RuntimeException("Número de dias deve ser maior que zero!");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Número de dias deve ser um valor válido!");
        }

        // Validar data
        String[] dataParts = dataStr.split("/");
        if (dataParts.length != 3) {
            throw new RuntimeException("Data deve estar no formato dd/MM/yyyy!");
        }

        Calendar dataLocacao;
        try {
            dataLocacao = Calendar.getInstance();
            dataLocacao.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataParts[0]));
            dataLocacao.set(Calendar.MONTH, Integer.parseInt(dataParts[1]) - 1); // Mês é base 0
            dataLocacao.set(Calendar.YEAR, Integer.parseInt(dataParts[2]));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Data inválida!");
        }

        // Executar locação no modelo
        veiculo.locar(dias, dataLocacao, cliente);

        // Persistir no banco
        veiculoDAO.atualizarEstado(veiculo.getPlaca(), Estado.LOCADO);
        locacaoDAO.salvar(veiculo.getLocacao(), veiculo);
    }

    public void devolverVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new RuntimeException("Selecione um veículo para devolver!");
        }

        String placa = veiculo.getPlaca();

        // Chama o método do modelo
        veiculo.devolver();

        // Persiste as mudanças
        veiculoDAO.atualizarEstado(placa, Estado.DISPONIVEL);
        locacaoDAO.excluirPorPlaca(placa);
    }

    public void venderVeiculo(Veiculo veiculo) {
        if (veiculo == null) {
            throw new RuntimeException("Selecione um veículo para vender!");
        }

        String placa = veiculo.getPlaca();

        // Chama o método do modelo
        veiculo.vender();

        // Persiste a mudança
        veiculoDAO.atualizarEstado(placa, Estado.VENDIDO);
    }
}