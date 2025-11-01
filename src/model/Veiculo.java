package model;

import enums.Estado;
import enums.Marca;
import enums.Categoria;
import java.util.Calendar;

public abstract class Veiculo implements VeiculoT {
    protected Marca marca;
    protected Estado estado;
    protected Locacao locacao;
    protected Categoria categoria;
    protected double valorDeCompra;
    protected String placa;
    protected int ano;

    public Veiculo(Marca marca, Estado estado, Categoria categoria,
                   double valorDeCompra, String placa, int ano) {
        this.marca = marca;
        this.estado = estado;
        this.categoria = categoria;
        this.valorDeCompra = valorDeCompra;
        this.placa = placa;
        this.ano = ano;
        this.locacao = null;
    }

    @Override
    public void locar(int dias, Calendar data, Cliente cliente) {
        if (this.estado != Estado.DISPONIVEL) {
            throw new IllegalStateException("Veículo não está disponível para locação");
        }

        double valorLocacao = getValorDiariaLocacao() * dias;
        this.locacao = new Locacao(dias, valorLocacao, data, cliente);
        this.estado = Estado.LOCADO;
    }

    @Override
    public void vender() {
        this.estado = Estado.VENDIDO;
        this.locacao = null;
    }

    @Override
    public void devolver() {
        this.estado = Estado.DISPONIVEL;
        this.locacao = null;
    }

    // Getters implementados da interface
    @Override
    public Estado getEstado() { return estado; }

    @Override
    public Marca getMarca() { return marca; }

    @Override
    public Categoria getCategoria() { return categoria; }

    @Override
    public Locacao getLocacao() { return locacao; }

    @Override
    public String getPlaca() { return placa; }

    @Override
    public int getAno() { return ano; }

    @Override
    public double getValorParaVenda() {
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        int idadeVeiculo = anoAtual - this.ano;

        double valorVenda = valorDeCompra - (idadeVeiculo * 0.15 * valorDeCompra);

        // Se valor for menor que 10% do valor de compra ou negativo
        if (valorVenda < (valorDeCompra * 0.1) || valorVenda < 0) {
            valorVenda = valorDeCompra * 0.1;
        }

        return valorVenda;
    }

    // Método abstrato que as classes filhas devem implementar
    @Override
    public abstract double getValorDiariaLocacao();
}