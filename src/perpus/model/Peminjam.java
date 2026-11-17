package perpus.model;

import perpus.exception.PerpusException;
import perpus.exception.ValidasiException;

/** Anggota perpustakaan yang login: mencari koleksi dan melihat pinjaman serta denda miliknya. */
public class Peminjam extends Pengguna {
    private final String idAnggota;

    public Peminjam(String username, String passwordHash, String namaLengkap, String idAnggota)
            throws PerpusException {
        super(username, passwordHash, namaLengkap);
        if (idAnggota == null || idAnggota.trim().isEmpty())
            throw new ValidasiException("Akun peminjam harus terhubung ke satu anggota.");
        this.idAnggota = idAnggota.trim();
    }

    @Override public Peran getPeran() { return Peran.PEMINJAM; }
    @Override public boolean bolehKelolaData() { return false; }
    @Override public String getIdAnggota() { return idAnggota; }
}
