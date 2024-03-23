package eu.faircode.xlua.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CollectionUtil {
    public static boolean isEmptyValuesOrInvalid(Collection<String> collection) {
        if(!isValid(collection))
            return true;

        for(String s : collection) {
            if(!s.isEmpty())
                return false;
        }

        return true;
    }

    public static boolean isValid(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }
    public static boolean isValid(Collection<?> collection) { return collection != null && !collection.isEmpty(); }

    public static List<String> getVerifiedStrings(List<String> list, boolean useContains, String... badStrs) { return getVerifiedStrings(list, useContains, Arrays.asList(badStrs)); }
    public static List<String> getVerifiedStrings(List<String> list, boolean useContains, List<String> badList) {
        List<String> strListClean = new ArrayList<>();
        if(useContains) {
            for(String s : list) {
                boolean found = false;
                for(String sRemove : badList) {
                    if(s.contains(sRemove)) {
                        found = true;
                        break;
                    }
                } if(!found) strListClean.add(s);
            }
        }else {
            for(String s : list) {
                boolean found = false;
                for(String sRemove : badList) {
                    if(s.equals(sRemove)) {
                        found = true;
                        break;
                    }
                } if(!found) strListClean.add(s);
            }
        }

        return strListClean;
    }
}
