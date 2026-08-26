package mu.rekolt.util;

public class Formatter {
    public static String money(double value) {
        return String.format("%,.2f", value);
    }
}
