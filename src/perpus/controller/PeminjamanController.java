package perpus.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import perpus.dao.AnggotaDao;
import perpus.dao.KoleksiDao;
import perpus.dao.PeminjamanDao;
import perpus.exception.PerpusException;
import perpus.exception.ValidasiException;
import perpus.exception.DataTidakDitemukanException;
import perpus.exception.AturanBisnisException;
import perpus.exception.StokHabisException;
import perpus.exception.BatasPinjamException;
import perpus.model.Anggota;
import perpus.model.Koleksi;
import perpus.model.Peminjaman;
import perpus.util.Format;

/** Logika transaksi peminjaman dan pengembalian (termasuk perhitungan denda secara polimorfik). */
public class PeminjamanController {
    private final PeminjamanDao dao;
    private final KoleksiDao koleksiDao;
    private final AnggotaDao anggotaDao;

    public PeminjamanController(PeminjamanDao dao, KoleksiDao koleksiDao, AnggotaDao anggotaDao) {
        this.dao = dao;
        this.koleksiDao = koleksiDao;
        this.anggotaDao = anggotaDao;
    }

    public Anggota cariAnggota(String id) { return anggotaDao.cari(id); }
    public Koleksi cariKoleksi(String id) { return koleksiDao.cari(id); }

    public List<Peminjaman> daftar(boolean hanyaBelumKembali) {
        List<Peminjaman> hasil = new ArrayList<Peminjaman>();
        for (Peminjaman p : dao.semua()) {
            if (!hanyaBelumKembali || !p.sudahKembali()) hasil.add(p);
        }
        return hasil;
    }

    public List<Anggota> anggotaAktif() {
        List<Anggota> hasil = new ArrayList<Anggota>();
        for (Anggota a : anggotaDao.semua()) {
            if (a.isAktif()) hasil.add(a);
        }
        return hasil;
    }

    public List<Koleksi> koleksiTersedia() {
        List<Koleksi> hasil = new ArrayList<Koleksi>();
        for (Koleksi k : koleksiDao.semua()) {
            if (k.bisaDipinjam()) hasil.add(k);
        }
        return hasil;
    }

    public Peminjaman pinjam(Anggota anggota, Koleksi koleksi, LocalDate tanggalPinjam) throws PerpusException {
        if (anggota == null) throw new ValidasiException("Pilih anggota terlebih dahulu.");
        if (koleksi == null) throw new ValidasiException("Pilih koleksi terlebih dahulu.");
        if (tanggalPinjam == null || tanggalPinjam.isAfter(LocalDate.now()))
            throw new ValidasiException("Tanggal pinjam tidak boleh di masa depan.");
        if (!anggota.isAktif()) throw new AturanBisnisException("Anggota tidak aktif.");
        if (koleksi.isReferensi()) throw new AturanBisnisException("Koleksi referensi hanya boleh dibaca di tempat.");
        if (koleksi.getStok() <= 0) throw new StokHabisException("Stok koleksi habis.");
        int aktif = dao.hitungAktifAnggota(anggota.getId());
        int batas = anggota.getStatus().getMaksPinjam();
        if (aktif >= batas)
            throw new BatasPinjamException("Batas pinjaman " + anggota.getStatus().getNama() + " adalah " + batas
                    + " koleksi dan sudah tercapai.");

        // getLamaPinjam() dipanggil lewat referensi Koleksi: hasilnya berbeda per jenis (polimorfisme)
        LocalDate jatuhTempo = tanggalPinjam.plusDays(koleksi.getLamaPinjam());
        Peminjaman p = new Peminjaman(dao.idBaru(), koleksi.getId(), anggota.getId(), tanggalPinjam,
                jatuhTempo, null, 0);
        dao.tambah(p);
        try {
            koleksi.setStok(koleksi.getStok() - 1);
            koleksiDao.ubah(koleksi);
        } catch (PerpusException e) {
            koleksi.setStok(koleksi.getStok() + 1);
            dao.hapus(p.getId());
            throw e;
        }
        return p;
    }

