package perpus.view;

import java.time.LocalDate;

import perpus.controller.PeminjamanController;
import perpus.model.Anggota;
import perpus.model.Koleksi;
import perpus.model.Peminjaman;
import perpus.util.Format;

public class PeminjamanTableModel extends DaftarTableModel<Peminjaman> {
    private static final long serialVersionUID = 1L;

    private final PeminjamanController controller;

    public PeminjamanTableModel(PeminjamanController controller) {
        super("ID", "Anggota", "Koleksi", "Jenis", "Tgl Pinjam", "Jatuh Tempo", "Tgl Kembali", "Status", "Denda");
        this.controller = controller;
    }

    /** Teks status: Dikembalikan / Dipinjam / Terlambat N hari. */
    public static String status(Peminjaman p) {
        if (p.sudahKembali()) return "Dikembalikan";
        int hari = p.hariTerlambat(LocalDate.now());
        return hari > 0 ? "Terlambat " + hari + " hari" : "Dipinjam";
    }

    @Override
    protected Object nilai(Peminjaman p, int c) {
        Anggota a = controller.cariAnggota(p.getIdAnggota());
        Koleksi k = controller.cariKoleksi(p.getIdKoleksi());
        switch (c) {
            case 0: return p.getId();
            case 1: return a == null ? p.getIdAnggota() : a.getNama();
            case 2: return k == null ? p.getIdKoleksi() : k.getJudul();
            case 3: return k == null ? "-" : k.getJenis().getNama();
            case 4: return Format.tanggal(p.getTanggalPinjam());
            case 5: return Format.tanggal(p.getJatuhTempo());
            case 6: return Format.tanggal(p.getTanggalKembali());
            case 7: return status(p);
            default: return Format.rupiah(controller.estimasiDenda(p, LocalDate.now()));
        }
    }
}
