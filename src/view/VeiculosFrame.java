package view;

import javax.swing.*;
import java.awt.*;
import model.*;
import enums.*;
import controller.VeiculoController; // Importa o Controller
import view.table.VeiculoTableModel; // Importa o TableModel
import javax.swing.text.MaskFormatter; // Para máscaras
import java.text.NumberFormat; // Para máscara de valor

import enums.ModeloAutomovel;
import enums.ModeloMotocicleta;
import enums.ModeloVan;

public class VeiculosFrame extends JFrame {
    private JComboBox<Marca> cmbMarca;
    private JComboBox<Estado> cmbEstado;
    private JComboBox<Categoria> cmbCategoria;
    private JComboBox<String> cmbTipoVeiculo;
    private JComboBox<Object> cmbModelo;
    private JFormattedTextField txtValorCompra, txtPlaca; // Mudou para JFormattedTextField
    private JTextField txtAno;
    private JButton btnSalvar, btnLimpar;
    private JTable tabelaVeiculos;
    private VeiculoTableModel tableModel; // Usa o AbstractTableModel
    private VeiculoController controller; // Usa o Controller

    public VeiculosFrame() {
        this.controller = new VeiculoController();
        initComponents();
        setupLayout();
        setupListeners();
        carregarVeiculos();
    }

    private void initComponents() {
        setTitle("Cadastrar Veículos");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Comboboxes para enums
        cmbMarca = new JComboBox<>(Marca.values());
        cmbEstado = new JComboBox<>(Estado.values());
        cmbCategoria = new JComboBox<>(Categoria.values());
        cmbTipoVeiculo = new JComboBox<>(new String[]{"AUTOMOVEL", "MOTOCICLETA", "VAN"});
        cmbModelo = new JComboBox<>();

        // Campos de texto com MÁSCARAS
        try {
            MaskFormatter placaFormatter = new MaskFormatter("UUU-####"); // XXX-0000
            placaFormatter.setPlaceholderCharacter('_');
            txtPlaca = new JFormattedTextField(placaFormatter);
            txtPlaca.setColumns(10);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            txtPlaca = new JFormattedTextField();
        }

        // Máscara para valor monetário
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        txtValorCompra = new JFormattedTextField(format);
        txtValorCompra.setColumns(15);

        txtAno = new JTextField(5);

        // Botões
        btnSalvar = new JButton("Salvar Veículo");
        btnLimpar = new JButton("Limpar Campos");

        // Tabela
        tableModel = new VeiculoTableModel(); // Usa o novo TableModel
        tabelaVeiculos = new JTable(tableModel);

        // Configurar combobox de tipo para atualizar modelos
        cmbTipoVeiculo.addActionListener(e -> atualizarModelos());
        atualizarModelos(); // Inicializar pela primeira vez
    }

    // *** MÉTODO QUE ESTAVA FALTANDO ***
    private void atualizarModelos() {
        cmbModelo.removeAllItems();
        String tipo = (String) cmbTipoVeiculo.getSelectedItem();

        if (tipo == null) return; // Proteção contra null

        switch (tipo) {
            case "AUTOMOVEL":
                for (ModeloAutomovel modelo : ModeloAutomovel.values()) {
                    cmbModelo.addItem(modelo);
                }
                break;
            case "MOTOCICLETA":
                for (ModeloMotocicleta modelo : ModeloMotocicleta.values()) {
                    cmbModelo.addItem(modelo);
                }
                break;
            case "VAN":
                for (ModeloVan modelo : ModeloVan.values()) {
                    cmbModelo.addItem(modelo);
                }
                break;
        }
    }
    // *** FIM DO MÉTODO FALTANTE ***

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Painel de formulário
        JPanel panelForm = new JPanel(new GridLayout(7, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Dados do Veículo"));

        panelForm.add(new JLabel("Tipo:"));
        panelForm.add(cmbTipoVeiculo);
        panelForm.add(new JLabel("Marca:"));
        panelForm.add(cmbMarca);
        panelForm.add(new JLabel("Modelo:"));
        panelForm.add(cmbModelo);
        panelForm.add(new JLabel("Placa:"));
        panelForm.add(txtPlaca);
        panelForm.add(new JLabel("Ano:"));
        panelForm.add(txtAno);
        panelForm.add(new JLabel("Categoria:"));
        panelForm.add(cmbCategoria);
        panelForm.add(new JLabel("Valor Compra:"));
        panelForm.add(txtValorCompra);

        // Painel estado (separado)
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEstado.add(new JLabel("Estado:"));
        panelEstado.add(cmbEstado);

        // Painel de botões
        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnLimpar);

        // Tabela
        JScrollPane scrollPane = new JScrollPane(tabelaVeiculos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Veículos Cadastrados"));

        // Organizar painéis superiores
        JPanel panelTop = new JPanel(new BorderLayout());
        JPanel panelFormCompleto = new JPanel(new BorderLayout());
        panelFormCompleto.add(panelForm, BorderLayout.CENTER);
        panelFormCompleto.add(panelEstado, BorderLayout.SOUTH);

        panelTop.add(panelFormCompleto, BorderLayout.CENTER);
        panelTop.add(panelBotoes, BorderLayout.SOUTH);

        add(panelTop, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupListeners() {
        btnSalvar.addActionListener(e -> salvarVeiculo());
        btnLimpar.addActionListener(e -> limparCampos());
    }

    private void salvarVeiculo() {
        try {
            // Obter dados do formulário
            String tipo = (String) cmbTipoVeiculo.getSelectedItem();
            Marca marca = (Marca) cmbMarca.getSelectedItem();
            Estado estado = (Estado) cmbEstado.getSelectedItem();
            Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
            Object modelo = cmbModelo.getSelectedItem();

            // Pega o valor do JFormattedTextField
            String valorCompraStr = txtValorCompra.getText().replace(".", "").replace(",", ".");
            if (valorCompraStr.isEmpty()) valorCompraStr = "0";

            // Chama o controller para salvar
            controller.salvar(
                    tipo,
                    marca,
                    estado,
                    categoria,
                    valorCompraStr,
                    txtPlaca.getText(),
                    txtAno.getText(),
                    modelo
            );

            JOptionPane.showMessageDialog(this,
                    "Veículo salvo com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparCampos();
            carregarVeiculos();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar veículo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void carregarVeiculos() {
        // Carrega do banco via controller
        tableModel.setVeiculos(controller.listarTodos());
    }

    private void limparCampos() {
        txtPlaca.setText("");
        txtAno.setText("");
        txtValorCompra.setValue(null); // Limpa campo formatado
        cmbMarca.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        cmbCategoria.setSelectedIndex(0);
        cmbTipoVeiculo.setSelectedIndex(0);
    }
}