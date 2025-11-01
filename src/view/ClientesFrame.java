package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.Cliente;
import dao.ClienteDAO;
import java.util.List;

public class ClientesFrame extends JFrame {
    private JTextField txtNome, txtSobrenome, txtRg, txtCpf, txtEndereco;
    private JButton btnSalvar, btnEditar, btnExcluir, btnLimpar, btnListar;
    private JTable tabelaClientes;
    private DefaultTableModel tableModel;
    private ClienteDAO clienteDAO;
    private Cliente clienteSelecionado;
    private boolean modoEdicao = false;

    public ClientesFrame() {
        this.clienteDAO = new ClienteDAO();
        initComponents();
        setupLayout();
        setupListeners();
        carregarClientes();
        atualizarEstadoBotoes();
    }

    private void initComponents() {
        setTitle("Gerenciar Clientes");
        setSize(900, 600);
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
        btnEditar = new JButton("Editar Cliente");
        btnExcluir = new JButton("Excluir Cliente");
        btnLimpar = new JButton("Limpar Campos");
        btnListar = new JButton("Atualizar Lista");

        // Tabela
        String[] colunas = {"Nome", "Sobrenome", "RG", "CPF", "Endereço"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaClientes = new JTable(tableModel);
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        panelBotoes.add(btnEditar);
        panelBotoes.add(btnExcluir);
        panelBotoes.add(btnLimpar);
        panelBotoes.add(btnListar);

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
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnListar.addActionListener(e -> carregarClientes());
        btnLimpar.addActionListener(e -> limparCampos());

        // Seleção na tabela
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaClientes.getSelectedRow();
                if (selectedRow != -1) {
                    carregarClienteSelecionado(selectedRow);
                }
            }
        });
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

    private void editarCliente() {
        if (clienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para editar!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String nome = txtNome.getText().trim();
            String sobrenome = txtSobrenome.getText().trim();
            String endereco = txtEndereco.getText().trim();

            if (nome.isEmpty() || sobrenome.isEmpty() || endereco.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            clienteSelecionado.setNome(nome);
            clienteSelecionado.setSobrenome(sobrenome);
            clienteSelecionado.setEndereco(endereco);

            clienteDAO.atualizar(clienteSelecionado);

            JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!");

            modoEdicao = false;
            limparCampos();
            carregarClientes();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao editar cliente: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirCliente() {
        if (clienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o cliente?\n" +
                        clienteSelecionado.getNome() + " " + clienteSelecionado.getSobrenome() + " - CPF: " + clienteSelecionado.getCpf(),
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                clienteDAO.excluir(clienteSelecionado);
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
                limparCampos();
                carregarClientes();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir cliente: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarClientes() {
        tableModel.setRowCount(0);
        List<Cliente> clientes = clienteDAO.listarTodos();

        for (Cliente cliente : clientes) {
            Object[] row = {
                    cliente.getNome(),
                    cliente.getSobrenome(),
                    clienteDAO.formatarRGExibicao(cliente.getRg()),
                    clienteDAO.formatarCPFExibicao(cliente.getCpf()),
                    cliente.getEndereco()
            };
            tableModel.addRow(row);
        }
        atualizarEstadoBotoes();
    }

    private void carregarClienteSelecionado(int rowIndex) {
        String cpf = (String) tableModel.getValueAt(rowIndex, 3);
        clienteSelecionado = clienteDAO.buscarPorCPF(cpf.replaceAll("[^0-9]", ""));

        if (clienteSelecionado != null) {
            txtNome.setText(clienteSelecionado.getNome());
            txtSobrenome.setText(clienteSelecionado.getSobrenome());
            txtRg.setText(clienteSelecionado.getRg());
            txtCpf.setText(clienteSelecionado.getCpf());
            txtEndereco.setText(clienteSelecionado.getEndereco());

            txtCpf.setEnabled(false);
            txtRg.setEnabled(false);

            modoEdicao = true;
        }
        atualizarEstadoBotoes();
    }

    private void limparCampos() {
        txtNome.setText("");
        txtSobrenome.setText("");
        txtRg.setText("");
        txtCpf.setText("");
        txtEndereco.setText("");

        txtCpf.setEnabled(true);
        txtRg.setEnabled(true);

        clienteSelecionado = null;
        modoEdicao = false;
        tabelaClientes.clearSelection();
        atualizarEstadoBotoes();
    }

    private void atualizarEstadoBotoes() {
        boolean temClienteSelecionado = this.clienteSelecionado != null;

        btnSalvar.setEnabled(!modoEdicao); // Bloqueado durante edição
        btnEditar.setEnabled(temClienteSelecionado && modoEdicao); // Só habilitado durante edição
        btnExcluir.setEnabled(temClienteSelecionado); // SEMPRE habilitado quando tem cliente selecionado

        if (modoEdicao) {
            btnSalvar.setText("Salvar Novo");
            btnEditar.setText("Confirmar Edição");
        } else {
            btnSalvar.setText("Salvar Cliente");
            btnEditar.setText("Editar Cliente");
        }
    }
}