package perpus.dao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import perpus.exception.PerpusException;
import perpus.exception.DataTidakDitemukanException;
import perpus.exception.DataDuplikatException;
import perpus.exception.PenyimpananException;

/**
 * Implementasi DAO berbasis file teks (satu baris = satu objek, kolom dipisah '|').
 * Data disimpan di folder "data/" dan dimuat ke Map saat objek dibuat.
 */
public abstract class FileDao<T> implements Dao<T> {

    private static final String FOLDER_BAWAAN = "data";

    private final Path berkas;
    private final Map<String, T> data = new LinkedHashMap<String, T>();

    protected FileDao(String namaBerkas) throws PerpusException {
        this.berkas = Paths.get(System.getProperty("perpus.data", FOLDER_BAWAAN), namaBerkas);
        muat();
    }

    // ---------- dikerjakan oleh kelas turunan ----------
    protected abstract String ambilId(T objek);
    protected abstract String keBaris(T objek);
    protected abstract T dariBaris(String[] kolom) throws PerpusException;
    protected abstract String awalanId();

    // ---------- operasi Dao ----------
    @Override
    public List<T> semua() {
        return new ArrayList<T>(data.values());
    }

    @Override
    public T cari(String id) {
        return data.get(id);
    }

    @Override
    public void tambah(T objek) throws PerpusException {
        String id = ambilId(objek);
        if (data.containsKey(id)) throw new DataDuplikatException("Data dengan ID " + id + " sudah ada.");
        data.put(id, objek);
        try {
            simpan();
        } catch (PerpusException e) {
            data.remove(id);
            throw e;
        }
    }

    @Override
    public void ubah(T objek) throws PerpusException {
        String id = ambilId(objek);
        T lama = data.get(id);
        if (lama == null) throw new DataTidakDitemukanException("Data dengan ID " + id + " tidak ditemukan.");
        data.put(id, objek);
        try {
            simpan();
        } catch (PerpusException e) {
            data.put(id, lama);
            throw e;
        }
    }

    @Override
    public void hapus(String id) throws PerpusException {
        T lama = data.remove(id);
        if (lama == null) throw new DataTidakDitemukanException("Data dengan ID " + id + " tidak ditemukan.");
        try {
            simpan();
        } catch (PerpusException e) {
            data.put(id, lama);
            throw e;
        }
    }

    @Override
    public String idBaru() {
        int maks = 0;
        for (String id : data.keySet()) {
            if (id.startsWith(awalanId())) {
                try {
                    maks = Math.max(maks, Integer.parseInt(id.substring(awalanId().length())));
                } catch (NumberFormatException e) {
                    // ID tidak berpola awalan+angka, abaikan
                }
            }
        }
        return String.format("%s%03d", awalanId(), maks + 1);
    }

    // ---------- baca/tulis berkas ----------
    private void muat() throws PerpusException {
        try {
            Files.createDirectories(berkas.getParent());
            if (!Files.exists(berkas)) {
                Files.createFile(berkas);
                return;
            }
            List<String> baris = Files.readAllLines(berkas, StandardCharsets.UTF_8);
            int no = 0;
            for (String b : baris) {
                no++;
                if (b.trim().isEmpty()) continue;
                try {
                    T objek = dariBaris(pisah(b));
                    data.put(ambilId(objek), objek);
                } catch (PerpusException e) {
                    throw new PenyimpananException("Berkas " + berkas.getFileName() + " baris " + no
                            + " rusak: " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new PenyimpananException("Gagal membaca berkas " + berkas + ": " + e.getMessage(), e);
        }
    }

    private void simpan() throws PerpusException {
        List<String> baris = new ArrayList<String>();
        for (T objek : data.values()) baris.add(keBaris(objek));
        try {
            Path sementara = Paths.get(berkas.toString() + ".tmp");
            Files.write(sementara, baris, StandardCharsets.UTF_8);
            Files.move(sementara, berkas, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new PenyimpananException("Gagal menyimpan ke berkas " + berkas + ": " + e.getMessage(), e);
        }
    }

    // ---------- utilitas format baris ----------
    protected static String gabung(String... kolom) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < kolom.length; i++) {
            if (i > 0) sb.append('|');
            sb.append(escape(kolom[i] == null ? "" : kolom[i]));
        }
        return sb.toString();
    }

    protected static String[] pisah(String baris) {
        String[] kolom = baris.split("\\|", -1);
        for (int i = 0; i < kolom.length; i++) kolom[i] = unescape(kolom[i]);
        return kolom;
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '\\') sb.append("\\\\");
            else if (c == '|') sb.append("\\p");
            else if (c == '\n') sb.append("\\n");
            else if (c == '\r') continue;
            else sb.append(c);
        }
        return sb.toString();
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(++i);
                if (n == 'p') sb.append('|');
                else if (n == 'n') sb.append('\n');
                else sb.append(n);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    protected static void pastikanKolom(String[] kolom, int jumlah) throws PerpusException {
        if (kolom.length < jumlah) throw new PenyimpananException("jumlah kolom kurang (" + kolom.length + "/" + jumlah + ")");
    }

    protected static int keInt(String s) throws PerpusException {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new PenyimpananException("angka tidak valid: '" + s + "'", e);
        }
    }

    protected static double keDouble(String s) throws PerpusException {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            throw new PenyimpananException("angka tidak valid: '" + s + "'", e);
        }
    }

    protected static LocalDate keTanggal(String s) throws PerpusException {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (DateTimeParseException e) {
            throw new PenyimpananException("tanggal tidak valid: '" + s + "'", e);
        }
    }

    protected static String dariTanggal(LocalDate t) {
        return t == null ? "" : t.toString();
    }
}
