package perpus.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import perpus.controller.AppContext;
import perpus.model.Pengguna;

/** Halaman peminjam: riwayat pinjaman, jatuh tempo, dan denda milik sendiri. */
public class PinjamanSayaPanel extends HalamanPanel {
    private static final long serialVersionUID = 1L;

    private final AppContext ctx;
    private final Pengguna pengguna;
    private final PinjamanSayaTableModel model;
    private final JTable tabel;
    private final JCheckBox chkBelumKembali = new JCheckBox("Hanya yang belum dikembalikan");
    private final JLabel lbRingkasan = new JLabel(" ");

    public PinjamanSayaPanel(AppContext ctx, Pengguna pengguna) {
        this.ctx = ctx;
        this.pengguna = pengguna;
        this.model = new PinjamanSayaTableModel(ctx.peminjaman);
        this.tabel = new JTable(model);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        atas.add(chkBelumKembali);
        chkBelumKembali.addActionListener(e -> segarkan());
        add(atas, BorderLayout.NORTH);

        Ui.rapikanTabel(tabel);
        Ui.lebarKolom(tabel, 50, 280, 70, 90, 90, 90, 130, 90);
        tabel.setDefaultRenderer(Object.class, new RenderTerlambat(PinjamanSayaTableModel.KOLOM_STATUS));
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        add(lbRingkasan, BorderLayout.SOUTH);
        segarkan();
    }

    @Override
    public void segarkan() {
        String id = pengguna.getIdAnggota();
        model.setData(ctx.peminjaman.daftarAnggota(id, chkBelumKembali.isSelected()));
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : ctx.peminjaman.ringkasanAnggota(id).entrySet()) {
            if (sb.length() > 0) sb.append("   |   ");
            sb.append(e.getKey()).append(": ").append(e.getValue());
        }
        lbRingkasan.setText(sb.toString());
    }
}
