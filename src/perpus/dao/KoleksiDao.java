package perpus.dao;

import perpus.exception.PerpusException;
import perpus.exception.PenyimpananException;
import perpus.model.JenisKoleksi;
import perpus.model.Koleksi;
import perpus.model.KoleksiFactory;

public class KoleksiDao extends FileDao<Koleksi> {

    public KoleksiDao() throws PerpusException {
        super("koleksi.txt");
    }

    @Override protected String ambilId(Koleksi k) { return k.getId(); }
    @Override protected String awalanId() { return "K"; }

    @Override
    protected String keBaris(Koleksi k) {
        return gabung(k.getJenis().name(), k.getId(), k.getJudul(), k.getPengarang(),
                String.valueOf(k.getTahun()), String.valueOf(k.getStok()),
                String.valueOf(k.isReferensi()), k.getDetail1(), k.getDetail2());
    }

    @Override
    protected Koleksi dariBaris(String[] f) throws PerpusException {
        pastikanKolom(f, 9);
        JenisKoleksi jenis;
        try {
            jenis = JenisKoleksi.valueOf(f[0]);
        } catch (IllegalArgumentException e) {
            throw new PenyimpananException("jenis koleksi tidak dikenal: " + f[0], e);
        }
        return KoleksiFactory.buat(jenis, f[1], f[2], f[3], keInt(f[4]), keInt(f[5]),
                Boolean.parseBoolean(f[6]), f[7], f[8]);
    }
}
