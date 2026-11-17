package perpus.exception;

/** Data dengan ID tertentu tidak ditemukan. */
public class DataTidakDitemukanException extends PerpusException {
    private static final long serialVersionUID = 1L;

    public DataTidakDitemukanException(String pesan) {
        super(pesan);
    }

    public DataTidakDitemukanException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
