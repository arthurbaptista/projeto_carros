package view.table;

import model.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class VeiculoLocacaoTableModel extends AbstractTableModel {

    private List<Veiculo> veiculos;
    // Colunas conforme PDF (3.c)
    private final String[] colunas = {"Placa", "Marca", "Modelo", "Ano", "Preço da diária"};

    public VeiculoLocacaoTableModel() {
        this.veiculos = new ArrayList<>();
    }

    public VeiculoLocacaoTableModel(List<Veiculo> veiculos) {
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
        String modelo = "";

        if (veiculo instanceof Automovel) {
            modelo = ((Automovel) veiculo).getModelo().name();
        } else if (veiculo instanceof Motocicleta) {
            modelo = ((Motocicleta) veiculo).getModelo().name();
        } else if (veiculo instanceof Van) {
            modelo = ((Van) veiculo).getModelo().name();
        }

        switch (columnIndex) {
            case 0: return veiculo.getPlaca();
            case 1: return veiculo.getMarca().name();
            case 2: return modelo;
            case 3: return veiculo.getAno(); // PDF pede 4 dígitos, int já faz isso
            case 4: return String.format("R$ %.2f", veiculo.getValorDiariaLocacao()); // PDF pede R$XXX,XX
            default: return null;
        }
    }
}