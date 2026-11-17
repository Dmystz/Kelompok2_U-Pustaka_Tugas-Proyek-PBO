package perpus.view;

import perpus.model.Anggota;
import perpus.util.Format;

public class AnggotaTableModel extends DaftarTableModel<Anggota> {
    private static final long serialVersionUID = 1L;

    public AnggotaTableModel() {
        super("ID", "Nama", "Nomor Induk", "Email", "Status", "Tgl Daftar", "Aktif");
    }

    @Override
    protected Object nilai(Anggota a, int c) {
        switch (c) {
            case 0: return a.getId();
            case 1: return a.getNama();
            case 2: return a.getNomorInduk();
            case 3: return a.getEmail();
            case 4: return a.getStatus().getNama();
            case 5: return Format.tanggal(a.getTanggalDaftar());
            default: return a.isAktif() ? "Ya" : "Tidak";
        }
    }
}
