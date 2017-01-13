package core.utilities;

/**
 * Created by jacobfalzon on 08/01/2017.
 */
public class StringUtils {

    public static String generate() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    public static String tab() {
        return "\t\t\t";
    }

    public static String newline() {
        return "\n";
    }
}
