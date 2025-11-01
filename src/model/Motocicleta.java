package model;

import enums.Marca;
import enums.Categoria;
import enums.Estado;
import enums.ModeloMotocicleta;

public class Motocicleta extends Veiculo {
    private ModeloMotocicleta modelo;

    public Motocicleta(Marca marca, Estado estado, Categoria categoria,
                       double valorDeCompra, String placa, int ano, ModeloMotocicleta modelo) {
        super(marca, estado, categoria, valorDeCompra, placa, ano);
        this.modelo = modelo;
    }

    public ModeloMotocicleta getModelo() {
        return modelo;
    }

    @Override
    public double getValorDiariaLocacao() {
        return switch (this.categoria) {
            case POPULAR -> 70.00;
            case INTERMEDIARIO -> 200.00;
            case LUXO -> 350.00;
        };
    }

    @Override
    public String toString() {
        return "Motocicleta: " + modelo + " - " + placa + " - " + categoria;
    }
}