package perpus.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Utilitas format tampilan (tanggal dan rupiah). */
public final class Format {
    private static final DateTimeFormatter TANGGAL = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private Format() { }

    public static String tanggal(LocalDate t) {
        return t == null ? "-" : TANGGAL.format(t);
    }

    public static String rupiah(double nilai) {
        return "Rp" + String.format(Locale.forLanguageTag("id-ID"), "%,.0f", nilai);
    }
}
