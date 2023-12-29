package dk.tandhjulet.utils;

public class Utils {
    public static String charToStr(char ch) {
        return new String(new char[] { ch });
    }

    public static String charToStr(char[] ch) {
        return new String(ch);
    }

    public static char strToChar(String str) {
        if (str.length() > 0)
            return str.charAt(0);
        throw new RuntimeException("strToChar called with empty string!");
    }
}
