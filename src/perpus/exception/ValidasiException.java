package perpus.exception;

/** Input atau nilai atribut tidak memenuhi aturan validasi (kosong, negatif, format salah, dll.). */
public class ValidasiException extends PerpusException {
    private static final long serialVersionUID = 1L;

    public ValidasiException(String pesan) {
        super(pesan);
    }

    public ValidasiException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
