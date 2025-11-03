import view.MenuPrincipal;
import database.DatabaseInitializer;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SISTEMA LOCADORA DE VEÍCULOS ===");

        // 1. Inicializar banco de dados e criar tabelas
        System.out.println("Inicializando banco de dados");
        DatabaseInitializer.inicializar();

        // 2. Iniciar interface gráfica
        System.out.println("Iniciando interface gráfica");
        java.awt.EventQueue.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}