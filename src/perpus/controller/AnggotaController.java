package perpus.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import perpus.dao.AnggotaDao;
import perpus.dao.PeminjamanDao;
import perpus.dao.PenggunaDao;
import perpus.exception.AturanBisnisException;
import perpus.exception.DataDuplikatException;
import perpus.exception.DataTidakDitemukanException;
import perpus.exception.PerpusException;
import perpus.model.Anggota;
import perpus.model.Peminjam;
import perpus.model.Pengguna;
import perpus.model.StatusAnggota;

/** Mengelola anggota beserta akun login-nya (username dan password awal = nomor induk). */
public class AnggotaController {
    private final AnggotaDao dao;
    private final PeminjamanDao peminjamanDao;
    private final PenggunaDao penggunaDao;

    public AnggotaController(AnggotaDao dao, PeminjamanDao peminjamanDao, PenggunaDao penggunaDao) {
        this.dao = dao;
        this.peminjamanDao = peminjamanDao;
        this.penggunaDao = penggunaDao;
    }

    public String idBaru() { return dao.idBaru(); }

    public Anggota cari(String id) { return dao.cari(id); }

    public List<Anggota> daftar(String kataKunci) {
        List<Anggota> hasil = new ArrayList<Anggota>();
        for (Anggota a : dao.semua()) {
            if (a.cocokDengan(kataKunci)) hasil.add(a);
        }
        return hasil;
    }

    public void tambah(String nama, String nomorInduk, String email, StatusAnggota status,
                       LocalDate tanggalDaftar, boolean aktif) throws PerpusException {
        Anggota a = new Anggota(dao.idBaru(), nama, nomorInduk, email, status, tanggalDaftar, aktif);
        cekNomorIndukUnik(a);
        dao.tambah(a);
        try {
            penggunaDao.tambah(akunUntuk(a, Pengguna.hash(a.getNomorInduk())));
        } catch (PerpusException e) {
            dao.hapus(a.getId()); // batalkan: anggota tanpa akun tidak boleh tersimpan
            throw e;
        }
    }

    public void ubah(String id, String nama, String nomorInduk, String email, StatusAnggota status,
                     LocalDate tanggalDaftar, boolean aktif) throws PerpusException {
        Anggota lama = dao.cari(id);
        if (lama == null) throw new DataTidakDitemukanException("Anggota " + id + " tidak ditemukan.");
        Anggota a = new Anggota(id, nama, nomorInduk, email, status, tanggalDaftar, aktif);
        cekNomorIndukUnik(a);
        dao.ubah(a);
        selaraskanAkun(lama, a);
    }

    public void hapus(String id) throws PerpusException {
        if (peminjamanDao.hitungAktifAnggota(id) > 0)
            throw new AturanBisnisException("Anggota tidak dapat dihapus karena masih memiliki pinjaman aktif.");
        Anggota a = dao.cari(id);
        dao.hapus(id);
        if (a != null) penggunaDao.hapusJikaAda(a.getNomorInduk());
    }

    /** Mengembalikan password akun anggota ke nilai awal (nomor induk). */
    public void resetPassword(String id) throws PerpusException {
        Anggota a = dao.cari(id);
        if (a == null) throw new DataTidakDitemukanException("Anggota " + id + " tidak ditemukan.");
        Peminjam akun = akunUntuk(a, Pengguna.hash(a.getNomorInduk()));
        if (penggunaDao.cari(akun.getUsername()) == null) penggunaDao.tambah(akun);
        else penggunaDao.ubah(akun);
    }

    /** Membuat akun untuk anggota lama yang belum punya (misalnya data dari versi sebelumnya). */
    public void pastikanSemuaPunyaAkun() throws PerpusException {
        for (Anggota a : dao.semua()) {
            if (penggunaDao.cari(a.getNomorInduk()) == null)
                penggunaDao.tambah(akunUntuk(a, Pengguna.hash(a.getNomorInduk())));
        }
    }

    // ---------- bantuan ----------
    private Peminjam akunUntuk(Anggota a, String hash) throws PerpusException {
        return new Peminjam(a.getNomorInduk(), hash, a.getNama(), a.getId());
    }

    /** Menyesuaikan akun saat nama atau nomor induk anggota berubah; password tetap. */
    private void selaraskanAkun(Anggota lama, Anggota baru) throws PerpusException {
        Pengguna akunLama = penggunaDao.cari(lama.getNomorInduk());
        String hash = akunLama != null ? akunLama.getPasswordHash() : Pengguna.hash(baru.getNomorInduk());
        Peminjam akunBaru = akunUntuk(baru, hash);
        if (akunLama == null) {
            penggunaDao.tambah(akunBaru);
        } else if (akunLama.getUsername().equals(akunBaru.getUsername())) {
            penggunaDao.ubah(akunBaru);
        } else {
            penggunaDao.hapus(akunLama.getUsername());
            penggunaDao.tambah(akunBaru);
        }
    }

    private void cekNomorIndukUnik(Anggota baru) throws PerpusException {
        for (Anggota a : dao.semua()) {
            if (!a.getId().equals(baru.getId()) && a.getNomorInduk().equals(baru.getNomorInduk()))
                throw new DataDuplikatException("Nomor induk " + baru.getNomorInduk() + " sudah terdaftar atas nama "
                        + a.getNama() + ".");
        }
    }
}
