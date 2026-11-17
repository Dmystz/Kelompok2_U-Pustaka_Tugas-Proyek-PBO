package perpus.model;

import perpus.exception.PerpusException;

/** Petugas perpustakaan: mengelola koleksi, anggota, peminjaman, dan pengembalian. */
public class Petugas extends Pengguna {

    public Petugas(String username, String passwordHash, String namaLengkap) throws PerpusException {
        super(username, passwordHash, namaLengkap);
    }

    @Override public Peran getPeran() { return Peran.PETUGAS; }
    @Override public boolean bolehKelolaData() { return true; }
}
