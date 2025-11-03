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

// *** NOVAS IMPORTAÇÕES ***
import java.util.Locale; // Para o formato "pt-BR" (1.000,00)
import javax.swing.text.PlainDocument; // Para aplicar o filtro
import view.utils.ValidacaoPreco; // <-- IMPORTAÇÃO DA CLASSE FALTANTE (AGORA COM NOME CERTO)

public class VeiculosFrame extends JFrame {
    private JComboBox<Marca> cmbMarca;
    private JComboBox<Estado> cmbEstado;
    private JComboBox<Categoria> cmbCategoria;
    private JComboBox<String> cmbTipoVeiculo;
    private JComboBox<Object> cmbModelo;
    private JFormattedTextField txtPlaca;
    private JTextField txtAno;

    // *** CAMPO DE VALOR ATUALIZADO (CAMPO ÚNICO) ***
    private JFormattedTextField txtValorCompra;

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

        // *** CAMPO 'VALOR COMPRA' CORRIGIDO (COM FORMATO 1.000,00) ***

        // 1. Cria um formato para o Locale "pt-BR" (Português, Brasil)
        // Isso garante que o separador de milhar é "." e o decimal é ","
        Locale brLocale = new Locale("pt", "BR");
        NumberFormat format = NumberFormat.getNumberInstance(brLocale);
        format.setMinimumFractionDigits(2); // Garante 2 casas decimais
        format.setMaximumFractionDigits(2); // Garante 2 casas decimais
        format.setGroupingUsed(true); // LIGA o separador de milhar (1.000)

        // 2. Cria o JFormattedTextField com esse formato
        // Este formato JÁ BLOQUEIA LETRAS automaticamente.
        txtValorCompra = new JFormattedTextField(format);
        txtValorCompra.setColumns(15); // Define um tamanho bom
        txtValorCompra.setValue(0.0); // Valor inicial

        // *** FIM DA ATUALIZAÇÃO ***

        txtAno = new JTextField(5);
        // *** LINHA CORRIGIDA (agora a classe ValidacaoPreco existe) ***
        ((PlainDocument) txtAno.getDocument()).setDocumentFilter(new ValidacaoPreco(4));

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

        // *** NOVO PAINEL DE VALOR (CAMPO ÚNICO) ***
        panelForm.add(new JLabel("Valor Compra:"));

        // Painel interno para "R$ [campo]"
        JPanel panelValor = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelValor.add(new JLabel("R$ ")); // "R$" fixo
        panelValor.add(txtValorCompra); // Campo único formatado

        panelForm.add(panelValor); // Adiciona o painel de valor ao formulário
        // *** FIM DO PAINEL DE VALOR ***

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

    // *** MÉTODO ATUALIZADO PARA LER O CAMPO ÚNICO ***
    private void salvarVeiculo() {
        try {
            // 1. Pega o valor (que é um Number, ex: 1500.50)
            Object valorObj = txtValorCompra.getValue();
            if (valorObj == null) {
                JOptionPane.showMessageDialog(this, "Valor de compra inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Converte para a String que o Controller espera (ex: "1500.50")
            // O .toString() de um Number (Double/Long) é o formato "1500.5"
            // Precisamos garantir o formato com ponto
            String valorCompraStr = String.format(Locale.US, "%.2f", ((Number)valorObj).doubleValue());


            if (modoEdicao) {
                // *** LÓGICA DE ATUALIZAÇÃO ***
                controller.atualizar(
                        veiculoSelecionado,
                        (Categoria) cmbCategoria.getSelectedItem(),
                        (Estado) cmbEstado.getSelectedItem(),
                        valorCompraStr // Passa a string numérica (ex: "1500.50")
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

    // *** MÉTODO ATUALIZADO PARA PREENCHER O CAMPO ÚNICO ***
    private void carregarVeiculoSelecionado() {
        if (veiculoSelecionado == null) return;

        // Carrega dados
        txtPlaca.setText(veiculoSelecionado.getPlaca());
        txtAno.setText(String.valueOf(veiculoSelecionado.getAno()));

        // Seta o valor (Double) no JFormattedTextField
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

    // *** MÉTODO ATUALIZADO PARA LIMPAR O CAMPO ÚNICO ***
    private void limparCampos() {
        txtPlaca.setText("");
        txtAno.setText("");
        txtValorCompra.setValue(0.0); // Reseta para 0,00
        cmbMarca.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        cmbCategoria.setSelectedIndex(0);
        cmbTipoVeiculo.setSelectedIndex(0);

        veiculoSelecionado = null;
        modoEdicao = false;
        tabelaVeiculos.clearSelection();

        atualizarEstadoBotoes();
    }

    // *** MÉTODO ATUALIZADO PARA HABILITAR/DESABILITAR O CAMPO ÚNICO ***
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