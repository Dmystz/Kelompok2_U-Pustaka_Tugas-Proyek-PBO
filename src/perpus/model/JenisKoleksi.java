package perpus.model;

/** Jenis koleksi beserta label untuk dua atribut khusus tiap jenis. */
public enum JenisKoleksi {
    BUKU("Buku", "Penerbit", "ISBN"),
    JURNAL("Jurnal", "Volume", "Nomor"),
    SKRIPSI("Skripsi", "Program Studi", "Dosen Pembimbing");

    private final String nama;
    private final String labelDetail1;
    private final String labelDetail2;

    JenisKoleksi(String nama, String labelDetail1, String labelDetail2) {
        this.nama = nama;
        this.labelDetail1 = labelDetail1;
        this.labelDetail2 = labelDetail2;
    }

    public String getNama() { return nama; }
    public String getLabelDetail1() { return labelDetail1; }
    public String getLabelDetail2() { return labelDetail2; }

    @Override
    public String toString() { return nama; }
}
