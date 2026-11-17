package perpus.view;

import perpus.model.Koleksi;

public class KoleksiTableModel extends DaftarTableModel<Koleksi> {
    private static final long serialVersionUID = 1L;

    public KoleksiTableModel() {
        super("ID", "Jenis", "Judul", "Pengarang", "Tahun", "Stok", "Referensi", "Detail");
    }

    @Override
    protected Object nilai(Koleksi k, int c) {
        switch (c) {
            case 0: return k.getId();
            case 1: return k.getJenis().getNama();
            case 2: return k.getJudul();
            case 3: return k.getPengarang();
            case 4: return k.getTahun();
            case 5: return k.getStok();
            case 6: return k.isReferensi() ? "Ya" : "Tidak";
            default: return k.getDetail1() + " | " + k.getDetail2();
        }
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return (c == 4 || c == 5) ? Integer.class : String.class;
    }
}
