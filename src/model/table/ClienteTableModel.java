package view.table;

import model.Cliente;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ClienteTableModel extends AbstractTableModel {

    private List<Cliente> clientes;
    private final String[] colunas = {"Nome", "Sobrenome", "RG", "CPF", "Endereço"};
    private dao.ClienteDAO daoFormatter = new dao.ClienteDAO();

    public ClienteTableModel() {
        this.clientes = new ArrayList<>();
    }

    public ClienteTableModel(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
        fireTableDataChanged(); // Notifica a JTable que os dados mudaram
    }

    public Cliente getClienteAt(int rowIndex) {
        return clientes.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return clientes.size();
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
        Cliente cliente = clientes.get(rowIndex);
        switch (columnIndex) {
            case 0: return cliente.getNome();
            case 1: return cliente.getSobrenome();
            case 2: return daoFormatter.formatarRGExibicao(cliente.getRg());
            case 3: return daoFormatter.formatarCPFExibicao(cliente.getCpf());
            case 4: return cliente.getEndereco();
            default: return null;
        }
    }
}