package eu.faircode.xlua.randomizers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class GlobalRandoms {
    private static final Object lock = new Object();
    public static Map<String, IRandomizer> randomizers = new Hashtable<>();

    public static String getRandomStringValue(String settingName) {
        IRandomizer ran = randomizers.get(settingName);
        if(ran == null) return null;
        return ran.generateString();
    }

    public static List<IRandomizer> getRandomizers() {
        if(randomizers.isEmpty())
            initRandomizers();

        synchronized (lock) {
            return new ArrayList<>(randomizers.values());
        }
    }

    public static void initRandomizers() {
        putRandomizer(new RandomAndroidID());
        putRandomizer(new RandomDRM());
        putRandomizer(new RandomGSF());
        putRandomizer(new RandomIMEI());
        putRandomizer(new RandomMAC());
        putRandomizer(new RandomMEID());
        putRandomizer(new RandomAdID());
        putRandomizer(new RandomSimSerial());
        putRandomizer(new RandomSubscriberID());
        putRandomizer(new RandomSSID());
    }

    public static void putRandomizer(IRandomizer randomizer) {
        synchronized (lock) {
            randomizers.put(randomizer.getSettingName(), randomizer);
        }
    }
}
