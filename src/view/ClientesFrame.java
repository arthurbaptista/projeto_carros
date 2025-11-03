package view;

import javax.swing.*;
import java.awt.*;
import model.Cliente;
import controller.ClienteController;
import view.table.ClienteTableModel; // Importa o novo TableModel
import javax.swing.text.MaskFormatter; // <-- IMPORTADO PARA AS MÁSCARAS

public class ClientesFrame extends JFrame {
    private JTextField txtNome, txtSobrenome, txtEndereco;
    private JFormattedTextField txtRg, txtCpf; // <-- MUDADO DE JTextField
    private JButton btnSalvar, btnEditar, btnExcluir, btnLimpar, btnListar;
    private JTable tabelaClientes;
    private ClienteTableModel tableModel; // Usa o AbstractTableModel
    private ClienteController controller; // Usa o Controller
    private Cliente clienteSelecionado;
    private boolean modoEdicao = false;

    public ClientesFrame() {
        this.controller = new ClienteController(); // Instancia o Controller
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

        // *** MÁSCARAS ADICIONADAS PARA RG E CPF ***
        try {
            // Máscara para RG (formato 9 dígitos, ex: 12.345.678-9)
            // Nota: Alguns RGs têm 8 dígitos. Esta máscara é para o formato mais comum de SP.
            // Ajuste o "##.###.###-#" se o formato do seu estado for diferente (ex: "##.###.###")
            MaskFormatter rgFormatter = new MaskFormatter("##.###.###-#");
            rgFormatter.setPlaceholderCharacter('_');
            txtRg = new JFormattedTextField(rgFormatter);
            txtRg.setColumns(15);

            // Máscara para CPF (formato 11 dígitos, ex: 123.456.789-00)
            MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
            cpfFormatter.setPlaceholderCharacter('_');
            txtCpf = new JFormattedTextField(cpfFormatter);
            txtCpf.setColumns(15);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
            // Fallback caso a máscara dê erro
            txtRg = new JFormattedTextField();
            txtCpf = new JFormattedTextField();
        }
        // *** FIM DAS MÁSCARAS ***

        txtEndereco = new JTextField(30);

        // Botões
        btnSalvar = new JButton("Salvar Cliente");
        btnEditar = new JButton("Editar Cliente");
        btnExcluir = new JButton("Excluir Cliente");
        btnLimpar = new JButton("Limpar Campos");
        btnListar = new JButton("Atualizar Lista");

        // Tabela
        // Inicializa o TableModel (ainda vazio)
        tableModel = new ClienteTableModel();
        tabelaClientes = new JTable(tableModel); // Passa o TableModel para a JTable
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
        panelForm.add(txtRg); // Adiciona o campo formatado
        panelForm.add(new JLabel("CPF:"));
        panelForm.add(txtCpf); // Adiciona o campo formatado
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
                    // Pega o cliente direto do TableModel
                    clienteSelecionado = tableModel.getClienteAt(selectedRow);
                    carregarClienteSelecionado();
                }
            }
        });
    }

    private void salvarCliente() {
        try {
            // Chama o controller para a lógica de salvar
            // O .getText() já envia o valor formatado (ex: "123.456.789-00")
            // O DAO já está preparado para limpar isso
            controller.salvar(
                    txtNome.getText().trim(),
                    txtSobrenome.getText().trim(),
                    txtRg.getText().trim(),
                    txtCpf.getText().trim(),
                    txtEndereco.getText().trim()
            );

            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!");
            limparCampos();
            carregarClientes();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar cliente: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarCliente() {
        try {
            // Chama o controller para a lógica de editar
            controller.atualizar(
                    clienteSelecionado,
                    txtNome.getText().trim(),
                    txtSobrenome.getText().trim(),
                    txtEndereco.getText().trim()
            );

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
                        clienteSelecionado.getNome() + " " + clienteSelecionado.getSobrenome(),
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Chama o controller para a lógica de excluir
                controller.excluir(clienteSelecionado);
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
        // Pede ao controller a lista e atualiza o tableModel
        tableModel.setClientes(controller.listarTodos());
        atualizarEstadoBotoes();
    }

    private void carregarClienteSelecionado() {
        if (clienteSelecionado != null) {
            txtNome.setText(clienteSelecionado.getNome());
            txtSobrenome.setText(clienteSelecionado.getSobrenome());
            // O .setText() no JFormattedTextField aplica a máscara automaticamente
            txtRg.setText(clienteSelecionado.getRg());
            txtCpf.setText(clienteSelecionado.getCpf());
            txtEndereco.setText(clienteSelecionado.getEndereco());

            // CPF e RG não podem ser editados (são chaves)
            txtCpf.setEnabled(false);
            txtRg.setEnabled(false);

            modoEdicao = true;
        }
        atualizarEstadoBotoes();
    }

    private void limparCampos() {
        txtNome.setText("");
        txtSobrenome.setText("");
        txtRg.setValue(null); // Limpa campo formatado
        txtCpf.setValue(null); // Limpa campo formatado
        txtEndereco.setText("");

        txtCpf.setEnabled(true);
        txtRg.setEnabled(true); // *** LINHA CORRIGIDA (removido o 'E>') ***

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
            btnSalvar.setText("Salvar Novo"); // Texto volta ao normal
            btnEditar.setText("Confirmar Edição");
        } else {
            btnSalvar.setText("Salvar Cliente");
            btnEditar.setText("Editar Cliente");
        }
    }
}