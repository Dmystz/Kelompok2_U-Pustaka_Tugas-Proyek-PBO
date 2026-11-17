package perpus.view;

import java.awt.Component;
import java.awt.GridBagLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import perpus.controller.AppContext;
import perpus.exception.PerpusException;
import perpus.model.Pengguna;

/** Dialog ganti password untuk pengguna yang sedang login (petugas maupun peminjam). */
public final class GantiPasswordDialog {
    private GantiPasswordDialog() { }

    public static void tampilkan(Component induk, AppContext ctx, Pengguna pengguna) {
        JPasswordField lama = new JPasswordField(16);
        JPasswordField baru = new JPasswordField(16);
        JPasswordField konfirmasi = new JPasswordField(16);

        JPanel form = new JPanel(new GridBagLayout());
        Ui.baris(form, 0, "Password lama", lama);
        Ui.baris(form, 1, "Password baru", baru);
        Ui.baris(form, 2, "Ulangi password baru", konfirmasi);

        Object[] pilihan = {"Simpan", "Batal"};
        int hasil = JOptionPane.showOptionDialog(induk, form, "Ganti Password", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, pilihan, pilihan[0]);
        if (hasil != 0) return;
        try {
            ctx.login.gantiPassword(pengguna, new String(lama.getPassword()), new String(baru.getPassword()),
                    new String(konfirmasi.getPassword()));
            Ui.info(induk, "Password berhasil diganti.");
        } catch (PerpusException ex) {
            Ui.tangani(induk, ex);
        }
    }
}
