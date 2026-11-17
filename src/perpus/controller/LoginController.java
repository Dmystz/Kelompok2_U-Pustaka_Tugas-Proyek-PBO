package perpus.controller;

import perpus.dao.AnggotaDao;
import perpus.dao.PenggunaDao;
import perpus.exception.AutentikasiException;
import perpus.exception.PerpusException;
import perpus.exception.ValidasiException;
import perpus.model.Anggota;
import perpus.model.Pengguna;

public class LoginController {
    private final PenggunaDao dao;
    private final AnggotaDao anggotaDao;

    public LoginController(PenggunaDao dao, AnggotaDao anggotaDao) {
        this.dao = dao;
        this.anggotaDao = anggotaDao;
    }

    /** Memeriksa kredensial; melempar PerpusException bila salah. */
    public Pengguna login(String username, char[] password) throws PerpusException {
        if (username == null || username.trim().isEmpty() || password == null || password.length == 0)
            throw new ValidasiException("Username dan password wajib diisi.");
        Pengguna p = dao.cari(username.trim());
        if (p == null || !p.passwordCocok(new String(password)))
            throw new AutentikasiException("Username atau password salah.");
        if (p.getIdAnggota() != null) {
            Anggota a = anggotaDao.cari(p.getIdAnggota());
            if (a == null || !a.isAktif())
                throw new AutentikasiException("Keanggotaan tidak aktif. Silakan hubungi petugas perpustakaan.");
        }
        return p;
    }

    public void gantiPassword(Pengguna p, String lama, String baru, String konfirmasi) throws PerpusException {
        if (!p.passwordCocok(lama)) throw new AutentikasiException("Password lama salah.");
        if (baru == null || !baru.equals(konfirmasi)) throw new ValidasiException("Konfirmasi password tidak sama.");
        p.gantiPassword(baru);
        dao.ubah(p);
    }
}
