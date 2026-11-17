package perpus.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import perpus.model.Pengguna;

/** Halaman beranda: kartu ringkasan yang isinya bergantung pada peran (sumber data disuntikkan). */
public class BerandaPanel extends HalamanPanel {
    private static final long serialVersionUID = 1L;

    private final transient Supplier<Map<String, String>> sumber;
    private final JPanel kartu = new JPanel(new GridLayout(1, 0, 12, 12));

    public BerandaPanel(Pengguna pengguna, Supplier<Map<String, String>> sumber, String petunjukHtml) {
        this.sumber = sumber;
        setLayout(new BorderLayout(10, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel judul = new JLabel("Selamat datang, " + pengguna.getNamaLengkap());
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 22f));
        JLabel sub = new JLabel("Masuk sebagai " + pengguna.getPeran().getNama());
        JPanel kepala = new JPanel(new GridLayout(2, 1));
        kepala.add(judul);
        kepala.add(sub);
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.add(Logo.horizontal(56), BorderLayout.WEST);
        header.add(kepala, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        JPanel tengah = new JPanel(new BorderLayout());
        tengah.add(kartu, BorderLayout.NORTH);
        add(tengah, BorderLayout.CENTER);

        add(new JLabel(petunjukHtml), BorderLayout.SOUTH);
        segarkan();
    }

    @Override
    public void segarkan() {
        kartu.removeAll();
        for (Map.Entry<String, String> e : sumber.get().entrySet()) {
            JPanel k = new JPanel(new BorderLayout());
            k.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(16, 16, 16, 16)));
            JLabel angka = new JLabel(e.getValue(), SwingConstants.CENTER);
            angka.setFont(angka.getFont().deriveFont(Font.BOLD, 28f));
            JLabel teks = new JLabel(e.getKey(), SwingConstants.CENTER);
            k.add(angka, BorderLayout.CENTER);
            k.add(teks, BorderLayout.SOUTH);
            kartu.add(k);
        }
        kartu.revalidate();
        kartu.repaint();
    }
}