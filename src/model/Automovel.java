package model;

import enums.Marca;
import enums.Categoria;
import enums.Estado;
import enums.ModeloAutomovel;

public class Automovel extends Veiculo {
    private ModeloAutomovel modelo;

    public Automovel(Marca marca, Estado estado, Categoria categoria,
                     double valorDeCompra, String placa, int ano, ModeloAutomovel modelo) {
        super(marca, estado, categoria, valorDeCompra, placa, ano);
        this.modelo = modelo;
    }

    public ModeloAutomovel getModelo() {
        return modelo;
    }

    @Override
    public double getValorDiariaLocacao() {
        return switch (this.categoria) {
            case POPULAR -> 100.00;
            case INTERMEDIARIO -> 300.00;
            case LUXO -> 450.00;
        };
    }

    @Override
    public String toString() {
        return "Automovel: " + modelo + " - " + placa + " - " + categoria;
    }
}