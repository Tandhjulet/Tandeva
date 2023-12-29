package dk.tandhjulet.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    public static boolean matches(String pattern, String match, int flags) {
        Pattern p = Pattern.compile(pattern, flags);
        Matcher m = p.matcher(match);
        return m.find();
    }

    public static boolean matches(String pattern, String match) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(match);
        return m.find();
    }

    public static boolean matches(String pattern, char match, int flags) {
        return matches(pattern, Utils.charToStr(match), flags);
    }

    public static boolean matches(String pattern, char match) {
        return matches(pattern, Utils.charToStr(match));
    }
}
