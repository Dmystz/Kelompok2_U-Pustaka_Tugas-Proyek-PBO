package perpus.view;

import javax.swing.JPanel;

/** Kelas dasar semua halaman pada jendela utama; dimuat ulang tiap kali halaman dibuka. */
public abstract class HalamanPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public abstract void segarkan();
}
