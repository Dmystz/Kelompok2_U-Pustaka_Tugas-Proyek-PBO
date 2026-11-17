package perpus.controller;

import java.time.LocalDate;

import perpus.dao.AnggotaDao;
import perpus.dao.KoleksiDao;
import perpus.dao.PeminjamanDao;
import perpus.dao.PenggunaDao;
import perpus.exception.PerpusException;
import perpus.model.JenisKoleksi;
import perpus.model.StatusAnggota;

/** Merangkai DAO dan controller; diteruskan ke seluruh tampilan. */
public class AppContext {
    public final LoginController login;
    public final KoleksiController koleksi;
    public final AnggotaController anggota;
    public final PeminjamanController peminjaman;

    public AppContext() throws PerpusException {
        PenggunaDao penggunaDao = new PenggunaDao();
        KoleksiDao koleksiDao = new KoleksiDao();
        AnggotaDao anggotaDao = new AnggotaDao();
        PeminjamanDao peminjamanDao = new PeminjamanDao();

        this.login = new LoginController(penggunaDao, anggotaDao);
        this.koleksi = new KoleksiController(koleksiDao, peminjamanDao);
        this.anggota = new AnggotaController(anggotaDao, peminjamanDao, penggunaDao);
        this.peminjaman = new PeminjamanController(peminjamanDao, koleksiDao, anggotaDao);

        isiDataContohJikaKosong();
        this.anggota.pastikanSemuaPunyaAkun();
    }

    /** Mengisi data contoh agar aplikasi langsung bisa didemokan saat pertama dijalankan. */
    private void isiDataContohJikaKosong() throws PerpusException {
        if (!koleksi.daftar("").isEmpty() || !anggota.daftar("").isEmpty()) return;

        koleksi.tambah(JenisKoleksi.BUKU, "Pemrograman Berorientasi Objek dengan Java", "Budi Raharjo", 2019, 3,
                false, "Informatika", "9786026231");
        koleksi.tambah(JenisKoleksi.BUKU, "Struktur Data dan Algoritma", "Rinaldi Munir", 2016, 2,
                false, "Informatika", "9786020101");
        koleksi.tambah(JenisKoleksi.BUKU, "Kamus Besar Bahasa Indonesia", "Tim Redaksi", 2016, 1,
                true, "Balai Pustaka", "9789794072");
        koleksi.tambah(JenisKoleksi.JURNAL, "Jurnal Teknologi Informasi dan Ilmu Komputer", "Tim Editor", 2023, 4,
                false, "10", "2");
        koleksi.tambah(JenisKoleksi.JURNAL, "Jurnal Teknik Sipil", "Tim Editor", 2022, 2,
                false, "5", "1");
        koleksi.tambah(JenisKoleksi.SKRIPSI, "Sistem Informasi Akademik Berbasis Web", "Ahmad Fauzi", 2021, 1,
                false, "Teknik Komputer", "Dr. Sutrisno, M.T.");
        koleksi.tambah(JenisKoleksi.SKRIPSI, "Rancang Bangun Jaringan Kampus", "Siti Nurhaliza", 2022, 1,
                false, "Teknik Elektro", "Ir. Wahyu, M.Eng.");

        anggota.tambah("Bagus Ariiq Ambiya", "21120124140151", "bagusariiq@students.undip.ac.id",
                StatusAnggota.MAHASISWA, LocalDate.now().minusMonths(6), true);
        anggota.tambah("Ignatius Dimas Prayogo", "21120124130058", "ignatiusdimasprayogo@students.undip.ac.id",
                StatusAnggota.MAHASISWA, LocalDate.now().minusMonths(3), true);
        anggota.tambah("Dr. Oky Dwi Nurhayati, S.T., M.T.", "198000000000000001", "oky@lecturer.undip.ac.id",
                StatusAnggota.DOSEN, LocalDate.now().minusYears(1), true);
        anggota.tambah("Muhammad Habiburrahman", "21120124140121", "habib@students.undip.ac.id",
                StatusAnggota.MAHASISWA, LocalDate.now().minusMonths(8), true);

        // contoh transaksi: satu sudah terlambat supaya fitur denda bisa langsung didemokan
        peminjaman.pinjam(anggota.cari("A001"), koleksi.cari("K001"), LocalDate.now().minusDays(20));
        peminjaman.pinjam(anggota.cari("A002"), koleksi.cari("K004"), LocalDate.now().minusDays(2));
    }
}