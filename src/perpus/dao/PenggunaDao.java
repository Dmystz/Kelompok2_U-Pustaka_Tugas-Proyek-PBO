package perpus.dao;

import perpus.exception.PerpusException;
import perpus.model.Peminjam;
import perpus.model.Pengguna;
import perpus.model.Petugas;

public class PenggunaDao extends FileDao<Pengguna> {

    public PenggunaDao() throws PerpusException {
        super("pengguna.txt");
        if (semua().isEmpty()) {
            // akun petugas bawaan saat pertama kali dijalankan
            tambah(new Petugas("admin", Pengguna.hash("admin123"), "Administrator Perpustakaan"));
        }
    }

    @Override protected String ambilId(Pengguna p) { return p.getUsername(); }
    @Override protected String awalanId() { return "U"; }

    @Override
    protected String keBaris(Pengguna p) {
        return gabung(p.getPeran().name(), p.getUsername(), p.getPasswordHash(), p.getNamaLengkap(),
                p.getIdAnggota());
    }

    @Override
    protected Pengguna dariBaris(String[] f) throws PerpusException {
        if (f.length >= 4 && "PETUGAS".equals(f[0])) return new Petugas(f[1], f[2], f[3]);
        if (f.length >= 5 && "PEMINJAM".equals(f[0])) return new Peminjam(f[1], f[2], f[3], f[4]);
        // format lama (sebelum ada peran): username|hash|nama, selalu petugas
        pastikanKolom(f, 3);
        return new Petugas(f[0], f[1], f[2]);
    }

    /** Menghapus akun bila ada (tidak melempar apa pun bila tidak ditemukan). */
    public void hapusJikaAda(String username) throws PerpusException {
        if (cari(username) != null) hapus(username);
    }
}
