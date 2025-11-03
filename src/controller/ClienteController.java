package controller;

import dao.ClienteDAO;
import model.Cliente;
import java.util.List;

public class ClienteController {
    private ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    public void salvar(String nome, String sobrenome, String rg, String cpf, String endereco) {
        if (nome.isEmpty() || sobrenome.isEmpty() || rg.isEmpty() || cpf.isEmpty() || endereco.isEmpty()) {
            throw new RuntimeException("Preencha todos os campos!");
        }

        Cliente cliente = new Cliente(nome, sobrenome, rg, cpf, endereco);
        clienteDAO.salvar(cliente);
    }

    public void atualizar(Cliente clienteSelecionado, String nome, String sobrenome, String endereco) {
        if (clienteSelecionado == null) {
            throw new RuntimeException("Nenhum cliente selecionado para editar!");
        }

        if (nome.isEmpty() || sobrenome.isEmpty() || endereco.isEmpty()) {
            throw new RuntimeException("Preencha todos os campos (Nome, Sobrenome, Endereço)!");
        }

        clienteSelecionado.setNome(nome);
        clienteSelecionado.setSobrenome(sobrenome);
        clienteSelecionado.setEndereco(endereco);

        clienteDAO.atualizar(clienteSelecionado);
    }

    public void excluir(Cliente clienteSelecionado) {
        if (clienteSelecionado == null) {
            throw new RuntimeException("Selecione um cliente para excluir!");
        }
        clienteDAO.excluir(clienteSelecionado);
    }

    public List<Cliente> listarTodos() {
        return clienteDAO.listarTodos();
    }

    public Cliente buscarPorCPF(String cpf) {
        return clienteDAO.buscarPorCPF(cpf);
    }
}