package perpus.model;

/** Peran pengguna aplikasi. */
public enum Peran {
    PETUGAS("Petugas"),
    PEMINJAM("Peminjam");

    private final String nama;

    Peran(String nama) { this.nama = nama; }

    public String getNama() { return nama; }

    @Override
    public String toString() { return nama; }
}
