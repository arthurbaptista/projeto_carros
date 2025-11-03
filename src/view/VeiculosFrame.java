package view;

import javax.swing.*;
import java.awt.*;
import model.*;
import enums.*;
import controller.VeiculoController; // Importa o Controller
import view.table.VeiculoTableModel; // Importa o TableModel
import javax.swing.text.MaskFormatter; // Para máscaras
import java.text.NumberFormat; // Para máscara de valor

// Imports que estavam faltando para o método 'atualizarModelos'
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
    private JButton btnSalvar, btnLimpar, btnEditar, btnExcluir; // NOVOS BOTÕES
    private JTable tabelaVeiculos;
    private VeiculoTableModel tableModel; // Usa o AbstractTableModel
    private VeiculoController controller; // Usa o Controller

    private Veiculo veiculoSelecionado; // Para saber quem está sendo editado
    private boolean modoEdicao = false; // Para controlar o estado da tela

    public VeiculosFrame() {
        this.controller = new VeiculoController();
        initComponents();
        setupLayout();
        setupListeners();
        carregarVeiculos();
        atualizarEstadoBotoes(); // Estado inicial
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

        // *** CAMPO 'VALOR COMPRA' ATUALIZADO ***
        // Use NumberInstance (ex: 50000.00) em vez de CurrencyInstance (ex: R$ 50.000,00)
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setGroupingUsed(false); // IMPORTANTE: Tira o "50,000" -> "50000"
        format.setMinimumFractionDigits(2); // Garante 2 casas decimais
        format.setMaximumFractionDigits(2); // Garante 2 casas decimais
        txtValorCompra = new JFormattedTextField(format);
        txtValorCompra.setColumns(15);
        txtValorCompra.setValue(0.0); // Valor inicial

        txtAno = new JTextField(5);

        // Botões
        btnSalvar = new JButton("Salvar Novo");
        btnLimpar = new JButton("Limpar Campos");
        btnEditar = new JButton("Editar Veículo"); // NOVO
        btnExcluir = new JButton("Excluir Veículo"); // NOVO

        // Tabela
        tableModel = new VeiculoTableModel(); // Usa o novo TableModel
        tabelaVeiculos = new JTable(tableModel);
        tabelaVeiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar um

        // Configurar combobox de tipo para atualizar modelos
        cmbTipoVeiculo.addActionListener(e -> atualizarModelos());
        atualizarModelos(); // Inicializar pela primeira vez
    }

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
        panelForm.add(txtValorCompra); // Adiciona o campo formatado

        // Painel estado (separado)
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEstado.add(new JLabel("Estado:"));
        panelEstado.add(cmbEstado);

        // Painel de botões
        JPanel panelBotoes = new JPanel(new FlowLayout());
        panelBotoes.add(btnSalvar);
        panelBotoes.add(btnEditar);   // NOVO
        panelBotoes.add(btnExcluir);  // NOVO
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
        btnEditar.addActionListener(e -> editarVeiculo());     // NOVO
        btnExcluir.addActionListener(e -> excluirVeiculo());   // NOVO

        // NOVO: Listener da Tabela para carregar dados
        tabelaVeiculos.getSelectionModel().addListSelectionListener(e -> {

            // *** LINHA CORRIGIDA ***
            if (!e.getValueIsAdjusting()) {

                int selectedRow = tabelaVeiculos.getSelectedRow();
                if (selectedRow != -1) {
                    // Pega o veículo do modelo e carrega no form
                    veiculoSelecionado = tableModel.getVeiculoAt(selectedRow);
                    carregarVeiculoSelecionado();
                    modoEdicao = true;
                } else {
                    veiculoSelecionado = null;
                    modoEdicao = false;
                }
                atualizarEstadoBotoes();
            }
        });
    }

    private void salvarVeiculo() {
        try {
            // Pega o valor (que é um Number) e converte para String
            Object valorObj = txtValorCompra.getValue();
            String valorCompraStr = (valorObj != null) ? valorObj.toString() : "0";

            if (modoEdicao) {
                // *** LÓGICA DE ATUALIZAÇÃO ***
                controller.atualizar(
                        veiculoSelecionado,
                        (Categoria) cmbCategoria.getSelectedItem(),
                        (Estado) cmbEstado.getSelectedItem(),
                        valorCompraStr // Passa a string numérica (ex: "50000.00")
                );
                JOptionPane.showMessageDialog(this, "Veículo atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } else {
                // *** LÓGICA DE SALVAR NOVO (existente) ***
                String tipo = (String) cmbTipoVeiculo.getSelectedItem();
                Marca marca = (Marca) cmbMarca.getSelectedItem();
                Estado estado = (Estado) cmbEstado.getSelectedItem();
                Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
                Object modelo = cmbModelo.getSelectedItem();

                controller.salvar(
                        tipo, marca, estado, categoria,
                        valorCompraStr,
                        txtPlaca.getText(),
                        txtAno.getText(),
                        modelo
                );
                JOptionPane.showMessageDialog(this, "Veículo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

            limparCampos(); // Reseta a tela
            carregarVeiculos(); // Recarrega a tabela

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void editarVeiculo() {
        // Apenas entra no modo de edição. A seleção da tabela já carregou os dados.
        // O usuário irá clicar em "Salvar Edição" (o botão btnSalvar)
        if (veiculoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um veículo na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modoEdicao = true;
        atualizarEstadoBotoes();
    }

    private void excluirVeiculo() {
        if (veiculoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um veículo na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o veículo: " + veiculoSelecionado.getPlaca() + "?\n" +
                        "Esta ação não pode ser desfeita.",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.excluir(veiculoSelecionado);
                JOptionPane.showMessageDialog(this, "Veículo excluído com sucesso!");
                limparCampos();
                carregarVeiculos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarVeiculos() {
        // Carrega do banco via controller
        tableModel.setVeiculos(controller.listarTodos());
    }

    // NOVO: Carrega dados do veículo selecionado nos campos
    private void carregarVeiculoSelecionado() {
        if (veiculoSelecionado == null) return;

        // Carrega dados
        txtPlaca.setText(veiculoSelecionado.getPlaca());
        txtAno.setText(String.valueOf(veiculoSelecionado.getAno()));
        txtValorCompra.setValue(veiculoSelecionado.getValorDeCompra());
        cmbMarca.setSelectedItem(veiculoSelecionado.getMarca());
        cmbEstado.setSelectedItem(veiculoSelecionado.getEstado());
        cmbCategoria.setSelectedItem(veiculoSelecionado.getCategoria());

        // Lógica para TIPO e MODELO (que não devem ser editados)
        if (veiculoSelecionado instanceof Automovel) {
            cmbTipoVeiculo.setSelectedItem("AUTOMOVEL");
            atualizarModelos(); // Atualiza a lista de modelos
            cmbModelo.setSelectedItem(((Automovel) veiculoSelecionado).getModelo());
        } else if (veiculoSelecionado instanceof Motocicleta) {
            cmbTipoVeiculo.setSelectedItem("MOTOCICLETA");
            atualizarModelos();
            cmbModelo.setSelectedItem(((Motocicleta) veiculoSelecionado).getModelo());
        } else if (veiculoSelecionado instanceof Van) {
            cmbTipoVeiculo.setSelectedItem("VAN");
            atualizarModelos();
            cmbModelo.setSelectedItem(((Van) veiculoSelecionado).getModelo());
        }
    }

    // ATUALIZADO: Limpa campos e reseta o modo de edição
    private void limparCampos() {
        txtPlaca.setText("");
        txtAno.setText("");
        txtValorCompra.setValue(0.0); // Limpa campo formatado
        cmbMarca.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        cmbCategoria.setSelectedIndex(0);
        cmbTipoVeiculo.setSelectedIndex(0);

        veiculoSelecionado = null;
        modoEdicao = false;
        tabelaVeiculos.clearSelection();

        atualizarEstadoBotoes();
    }

    // NOVO: Gerencia quais botões e campos estão ativos
    private void atualizarEstadoBotoes() {
        boolean veiculoSelecionado = this.veiculoSelecionado != null;

        if (modoEdicao) {
            // -- MODO DE EDIÇÃO --
            btnSalvar.setText("Salvar Edição");
            btnSalvar.setEnabled(true);
            btnLimpar.setText("Cancelar Edição");
            btnEditar.setEnabled(false); // Já está editando
            btnExcluir.setEnabled(true); // Pode excluir

            // Campos que não podem ser editados (IDs, Tipos)
            txtPlaca.setEnabled(false);
            txtAno.setEnabled(false);
            cmbTipoVeiculo.setEnabled(false);
            cmbModelo.setEnabled(false);
            cmbMarca.setEnabled(false);

            // Campos que podem ser editados
            cmbCategoria.setEnabled(true);
            cmbEstado.setEnabled(true);
            txtValorCompra.setEnabled(true);

        } else {
            // -- MODO DE CRIAÇÃO (NOVO) --
            btnSalvar.setText("Salvar Novo");
            btnSalvar.setEnabled(true);
            btnLimpar.setText("Limpar Campos");
            btnEditar.setEnabled(veiculoSelecionado); // Só pode editar se selecionar
            btnExcluir.setEnabled(veiculoSelecionado); // Só pode excluir se selecionar

            // Todos os campos habilitados
            txtPlaca.setEnabled(true);
            txtAno.setEnabled(true);
            cmbTipoVeiculo.setEnabled(true);
            cmbModelo.setEnabled(true);
            cmbMarca.setEnabled(true);
            cmbCategoria.setEnabled(true);
            cmbEstado.setEnabled(true);
            txtValorCompra.setEnabled(true);
        }
    }
}