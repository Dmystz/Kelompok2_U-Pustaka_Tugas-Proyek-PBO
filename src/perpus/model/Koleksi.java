package perpus.model;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;

import perpus.exception.PerpusException;
import perpus.exception.ValidasiException;

/**
 * Kelas induk abstrak untuk semua koleksi perpustakaan.
 * Menerapkan enkapsulasi (atribut private + setter bervalidasi),
 * abstraksi (method abstrak), dan polimorfisme (hitungDenda dll. di-override anak).
 */
public abstract class Koleksi implements DapatDicari {

    private String id;
    private String judul;
    private String pengarang;
    private int tahun;
    private int stok;
    private boolean referensi;

    protected Koleksi(String id, String judul, String pengarang, int tahun, int stok, boolean referensi)
            throws PerpusException {
        setId(id);
        setJudul(judul);
        setPengarang(pengarang);
        setTahun(tahun);
        setStok(stok);
        this.referensi = referensi;
    }

    // ---------- method abstrak: wajib di-override kelas turunan ----------
    public abstract JenisKoleksi getJenis();
    public abstract int getLamaPinjam();
    public abstract double hitungDenda(int hariTerlambat);
    public abstract String getDetail1();
    public abstract String getDetail2();

    // ---------- overloading hitungDenda ----------
    /** Overload: hitung denda dari tanggal jatuh tempo dan tanggal kembali. */
    public double hitungDenda(LocalDate jatuhTempo, LocalDate tanggalKembali) {
        long hari = ChronoUnit.DAYS.between(jatuhTempo, tanggalKembali);
        return hari > 0 ? hitungDenda((int) hari) : 0;
    }

    // ---------- getter & setter dengan validasi ----------
    public String getId() { return id; }
    public void setId(String id) throws PerpusException {
        if (id == null || id.trim().isEmpty()) throw new ValidasiException("ID koleksi tidak boleh kosong.");
        this.id = id.trim();
    }

    public String getJudul() { return judul; }
    public void setJudul(String judul) throws PerpusException {
        if (judul == null || judul.trim().isEmpty()) throw new ValidasiException("Judul tidak boleh kosong.");
        this.judul = judul.trim();
    }

    public String getPengarang() { return pengarang; }
    public void setPengarang(String pengarang) throws PerpusException {
        if (pengarang == null || pengarang.trim().isEmpty()) throw new ValidasiException("Pengarang tidak boleh kosong.");
        this.pengarang = pengarang.trim();
    }

    public int getTahun() { return tahun; }
    public void setTahun(int tahun) throws PerpusException {
        int maks = Year.now().getValue() + 1;
        if (tahun < 1900 || tahun > maks)
            throw new ValidasiException("Tahun terbit harus antara 1900 dan " + maks + ".");
        this.tahun = tahun;
    }

    public int getStok() { return stok; }
    public void setStok(int stok) throws PerpusException {
        if (stok < 0) throw new ValidasiException("Stok tidak boleh negatif.");
        this.stok = stok;
    }

    public boolean isReferensi() { return referensi; }
    public void setReferensi(boolean referensi) { this.referensi = referensi; }

    /** Koleksi bisa dipinjam jika bukan referensi dan stok masih ada. */
    public boolean bisaDipinjam() {
        return !referensi && stok > 0;
    }

    @Override
    public boolean cocokDengan(String kataKunci) {
        if (kataKunci == null || kataKunci.trim().isEmpty()) return true;
        String k = kataKunci.trim().toLowerCase();
        return id.toLowerCase().contains(k)
                || judul.toLowerCase().contains(k)
                || pengarang.toLowerCase().contains(k)
                || getJenis().getNama().toLowerCase().contains(k);
    }

    @Override
    public String toString() {
        return id + " - " + judul + " (" + getJenis().getNama() + ")";
    }
}
