package perpus;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;

import perpus.controller.AppContext;
import perpus.dao.PenggunaDao;
import perpus.exception.AturanBisnisException;
import perpus.exception.AutentikasiException;
import perpus.exception.BatasPinjamException;
import perpus.exception.DataDuplikatException;
import perpus.exception.DataTidakDitemukanException;
import perpus.exception.PenyimpananException;
import perpus.exception.PerpusException;
import perpus.exception.StokHabisException;
import perpus.exception.ValidasiException;
import perpus.model.Anggota;
import perpus.model.JenisKoleksi;
import perpus.model.Koleksi;
import perpus.model.Peminjaman;
import perpus.model.Pengguna;
import perpus.model.Peran;
import perpus.model.Petugas;
import perpus.model.StatusAnggota;
import perpus.util.Format;

/**
 * Pengujian lewat konsol (tanpa GUI) untuk model, DAO, dan controller.
 * Jalankan sebagai Java Application; data uji ditulis ke folder data/.
 */
public class ConsoleTest {
    private static int lulus = 0;
    private static int gagal = 0;

    public static void main(String[] args) throws Exception {
        // data uji dipisah dari data aplikasi (folder "data")
        System.setProperty("perpus.data", "data_uji");
        hapusFolder(new File("data_uji"));

        System.out.println("=== Uji polimorfisme: hitungDenda lewat referensi Koleksi ===");
        AppContext ctx = new AppContext();
        for (Koleksi k : ctx.koleksi.daftar("")) {
            System.out.printf("%-8s %-45s pinjam %2d hari | denda 5 hari = %s%n", k.getJenis(),
                    k.getJudul(), k.getLamaPinjam(), Format.rupiah(k.hitungDenda(5)));
        }
        Koleksi buku = ctx.koleksi.cari("K001");
        Koleksi jurnal = ctx.koleksi.cari("K004");
        Koleksi skripsi = ctx.koleksi.cari("K006");
        cek("denda buku 5 hari = 5000", buku.hitungDenda(5) == 5000);
        cek("denda jurnal 5 hari = 10000", jurnal.hitungDenda(5) == 10000);
        cek("denda jurnal dibatasi 30000", jurnal.hitungDenda(100) == 30000);
        cek("denda skripsi 2 hari = 20000", skripsi.hitungDenda(2) == 20000);
        cek("tidak telat = denda 0", skripsi.hitungDenda(0) == 0);
        cek("overload tanggal: buku telat 3 hari",
                buku.hitungDenda(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 4)) == 3000);

        System.out.println("\n=== Uji validasi & exception ===");
        gagalDenganPesan("stok negatif", ValidasiException.class, () -> ctx.koleksi.tambah(JenisKoleksi.BUKU, "X", "Y", 2020, -1, false, "", ""));
        gagalDenganPesan("tahun tidak valid", ValidasiException.class, () -> ctx.koleksi.tambah(JenisKoleksi.BUKU, "X", "Y", 1800, 1, false, "", ""));
        gagalDenganPesan("judul kosong", ValidasiException.class, () -> ctx.koleksi.tambah(JenisKoleksi.BUKU, " ", "Y", 2020, 1, false, "", ""));
        gagalDenganPesan("email salah", ValidasiException.class, () -> ctx.anggota.tambah("Tes", "12345678", "bukan-email",
                StatusAnggota.MAHASISWA, LocalDate.now(), true));
        gagalDenganPesan("NIM bukan angka", ValidasiException.class, () -> ctx.anggota.tambah("Tes", "abc", "a@b.com",
                StatusAnggota.MAHASISWA, LocalDate.now(), true));
        gagalDenganPesan("NIM duplikat", DataDuplikatException.class, () -> ctx.anggota.tambah("Tes", "21120123140001", "a@b.com",
                StatusAnggota.MAHASISWA, LocalDate.now(), true));
        gagalDenganPesan("login salah", AutentikasiException.class, () -> ctx.login.login("admin", "salah".toCharArray()));
        cek("login benar", ctx.login.login("admin", "admin123".toCharArray()) != null);

        System.out.println("\n=== Uji aturan peminjaman ===");
        Anggota dewi = ctx.anggota.cari("A001");
        Anggota nonaktif = ctx.anggota.cari("A004");
        Koleksi referensi = ctx.koleksi.cari("K003");
        gagalDenganPesan("pinjam koleksi referensi", AturanBisnisException.class, () -> ctx.peminjaman.pinjam(dewi, referensi, LocalDate.now()));
        gagalDenganPesan("anggota nonaktif", AturanBisnisException.class, () -> ctx.peminjaman.pinjam(nonaktif, buku, LocalDate.now()));
        gagalDenganPesan("hapus koleksi yang dipinjam", AturanBisnisException.class, () -> ctx.koleksi.hapus("K001"));
        gagalDenganPesan("hapus anggota yang meminjam", AturanBisnisException.class, () -> ctx.anggota.hapus("A001"));

