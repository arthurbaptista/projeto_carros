package view.table;

import model.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class VeiculoTableModel extends AbstractTableModel {

    private List<Veiculo> veiculos;
    private final String[] colunas = {"Tipo", "Marca", "Modelo", "Placa", "Ano", "Categoria", "Estado", "Valor Compra"};

    public VeiculoTableModel() {
        this.veiculos = new ArrayList<>();
    }

    public VeiculoTableModel(List<Veiculo> veiculos) {
        this.veiculos = veiculos;
    }

    public void setVeiculos(List<Veiculo> veiculos) {
        this.veiculos = veiculos;
        fireTableDataChanged();
    }

    public Veiculo getVeiculoAt(int rowIndex) {
        return veiculos.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return veiculos.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Veiculo veiculo = veiculos.get(rowIndex);
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

        switch (columnIndex) {
            case 0: return tipo;
            case 1: return veiculo.getMarca().name();
            case 2: return modelo;
            case 3: return veiculo.getPlaca();
            case 4: return veiculo.getAno();
            case 5: return veiculo.getCategoria().name();
            case 6: return veiculo.getEstado().name();
            case 7: return String.format("R$ %.2f", veiculo.getValorDeCompra());
            default: return null;
        }
    }
}