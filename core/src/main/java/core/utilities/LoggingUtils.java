package core.utilities;

/**
 * Created by jacobfalzon on 30/12/2016.
 */
public class LoggingUtils {


    public static void error(final String message){
        System.err.println("ERROR: " + message);
    }

    public static void info(final String message){
        System.out.println("INFO: " + message);
    }

    public static void log(final String type, final String message) {
        System.out.println(type + ": " + message);
    }
}