        Peminjaman p = null;
        for (Peminjaman x : ctx.peminjaman.daftar(true)) if (x.getIdKoleksi().equals("K001")) p = x;
        int stokSebelum = ctx.koleksi.cari("K001").getStok();
        Peminjaman hasil = ctx.peminjaman.kembalikan(p.getId(), LocalDate.now());
        System.out.println("Pengembalian " + hasil.getId() + ": denda " + Format.rupiah(hasil.getDenda()));
        cek("buku telat 6 hari didenda 6000", hasil.getDenda() == 6000);
        cek("stok bertambah setelah kembali", ctx.koleksi.cari("K001").getStok() == stokSebelum + 1);
        gagalDenganPesan("kembalikan dua kali", AturanBisnisException.class, () -> ctx.peminjaman.kembalikan(hasil.getId(), LocalDate.now()));

        System.out.println("\n=== Uji batas pinjam mahasiswa (maks 3) ===");
        Anggota rizky = ctx.anggota.cari("A002"); // sudah pinjam 1 jurnal
        ctx.peminjaman.pinjam(rizky, ctx.koleksi.cari("K001"), LocalDate.now());
        ctx.peminjaman.pinjam(rizky, ctx.koleksi.cari("K002"), LocalDate.now());
        gagalDenganPesan("pinjaman ke-4 ditolak", BatasPinjamException.class, () -> ctx.peminjaman.pinjam(rizky, ctx.koleksi.cari("K005"), LocalDate.now()));

        System.out.println("\n=== Uji peran peminjam (anggota) ===");
        Pengguna akunDewi = ctx.login.login("21120123140001", "21120123140001".toCharArray());
        cek("anggota login dengan NIM", akunDewi.getPeran() == Peran.PEMINJAM && !akunDewi.bolehKelolaData());
        cek("akun terhubung ke ID anggota", "A001".equals(akunDewi.getIdAnggota()));
        cek("petugas boleh kelola data", ctx.login.login("admin", "admin123".toCharArray()).bolehKelolaData());
        gagalDenganPesan("anggota nonaktif tidak bisa login", AutentikasiException.class,
                () -> ctx.login.login("198803152015041002", "198803152015041002".toCharArray()));
        List<Peminjaman> milikDewi = ctx.peminjaman.daftarAnggota("A001", false);
        boolean semuaMilikDewi = !milikDewi.isEmpty();
        for (Peminjaman x : milikDewi) if (!x.getIdAnggota().equals("A001")) semuaMilikDewi = false;
        cek("riwayat pinjaman hanya milik sendiri", semuaMilikDewi);
        cek("ringkasan anggota: Rizky sedang pinjam 3", "3".equals(ctx.peminjaman.ringkasanAnggota("A002").get("Sedang dipinjam")));
        cek("ringkasan anggota: sisa kuota Rizky 0", "0".equals(ctx.peminjaman.ringkasanAnggota("A002").get("Sisa kuota pinjam")));

        ctx.login.gantiPassword(akunDewi, "21120123140001", "rahasia123", "rahasia123");
        cek("login dengan password baru", ctx.login.login("21120123140001", "rahasia123".toCharArray()) != null);
        gagalDenganPesan("password lama tidak berlaku lagi", AutentikasiException.class,
                () -> ctx.login.login("21120123140001", "21120123140001".toCharArray()));
        gagalDenganPesan("ganti password: password lama salah", AutentikasiException.class,
                () -> ctx.login.gantiPassword(akunDewi, "salah", "abcdef", "abcdef"));
        gagalDenganPesan("ganti password: konfirmasi beda", ValidasiException.class,
                () -> ctx.login.gantiPassword(akunDewi, "rahasia123", "abcdef", "abcdeX"));
        gagalDenganPesan("ganti password: terlalu pendek", ValidasiException.class,
                () -> ctx.login.gantiPassword(akunDewi, "rahasia123", "abc", "abc"));
        ctx.anggota.resetPassword("A001");
        cek("reset password kembali ke NIM", ctx.login.login("21120123140001", "21120123140001".toCharArray()) != null);

        String idBudi = ctx.anggota.idBaru();
        ctx.anggota.tambah("Budi Uji", "99887766", "budi@uji.id", StatusAnggota.MAHASISWA, LocalDate.now(), true);
        cek("akun dibuat otomatis untuk anggota baru", ctx.login.login("99887766", "99887766".toCharArray()) != null);
        ctx.anggota.ubah(idBudi, "Budi Uji", "99887755", "budi@uji.id", StatusAnggota.MAHASISWA, LocalDate.now(), true);
        cek("ubah NIM memindahkan username", ctx.login.login("99887755", "99887766".toCharArray()) != null);
        gagalDenganPesan("username lama (NIM lama) tidak berlaku", AutentikasiException.class,
                () -> ctx.login.login("99887766", "99887766".toCharArray()));
        ctx.anggota.hapus(idBudi);
        gagalDenganPesan("hapus anggota ikut menghapus akun", AutentikasiException.class,
                () -> ctx.login.login("99887755", "99887766".toCharArray()));

