package perpus.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import perpus.exception.PerpusException;
import perpus.exception.ValidasiException;

/** Transaksi peminjaman satu koleksi oleh satu anggota. */
public class Peminjaman {
    private String id;
    private String idKoleksi;
    private String idAnggota;
    private LocalDate tanggalPinjam;
    private LocalDate jatuhTempo;
    private LocalDate tanggalKembali; // null = belum kembali
    private double denda;

    public Peminjaman(String id, String idKoleksi, String idAnggota, LocalDate tanggalPinjam,
                      LocalDate jatuhTempo, LocalDate tanggalKembali, double denda) throws PerpusException {
        if (id == null || id.trim().isEmpty()) throw new ValidasiException("ID peminjaman tidak boleh kosong.");
        if (idKoleksi == null || idAnggota == null) throw new ValidasiException("Koleksi dan anggota harus diisi.");
        if (tanggalPinjam == null || jatuhTempo == null) throw new ValidasiException("Tanggal pinjam/jatuh tempo kosong.");
        if (jatuhTempo.isBefore(tanggalPinjam)) throw new ValidasiException("Jatuh tempo sebelum tanggal pinjam.");
        this.id = id.trim();
        this.idKoleksi = idKoleksi;
        this.idAnggota = idAnggota;
        this.tanggalPinjam = tanggalPinjam;
        this.jatuhTempo = jatuhTempo;
        this.tanggalKembali = tanggalKembali;
        setDenda(denda);
    }

    public String getId() { return id; }
    public String getIdKoleksi() { return idKoleksi; }
    public String getIdAnggota() { return idAnggota; }
    public LocalDate getTanggalPinjam() { return tanggalPinjam; }
    public LocalDate getJatuhTempo() { return jatuhTempo; }
    public LocalDate getTanggalKembali() { return tanggalKembali; }
    public double getDenda() { return denda; }

    public void setDenda(double denda) throws PerpusException {
        if (denda < 0) throw new ValidasiException("Denda tidak boleh negatif.");
        this.denda = denda;
    }

    public void setTanggalKembali(LocalDate tanggalKembali) throws PerpusException {
        if (tanggalKembali != null && tanggalKembali.isBefore(tanggalPinjam))
            throw new ValidasiException("Tanggal kembali tidak boleh sebelum tanggal pinjam.");
        this.tanggalKembali = tanggalKembali;
    }

    public boolean sudahKembali() { return tanggalKembali != null; }

    /** Jumlah hari keterlambatan pada tanggal tertentu (0 bila tidak terlambat). */
    public int hariTerlambat(LocalDate pada) {
        long hari = ChronoUnit.DAYS.between(jatuhTempo, pada);
        return hari > 0 ? (int) hari : 0;
    }

    public boolean terlambat() {
        return !sudahKembali() && hariTerlambat(LocalDate.now()) > 0;
    }
}
