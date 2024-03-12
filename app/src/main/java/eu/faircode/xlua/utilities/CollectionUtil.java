package eu.faircode.xlua.utilities;

import java.util.Collection;
import java.util.Map;

public class CollectionUtil {
    public static boolean isValid(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public static boolean isValid(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }
}
