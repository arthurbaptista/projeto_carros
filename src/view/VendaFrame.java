package view;

import controller.LocacaoController;
import controller.VeiculoController;
import model.Veiculo;
import view.table.VeiculoVendaTableModel;
import enums.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VendaFrame extends JFrame {
    private JTable tabelaVeiculosVenda;
    private VeiculoVendaTableModel tableModel;
    private JButton btnVender, btnFiltrar;
    private JComboBox<String> cmbTipoVeiculo, cmbMarca, cmbCategoria;
    private LocacaoController locacaoController;
    private VeiculoController veiculoController;
    private Veiculo veiculoSelecionado;

    public VendaFrame() {
        this.locacaoController = new LocacaoController();
        this.veiculoController = new VeiculoController();
        initComponents();
        setupLayout();
        setupListeners();
        carregarVeiculosDisponiveis();
    }

    private void initComponents() {
        setTitle("Venda de Veículos");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Comboboxes para filtros
        cmbTipoVeiculo = new JComboBox<>(new String[]{"TODOS", "AUTOMOVEL", "MOTOCICLETA", "VAN"});
        cmbMarca = new JComboBox<>();
        cmbCategoria = new JComboBox<>(new String[]{"TODOS", "POPULAR", "INTERMEDIARIO", "LUXO"});

        // Popular marcas
        cmbMarca.addItem("TODAS");
        for (Marca marca : Marca.values()) {
            cmbMarca.addItem(marca.name());
        }

        // Tabela
        tableModel = new VeiculoVendaTableModel();
        tabelaVeiculosVenda = new JTable(tableModel);
        tabelaVeiculosVenda.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Botões
        btnVender = new JButton("Vender Veículo");
        btnFiltrar = new JButton("Filtrar Veículos");
        btnVender.setEnabled(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Painel de filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtrar Veículos Disponíveis para Venda"));
        panelFiltros.add(new JLabel("Tipo:"));
        panelFiltros.add(cmbTipoVeiculo);
        panelFiltros.add(new JLabel("Marca:"));
        panelFiltros.add(cmbMarca);
        panelFiltros.add(new JLabel("Categoria:"));
        panelFiltros.add(cmbCategoria);
        panelFiltros.add(btnFiltrar);

        // Tabela
        JScrollPane scrollPane = new JScrollPane(tabelaVeiculosVenda);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Veículos Disponíveis"));

        // Painel de botões
        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.add(btnVender);

        add(panelFiltros, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnFiltrar.addActionListener(e -> filtrarVeiculos());
        btnVender.addActionListener(e -> venderVeiculo());

        tabelaVeiculosVenda.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaVeiculosVenda.getSelectedRow();
                if (selectedRow != -1) {
                    veiculoSelecionado = tableModel.getVeiculoAt(selectedRow);
                    btnVender.setEnabled(true);
                } else {
                    veiculoSelecionado = null;
                    btnVender.setEnabled(false);
                }
            }
        });
    }

    private void carregarVeiculosDisponiveis() {
        tableModel.setVeiculos(veiculoController.listarDisponiveis());
    }

    private void filtrarVeiculos() {
        List<Veiculo> veiculosFiltrados = veiculoController.filtrarDisponiveis(
                (String) cmbTipoVeiculo.getSelectedItem(),
                (String) cmbMarca.getSelectedItem(),
                (String) cmbCategoria.getSelectedItem()
        );
        tableModel.setVeiculos(veiculosFiltrados);
        veiculoSelecionado = null;
        btnVender.setEnabled(false);
        tabelaVeiculosVenda.clearSelection();
    }

    private void venderVeiculo() {
        try {
            double valorVenda = veiculoSelecionado.getValorParaVenda();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirmar a venda do veículo: " + veiculoSelecionado.getPlaca() + "\n" +
                            "Pelo valor de: R$ " + String.format("%.2f", valorVenda),
                    "Confirmar Venda", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                locacaoController.venderVeiculo(veiculoSelecionado);
                JOptionPane.showMessageDialog(this, "Veículo vendido com sucesso!");
                filtrarVeiculos(); // Atualiza a lista (ele não estará mais como DISPONIVEL)
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao vender veículo: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}