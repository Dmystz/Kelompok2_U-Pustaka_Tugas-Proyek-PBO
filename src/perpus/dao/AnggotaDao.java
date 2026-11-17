package perpus.dao;

import perpus.exception.PerpusException;
import perpus.exception.PenyimpananException;
import perpus.model.Anggota;
import perpus.model.StatusAnggota;

public class AnggotaDao extends FileDao<Anggota> {

    public AnggotaDao() throws PerpusException {
        super("anggota.txt");
    }

    @Override protected String ambilId(Anggota a) { return a.getId(); }
    @Override protected String awalanId() { return "A"; }

    @Override
    protected String keBaris(Anggota a) {
        return gabung(a.getId(), a.getNama(), a.getNomorInduk(), a.getEmail(), a.getStatus().name(),
                dariTanggal(a.getTanggalDaftar()), String.valueOf(a.isAktif()));
    }

    @Override
    protected Anggota dariBaris(String[] f) throws PerpusException {
        pastikanKolom(f, 7);
        StatusAnggota status;
        try {
            status = StatusAnggota.valueOf(f[4]);
        } catch (IllegalArgumentException e) {
            throw new PenyimpananException("status anggota tidak dikenal: " + f[4], e);
        }
        return new Anggota(f[0], f[1], f[2], f[3], status, keTanggal(f[5]), Boolean.parseBoolean(f[6]));
    }
}
