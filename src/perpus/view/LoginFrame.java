package perpus.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import perpus.controller.AppContext;
import perpus.exception.PerpusException;
import perpus.model.Pengguna;

/** Halaman login petugas. */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final AppContext ctx;
    private final JTextField tfUser = new JTextField(16);
    private final JPasswordField pfPass = new JPasswordField(16);
    private final JButton btnMasuk = new JButton("Masuk");

    public LoginFrame(AppContext ctx) {
        super("Login - U-Pustaka");
        this.ctx = ctx;

        setIconImages(Logo.gambarBanyak());

        JLabel judul = Logo.vertikal(150);
        judul.setBorder(BorderFactory.createEmptyBorder(18, 10, 6, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        Ui.baris(form, 0, "Username", tfUser);
        Ui.baris(form, 1, "Password", pfPass);

        JLabel petunjuk = new JLabel("<html><center>Anggota: NIM atau NIP (User dan Pass sama)</center></html>", SwingConstants.CENTER);
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tombol.add(btnMasuk);

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.add(tombol, BorderLayout.NORTH);
        bawah.add(petunjuk, BorderLayout.SOUTH);
        bawah.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        setLayout(new BorderLayout());
        add(judul, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(bawah, BorderLayout.SOUTH);

        btnMasuk.addActionListener(e -> masuk());
        getRootPane().setDefaultButton(btnMasuk);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void masuk() {
        try {
            Pengguna p = ctx.login.login(tfUser.getText(), pfPass.getPassword());
            dispose();
            new MainFrame(ctx, p, () -> new LoginFrame(ctx).setVisible(true)).setVisible(true);
        } catch (PerpusException ex) {
            Ui.tangani(this, ex);
            pfPass.setText("");
            pfPass.requestFocusInWindow();
        }
    }
}