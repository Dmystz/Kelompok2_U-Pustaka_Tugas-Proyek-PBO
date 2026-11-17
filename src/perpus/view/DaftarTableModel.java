package perpus.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/** Model tabel generik: kelas turunan hanya menentukan isi tiap sel. */
public abstract class DaftarTableModel<T> extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final String[] kolom;
    private List<T> data = new ArrayList<T>();

    protected DaftarTableModel(String... kolom) {
        this.kolom = kolom;
    }

    protected abstract Object nilai(T objek, int indeksKolom);

    public void setData(List<T> baru) {
        this.data = new ArrayList<T>(baru);
        fireTableDataChanged();
    }

    public T getBaris(int indeks) { return data.get(indeks); }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return kolom.length; }
    @Override public String getColumnName(int c) { return kolom[c]; }
    @Override public Object getValueAt(int r, int c) { return nilai(data.get(r), c); }
}
