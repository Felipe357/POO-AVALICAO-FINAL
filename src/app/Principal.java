package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import view.TelaClube;

public final class Principal {
    private Principal() { }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                     | UnsupportedLookAndFeelException ex) {
                // Mantém o visual padrão do Swing se o Nimbus não estiver disponível.
            }
            TelaClube tela = new TelaClube();
            tela.setVisible(true);
        });
    }
}
