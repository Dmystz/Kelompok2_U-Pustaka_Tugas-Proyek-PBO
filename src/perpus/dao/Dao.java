package perpus.dao;

import java.util.List;

import perpus.exception.PerpusException;

/** Kontrak umum akses data (generic) untuk semua entitas. */
public interface Dao<T> {
    List<T> semua();
    T cari(String id);
    void tambah(T objek) throws PerpusException;
    void ubah(T objek) throws PerpusException;
    void hapus(String id) throws PerpusException;
    String idBaru();
}
