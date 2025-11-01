package dao;

import java.util.List;

public interface Dao<T> {
    void salvar(T entidade);

    void atualizar(T entidade);

    void excluir(T entidade);

    T buscarPorId(int id);

    List<T> listarTodos();
}