package perpus.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import perpus.controller.AppContext;
import perpus.model.Koleksi;

/** Halaman peminjam: mencari koleksi dan melihat ketersediaannya (hanya baca). */
public class KatalogPanel extends HalamanPanel {
    private static final long serialVersionUID = 1L;

    private final AppContext ctx;
    private final KatalogTableModel model = new KatalogTableModel();
    private final JTable tabel = new JTable(model);
    private final JTextField tfCari = new JTextField(24);
    private final JLabel lbDetail = new JLabel(" ");

    public KatalogPanel(AppContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        atas.add(new JLabel("Cari judul, pengarang, atau jenis:"));
        atas.add(tfCari);
        JButton btnCari = new JButton("Cari");
        btnCari.addActionListener(e -> segarkan());
        tfCari.addActionListener(e -> segarkan());
        JButton btnSemua = new JButton("Tampilkan semua");
        btnSemua.addActionListener(e -> {
            tfCari.setText("");
            segarkan();
        });
        atas.add(btnCari);
        atas.add(btnSemua);
        add(atas, BorderLayout.NORTH);

        Ui.rapikanTabel(tabel);
        Ui.lebarKolom(tabel, 50, 70, 300, 150, 55, 90, 160);
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) tampilkanDetail();
        });
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new BorderLayout(0, 4));
        bawah.add(lbDetail, BorderLayout.NORTH);
        bawah.add(new JLabel("Peminjaman dilakukan di meja perpustakaan oleh petugas."), BorderLayout.SOUTH);
        add(bawah, BorderLayout.SOUTH);

        segarkan();
    }

    private void tampilkanDetail() {
        int baris = tabel.getSelectedRow();
        if (baris < 0) {
            lbDetail.setText(" ");
            return;
        }
        Koleksi k = model.getBaris(tabel.convertRowIndexToModel(baris));
        lbDetail.setText(k.getJenis().getLabelDetail1() + ": " + k.getDetail1() + "   |   "
                + k.getJenis().getLabelDetail2() + ": " + k.getDetail2());
    }

    @Override
    public void segarkan() {
        model.setData(ctx.koleksi.daftar(tfCari.getText()));
        tampilkanDetail();
    }
}
