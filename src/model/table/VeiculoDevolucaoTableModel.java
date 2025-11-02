package view.table;

import model.*;
import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDevolucaoTableModel extends AbstractTableModel {

    private List<Veiculo> veiculos;
    // Colunas conforme PDF (4.a)
    private final String[] colunas = {"Cliente", "Placa", "Marca", "Modelo", "Ano", "Data Locação", "Preço Diária", "Dias", "Valor Locação"};

    public VeiculoDevolucaoTableModel() {
        this.veiculos = new ArrayList<>();
    }

    public VeiculoDevolucaoTableModel(List<Veiculo> veiculos) {
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
        Locacao locacao = veiculo.getLocacao();
        if (locacao == null) return null; // Segurança

        String modelo = "";
        if (veiculo instanceof Automovel) modelo = ((Automovel) veiculo).getModelo().name();
        else if (veiculo instanceof Motocicleta) modelo = ((Motocicleta) veiculo).getModelo().name();
        else if (veiculo instanceof Van) modelo = ((Van) veiculo).getModelo().name();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = sdf.format(locacao.getData().getTime());

        switch (columnIndex) {
            case 0: return locacao.getCliente().getNome() + " " + locacao.getCliente().getSobrenome();
            case 1: return veiculo.getPlaca();
            case 2: return veiculo.getMarca().name();
            case 3: return modelo;
            case 4: return veiculo.getAno();
            case 5: return dataFormatada;
            case 6: return String.format("R$ %.2f", veiculo.getValorDiariaLocacao());
            case 7: return locacao.getDias();
            case 8: return String.format("R$ %.2f", locacao.getValor());
            default: return null;
        }
    }
}