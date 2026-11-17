package perpus.model;

/** Interface untuk objek yang dapat dicari berdasarkan kata kunci (dipakai Koleksi dan Anggota). */
public interface DapatDicari {
    boolean cocokDengan(String kataKunci);
}
