package perpus.model;

import perpus.exception.PerpusException;

/** Skripsi: pinjam 3 hari, denda Rp5.000 per hari ditambah biaya administrasi Rp10.000 bila terlambat. */
public class Skripsi extends Koleksi {
    private static final double BIAYA_ADMIN = 10000;

    private String programStudi;
    private String pembimbing;

    public Skripsi(String id, String judul, String pengarang, int tahun, int stok, boolean referensi,
                   String programStudi, String pembimbing) throws PerpusException {
        super(id, judul, pengarang, tahun, stok, referensi);
        this.programStudi = programStudi == null ? "" : programStudi.trim();
        this.pembimbing = pembimbing == null ? "" : pembimbing.trim();
    }

    @Override public JenisKoleksi getJenis() { return JenisKoleksi.SKRIPSI; }
    @Override public int getLamaPinjam() { return 3; }

    @Override
    public double hitungDenda(int hariTerlambat) {
        return hariTerlambat <= 0 ? 0 : hariTerlambat * 5000.0 + BIAYA_ADMIN;
    }

    @Override public String getDetail1() { return programStudi; }
    @Override public String getDetail2() { return pembimbing; }
}
