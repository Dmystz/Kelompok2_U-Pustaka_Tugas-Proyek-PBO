package perpus.exception;

/** Data yang sama sudah ada (ID atau nomor induk ganda). */
public class DataDuplikatException extends PerpusException {
    private static final long serialVersionUID = 1L;

    public DataDuplikatException(String pesan) {
        super(pesan);
    }

    public DataDuplikatException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
