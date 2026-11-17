package perpus.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;

import perpus.controller.AppContext;
import perpus.exception.PerpusException;
import perpus.model.Anggota;
import perpus.model.StatusAnggota;

/** Halaman CRUD anggota perpustakaan. */
public class AnggotaPanel extends HalamanPanel {
    private static final long serialVersionUID = 1L;

    private final AppContext ctx;
    private final AnggotaTableModel model = new AnggotaTableModel();
    private final JTable tabel = new JTable(model);

    private final JTextField tfCari = new JTextField(22);
    private final JTextField tfId = new JTextField();
    private final JTextField tfNama = new JTextField();
    private final JTextField tfNomorInduk = new JTextField();
    private final JTextField tfEmail = new JTextField();
    private final JRadioButton[] rbStatus = new JRadioButton[StatusAnggota.values().length];
    private final JSpinner spDaftar = Ui.spinnerTanggal(LocalDate.now());
    private final JCheckBox chkAktif = new JCheckBox("Anggota aktif", true);

    private final JButton btnTambah = new JButton("Tambah");
    private final JButton btnUbah = new JButton("Ubah");
    private final JButton btnHapus = new JButton("Hapus");
    private final JButton btnBersihkan = new JButton("Bersihkan");
    private final JButton btnReset = new JButton("Reset Password");

    public AnggotaPanel(AppContext ctx) {
        this.ctx = ctx;
        susunTampilan();
        pasangListener();
        bersihkanForm();
        segarkan();
    }

    private void susunTampilan() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        atas.add(new JLabel("Cari anggota:"));
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
        Ui.lebarKolom(tabel, 45, 130, 155, 180, 80, 85, 45);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        // radio button status anggota
        ButtonGroup grup = new ButtonGroup();
        JPanel panelStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        StatusAnggota[] semua = StatusAnggota.values();
        for (int i = 0; i < semua.length; i++) {
            rbStatus[i] = new JRadioButton(semua[i].getNama());
            grup.add(rbStatus[i]);
            panelStatus.add(rbStatus[i]);
        }

        tfId.setEditable(false);
        JPanel form = new JPanel(new java.awt.GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Data anggota"));
        int r = 0;
        Ui.baris(form, r++, "ID", tfId);
        Ui.baris(form, r++, "Nama", tfNama);
        Ui.baris(form, r++, "NIM / NIP", tfNomorInduk);
        Ui.baris(form, r++, "Email", tfEmail);
        Ui.baris(form, r++, "Status", panelStatus);
        Ui.baris(form, r++, "Tgl daftar", spDaftar);
        Ui.baris(form, r++, "", chkAktif);
        Ui.baris(form, r++, "", new JLabel("<html><small>Akun login dibuat otomatis:<br>username dan password awal = NIM/NIP.</small></html>"));

        JPanel tombol = new JPanel(new GridLayout(0, 2, 6, 6));
        tombol.add(btnTambah);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnBersihkan);
        tombol.add(btnReset);

        JPanel kolom = new JPanel(new BorderLayout(0, 8));
        kolom.add(form, BorderLayout.CENTER);
        kolom.add(tombol, BorderLayout.SOUTH);
        JPanel kanan = new JPanel(new BorderLayout());
        kanan.add(kolom, BorderLayout.NORTH);
        kanan.setPreferredSize(new java.awt.Dimension(360, 100));
        add(new JScrollPane(kanan, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.EAST);
    }

    private void pasangListener() {
        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersihkan.addActionListener(e -> bersihkanForm());
        btnReset.addActionListener(e -> resetPassword());
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) isiFormDariTabel();
        });
    }

    private void tambah() {
        try {
            ctx.anggota.tambah(tfNama.getText(), tfNomorInduk.getText(), tfEmail.getText(),
                    statusTerpilih(), Ui.ambilTanggal(spDaftar), chkAktif.isSelected());
            Ui.info(this, "Anggota berhasil ditambahkan.");
            bersihkanForm();
            segarkan();
        } catch (PerpusException ex) {
            Ui.tangani(this, ex);
        }
    }

    private void ubah() {
        if (tfId.getText().isEmpty()) {
            Ui.error(this, "Pilih anggota pada tabel terlebih dahulu.");
            return;
        }
        try {
            ctx.anggota.ubah(tfId.getText(), tfNama.getText(), tfNomorInduk.getText(), tfEmail.getText(),
                    statusTerpilih(), Ui.ambilTanggal(spDaftar), chkAktif.isSelected());
            Ui.info(this, "Data anggota berhasil diubah.");
            segarkan();
        } catch (PerpusException ex) {
            Ui.tangani(this, ex);
        }
    }

    private void hapus() {
        if (tfId.getText().isEmpty()) {
            Ui.error(this, "Pilih anggota pada tabel terlebih dahulu.");
            return;
        }
        if (!Ui.konfirmasi(this, "Hapus anggota \"" + tfNama.getText() + "\"?\nTindakan ini tidak dapat dibatalkan.")) {
            return;
        }
        try {
            ctx.anggota.hapus(tfId.getText());
            bersihkanForm();
            segarkan();
        } catch (PerpusException ex) {
            Ui.tangani(this, ex);
        }
    }

    private void resetPassword() {
        if (tfId.getText().isEmpty()) {
            Ui.error(this, "Pilih anggota pada tabel terlebih dahulu.");
            return;
        }
        if (!Ui.konfirmasi(this, "Kembalikan password akun \"" + tfNama.getText() + "\" ke NIM/NIP awal?")) {
            return;
        }
        try {
            ctx.anggota.resetPassword(tfId.getText());
            Ui.info(this, "Password dikembalikan ke NIM/NIP anggota.");
        } catch (PerpusException ex) {
            Ui.tangani(this, ex);
        }
    }

    private void bersihkanForm() {
        tabel.clearSelection();
        tfId.setText("");
        tfNama.setText("");
        tfNomorInduk.setText("");
        tfEmail.setText("");
        rbStatus[0].setSelected(true);
        Ui.setTanggal(spDaftar, LocalDate.now());
        chkAktif.setSelected(true);
    }

    private void isiFormDariTabel() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) return;
        Anggota a = model.getBaris(tabel.convertRowIndexToModel(baris));
        tfId.setText(a.getId());
        tfNama.setText(a.getNama());
        tfNomorInduk.setText(a.getNomorInduk());
        tfEmail.setText(a.getEmail());
        rbStatus[a.getStatus().ordinal()].setSelected(true);
        Ui.setTanggal(spDaftar, a.getTanggalDaftar());
        chkAktif.setSelected(a.isAktif());
    }

    private StatusAnggota statusTerpilih() {
        StatusAnggota[] semua = StatusAnggota.values();
        for (int i = 0; i < rbStatus.length; i++) {
            if (rbStatus[i].isSelected()) return semua[i];
        }
        return null;
    }

    @Override
    public void segarkan() {
        model.setData(ctx.anggota.daftar(tfCari.getText()));
    }
}
