package perpus;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import perpus.controller.AppContext;
import perpus.exception.PerpusException;
import perpus.view.LoginFrame;
import perpus.view.Ui;

/** Titik masuk aplikasi. */
public class Main {
    public static void main(String[] args) {
        // jaring pengaman: exception yang tidak tertangkap (bug) tampil sebagai dialog, bukan hanya di konsol
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            e.printStackTrace();
            Ui.tangani(null, e);
        });
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                // pakai tampilan bawaan bila Nimbus tidak tersedia
            }
            try {
                AppContext ctx = new AppContext();
                new LoginFrame(ctx).setVisible(true);
            } catch (PerpusException e) {
                JOptionPane.showMessageDialog(null, "Aplikasi gagal dimulai:\n" + e.getMessage(),
                        "Kesalahan", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
