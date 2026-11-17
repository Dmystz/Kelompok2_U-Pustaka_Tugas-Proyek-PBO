package perpus.view;

import perpus.model.Koleksi;

/** Tabel katalog untuk peminjam: hanya baca. */
public class KatalogTableModel extends DaftarTableModel<Koleksi> {
    private static final long serialVersionUID = 1L;

    public KatalogTableModel() {
        super("ID", "Jenis", "Judul", "Pengarang", "Tahun", "Lama Pinjam", "Ketersediaan");
    }

    public static String ketersediaan(Koleksi k) {
        if (k.isReferensi()) return "Baca di tempat";
        return k.getStok() > 0 ? "Tersedia (" + k.getStok() + ")" : "Sedang dipinjam semua";
    }

    @Override
    protected Object nilai(Koleksi k, int c) {
        switch (c) {
            case 0: return k.getId();
            case 1: return k.getJenis().getNama();
            case 2: return k.getJudul();
            case 3: return k.getPengarang();
            case 4: return k.getTahun();
            case 5: return k.getLamaPinjam() + " hari";
            default: return ketersediaan(k);
        }
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return c == 4 ? Integer.class : String.class;
    }
}
