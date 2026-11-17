package perpus.model;

/** Status keanggotaan beserta batas maksimal buku yang boleh dipinjam bersamaan. */
public enum StatusAnggota {
    MAHASISWA("Mahasiswa", 3),
    DOSEN("Dosen", 5),
    STAF("Staf", 4);

    private final String nama;
    private final int maksPinjam;

    StatusAnggota(String nama, int maksPinjam) {
        this.nama = nama;
        this.maksPinjam = maksPinjam;
    }

    public String getNama() { return nama; }
    public int getMaksPinjam() { return maksPinjam; }

    @Override
    public String toString() { return nama; }
}
