import view.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Titik masuk aplikasi (entry point).
 * Menjalankan jendela utama (MainFrame) di Event Dispatch Thread.
 */
public class Main {

    public static void main(String[] args) {
        // Pakai tampilan bawaan sistem operasi agar UI terlihat lebih natural
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Abaikan; gunakan look & feel default jika gagal
        }

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
