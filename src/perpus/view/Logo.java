package perpus.view;

import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/** Memuat gambar logo U-Pustaka dari package perpus.resources dan menyesuaikan ukurannya. */
public final class Logo {
    private Logo() { }

    private static final String FOLDER = "/perpus/resources/";
    private static final String HORIZONTAL = "logo-upustaka.png";
    private static final String VERTIKAL = "logo-upustaka-vertikal.png";
    private static final String IKON = "ikon-upustaka.png";

    private static Image muat(String nama) {
        URL url = Logo.class.getResource(FOLDER + nama);
        if (url == null) {
            throw new IllegalStateException("Gambar tidak ditemukan: " + FOLDER + nama);
        }
        return new ImageIcon(url).getImage();
    }

    /** Skala gambar ke tinggi tertentu dengan menjaga perbandingan lebar-tinggi. */
    private static Image skala(String nama, int tinggi) {
        Image asli = muat(nama);
        int lebar = Math.max(1, Math.round(asli.getWidth(null) * (float) tinggi / asli.getHeight(null)));
        return asli.getScaledInstance(lebar, tinggi, Image.SCALE_SMOOTH);
    }

    /** Ikon logo (kotak buku) setinggi tinggi piksel. */
    public static Icon ikon(int tinggi) {
        return new ImageIcon(skala(IKON, tinggi));
    }

    /** Logo ikon + tulisan "U-Pustaka" berdampingan. */
    public static JLabel horizontal(int tinggi) {
        return new JLabel(new ImageIcon(skala(HORIZONTAL, tinggi)), SwingConstants.CENTER);
    }

    /** Logo ikon di atas tulisan "U-Pustaka". */
    public static JLabel vertikal(int tinggi) {
        return new JLabel(new ImageIcon(skala(VERTIKAL, tinggi)), SwingConstants.CENTER);
    }

    /** Beberapa ukuran ikon sekaligus untuk ikon jendela / taskbar. */
    public static List<Image> gambarBanyak() {
        List<Image> daftar = new ArrayList<Image>();
        for (int s : new int[] {16, 32, 48, 64, 128}) {
            daftar.add(skala(IKON, s));
        }
        return daftar;
    }
}