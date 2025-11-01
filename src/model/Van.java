package model;

import enums.Marca;
import enums.Categoria;
import enums.Estado;
import enums.ModeloVan;

public class Van extends Veiculo {
    private ModeloVan modelo;

    public Van(Marca marca, Estado estado, Categoria categoria,
               double valorDeCompra, String placa, int ano, ModeloVan modelo) {
        super(marca, estado, categoria, valorDeCompra, placa, ano);
        this.modelo = modelo;
    }

    public ModeloVan getModelo() {
        return modelo;
    }

    @Override
    public double getValorDiariaLocacao() {
        return switch (this.categoria) {
            case POPULAR -> 200.00;
            case INTERMEDIARIO -> 400.00;
            case LUXO -> 600.00;
        };
    }

    @Override
    public String toString() {
        return "Van: " + modelo + " - " + placa + " - " + categoria;
    }
}