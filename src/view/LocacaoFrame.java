package view;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.List;
import model.*;
import enums.*;
import controller.LocacaoController; // Controller principal
import controller.VeiculoController; // Controller para filtros
import view.table.VeiculoLocacaoTableModel; // TableModel
import java.text.NumberFormat; // *** ADICIONADO ***

public class LocacaoFrame extends JFrame {
    private JTextField txtBuscaCliente, txtData;
    private JFormattedTextField txtDias; // *** MUDADO ***
    private JComboBox<String> cmbTipoVeiculo, cmbMarca, cmbCategoria;
    private JButton btnBuscarCliente, btnFiltrar, btnLocar;
    private JTable tabelaVeiculos;
    private VeiculoLocacaoTableModel tableModel; // Usa o TableModel
    private LocacaoController locacaoController;
    private VeiculoController veiculoController;
    private Cliente clienteSelecionado;
    private Veiculo veiculoSelecionado;

    public LocacaoFrame() {
        this.locacaoController = new LocacaoController();
        this.veiculoController = new VeiculoController();
        initComponents();
        setupLayout();
        setupListeners();
        carregarVeiculosDisponiveis();
    }

    private void initComponents() {
        setTitle("Locação de Veículos");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Campos de busca
        txtBuscaCliente = new JTextField(20);

        // *** CAMPO 'DIAS' ATUALIZADO PARA ACEITAR APENAS NÚMEROS ***
        NumberFormat diasFormat = NumberFormat.getIntegerInstance();
        diasFormat.setGroupingUsed(false); // Tira o separador (ex: 1,000)
        txtDias = new JFormattedTextField(diasFormat);
        txtDias.setColumns(5); // Define o tamanho
        txtDias.setValue(1); // Valor inicial

        txtData = new JTextField(10);
        // Data atual
        Calendar hoje = Calendar.getInstance();
        txtData.setText(String.format("%02d/%02d/%d",
                hoje.get(Calendar.DAY_OF_MONTH),
                hoje.get(Calendar.MONTH) + 1,
                hoje.get(Calendar.YEAR)));


        // Comboboxes para filtros
        cmbTipoVeiculo = new JComboBox<>(new String[]{"TODOS", "AUTOMOVEL", "MOTOCICLETA", "VAN"});
        cmbMarca = new JComboBox<>();
        cmbCategoria = new JComboBox<>(new String[]{"TODOS", "POPULAR", "INTERMEDIARIO", "LUXO"});

        // Popular marcas
        cmbMarca.addItem("TODAS");
        for (Marca marca : Marca.values()) {
            cmbMarca.addItem(marca.name());
        }

        // Botões
        btnBuscarCliente = new JButton("Buscar Cliente");
        btnFiltrar = new JButton("Filtrar Veículos");
        btnLocar = new JButton("Locar Veículo");
        btnLocar.setEnabled(false);

        // Tabela
        tableModel = new VeiculoLocacaoTableModel(); // Usa o novo TableModel
        tabelaVeiculos = new JTable(tableModel);
        tabelaVeiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Painel superior - Cliente e dados da locação
        JPanel panelCliente = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelCliente.setBorder(BorderFactory.createTitledBorder("Dados do Cliente e Locação"));
        panelCliente.add(new JLabel("Buscar Cliente (nome/CPF):"));
        panelCliente.add(txtBuscaCliente);
        panelCliente.add(btnBuscarCliente);
        panelCliente.add(new JLabel("Dias:"));
        panelCliente.add(txtDias); // Adiciona o novo JFormattedTextField
        panelCliente.add(new JLabel("Data:"));
        panelCliente.add(txtData);

        // Painel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtrar Veículos Disponíveis"));
        panelFiltros.add(new JLabel("Tipo:"));
        panelFiltros.add(cmbTipoVeiculo);
        panelFiltros.add(new JLabel("Marca:"));
        panelFiltros.add(cmbMarca);
        panelFiltros.add(new JLabel("Categoria:"));
        panelFiltros.add(cmbCategoria);
        panelFiltros.add(btnFiltrar);

        // Painel de informações
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.add(panelCliente, BorderLayout.NORTH);
        panelInfo.add(panelFiltros, BorderLayout.CENTER);

        // Tabela
        JScrollPane scrollPane = new JScrollPane(tabelaVeiculos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Veículos Disponíveis para Locação"));

        // Painel inferior - botão locar
        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.add(btnLocar);

        // Adicionar tudo ao frame
        add(panelInfo, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnBuscarCliente.addActionListener(e -> buscarCliente());
        btnFiltrar.addActionListener(e -> filtrarVeiculos());
        btnLocar.addActionListener(e -> locarVeiculo());

        // Seleção na tabela
        tabelaVeiculos.getSelectionModel().addListSelectionListener(e -> {

            // *** LINHA CORRIGIDA ***
            if (!e.getValueIsAdjusting()) {

                int selectedRow = tabelaVeiculos.getSelectedRow();
                if (selectedRow != -1) {
                    // Pega o veículo direto do TableModel
                    veiculoSelecionado = tableModel.getVeiculoAt(selectedRow);
                    atualizarEstadoBotaoLocar();
                }
            }
        });
    }

    private void buscarCliente() {
        try {
            clienteSelecionado = locacaoController.buscarCliente(txtBuscaCliente.getText().trim());

            JOptionPane.showMessageDialog(this,
                    "Cliente selecionado:\n" +
                            "Nome: " + clienteSelecionado.getNome() + " " + clienteSelecionado.getSobrenome() + "\n" +
                            "CPF: " + clienteSelecionado.getCpf(), "Cliente Encontrado", JOptionPane.INFORMATION_MESSAGE);
            atualizarEstadoBotaoLocar();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            clienteSelecionado = null;
        }
    }

    private void carregarVeiculosDisponiveis() {
        tableModel.setVeiculos(veiculoController.listarDisponiveis());
    }

    private void filtrarVeiculos() {
        // Lógica de filtro agora está no Controller
        List<Veiculo> veiculosFiltrados = veiculoController.filtrarDisponiveis(
                (String) cmbTipoVeiculo.getSelectedItem(),
                (String) cmbMarca.getSelectedItem(),
                (String) cmbCategoria.getSelectedItem()
        );
        tableModel.setVeiculos(veiculosFiltrados);
    }

    // *** MÉTODO CORRIGIDO ***
    private void locarVeiculo() {
        try {
            // Pega o valor numérico do campo formatado
            Object diasObj = txtDias.getValue();
            if (diasObj == null) {
                JOptionPane.showMessageDialog(this, "Por favor, insira o número de dias.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Converte para int
            int dias = ((Number) diasObj).intValue();
            String diasStr = diasObj.toString(); // Passa a string numérica para o controller

            // Agora podemos calcular o valor total
            double valorTotal = veiculoSelecionado.getValorDiariaLocacao() * dias;

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirmar locação?\n\n" +
                            "Cliente: " + clienteSelecionado.getNome() + "\n" +
                            "Veículo: " + veiculoSelecionado.getPlaca() + "\n" +
                            "Dias: " + dias + "\n" +
                            "Valor total: R$ " + String.format("%.2f", valorTotal),
                    "Confirmar Locação", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Controller cuida de toda a lógica
                locacaoController.locarVeiculo(
                        clienteSelecionado,
                        veiculoSelecionado,
                        diasStr, // Passa a string numérica
                        txtData.getText()
                );

                JOptionPane.showMessageDialog(this,
                        "Locação realizada com sucesso!\n" +
                                "Valor total: R$ " + String.format("%.2f", valorTotal),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // Limpar e recarregar
                limparSelecao();
                carregarVeiculosDisponiveis();
            }

        } catch (Exception e) {
            // Este 'catch' pega os erros do controller (ex: "dias <= 0")
            JOptionPane.showMessageDialog(this, "Erro ao realizar locação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparSelecao() {
        clienteSelecionado = null;
        veiculoSelecionado = null;
        txtBuscaCliente.setText("");
        txtDias.setValue(1); // Reseta para 1
        tabelaVeiculos.clearSelection();
        atualizarEstadoBotaoLocar();
    }

    private void atualizarEstadoBotaoLocar() {
        btnLocar.setEnabled(clienteSelecionado != null && veiculoSelecionado != null);
    }
}