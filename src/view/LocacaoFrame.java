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
// Removida a importação do NumberFormat, pois não é mais necessária para 'dias'
import javax.swing.text.MaskFormatter; // Para a Data

// *** IMPORTAÇÕES PARA O FILTRO DE DOCUMENTO ***
import javax.swing.text.PlainDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;


public class LocacaoFrame extends JFrame {
    private JTextField txtBuscaCliente;
    private JTextField txtDias; // <-- MUDADO DE VOLTA PARA JTextField
    private JFormattedTextField txtData;
    private JComboBox<String> cmbTipoVeiculo, cmbMarca, cmbCategoria;
    private JButton btnBuscarCliente, btnFiltrar, btnLocar;
    private JTable tabelaVeiculos;
    private VeiculoLocacaoTableModel tableModel;
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

        // *** INÍCIO DA NOVA VALIDAÇÃO DE 'DIAS' ***

        // 1. Usamos um JTextField simples
        txtDias = new JTextField(5);
        txtDias.setText("1"); // Valor inicial

        // 2. Pega o "documento" (o modelo de texto) por trás do campo
        PlainDocument doc = (PlainDocument) txtDias.getDocument();

        // 3. Cria um DocumentFilter que SÓ PERMITE NÚMEROS e SÓ ATÉ 4 DÍGITOS
        DocumentFilter filter = new DocumentFilter() {

            // Método helper para verificar se a string contém APENAS dígitos
            private boolean isNumeric(String str) {
                if (str == null) return true; // Permite deleção
                return str.matches("\\d*"); // Regex: 0 ou mais dígitos
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                // SÓ insere se for numérico E o tamanho total for <= 4
                if (isNumeric(string) && (fb.getDocument().getLength() + string.length()) <= 4) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String newText = (text == null) ? "" : text;
                // SÓ substitui se for numérico E o tamanho total for <= 4
                if (isNumeric(newText) && (fb.getDocument().getLength() + newText.length() - length) <= 4) {
                    super.replace(fb, offset, length, newText, attrs);
                }
            }
        };

        // 4. Aplica o filtro
        doc.setDocumentFilter(filter);
        // *** FIM DA NOVA VALIDAÇÃO DE 'DIAS' ***


        // Campo 'Data' (com máscara)
        try {
            MaskFormatter dataFormatter = new MaskFormatter("##/##/####");
            dataFormatter.setPlaceholderCharacter('_');
            txtData = new JFormattedTextField(dataFormatter);
            txtData.setColumns(10);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            txtData = new JFormattedTextField(); // Fallback
        }

        // Data atual (continua funcionando com a máscara)
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
        tableModel = new VeiculoLocacaoTableModel();
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
        panelCliente.add(txtDias); // Adiciona o JTextField de Dias
        panelCliente.add(new JLabel("Data:"));
        panelCliente.add(txtData); // Adiciona o JFormattedTextField de Data

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
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaVeiculos.getSelectedRow();
                if (selectedRow != -1) {
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


    private void locarVeiculo() {
        try {
            // Pega o TEXTO do campo formatado
            String diasStr = txtDias.getText();

            // Valida se está vazio
            if (diasStr == null || diasStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, insira o número de dias.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Pega o texto da data
            String dataStr = txtData.getText();
            // Validação simples para ver se a máscara foi preenchida
            if(dataStr.contains("_")) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha a data corretamente (dd/mm/aaaa).", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Converte para int
            int dias = Integer.parseInt(diasStr.trim());
            double valorTotal = veiculoSelecionado.getValorDiariaLocacao() * dias;

            // *** LINHA CORRIGIDA ***
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirmar locação?\n\n" +
                            "Cliente: " + clienteSelecionado.getNome() + "\n" +
                            "Veículo: " + veiculoSelecionado.getPlaca() + "\n" + // <-- ERRO ESTAVA AQUI
                            "Dias: " + dias + "\n" +
                            "Valor total: R$ " + String.format("%.2f", valorTotal),
                    "Confirmar Locação", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Controller cuida de toda a lógica
                locacaoController.locarVeiculo(
                        clienteSelecionado,
                        veiculoSelecionado,
                        diasStr, // Passa a string numérica
                        dataStr  // Passa a string da data
                );

                JOptionPane.showMessageDialog(this,
                        "Locação realizada com sucesso!\n" +
                                "Valor total: R$ " + String.format("%.2f", valorTotal),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // Limpar e recarregar
                limparSelecao();
                carregarVeiculosDisponiveis();
            }

        } catch (NumberFormatException e) {
            // Isso não deve mais acontecer (por causa do filtro), mas é bom ter
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao processar o número de dias.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            // Este 'catch' pega os erros do controller (ex: "dias <= 0" ou "data inválida")
            JOptionPane.showMessageDialog(this, "Erro ao realizar locação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparSelecao() {
        clienteSelecionado = null;
        veiculoSelecionado = null;
        txtBuscaCliente.setText("");
        txtDias.setText("1"); // Reseta para "1"

        // Limpa e reseta a data para o dia atual
        Calendar hoje = Calendar.getInstance();
        txtData.setText(String.format("%02d/%02d/%d",
                hoje.get(Calendar.DAY_OF_MONTH),
                hoje.get(Calendar.MONTH) + 1,
                hoje.get(Calendar.YEAR)));

        tabelaVeiculos.clearSelection();
        atualizarEstadoBotaoLocar();
    }

    private void atualizarEstadoBotaoLocar() {
        btnLocar.setEnabled(clienteSelecionado != null && veiculoSelecionado != null);
    }
}