    public Peminjaman kembalikan(String idPeminjaman, LocalDate tanggalKembali) throws PerpusException {
        Peminjaman p = dao.cari(idPeminjaman);
        if (p == null) throw new DataTidakDitemukanException("Data peminjaman tidak ditemukan.");
        if (p.sudahKembali()) throw new AturanBisnisException("Koleksi ini sudah dikembalikan.");
        if (tanggalKembali == null || tanggalKembali.isAfter(LocalDate.now()))
            throw new ValidasiException("Tanggal kembali tidak boleh di masa depan.");
        Koleksi k = koleksiDao.cari(p.getIdKoleksi());
        if (k == null) throw new DataTidakDitemukanException("Data koleksi untuk peminjaman ini sudah tidak ada.");

        p.setTanggalKembali(tanggalKembali);
        // hitungDenda dipanggil lewat referensi Koleksi (overriding berbeda untuk Buku/Jurnal/Skripsi)
        p.setDenda(k.hitungDenda(p.getJatuhTempo(), tanggalKembali));
        dao.ubah(p);
        k.setStok(k.getStok() + 1);
        koleksiDao.ubah(k);
        return p;
    }

    /** Perkiraan denda bila koleksi dikembalikan pada tanggal tertentu. */
    public double estimasiDenda(Peminjaman p, LocalDate pada) {
        if (p.sudahKembali()) return p.getDenda();
        Koleksi k = koleksiDao.cari(p.getIdKoleksi());
        return k == null ? 0 : k.hitungDenda(p.getJatuhTempo(), pada);
    }

    /** Pinjaman milik satu anggota (untuk halaman "Pinjaman Saya"). */
    public List<Peminjaman> daftarAnggota(String idAnggota, boolean hanyaBelumKembali) {
        List<Peminjaman> hasil = new ArrayList<Peminjaman>();
        for (Peminjaman p : dao.semua()) {
            if (p.getIdAnggota().equals(idAnggota) && (!hanyaBelumKembali || !p.sudahKembali())) hasil.add(p);
        }
        return hasil;
    }

    /** Ringkasan untuk beranda petugas. */
    public Map<String, String> ringkasan() {
        Map<String, String> r = new LinkedHashMap<String, String>();
        int sedangDipinjam = 0;
        int terlambat = 0;
        for (Peminjaman p : dao.semua()) {
            if (!p.sudahKembali()) sedangDipinjam++;
            if (p.terlambat()) terlambat++;
        }
        r.put("Judul koleksi", String.valueOf(koleksiDao.semua().size()));
        r.put("Anggota", String.valueOf(anggotaDao.semua().size()));
        r.put("Sedang dipinjam", String.valueOf(sedangDipinjam));
        r.put("Terlambat", String.valueOf(terlambat));
        return r;
    }

    /** Ringkasan untuk beranda peminjam (anggota). */
    public Map<String, String> ringkasanAnggota(String idAnggota) {
        int aktif = 0;
        int terlambat = 0;
        double denda = 0;
        for (Peminjaman p : daftarAnggota(idAnggota, true)) {
            aktif++;
            if (p.terlambat()) {
                terlambat++;
                denda += estimasiDenda(p, LocalDate.now());
            }
        }
        Anggota a = anggotaDao.cari(idAnggota);
        int batas = a == null ? 0 : a.getStatus().getMaksPinjam();
        Map<String, String> r = new LinkedHashMap<String, String>();
        r.put("Sedang dipinjam", String.valueOf(aktif));
        r.put("Terlambat", String.valueOf(terlambat));
        r.put("Denda berjalan", Format.rupiah(denda));
        r.put("Sisa kuota pinjam", String.valueOf(Math.max(0, batas - aktif)));
        return r;
    }
}
