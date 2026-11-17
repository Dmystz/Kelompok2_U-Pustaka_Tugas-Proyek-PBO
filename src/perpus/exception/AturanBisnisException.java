package perpus.exception;

/** Tindakan melanggar aturan perpustakaan (anggota nonaktif, koleksi referensi, dll.). */
public class AturanBisnisException extends PerpusException {
    private static final long serialVersionUID = 1L;

    public AturanBisnisException(String pesan) {
        super(pesan);
    }

    public AturanBisnisException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
