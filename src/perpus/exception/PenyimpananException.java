package perpus.exception;

/** Kegagalan membaca atau menulis berkas data, atau isi berkas rusak. */
public class PenyimpananException extends PerpusException {
    private static final long serialVersionUID = 1L;

    public PenyimpananException(String pesan) {
        super(pesan);
    }

    public PenyimpananException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
