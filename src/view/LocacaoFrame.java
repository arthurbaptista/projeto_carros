package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Calendar;
import java.util.List;
import model.*;
import enums.*;
import dao.*;

public class LocacaoFrame extends JFrame {
    private JTextField txtBuscaCliente, txtDias, txtData;
    private JComboBox<String> cmbTipoVeiculo, cmbMarca, cmbCategoria;
    private JButton btnBuscarCliente, btnFiltrar, btnLocar;
    private JTable tabelaVeiculos;
    private DefaultTableModel tableModel;
    private ClienteDAO clienteDAO;
    private VeiculoDAO veiculoDAO;
    private Cliente clienteSelecionado;
    private Veiculo veiculoSelecionado;

    public LocacaoFrame() {
        this.clienteDAO = new ClienteDAO();
        this.veiculoDAO = new VeiculoDAO();
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
        txtDias = new JTextField(5);
        txtData = new JTextField(10);
        txtData.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" +
                (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" +
                Calendar.getInstance().get(Calendar.YEAR));

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
        String[] colunas = {"Placa", "Tipo", "Marca", "Modelo", "Ano", "Categoria", "Preço Diária"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
        };
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
        panelCliente.add(txtDias);
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
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tabelaVeiculos.getSelectedRow();
                if (selectedRow != -1) {
                    String placa = (String) tableModel.getValueAt(selectedRow, 0);
                    veiculoSelecionado = veiculoDAO.buscarPorPlaca(placa);
                    atualizarEstadoBotaoLocar();
                }
            }
        });
    }

    private void buscarCliente() {
        String busca = txtBuscaCliente.getText().trim();
        if (busca.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um nome ou CPF para buscar!");
            return;
        }

        List<Cliente> clientes = clienteDAO.listarTodos();
        for (Cliente cliente : clientes) {
            if (cliente.getNome().toLowerCase().contains(busca.toLowerCase()) ||
                    cliente.getCpf().contains(busca)) {
                clienteSelecionado = cliente;
                JOptionPane.showMessageDialog(this,
                        "Cliente selecionado:\n" +
                                "Nome: " + cliente.getNome() + " " + cliente.getSobrenome() + "\n" +
                                "CPF: " + cliente.getCpf(), "Cliente Encontrado", JOptionPane.INFORMATION_MESSAGE);
                atualizarEstadoBotaoLocar();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Cliente não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void carregarVeiculosDisponiveis() {
        tableModel.setRowCount(0);
        List<Veiculo> veiculos = veiculoDAO.listarDisponiveis();

        for (Veiculo veiculo : veiculos) {
            adicionarVeiculoNaTabela(veiculo);
        }
    }

    private void filtrarVeiculos() {
        tableModel.setRowCount(0);
        List<Veiculo> veiculos = veiculoDAO.listarDisponiveis();
        String tipoFiltro = (String) cmbTipoVeiculo.getSelectedItem();
        String marcaFiltro = (String) cmbMarca.getSelectedItem();
        String categoriaFiltro = (String) cmbCategoria.getSelectedItem();

        for (Veiculo veiculo : veiculos) {
            // Aplicar filtros
            boolean passaTipo = tipoFiltro.equals("TODOS") ||
                    (tipoFiltro.equals("AUTOMOVEL") && veiculo instanceof Automovel) ||
                    (tipoFiltro.equals("MOTOCICLETA") && veiculo instanceof Motocicleta) ||
                    (tipoFiltro.equals("VAN") && veiculo instanceof Van);

            boolean passaMarca = marcaFiltro.equals("TODAS") ||
                    veiculo.getMarca().name().equals(marcaFiltro);

            boolean passaCategoria = categoriaFiltro.equals("TODOS") ||
                    veiculo.getCategoria().name().equals(categoriaFiltro);

            if (passaTipo && passaMarca && passaCategoria) {
                adicionarVeiculoNaTabela(veiculo);
            }
        }
    }

    private void adicionarVeiculoNaTabela(Veiculo veiculo) {
        String tipo = "";
        String modelo = "";

        if (veiculo instanceof Automovel) {
            tipo = "AUTOMOVEL";
            modelo = ((Automovel) veiculo).getModelo().name();
        } else if (veiculo instanceof Motocicleta) {
            tipo = "MOTOCICLETA";
            modelo = ((Motocicleta) veiculo).getModelo().name();
        } else if (veiculo instanceof Van) {
            tipo = "VAN";
            modelo = ((Van) veiculo).getModelo().name();
        }

        Object[] row = {
                veiculo.getPlaca(),
                tipo,
                veiculo.getMarca().name(),
                modelo,
                veiculo.getAno(),
                veiculo.getCategoria().name(),
                String.format("R$ %.2f", veiculo.getValorDiariaLocacao())
        };
        tableModel.addRow(row);
    }

    private void locarVeiculo() {
        try {
            if (clienteSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um cliente primeiro!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (veiculoSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Selecione um veículo da tabela!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int dias = Integer.parseInt(txtDias.getText().trim());
            if (dias <= 0) {
                JOptionPane.showMessageDialog(this, "Número de dias deve ser maior que zero!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar data (formato simples dd/MM/yyyy)
            String[] dataParts = txtData.getText().split("/");
            if (dataParts.length != 3) {
                JOptionPane.showMessageDialog(this, "Data deve estar no formato dd/MM/yyyy!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calcular valor total
            double valorTotal = veiculoSelecionado.getValorDiariaLocacao() * dias;

            // Confirmar locação
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Confirmar locação?\n\n" +
                            "Cliente: " + clienteSelecionado.getNome() + " " + clienteSelecionado.getSobrenome() + "\n" +
                            "Veículo: " + veiculoSelecionado.getPlaca() + " - " + veiculoSelecionado.getMarca() + "\n" +
                            "Dias: " + dias + "\n" +
                            "Valor total: R$ " + String.format("%.2f", valorTotal),
                    "Confirmar Locação", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Criar data (simplificado)
                Calendar dataLocacao = Calendar.getInstance();
                dataLocacao.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataParts[0]));
                dataLocacao.set(Calendar.MONTH, Integer.parseInt(dataParts[1]) - 1);
                dataLocacao.set(Calendar.YEAR, Integer.parseInt(dataParts[2]));

                // Executar locação
                veiculoSelecionado.locar(dias, dataLocacao, clienteSelecionado);
                veiculoDAO.atualizarEstado(veiculoSelecionado.getPlaca(), Estado.LOCADO);

                JOptionPane.showMessageDialog(this,
                        "Locação realizada com sucesso!\n" +
                                "Valor total: R$ " + String.format("%.2f", valorTotal),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // Limpar e recarregar
                clienteSelecionado = null;
                veiculoSelecionado = null;
                txtBuscaCliente.setText("");
                txtDias.setText("");
                atualizarEstadoBotaoLocar();
                carregarVeiculosDisponiveis();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Número de dias deve ser um valor válido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao realizar locação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarEstadoBotaoLocar() {
        btnLocar.setEnabled(clienteSelecionado != null && veiculoSelecionado != null);
    }
}