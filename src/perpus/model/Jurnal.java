package perpus.model;

import perpus.exception.PerpusException;

/** Jurnal: pinjam 7 hari, denda Rp2.000 per hari dengan batas maksimal Rp30.000. */
public class Jurnal extends Koleksi {
    private static final double DENDA_MAKS = 30000;

    private String volume;
    private String nomor;

    public Jurnal(String id, String judul, String pengarang, int tahun, int stok, boolean referensi,
                  String volume, String nomor) throws PerpusException {
        super(id, judul, pengarang, tahun, stok, referensi);
        this.volume = volume == null ? "" : volume.trim();
        this.nomor = nomor == null ? "" : nomor.trim();
    }

    @Override public JenisKoleksi getJenis() { return JenisKoleksi.JURNAL; }
    @Override public int getLamaPinjam() { return 7; }

    @Override
    public double hitungDenda(int hariTerlambat) {
        if (hariTerlambat <= 0) return 0;
        return Math.min(hariTerlambat * 2000.0, DENDA_MAKS);
    }

    @Override public String getDetail1() { return volume; }
    @Override public String getDetail2() { return nomor; }
}
