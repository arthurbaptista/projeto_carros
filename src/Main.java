import view.MenuPrincipal;
import database.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SISTEMA LOCADORA DE VEÍCULOS ===");

        // 1. Inicializar banco de dados e criar tabelas
        System.out.println("🔄 Inicializando banco de dados...");
        DatabaseInitializer.inicializar();

        // 2. Iniciar interface gráfica
        System.out.println("🖥️ Iniciando interface gráfica...");
        java.awt.EventQueue.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });

        System.out.println("✅ Sistema iniciado com sucesso!");
        System.out.println("📊 Estrutura do projeto:");
        System.out.println("   - 6 Enums criados");
        System.out.println("   - 7 Classes de modelo");
        System.out.println("   - 4 DAOs para persistência");
        System.out.println("   - 3 Telas Swing");
        System.out.println("   - Banco de dados configurado");
    }
}