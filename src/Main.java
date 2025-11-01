import enums.*;
import model.*;
import dao.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA LOCADORA DE VEÍCULOS ===");

        // Testar conexão com banco
        DatabaseConnection.getConnection();

        // Teste DAO Cliente
        ClienteDAO clienteDAO = new ClienteDAO();

        // Criar e salvar cliente
        Cliente cliente = new Cliente("Maria", "Santos", "7654321", "999.888.777-66", "Av. Principal, 456");
        clienteDAO.salvar(cliente);

        // Listar clientes
        System.out.println("\n📋 Clientes cadastrados:");
        for (Cliente c : clienteDAO.listarTodos()) {
            System.out.println(" - " + c);
        }

        System.out.println("\n✅ Banco de dados e DAO testados com sucesso!");
    }
}