        System.out.println("\n=== Uji jenis exception lainnya ===");
        ctx.peminjaman.pinjam(ctx.anggota.cari("A003"), ctx.koleksi.cari("K007"), LocalDate.now()); // stok 1 -> 0
        gagalDenganPesan("stok habis", StokHabisException.class,
                () -> ctx.peminjaman.pinjam(dewi, ctx.koleksi.cari("K007"), LocalDate.now()));
        gagalDenganPesan("pengembalian ID tidak ada", DataTidakDitemukanException.class,
                () -> ctx.peminjaman.kembalikan("P999", LocalDate.now()));
        gagalDenganPesan("ubah koleksi ID tidak ada", DataTidakDitemukanException.class,
                () -> ctx.koleksi.ubah("K999", JenisKoleksi.BUKU, "J", "P", 2020, 1, false, "", ""));
        gagalDenganPesan("tanggal pinjam di masa depan", ValidasiException.class,
                () -> ctx.peminjaman.pinjam(dewi, ctx.koleksi.cari("K002"), LocalDate.now().plusDays(1)));
        cek("StokHabisException adalah AturanBisnisException", new StokHabisException("x") instanceof AturanBisnisException);
        cek("semua exception turunan PerpusException", new PenyimpananException("x") instanceof PerpusException);

        System.out.println("\n=== Uji penyimpanan permanen (muat ulang dari file) ===");
        int jumlah = ctx.koleksi.daftar("").size();
        ctx.koleksi.tambah(JenisKoleksi.SKRIPSI, "Judul | dengan pemisah\nbaris baru", "Penulis", 2024, 1, false, "TI", "Dr. A");
        AppContext ulang = new AppContext();
        List<Koleksi> semua = ulang.koleksi.daftar("");
        cek("jumlah koleksi bertambah setelah muat ulang", semua.size() == jumlah + 1);
        cek("karakter khusus tersimpan utuh",
                ulang.koleksi.cari("K008").getJudul()
                        .equals("Judul | dengan pemisah\nbaris baru"));
        cek("pengembalian tetap tersimpan", ulang.peminjaman.daftar(false).size() == 5);

        System.out.println("\n=== Uji migrasi berkas pengguna format lama ===");
        System.setProperty("perpus.data", "data_uji2");
        hapusFolder(new File("data_uji2"));
        Files.createDirectories(Paths.get("data_uji2"));
        Files.write(Paths.get("data_uji2", "pengguna.txt"),
                ("admin|" + Pengguna.hash("admin123") + "|Admin Lama\n").getBytes(StandardCharsets.UTF_8));
        PenggunaDao daoLama = new PenggunaDao();
        cek("format lama dibaca sebagai Petugas",
                daoLama.cari("admin") instanceof Petugas && daoLama.cari("admin").passwordCocok("admin123"));
        System.setProperty("perpus.data", "data_uji");
        hapusFolder(new File("data_uji2"));

        System.out.println("\n=== Uji berkas data rusak ===");
        Files.write(Paths.get("data_uji", "koleksi.txt"), "RUSAK|bukan|data\n".getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.APPEND);
        try {
            new AppContext();
            cek("berkas rusak (seharusnya ditolak)", false);
        } catch (PenyimpananException e) {
            cek("berkas rusak -> \"" + e.getMessage() + "\"", true);
        }
        hapusFolder(new File("data_uji"));

        System.out.println("\nHASIL: " + lulus + " lulus, " + gagal + " gagal");
        if (gagal > 0) System.exit(1);
    }

    private static void hapusFolder(File f) {
        File[] isi = f.listFiles();
        if (isi != null) for (File x : isi) hapusFolder(x);
        f.delete();
    }

    private interface Aksi { void jalan() throws PerpusException; }

    /** Aksi harus ditolak dengan exception bertipe tertentu. */
    private static void gagalDenganPesan(String nama, Class<? extends PerpusException> tipe, Aksi aksi) {
        try {
            aksi.jalan();
            cek(nama + " (seharusnya ditolak)", false);
        } catch (PerpusException e) {
            boolean sesuai = tipe.isInstance(e);
            cek(nama + " -> " + e.getClass().getSimpleName() + ": \"" + e.getMessage() + "\"", sesuai);
        }
    }

    private static void cek(String nama, boolean ok) {
        System.out.println((ok ? "[LULUS] " : "[GAGAL] ") + nama);
        if (ok) lulus++; else gagal++;
    }
}
