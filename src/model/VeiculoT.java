package model;

import enums.Estado;
import enums.Marca;
import enums.Categoria;
import java.util.Calendar;

public interface VeiculoT {
    void locar(int dias, Calendar data, Cliente cliente);
    void vender();
    void devolver();
    Estado getEstado();
    Marca getMarca();
    Categoria getCategoria();
    Locacao getLocacao();
    String getPlaca();
    int getAno();
    double getValorParaVenda();
    double getValorDiariaLocacao();
}