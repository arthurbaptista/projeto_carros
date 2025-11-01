package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.Cliente;
import dao.ClienteDAO;
import java.util.List;

public class ClientesFrame extends JFrame {
    private JTextField txtNome, txtSobrenome, txtRg, txtCpf, txtEndereco;
    private JButton btnSalvar, btnListar, btnLimpar;
    private JTable tabelaClientes;
    private DefaultTableModel tableModel;
    private ClienteDAO clienteDAO;

    public ClientesFrame() {
        this.clienteDAO = new ClienteDAO();
        initComponents();
        setupLayout();
        setupListeners();
        carregarClientes();
    }

    private void initComponents() {
        setTitle("Gerenciar Clientes");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Campos de texto
        txtNome = new JTextField(20);
        txtSobrenome = new JTextField(20);
        txtRg = new JTextField(15);
        txtCpf = new JTextField(15);
        txtEndereco = new JTextField(30);

        // Botões
        btnSalvar = new JButton("Salvar Cliente");
        btnListar = new JButton("Atualizar Lista");
        btnLimpar = new JButton("Limpar Campos");

        // Tabela
        String[] colunas = {"Nome", "Sobrenome", "RG", "CPF", "Endereço"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaClientes = new JTable(tableModel);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Painel de formulário
        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));

        panelForm.add(new JLabel("Nome:"));
        panelForm.add(txtNome);
        panelForm.add(new JLabel("Sobrenome:"));
        panelForm.add(txtSobrenome);
        panelForm.add(new JLabel("RG:"));
        panelForm.add(txtRg);
        panelForm.add(new JLabel("CPF:"));
        panelForm.add(txtCpf);
        panelForm.add(new JLabel("Endereço:"));
        panelForm.add(txtEndereco);

        // Painel de botões
        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnListar);
        panelBotoes.add(btnLimpar);

        // Tabela
        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Clientes Cadastrados"));

        // Adicionar tudo ao frame
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.add(panelForm, BorderLayout.CENTER);
        panelTop.add(panelBotoes, BorderLayout.SOUTH);

        add(panelTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupListeners() {
        btnSalvar.addActionListener(e -> salvarCliente());
        btnListar.addActionListener(e -> carregarClientes());
        btnLimpar.addActionListener(e -> limparCampos());
    }

    private void salvarCliente() {
        try {
            String nome = txtNome.getText().trim();
            String sobrenome = txtSobrenome.getText().trim();
            String rg = txtRg.getText().trim();
            String cpf = txtCpf.getText().trim();
            String endereco = txtEndereco.getText().trim();

            if (nome.isEmpty() || sobrenome.isEmpty() || rg.isEmpty() || cpf.isEmpty() || endereco.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cliente cliente = new Cliente(nome, sobrenome, rg, cpf, endereco);
            clienteDAO.salvar(cliente);

            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!");
            limparCampos();
            carregarClientes();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar cliente: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarClientes() {
        tableModel.setRowCount(0); // Limpa a tabela
        List<Cliente> clientes = clienteDAO.listarTodos();

        for (Cliente cliente : clientes) {
            Object[] row = {
                    cliente.getNome(),
                    cliente.getSobrenome(),
                    cliente.getRg(),
                    cliente.getCpf(),
                    cliente.getEndereco()
            };
            tableModel.addRow(row);
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtSobrenome.setText("");
        txtRg.setText("");
        txtCpf.setText("");
        txtEndereco.setText("");
    }
}