package perpus.dao;

import perpus.exception.PerpusException;
import perpus.model.Peminjaman;

public class PeminjamanDao extends FileDao<Peminjaman> {

    public PeminjamanDao() throws PerpusException {
        super("peminjaman.txt");
    }

    @Override protected String ambilId(Peminjaman p) { return p.getId(); }
    @Override protected String awalanId() { return "P"; }

    @Override
    protected String keBaris(Peminjaman p) {
        return gabung(p.getId(), p.getIdKoleksi(), p.getIdAnggota(), dariTanggal(p.getTanggalPinjam()),
                dariTanggal(p.getJatuhTempo()), dariTanggal(p.getTanggalKembali()),
                String.valueOf(p.getDenda()));
    }

    @Override
    protected Peminjaman dariBaris(String[] f) throws PerpusException {
        pastikanKolom(f, 7);
        return new Peminjaman(f[0], f[1], f[2], keTanggal(f[3]), keTanggal(f[4]), keTanggal(f[5]), keDouble(f[6]));
    }

    /** Jumlah pinjaman yang belum dikembalikan oleh seorang anggota. */
    public int hitungAktifAnggota(String idAnggota) {
        int n = 0;
        for (Peminjaman p : semua()) {
            if (p.getIdAnggota().equals(idAnggota) && !p.sudahKembali()) n++;
        }
        return n;
    }

    /** Jumlah pinjaman yang belum dikembalikan untuk satu koleksi. */
    public int hitungAktifKoleksi(String idKoleksi) {
        int n = 0;
        for (Peminjaman p : semua()) {
            if (p.getIdKoleksi().equals(idKoleksi) && !p.sudahKembali()) n++;
        }
        return n;
    }
}
