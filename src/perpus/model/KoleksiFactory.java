package perpus.model;

import perpus.exception.PerpusException;
import perpus.exception.ValidasiException;

/** Pabrik objek Koleksi: mengembalikan Buku/Jurnal/Skripsi lewat referensi tipe induk. */
public final class KoleksiFactory {
    private KoleksiFactory() { }

    public static Koleksi buat(JenisKoleksi jenis, String id, String judul, String pengarang, int tahun,
                               int stok, boolean referensi, String detail1, String detail2)
            throws PerpusException {
        if (jenis == null) throw new ValidasiException("Jenis koleksi harus dipilih.");
        switch (jenis) {
            case BUKU:    return new Buku(id, judul, pengarang, tahun, stok, referensi, detail1, detail2);
            case JURNAL:  return new Jurnal(id, judul, pengarang, tahun, stok, referensi, detail1, detail2);
            case SKRIPSI: return new Skripsi(id, judul, pengarang, tahun, stok, referensi, detail1, detail2);
            default:      throw new ValidasiException("Jenis koleksi tidak dikenal.");
        }
    }
}
