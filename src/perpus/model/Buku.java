package perpus.model;

import perpus.exception.PerpusException;

/** Buku: pinjam 14 hari, denda Rp1.000 per hari. */
public class Buku extends Koleksi {
    private String penerbit;
    private String isbn;

    public Buku(String id, String judul, String pengarang, int tahun, int stok, boolean referensi,
                String penerbit, String isbn) throws PerpusException {
        super(id, judul, pengarang, tahun, stok, referensi);
        this.penerbit = penerbit == null ? "" : penerbit.trim();
        this.isbn = isbn == null ? "" : isbn.trim();
    }

    @Override public JenisKoleksi getJenis() { return JenisKoleksi.BUKU; }
    @Override public int getLamaPinjam() { return 14; }

    @Override
    public double hitungDenda(int hariTerlambat) {
        return hariTerlambat <= 0 ? 0 : hariTerlambat * 1000.0;
    }

    @Override public String getDetail1() { return penerbit; }
    @Override public String getDetail2() { return isbn; }
}
