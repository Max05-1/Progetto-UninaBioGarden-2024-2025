import view.LoginFrame;
import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Taskbar;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.net.URL;

public class Main {
    public static void main(String[] args) {

        try {
            URL iconUrl = Main.class.getClassLoader().getResource("icons/app_icon.png");

            if (iconUrl != null) {
                Image image = new ImageIcon(iconUrl).getImage();

                if (Taskbar.isTaskbarSupported()) {
                    Taskbar.getTaskbar().setIconImage(image);
                }
            } else {
                System.err.println("Main: Impossibile trovare l'icona app_icon.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Main: Errore durante il caricamento dell'icona per il Dock.");
        }


        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Impossibile impostare il Look and Feel FlatLaf.");
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}