package perpus.controller;

import java.util.ArrayList;
import java.util.List;

import perpus.dao.KoleksiDao;
import perpus.dao.PeminjamanDao;
import perpus.exception.PerpusException;
import perpus.exception.DataTidakDitemukanException;
import perpus.exception.AturanBisnisException;
import perpus.model.JenisKoleksi;
import perpus.model.Koleksi;
import perpus.model.KoleksiFactory;

public class KoleksiController {
    private final KoleksiDao dao;
    private final PeminjamanDao peminjamanDao;

    public KoleksiController(KoleksiDao dao, PeminjamanDao peminjamanDao) {
        this.dao = dao;
        this.peminjamanDao = peminjamanDao;
    }

    public String idBaru() { return dao.idBaru(); }

    public Koleksi cari(String id) { return dao.cari(id); }

    /** Daftar koleksi yang cocok dengan kata kunci (kosong = semua). */
    public List<Koleksi> daftar(String kataKunci) {
        List<Koleksi> hasil = new ArrayList<Koleksi>();
        for (Koleksi k : dao.semua()) {
            if (k.cocokDengan(kataKunci)) hasil.add(k);
        }
        return hasil;
    }

    public void tambah(JenisKoleksi jenis, String judul, String pengarang, int tahun, int stok,
                       boolean referensi, String detail1, String detail2) throws PerpusException {
        Koleksi k = KoleksiFactory.buat(jenis, dao.idBaru(), judul, pengarang, tahun, stok, referensi, detail1, detail2);
        dao.tambah(k);
    }

    public void ubah(String id, JenisKoleksi jenis, String judul, String pengarang, int tahun, int stok,
                     boolean referensi, String detail1, String detail2) throws PerpusException {
        if (dao.cari(id) == null) throw new DataTidakDitemukanException("Koleksi " + id + " tidak ditemukan.");
        Koleksi k = KoleksiFactory.buat(jenis, id, judul, pengarang, tahun, stok, referensi, detail1, detail2);
        dao.ubah(k);
    }

    public void hapus(String id) throws PerpusException {
        if (peminjamanDao.hitungAktifKoleksi(id) > 0)
            throw new AturanBisnisException("Koleksi tidak dapat dihapus karena masih ada yang dipinjam.");
        dao.hapus(id);
    }
}
