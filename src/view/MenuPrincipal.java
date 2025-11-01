package view;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {
    private JButton btnClientes, btnVeiculos, btnLocacao, btnDevolucao, btnVenda;

    public MenuPrincipal() {
        initComponents();
        setupLayout();
        setupListeners();
    }

    private void initComponents() {
        setTitle("Sistema Locadora de Veículos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        btnClientes = new JButton("Gerenciar Clientes");
        btnVeiculos = new JButton("Cadastrar Veículos");
        btnLocacao = new JButton("Locar Veículos");
        btnDevolucao = new JButton("Devolver Veículos");
        btnVenda = new JButton("Vender Veículos");

        // Aumentar tamanho dos botões
        Font font = new Font("Arial", Font.BOLD, 14);
        btnClientes.setFont(font);
        btnVeiculos.setFont(font);
        btnLocacao.setFont(font);
        btnDevolucao.setFont(font);
        btnVenda.setFont(font);
    }

    private void setupLayout() {
        setLayout(new GridLayout(5, 1, 10, 10));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        panel.add(btnClientes);
        panel.add(btnVeiculos);
        panel.add(btnLocacao);
        panel.add(btnDevolucao);
        panel.add(btnVenda);

        add(panel);
    }

    private void setupListeners() {
        btnClientes.addActionListener(e -> {
            new ClientesFrame().setVisible(true);
        });

        btnVeiculos.addActionListener(e -> {
            new VeiculosFrame().setVisible(true);
        });

        btnLocacao.addActionListener(e -> {
            new LocacaoFrame().setVisible(true);
        });

        btnDevolucao.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Tela de Devolução em desenvolvimento!");
        });

        btnVenda.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Tela de Venda em desenvolvimento!");
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}