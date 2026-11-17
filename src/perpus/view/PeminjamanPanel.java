package perpus.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;

import perpus.controller.AppContext;
import perpus.exception.PerpusException;
import perpus.model.Anggota;
import perpus.model.Koleksi;
import perpus.model.Peminjaman;
import perpus.util.Format;

/** Halaman transaksi peminjaman dan pengembalian. */
public class PeminjamanPanel extends HalamanPanel {
    private static final long serialVersionUID = 1L;

    private final AppContext ctx;
    private final PeminjamanTableModel model;
    private final JTable tabel;

    private final JComboBox<Anggota> cbAnggota = new JComboBox<Anggota>();
    private final JComboBox<Koleksi> cbKoleksi = new JComboBox<Koleksi>();
    private final JSpinner spPinjam = Ui.spinnerTanggal(LocalDate.now());
    private final JLabel lbInfo = new JLabel(" ");
    private final JCheckBox chkBelumKembali = new JCheckBox("Hanya yang belum dikembalikan", true);
    private final JSpinner spKembali = Ui.spinnerTanggal(LocalDate.now());
    private final JButton btnPinjam = new JButton("Pinjamkan");
    private final JButton btnKembali = new JButton("Kembalikan yang dipilih");

    public PeminjamanPanel(AppContext ctx) {
        this.ctx = ctx;
        this.model = new PeminjamanTableModel(ctx.peminjaman);
        this.tabel = new JTable(model);
        susunTampilan();
        pasangListener();
        segarkan();
    }

    private void susunTampilan() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // --- form peminjaman baru ---
        JPanel form = new JPanel(new java.awt.GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Peminjaman baru"));
        Ui.baris(form, 0, "Anggota", cbAnggota);
        Ui.baris(form, 1, "Koleksi", cbKoleksi);
        Ui.baris(form, 2, "Tanggal pinjam", spPinjam);
        Ui.baris(form, 3, "", lbInfo);
        JPanel barisTombol = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barisTombol.add(btnPinjam);
        Ui.baris(form, 4, "", barisTombol);
        add(form, BorderLayout.NORTH);

        // --- tabel ---
        Ui.rapikanTabel(tabel);
        Ui.lebarKolom(tabel, 50, 150, 260, 70, 90, 90, 90, 130, 90);
        tabel.setDefaultRenderer(Object.class, new RenderTerlambat(7));
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        // --- pengembalian ---
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bawah.setBorder(BorderFactory.createTitledBorder("Pengembalian"));
        bawah.add(chkBelumKembali);
        bawah.add(new JLabel("Tanggal kembali:"));
        bawah.add(spKembali);
        bawah.add(btnKembali);
        add(bawah, BorderLayout.SOUTH);
    }

    private void pasangListener() {
        btnPinjam.addActionListener(e -> pinjam());
        btnKembali.addActionListener(e -> kembalikan());
        chkBelumKembali.addActionListener(e -> muatTabel());
        cbKoleksi.addActionListener(e -> perbaruiInfo());
        cbAnggota.addActionListener(e -> perbaruiInfo());
    }

    private void pinjam() {
        try {
            Anggota a = (Anggota) cbAnggota.getSelectedItem();
            Koleksi k = (Koleksi) cbKoleksi.getSelectedItem();
            Peminjaman p = ctx.peminjaman.pinjam(a, k, Ui.ambilTanggal(spPinjam));
            Ui.info(this, "Peminjaman " + p.getId() + " berhasil.\nJatuh tempo: " + Format.tanggal(p.getJatuhTempo()));
            segarkan();
        } catch (PerpusException ex) {
            Ui.tangani(this, ex);
        }
    }

    private void kembalikan() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            Ui.error(this, "Pilih data peminjaman pada tabel terlebih dahulu.");
            return;
        }
        Peminjaman dipilih = model.getBaris(tabel.convertRowIndexToModel(baris));
        try {
            Peminjaman p = ctx.peminjaman.kembalikan(dipilih.getId(), Ui.ambilTanggal(spKembali));
            String pesan = "Koleksi berhasil dikembalikan.";
            if (p.getDenda() > 0) pesan += "\nDenda keterlambatan: " + Format.rupiah(p.getDenda());
            else pesan += "\nTidak ada denda.";
            Ui.info(this, pesan);
            segarkan();
        } catch (PerpusException ex) {
            Ui.tangani(this, ex);
        }
    }

    private void perbaruiInfo() {
        Koleksi k = (Koleksi) cbKoleksi.getSelectedItem();
        Anggota a = (Anggota) cbAnggota.getSelectedItem();
        if (k == null) {
            lbInfo.setText(" ");
            return;
        }
        String info = "Lama pinjam " + k.getJenis().getNama() + ": " + k.getLamaPinjam() + " hari | Stok: " + k.getStok();
        if (a != null) info += " | Batas pinjam " + a.getStatus().getNama() + ": " + a.getStatus().getMaksPinjam();
        lbInfo.setText(info);
    }

    private void muatCombo() {
        List<Anggota> anggota = ctx.peminjaman.anggotaAktif();
        List<Koleksi> koleksi = ctx.peminjaman.koleksiTersedia();
        cbAnggota.setModel(new DefaultComboBoxModel<Anggota>(anggota.toArray(new Anggota[0])));
        cbKoleksi.setModel(new DefaultComboBoxModel<Koleksi>(koleksi.toArray(new Koleksi[0])));
        perbaruiInfo();
    }

    private void muatTabel() {
        model.setData(ctx.peminjaman.daftar(chkBelumKembali.isSelected()));
    }

    @Override
    public void segarkan() {
        muatCombo();
        muatTabel();
    }
}
