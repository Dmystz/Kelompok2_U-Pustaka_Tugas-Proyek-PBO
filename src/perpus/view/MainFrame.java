package perpus.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import perpus.controller.AppContext;
import perpus.model.Pengguna;
import perpus.util.Format;

/**
 * Jendela utama. Menu dan halaman yang tersedia mengikuti peran pengguna:
 * petugas mengelola data dan transaksi, peminjam mencari koleksi dan melihat pinjaman sendiri.
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final String BERANDA = "beranda";
    private static final String KOLEKSI = "koleksi";
    private static final String ANGGOTA = "anggota";
    private static final String PEMINJAMAN = "peminjaman";
    private static final String KATALOG = "katalog";
    private static final String PINJAMAN_SAYA = "pinjamanSaya";

    private final CardLayout kartu = new CardLayout();
    private final JPanel isi = new JPanel(kartu);
    private final Map<String, HalamanPanel> halaman = new LinkedHashMap<String, HalamanPanel>();
    private final AppContext ctx;
    private final Pengguna pengguna;
    private final Runnable aksiLogout;

    public MainFrame(AppContext ctx, Pengguna pengguna, Runnable aksiLogout) {
        super("U-Pustaka");
        this.ctx = ctx;
        this.pengguna = pengguna;
        this.aksiLogout = aksiLogout;
        setIconImages(Logo.gambarBanyak());

        if (pengguna.bolehKelolaData()) {
            Supplier<Map<String, String>> sumber = ctx.peminjaman::ringkasan;
            daftarkan(BERANDA, new BerandaPanel(pengguna, sumber,
                    "<html>Gunakan menu <b>Data</b> untuk mengelola koleksi dan anggota, "
                            + "dan menu <b>Transaksi</b> untuk peminjaman serta pengembalian.</html>"));
            daftarkan(KOLEKSI, new KoleksiPanel(ctx));
            daftarkan(ANGGOTA, new AnggotaPanel(ctx));
            daftarkan(PEMINJAMAN, new PeminjamanPanel(ctx));
            setJMenuBar(menuPetugas());
        } else {
            final String idAnggota = pengguna.getIdAnggota();
            Supplier<Map<String, String>> sumber = () -> ctx.peminjaman.ringkasanAnggota(idAnggota);
            daftarkan(BERANDA, new BerandaPanel(pengguna, sumber,
                    "<html>Gunakan menu <b>Katalog</b> untuk mencari koleksi dan melihat <b>Pinjaman Saya</b>. "
                            + "Peminjaman dan pengembalian dilakukan di meja perpustakaan.</html>"));
            daftarkan(KATALOG, new KatalogPanel(ctx));
            daftarkan(PINJAMAN_SAYA, new PinjamanSayaPanel(ctx, pengguna));
            setJMenuBar(menuPeminjam());
        }

        setLayout(new BorderLayout());
        add(isi, BorderLayout.CENTER);
        add(buatStatusBar(), BorderLayout.SOUTH);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setSize(1100, 650);
        setLocationRelativeTo(null);
        tampil(BERANDA);
    }

    private void daftarkan(String nama, HalamanPanel panel) {
        halaman.put(nama, panel);
        isi.add(panel, nama);
    }

    // ---------- menu ----------
    private JMenuBar menuPetugas() {
        JMenuBar bar = new JMenuBar();
        bar.add(menuBerkas());

        JMenu data = new JMenu("Data");
        data.add(item("Koleksi", KeyEvent.VK_2, () -> tampil(KOLEKSI)));
        data.add(item("Anggota", KeyEvent.VK_3, () -> tampil(ANGGOTA)));
        bar.add(data);

        JMenu transaksi = new JMenu("Transaksi");
        transaksi.add(item("Peminjaman dan Pengembalian", KeyEvent.VK_4, () -> tampil(PEMINJAMAN)));
        bar.add(transaksi);

        bar.add(menuBantuan());
        return bar;
    }

    private JMenuBar menuPeminjam() {
        JMenuBar bar = new JMenuBar();
        bar.add(menuBerkas());

        JMenu katalog = new JMenu("Katalog");
        katalog.add(item("Cari Koleksi", KeyEvent.VK_2, () -> tampil(KATALOG)));
        katalog.add(item("Pinjaman Saya", KeyEvent.VK_3, () -> tampil(PINJAMAN_SAYA)));
        bar.add(katalog);

        bar.add(menuBantuan());
        return bar;
    }

    private JMenu menuBerkas() {
        JMenu berkas = new JMenu("Berkas");
        berkas.add(item("Beranda", KeyEvent.VK_1, () -> tampil(BERANDA)));
        berkas.add(item("Ganti Password", 0, () -> GantiPasswordDialog.tampilkan(this, ctx, pengguna)));
        berkas.addSeparator();
        berkas.add(item("Keluar Akun", 0, this::logout));
        berkas.add(item("Tutup Aplikasi", KeyEvent.VK_Q, () -> System.exit(0)));
        return berkas;
    }

    private JMenu menuBantuan() {
        JMenu bantuan = new JMenu("Bantuan");
        bantuan.add(item("Tentang", 0, () -> JOptionPane.showMessageDialog(this,
                "U-Pustaka\nSistem Informasi Perpustakaan Kampus Undip",
                "Tentang U-Pustaka", JOptionPane.INFORMATION_MESSAGE, Logo.ikon(64))));
        return bantuan;
    }

    private JMenuItem item(String teks, int tombol, Runnable aksi) {
        JMenuItem mi = new JMenuItem(teks);
        if (tombol != 0) mi.setAccelerator(KeyStroke.getKeyStroke(tombol, InputEvent.CTRL_DOWN_MASK));
        mi.addActionListener(e -> aksi.run());
        return mi;
    }

    private JPanel buatStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        bar.add(new JLabel("Masuk sebagai: " + pengguna.getUsername() + " (" + pengguna.getPeran().getNama() + ")"),
                BorderLayout.WEST);
        bar.add(new JLabel(Format.tanggal(LocalDate.now())), BorderLayout.EAST);
        return bar;
    }

    private void tampil(String nama) {
        HalamanPanel h = halaman.get(nama);
        if (h == null) return;
        h.segarkan();
        kartu.show(isi, nama);
    }

    private void logout() {
        if (Ui.konfirmasi(this, "Keluar dari akun ini?")) {
            dispose();
            aksiLogout.run();
        }
    }
}