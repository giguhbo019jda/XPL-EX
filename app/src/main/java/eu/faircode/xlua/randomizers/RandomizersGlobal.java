package eu.faircode.xlua.randomizers;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class RandomizersGlobal {
    public static Map<String, IRandomizer> randomizers = new Hashtable<>();

    public static String getRandomStringValue(String settingName) {
        IRandomizer ran = randomizers.get(settingName);
        if(ran == null) return null;
        return ran.generateString();
    }

    public static List<IRandomizer> getRandomizers() {
        if(randomizers.isEmpty())
            initRandomizers();

        return new ArrayList<>(randomizers.values());
    }

    public static void initRandomizers() {
        randomizers.put("id.androidid", new RandomAndroidID());
        randomizers.put("id.drm", new RandomDRM());
        randomizers.put("id.gsf", new RandomGSF());
        randomizers.put("id.imei", new RandomIMEI());
    }
}
