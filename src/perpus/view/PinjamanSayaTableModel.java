package perpus.view;

import java.time.LocalDate;

import perpus.controller.PeminjamanController;
import perpus.model.Koleksi;
import perpus.model.Peminjaman;
import perpus.util.Format;

/** Tabel pinjaman milik satu anggota. */
public class PinjamanSayaTableModel extends DaftarTableModel<Peminjaman> {
    private static final long serialVersionUID = 1L;

    /** Indeks kolom "Status" (dipakai untuk pewarnaan baris terlambat). */
    public static final int KOLOM_STATUS = 6;

    private final PeminjamanController controller;

    public PinjamanSayaTableModel(PeminjamanController controller) {
        super("ID", "Koleksi", "Jenis", "Tgl Pinjam", "Jatuh Tempo", "Tgl Kembali", "Status", "Denda");
        this.controller = controller;
    }

    @Override
    protected Object nilai(Peminjaman p, int c) {
        Koleksi k = controller.cariKoleksi(p.getIdKoleksi());
        switch (c) {
            case 0: return p.getId();
            case 1: return k == null ? p.getIdKoleksi() : k.getJudul();
            case 2: return k == null ? "-" : k.getJenis().getNama();
            case 3: return Format.tanggal(p.getTanggalPinjam());
            case 4: return Format.tanggal(p.getJatuhTempo());
            case 5: return Format.tanggal(p.getTanggalKembali());
            case 6: return PeminjamanTableModel.status(p);
            default: return Format.rupiah(controller.estimasiDenda(p, LocalDate.now()));
        }
    }
}
