package perpus.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/** Mewarnai baris yang statusnya "Terlambat ..." dengan warna merah. */
public class RenderTerlambat extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    private final int kolomStatus;

    public RenderTerlambat(int kolomStatus) {
        this.kolomStatus = kolomStatus;
    }

    @Override
    public Component getTableCellRendererComponent(JTable t, Object nilai, boolean dipilih,
                                                   boolean fokus, int baris, int kolom) {
        Component c = super.getTableCellRendererComponent(t, nilai, dipilih, fokus, baris, kolom);
        Object status = t.getValueAt(baris, kolomStatus);
        boolean terlambat = status != null && status.toString().startsWith("Terlambat");
        if (!dipilih) c.setForeground(terlambat ? new Color(0xC62828) : t.getForeground());
        return c;
    }
}
