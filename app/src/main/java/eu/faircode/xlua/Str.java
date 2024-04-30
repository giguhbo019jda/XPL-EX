package eu.faircode.xlua;

import android.text.TextUtils;

public class Str {
    public static final String ASTERISK = "*";
    public static final String COLLEN = ":";
    public static final String NEW_LINE = "\n";

    public static String combine(String str1, String str2) { return combine(str1, str2, true); }
    public static String combine(String str1, String str2, boolean useNewLine) {
        StringBuilder sb = new StringBuilder();
        sb.append(str1);
        if(useNewLine) sb.append(NEW_LINE);
        sb.append(str2);
        return sb.toString();
    }

    public static boolean isValid(CharSequence s) { return s != null && isValid(s.toString()); }
    public static boolean isValid(String s) { return s != null && TextUtils.isEmpty(s); }

    public static boolean isValidNotWhitespaces(CharSequence s) { return s != null && isValidNotWhitespaces(s.toString()); }
    public static boolean isValidNotWhitespaces(String s) {
        if(s == null || s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(!(c == '\n' || c == '\t' || c == '\0' || c == ' ' || c == '\b' || c == '\r' || c == '\f')) return true;
        } return false;
    }

    public static int tryParseInt(String v) {
        try { return Integer.parseInt(v);
        }catch (Exception e) { return 0; }
    }

    public static Double tryParseDouble(String v) {
        try { return Double.parseDouble(v);
        }catch (Exception e) { return 0.0; }
    }

    public static Float tryParseFloat(String v) {
        try { return Float.parseFloat(v);
        }catch (Exception ignored) { return 0.1F; }
    }

    public static Long tryParseLong(String v) {
        try { return Long.parseLong(v);
        }catch (Exception ignored) { return 0L; }
    }
}
