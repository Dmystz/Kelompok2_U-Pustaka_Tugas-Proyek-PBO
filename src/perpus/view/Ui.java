package perpus.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;

import perpus.exception.AturanBisnisException;
import perpus.exception.AutentikasiException;
import perpus.exception.BatasPinjamException;
import perpus.exception.DataDuplikatException;
import perpus.exception.DataTidakDitemukanException;
import perpus.exception.PenyimpananException;
import perpus.exception.StokHabisException;
import perpus.exception.ValidasiException;

/** Kumpulan bantuan tampilan: dialog standar, spinner, dan penataan form. */
public final class Ui {
    private Ui() { }

    // ---------- dialog ----------
    public static void error(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    /** Menampilkan dialog yang sesuai dengan jenis exception (peringatan untuk kesalahan pengguna, error untuk sistem). */
    public static void tangani(Component induk, Throwable e) {
        String judul;
        int ikon = JOptionPane.WARNING_MESSAGE;
        if (e instanceof ValidasiException) judul = "Input tidak valid";
        else if (e instanceof AutentikasiException) judul = "Login gagal";
        else if (e instanceof StokHabisException) judul = "Stok habis";
        else if (e instanceof BatasPinjamException) judul = "Batas pinjam tercapai";
        else if (e instanceof AturanBisnisException) judul = "Tidak dapat diproses";
        else if (e instanceof DataTidakDitemukanException) judul = "Data tidak ditemukan";
        else if (e instanceof DataDuplikatException) judul = "Data sudah ada";
        else if (e instanceof PenyimpananException) {
            judul = "Kesalahan penyimpanan data";
            ikon = JOptionPane.ERROR_MESSAGE;
        } else {
            judul = "Kesalahan tak terduga";
            ikon = JOptionPane.ERROR_MESSAGE;
        }
        String pesan = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        JOptionPane.showMessageDialog(induk, pesan, judul, ikon);
    }

    public static void info(Component induk, String pesan) {
        JOptionPane.showMessageDialog(induk, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean konfirmasi(Component induk, String pesan) {
        Object[] pilihan = {"Ya", "Tidak"};
        int hasil = JOptionPane.showOptionDialog(induk, pesan, "Konfirmasi", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, pilihan, pilihan[1]);
        return hasil == 0;
    }

    // ---------- spinner ----------
    public static JSpinner spinnerTanggal(LocalDate awal) {
        JSpinner sp = new JSpinner(new SpinnerDateModel(keDate(awal), null, null, Calendar.DAY_OF_MONTH));
        sp.setEditor(new JSpinner.DateEditor(sp, "dd-MM-yyyy"));
        return sp;
    }

    public static JSpinner spinnerAngka(int awal, int min, int maks) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(awal, min, maks, 1));
        sp.setEditor(new JSpinner.NumberEditor(sp, "#"));
        return sp;
    }

    public static LocalDate ambilTanggal(JSpinner sp) {
        return ((Date) sp.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static void setTanggal(JSpinner sp, LocalDate t) {
        sp.setValue(keDate(t));
    }

    public static int ambilAngka(JSpinner sp) {
        return ((Number) sp.getValue()).intValue();
    }

    private static Date keDate(LocalDate t) {
        return Date.from(t.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // ---------- penataan ----------
    /** Menambah satu baris "label : komponen" ke panel ber-GridBagLayout. */
    public static void baris(JPanel form, int baris, String teksLabel, JComponent isi) {
        GridBagConstraints k = new GridBagConstraints();
        k.insets = new Insets(4, 6, 4, 6);
        k.gridy = baris;
        k.anchor = GridBagConstraints.WEST;

        k.gridx = 0;
        k.weightx = 0;
        form.add(new JLabel(teksLabel), k);

        k.gridx = 1;
        k.weightx = 1;
        k.fill = GridBagConstraints.HORIZONTAL;
        form.add(isi, k);
    }

    /** Menambah satu baris dengan label yang dapat diganti (dipakai untuk detail per jenis koleksi). */
    public static void baris(JPanel form, int baris, JLabel label, JComponent isi) {
        GridBagConstraints k = new GridBagConstraints();
        k.insets = new Insets(4, 6, 4, 6);
        k.gridy = baris;
        k.anchor = GridBagConstraints.WEST;

        k.gridx = 0;
        k.weightx = 0;
        form.add(label, k);

        k.gridx = 1;
        k.weightx = 1;
        k.fill = GridBagConstraints.HORIZONTAL;
        form.add(isi, k);
    }

    /** Mengatur lebar awal tiap kolom tabel (dalam piksel). */
    public static void lebarKolom(JTable t, int... lebar) {
        for (int i = 0; i < lebar.length && i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
    }

    public static void rapikanTabel(JTable t) {
        t.setAutoCreateRowSorter(true);
        t.setRowHeight(24);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setReorderingAllowed(false);
        t.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }
}
