package perpus.exception;

/** Login gagal (username atau password salah). */
public class AutentikasiException extends PerpusException {
    private static final long serialVersionUID = 1L;

    public AutentikasiException(String pesan) {
        super(pesan);
    }

    public AutentikasiException(String pesan, Throwable penyebab) {
        super(pesan, penyebab);
    }
}
