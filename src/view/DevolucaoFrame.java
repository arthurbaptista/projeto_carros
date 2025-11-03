package view;

import controller.LocacaoController;
import controller.VeiculoController;
import model.Veiculo;
import view.table.VeiculoDevolucaoTableModel;

import javax.swing.*;
import java.awt.*;

public class DevolucaoFrame extends JFrame {
    private JTable tabelaVeiculosLocados;
    private VeiculoDevolucaoTableModel tableModel;
    private JButton btnDevolver, btnAtualizar;
    private LocacaoController locacaoController;
    private VeiculoController veiculoController;
    private Veiculo veiculoSelecionado;

    public DevolucaoFrame() {
        this.locacaoController = new LocacaoController();
        this.veiculoController = new VeiculoController();
        initComponents();
        setupLayout();
        setupListeners();
        carregarVeiculosLocados();
    }

    private void initComponents() {
        setTitle("Devolução de Veículos");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Tabela
        tableModel = new VeiculoDevolucaoTableModel();
        tabelaVeiculosLocados = new JTable(tableModel);
        tabelaVeiculosLocados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Botões
        btnDevolver = new JButton("Devolver Veículo");
        btnAtualizar = new JButton("Atualizar Lista");
        btnDevolver.setEnabled(false);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Tabela
        JScrollPane scrollPane = new JScrollPane(tabelaVeiculosLocados);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Veículos Locados"));

        // Painel de botões
        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.add(btnAtualizar);
        panelBotoes.add(btnDevolver);

        add(scrollPane, BorderLayout.CENTER);
        add(panelBotoes, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnAtualizar.addActionListener(e -> carregarVeiculosLocados());
        btnDevolver.addActionListener(e -> devolverVeiculo());

        tabelaVeiculosLocados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaVeiculosLocados.getSelectedRow();
                if (selectedRow != -1) {
                    veiculoSelecionado = tableModel.getVeiculoAt(selectedRow);
                    btnDevolver.setEnabled(true);
                } else {
                    veiculoSelecionado = null;
                    btnDevolver.setEnabled(false);
                }
            }
        });
    }

    private void carregarVeiculosLocados() {
        tableModel.setVeiculos(veiculoController.listarLocados());
        veiculoSelecionado = null;
        btnDevolver.setEnabled(false);
        tabelaVeiculosLocados.clearSelection();
    }

    private void devolverVeiculo() {
        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirmar a devolução do veículo: " + veiculoSelecionado.getPlaca() + "?",
                    "Confirmar Devolução", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                locacaoController.devolverVeiculo(veiculoSelecionado);
                JOptionPane.showMessageDialog(this, "Veículo devolvido com sucesso!");
                carregarVeiculosLocados(); // Atualiza a lista
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao devolver veículo: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}