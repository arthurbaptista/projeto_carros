package controller;

import dao.VeiculoDAO;
import enums.*;
import model.*;

import java.util.List;
import java.util.stream.Collectors;

public class VeiculoController {
    private VeiculoDAO veiculoDAO;

    public VeiculoController() {
        this.veiculoDAO = new VeiculoDAO();
    }

    public void salvar(String tipo, Marca marca, Estado estado, Categoria categoria,
                       String valorCompraStr, String placa, String anoStr, Object modeloObj) {

        if (placa.trim().isEmpty() || anoStr.trim().isEmpty() || valorCompraStr.trim().isEmpty()) {
            throw new RuntimeException("Preencha todos os campos (Placa, Ano, Valor Compra)!");
        }

        placa = placa.trim().toUpperCase();
        if (!placa.matches("[A-Z]{3}-\\d{4}")) {
            throw new RuntimeException("Placa deve estar no formato XXX-0000!");
        }

        double valorCompra;
        int ano;
        try {
            valorCompra = Double.parseDouble(valorCompraStr.replace(",", "."));
            ano = Integer.parseInt(anoStr.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Ano e Valor Compra devem ser números válidos!");
        }

        Veiculo veiculo = null;

        switch (tipo) {
            case "AUTOMOVEL":
                ModeloAutomovel modeloAuto = (ModeloAutomovel) modeloObj;
                veiculo = new Automovel(marca, estado, categoria, valorCompra, placa, ano, modeloAuto);
                break;

            case "MOTOCICLETA":
                ModeloMotocicleta modeloMoto = (ModeloMotocicleta) modeloObj;
                veiculo = new Motocicleta(marca, estado, categoria, valorCompra, placa, ano, modeloMoto);
                break;

            case "VAN":
                ModeloVan modeloVan = (ModeloVan) modeloObj;
                veiculo = new Van(marca, estado, categoria, valorCompra, placa, ano, modeloVan);
                break;
        }

        if (veiculo != null) {
            veiculoDAO.salvar(veiculo);
        } else {
            throw new RuntimeException("Tipo de veículo inválido!");
        }
    }

    public List<Veiculo> listarTodos() {
        return veiculoDAO.listarTodos();
    }

    public List<Veiculo> listarDisponiveis() {
        return veiculoDAO.listarDisponiveis();
    }

    public List<Veiculo> listarLocados() {
        return veiculoDAO.listarLocados();
    }

    public List<Veiculo> filtrarDisponiveis(String tipoFiltro, String marcaFiltro, String categoriaFiltro) {
        List<Veiculo> veiculos = veiculoDAO.listarDisponiveis();

        return veiculos.stream()
                .filter(veiculo -> tipoFiltro.equals("TODOS") ||
                        (tipoFiltro.equals("AUTOMOVEL") && veiculo instanceof Automovel) ||
                        (tipoFiltro.equals("MOTOCICLETA") && veiculo instanceof Motocicleta) ||
                        (tipoFiltro.equals("VAN") && veiculo instanceof Van))
                .filter(veiculo -> marcaFiltro.equals("TODAS") ||
                        veiculo.getMarca().name().equals(marcaFiltro))
                .filter(veiculo -> categoriaFiltro.equals("TODOS") ||
                        veiculo.getCategoria().name().equals(categoriaFiltro))
                .collect(Collectors.toList());
    }

    public Veiculo buscarPorPlaca(String placa) {
        return veiculoDAO.buscarPorPlaca(placa);
    }
}