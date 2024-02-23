package eu.faircode.xlua.utilities;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class StringUtil {
    public static String join(List<String> args) {
        if (args == null || args.size() == 0)
            return ""; // Return an empty string if the array is null or empty.

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.size(); i++)
            sb.append(args.get(i));

        return sb.toString();
    }


    public static String joinDelimiter(String delimiter, List<String> args) {
        if (args == null || args.size() == 0)
            return ""; // Return an empty string if the array is null or empty.

        StringBuilder sb = new StringBuilder();
        int len = args.size() - 1;
        for (int i = 0; i < args.size(); i++) {
            sb.append(args.get(i));
            if (i < len) { // Add a space after each element except the last one.
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static String join(String... args) {
        if (args == null || args.length == 0)
            return ""; // Return an empty string if the array is null or empty.

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++)
            sb.append(args[i]);

        return sb.toString();
    }

    public static String joinDelimiter(String delimiter, String... args) {
        if (args == null || args.length == 0)
            return ""; // Return an empty string if the array is null or empty.

        StringBuilder sb = new StringBuilder();
        int len = args.length - 1;
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]);
            if (i < len) { // Add a space after each element except the last one.
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static boolean isValidString(String str) {
        return str != null && !str.equals(" ") && !str.isEmpty();
    }

    public static String random(int minLen, int maxLen) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        //int randomLength = generator.nextInt(minLen, maxLen);
        int randomLength = ThreadLocalRandom.current().nextInt(minLen, maxLen);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
