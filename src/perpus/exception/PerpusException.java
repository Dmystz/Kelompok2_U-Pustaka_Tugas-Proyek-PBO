package perpus.exception;

/**
 * Custom exception untuk seluruh kesalahan validasi, aturan bisnis,
 * dan kegagalan baca/tulis data pada aplikasi perpustakaan.
 */
public class PerpusException extends Exception {
    private static final long serialVersionUID = 1L;

    public PerpusException(String pesan) {
        super(pesan);
    }

    public PerpusException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
