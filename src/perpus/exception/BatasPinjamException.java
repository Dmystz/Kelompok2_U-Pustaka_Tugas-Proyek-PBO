package perpus.exception;

/** Anggota sudah mencapai batas maksimal pinjaman bersamaan. */
public class BatasPinjamException extends AturanBisnisException {
    private static final long serialVersionUID = 1L;

    public BatasPinjamException(String pesan) {
        super(pesan);
    }

    public BatasPinjamException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
