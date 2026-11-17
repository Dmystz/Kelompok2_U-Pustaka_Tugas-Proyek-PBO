package perpus.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;

import perpus.controller.AppContext;
import perpus.exception.PerpusException;
import perpus.model.JenisKoleksi;
import perpus.model.Koleksi;

/** Halaman CRUD koleksi (buku, jurnal, skripsi). */
public class KoleksiPanel extends HalamanPanel {
    private static final long serialVersionUID = 1L;

    private final AppContext ctx;
    private final KoleksiTableModel model = new KoleksiTableModel();
    private final JTable tabel = new JTable(model);

    private final JTextField tfCari = new JTextField(22);
    private final JTextField tfId = new JTextField();
    private final JComboBox<JenisKoleksi> cbJenis = new JComboBox<JenisKoleksi>(JenisKoleksi.values());
    private final JTextField tfJudul = new JTextField();
    private final JTextField tfPengarang = new JTextField();
    private final JSpinner spTahun = Ui.spinnerAngka(java.time.Year.now().getValue(), 1900, 2100);
    private final JSpinner spStok = Ui.spinnerAngka(1, 0, 9999);
    private final JCheckBox chkReferensi = new JCheckBox("Koleksi referensi (hanya baca di tempat)");
    private final JLabel lbDetail1 = new JLabel();
    private final JLabel lbDetail2 = new JLabel();
    private final JTextField tfDetail1 = new JTextField();
    private final JTextField tfDetail2 = new JTextField();

    private final JButton btnTambah = new JButton("Tambah");
    private final JButton btnUbah = new JButton("Ubah");
    private final JButton btnHapus = new JButton("Hapus");
    private final JButton btnBersihkan = new JButton("Bersihkan");

    public KoleksiPanel(AppContext ctx) {
        this.ctx = ctx;
        susunTampilan();
        pasangListener();
        bersihkanForm();
        segarkan();
    }

    // ---------- tampilan ----------
    private void susunTampilan() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        atas.add(new JLabel("Cari koleksi:"));
        atas.add(tfCari);
        JButton btnCari = new JButton("Cari");
        btnCari.addActionListener(e -> segarkan());
        tfCari.addActionListener(e -> segarkan());
        JButton btnSemua = new JButton("Tampilkan semua");
        btnSemua.addActionListener(e -> {
            tfCari.setText("");
            segarkan();
        });
        atas.add(btnCari);
        atas.add(btnSemua);
        add(atas, BorderLayout.NORTH);

        Ui.rapikanTabel(tabel);
        Ui.lebarKolom(tabel, 45, 60, 200, 110, 48, 40, 70, 180);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        tfId.setEditable(false);
        JPanel form = new JPanel(new java.awt.GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Data koleksi"));
        int r = 0;
        Ui.baris(form, r++, "ID", tfId);
        Ui.baris(form, r++, "Jenis", cbJenis);
        Ui.baris(form, r++, "Judul", tfJudul);
        Ui.baris(form, r++, "Pengarang", tfPengarang);
        Ui.baris(form, r++, "Tahun terbit", spTahun);
        Ui.baris(form, r++, "Stok", spStok);
        Ui.baris(form, r++, lbDetail1, tfDetail1);
        Ui.baris(form, r++, lbDetail2, tfDetail2);
        Ui.baris(form, r++, "", chkReferensi);

        JPanel tombol = new JPanel(new GridLayout(2, 2, 6, 6));
        tombol.add(btnTambah);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnBersihkan);

        JPanel kolom = new JPanel(new BorderLayout(0, 8));
        kolom.add(form, BorderLayout.CENTER);
        kolom.add(tombol, BorderLayout.SOUTH);
        JPanel kanan = new JPanel(new BorderLayout());
        kanan.add(kolom, BorderLayout.NORTH);
        kanan.setPreferredSize(new java.awt.Dimension(350, 100));
        add(new JScrollPane(kanan, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.EAST);
    }

    private void pasangListener() {
        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersihkan.addActionListener(e -> bersihkanForm());
        cbJenis.addActionListener(e -> perbaruiLabelDetail());
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) isiFormDariTabel();
        });
    }

    // ---------- handler (masing-masing hanya mengerjakan satu hal) ----------
    private void tambah() {
        try {
            ctx.koleksi.tambah(jenisTerpilih(), tfJudul.getText(), tfPengarang.getText(),
                    Ui.ambilAngka(spTahun), Ui.ambilAngka(spStok), chkReferensi.isSelected(),
                    tfDetail1.getText(), tfDetail2.getText());
            Ui.info(this, "Koleksi berhasil ditambahkan.");
            bersihkanForm();
            segarkan();
        } catch (PerpusException ex) {
            Ui.tangani(this, ex);
        }
    }

    private void ubah() {
        if (tfId.getText().isEmpty()) {
            Ui.error(this, "Pilih koleksi pada tabel terlebih dahulu.");
            return;
        }
        try {
            ctx.koleksi.ubah(tfId.getText(), jenisTerpilih(), tfJudul.getText(), tfPengarang.getText(),
                    Ui.ambilAngka(spTahun), Ui.ambilAngka(spStok), chkReferensi.isSelected(),
                    tfDetail1.getText(), tfDetail2.getText());
            Ui.info(this, "Data koleksi berhasil diubah.");
            segarkan();
        } catch (PerpusException ex) {
            Ui.tangani(this, ex);
        }
    }

    private void hapus() {
        if (tfId.getText().isEmpty()) {
            Ui.error(this, "Pilih koleksi pada tabel terlebih dahulu.");
            return;
        }
        if (!Ui.konfirmasi(this, "Hapus koleksi \"" + tfJudul.getText() + "\"?\nTindakan ini tidak dapat dibatalkan.")) {
            return;
        }
        try {
            ctx.koleksi.hapus(tfId.getText());
            bersihkanForm();
            segarkan();
        } catch (PerpusException ex) {
            Ui.tangani(this, ex);
        }
    }

    private void bersihkanForm() {
        tabel.clearSelection();
        tfId.setText("");
        cbJenis.setSelectedIndex(0);
        tfJudul.setText("");
        tfPengarang.setText("");
        spTahun.setValue(java.time.Year.now().getValue());
        spStok.setValue(1);
        chkReferensi.setSelected(false);
        tfDetail1.setText("");
        tfDetail2.setText("");
        perbaruiLabelDetail();
    }

    private void isiFormDariTabel() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) return;
        Koleksi k = model.getBaris(tabel.convertRowIndexToModel(baris));
        tfId.setText(k.getId());
        cbJenis.setSelectedItem(k.getJenis());
        tfJudul.setText(k.getJudul());
        tfPengarang.setText(k.getPengarang());
        spTahun.setValue(k.getTahun());
        spStok.setValue(k.getStok());
        chkReferensi.setSelected(k.isReferensi());
        tfDetail1.setText(k.getDetail1());
        tfDetail2.setText(k.getDetail2());
        perbaruiLabelDetail();
    }

    private void perbaruiLabelDetail() {
        JenisKoleksi j = jenisTerpilih();
        lbDetail1.setText(j.getLabelDetail1());
        lbDetail2.setText(j.getLabelDetail2());
    }

    private JenisKoleksi jenisTerpilih() {
        return (JenisKoleksi) cbJenis.getSelectedItem();
    }

    @Override
    public void segarkan() {
        model.setData(ctx.koleksi.daftar(tfCari.getText()));
    }
}
