package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import model.*;
import enums.*;
import dao.VeiculoDAO;

public class VeiculosFrame extends JFrame {
    private JComboBox<Marca> cmbMarca;
    private JComboBox<Estado> cmbEstado;
    private JComboBox<Categoria> cmbCategoria;
    private JComboBox<String> cmbTipoVeiculo;
    private JComboBox<Object> cmbModelo;
    private JTextField txtValorCompra, txtPlaca, txtAno;
    private JButton btnSalvar, btnLimpar;
    private JTable tabelaVeiculos;
    private DefaultTableModel tableModel;

    public VeiculosFrame() {
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

        // Combobox para tipo de veículo
        cmbTipoVeiculo = new JComboBox<>(new String[]{"AUTOMOVEL", "MOTOCICLETA", "VAN"});

        // Combobox para modelos (será preenchido dinamicamente)
        cmbModelo = new JComboBox<>();

        // Campos de texto
        txtValorCompra = new JTextField(15);
        txtPlaca = new JTextField(10);
        txtAno = new JTextField(5);

        // Botões
        btnSalvar = new JButton("Salvar Veículo");
        btnLimpar = new JButton("Limpar Campos");

        // Tabela
        String[] colunas = {"Tipo", "Marca", "Modelo", "Placa", "Ano", "Categoria", "Estado", "Valor Compra"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabelaVeiculos = new JTable(tableModel);

        // Configurar combobox de tipo para atualizar modelos
        cmbTipoVeiculo.addActionListener(e -> atualizarModelos());
        atualizarModelos(); // Inicializar pela primeira vez
    }

    private void atualizarModelos() {
        cmbModelo.removeAllItems();
        String tipo = (String) cmbTipoVeiculo.getSelectedItem();

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
            // Validar campos
            if (txtPlaca.getText().trim().isEmpty() || txtAno.getText().trim().isEmpty() ||
                    txtValorCompra.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar placa no formato XXX-0000
            String placa = txtPlaca.getText().trim().toUpperCase();
            if (!placa.matches("[A-Z]{3}-\\d{4}")) {
                JOptionPane.showMessageDialog(this, "Placa deve estar no formato XXX-0000!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Obter dados do formulário
            Marca marca = (Marca) cmbMarca.getSelectedItem();
            Estado estado = (Estado) cmbEstado.getSelectedItem();
            Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
            double valorCompra = Double.parseDouble(txtValorCompra.getText().replace(",", "."));
            int ano = Integer.parseInt(txtAno.getText().trim());
            String tipo = (String) cmbTipoVeiculo.getSelectedItem();

            // Criar veículo baseado no tipo
            Veiculo veiculo = null;

            switch (tipo) {
                case "AUTOMOVEL":
                    ModeloAutomovel modeloAuto = (ModeloAutomovel) cmbModelo.getSelectedItem();
                    veiculo = new Automovel(marca, estado, categoria, valorCompra, placa, ano, modeloAuto);
                    break;

                case "MOTOCICLETA":
                    ModeloMotocicleta modeloMoto = (ModeloMotocicleta) cmbModelo.getSelectedItem();
                    veiculo = new Motocicleta(marca, estado, categoria, valorCompra, placa, ano, modeloMoto);
                    break;

                case "VAN":
                    ModeloVan modeloVan = (ModeloVan) cmbModelo.getSelectedItem();
                    veiculo = new Van(marca, estado, categoria, valorCompra, placa, ano, modeloVan);
                    break;
            }

            if (veiculo != null) {
                // TODO: Salvar no banco (precisamos criar o VeiculoDAO)
                JOptionPane.showMessageDialog(this,
                        "Veículo criado!\n" +
                                "Tipo: " + tipo + "\n" +
                                "Placa: " + placa + "\n" +
                                "Diária: R$ " + String.format("%.2f", veiculo.getValorDiariaLocacao()) + "\n" +
                                "Valor Venda: R$ " + String.format("%.2f", veiculo.getValorParaVenda()),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                limparCampos();
                carregarVeiculos();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ano e Valor Compra devem ser números válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar veículo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void carregarVeiculos() {
        // TODO: Carregar veículos do banco quando tivermos o DAO
        tableModel.setRowCount(0); // Limpa a tabela por enquanto

        // Exemplo de dados (remover depois)
        Object[] rowExemplo = {"AUTOMOVEL", "VW", "Gol", "ABC-1234", 2022, "POPULAR", "DISPONIVEL", 25000.00};
        tableModel.addRow(rowExemplo);
    }

    private void limparCampos() {
        txtPlaca.setText("");
        txtAno.setText("");
        txtValorCompra.setText("");
        cmbMarca.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        cmbCategoria.setSelectedIndex(0);
        cmbTipoVeiculo.setSelectedIndex(0);
    }
}