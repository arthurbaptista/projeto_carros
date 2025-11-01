import enums.*;
import model.Cliente;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA LOCADORA DE VEÍCULOS ===");

        // Teste dos enums
        System.out.println("Marcas disponíveis:");
        for (Marca marca : Marca.values()) {
            System.out.println("- " + marca);
        }

        // Teste da classe Cliente
        Cliente cliente = new Cliente("João", "Silva", "1234567", "111.222.333-44", "Rua A, 123");
        System.out.println("\nCliente criado: " + cliente);

        // Teste dos estados
        System.out.println("\nEstados possíveis:");
        for (Estado estado : Estado.values()) {
            System.out.println("- " + estado);
        }

        System.out.println("\n✅ Estrutura básica criada com sucesso!");
        System.out.println("📝 Próximo passo: Criar Automovel, Motocicleta e Van");
    }
}