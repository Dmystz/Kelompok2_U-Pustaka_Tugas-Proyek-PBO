package perpus.exception;

/** Stok koleksi habis sehingga tidak dapat dipinjam. */
public class StokHabisException extends AturanBisnisException {
    private static final long serialVersionUID = 1L;

    public StokHabisException(String pesan) {
        super(pesan);
    }

    public StokHabisException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
