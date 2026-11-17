package perpus.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import perpus.exception.PerpusException;
import perpus.exception.ValidasiException;

/**
 * Kelas induk abstrak akun yang boleh login. Password disimpan sebagai hash SHA-256.
 * Turunannya: {@link Petugas} (mengelola data) dan {@link Peminjam} (anggota perpustakaan).
 */
public abstract class Pengguna {
    private static final int PASSWORD_MIN = 6;

    private String username;
    private String passwordHash;
    private String namaLengkap;

    protected Pengguna(String username, String passwordHash, String namaLengkap) throws PerpusException {
        if (username == null || username.trim().isEmpty()) throw new ValidasiException("Username tidak boleh kosong.");
        if (passwordHash == null || passwordHash.isEmpty()) throw new ValidasiException("Password tidak boleh kosong.");
        this.username = username.trim();
        this.passwordHash = passwordHash;
        this.namaLengkap = namaLengkap == null ? "" : namaLengkap;
    }

    // ---------- perilaku yang berbeda per peran (polimorfisme) ----------
    public abstract Peran getPeran();

    /** Hanya petugas yang boleh mengelola koleksi, anggota, dan transaksi. */
    public abstract boolean bolehKelolaData();

    /** ID anggota yang terhubung dengan akun ini; null untuk petugas. */
    public String getIdAnggota() { return null; }

    // ---------- umum ----------
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getNamaLengkap() { return namaLengkap; }

    public boolean passwordCocok(String passwordPolos) {
        return passwordPolos != null && passwordHash.equals(hash(passwordPolos));
    }

    public void gantiPassword(String baru) throws PerpusException {
        if (baru == null || baru.length() < PASSWORD_MIN)
            throw new ValidasiException("Password baru minimal " + PASSWORD_MIN + " karakter.");
        this.passwordHash = hash(baru);
    }

    public static String hash(String teks) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest(teks.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : h) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 tidak tersedia", e);
        }
    }
}
