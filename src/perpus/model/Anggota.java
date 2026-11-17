package perpus.model;

import java.time.LocalDate;

import perpus.exception.PerpusException;
import perpus.exception.ValidasiException;

/** Anggota perpustakaan (mahasiswa, dosen, atau staf). */
public class Anggota implements DapatDicari {
    private String id;
    private String nama;
    private String nomorInduk;
    private String email;
    private StatusAnggota status;
    private LocalDate tanggalDaftar;
    private boolean aktif;

    /** Konstruktor lengkap. */
    public Anggota(String id, String nama, String nomorInduk, String email, StatusAnggota status,
                   LocalDate tanggalDaftar, boolean aktif) throws PerpusException {
        setId(id);
        setNama(nama);
        setNomorInduk(nomorInduk);
        setEmail(email);
        setStatus(status);
        setTanggalDaftar(tanggalDaftar);
        this.aktif = aktif;
    }

    /** Overload: anggota baru, otomatis terdaftar hari ini dan aktif. */
    public Anggota(String id, String nama, String nomorInduk, String email, StatusAnggota status)
            throws PerpusException {
        this(id, nama, nomorInduk, email, status, LocalDate.now(), true);
    }

    public String getId() { return id; }
    public void setId(String id) throws PerpusException {
        if (id == null || id.trim().isEmpty()) throw new ValidasiException("ID anggota tidak boleh kosong.");
        this.id = id.trim();
    }

    public String getNama() { return nama; }
    public void setNama(String nama) throws PerpusException {
        if (nama == null || nama.trim().isEmpty()) throw new ValidasiException("Nama anggota tidak boleh kosong.");
        this.nama = nama.trim();
    }

    public String getNomorInduk() { return nomorInduk; }
    public void setNomorInduk(String nomorInduk) throws PerpusException {
        if (nomorInduk == null || !nomorInduk.trim().matches("\\d{5,20}"))
            throw new ValidasiException("Nomor induk (NIM/NIP) harus berupa angka 5-20 digit.");
        this.nomorInduk = nomorInduk.trim();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) throws PerpusException {
        if (email == null || !email.trim().matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"))
            throw new ValidasiException("Format email tidak valid.");
        this.email = email.trim();
    }

    public StatusAnggota getStatus() { return status; }
    public void setStatus(StatusAnggota status) throws PerpusException {
        if (status == null) throw new ValidasiException("Status anggota harus dipilih.");
        this.status = status;
    }

    public LocalDate getTanggalDaftar() { return tanggalDaftar; }
    public void setTanggalDaftar(LocalDate tanggalDaftar) throws PerpusException {
        if (tanggalDaftar == null) throw new ValidasiException("Tanggal daftar tidak boleh kosong.");
        if (tanggalDaftar.isAfter(LocalDate.now()))
            throw new ValidasiException("Tanggal daftar tidak boleh di masa depan.");
        this.tanggalDaftar = tanggalDaftar;
    }

    public boolean isAktif() { return aktif; }
    public void setAktif(boolean aktif) { this.aktif = aktif; }

    @Override
    public boolean cocokDengan(String kataKunci) {
        if (kataKunci == null || kataKunci.trim().isEmpty()) return true;
        String k = kataKunci.trim().toLowerCase();
        return id.toLowerCase().contains(k)
                || nama.toLowerCase().contains(k)
                || nomorInduk.contains(k)
                || status.getNama().toLowerCase().contains(k);
    }

    @Override
    public String toString() { return id + " - " + nama; }
}
