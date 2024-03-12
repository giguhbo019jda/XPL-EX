package eu.faircode.xlua.randomizers;

import androidx.annotation.NonNull;

import eu.faircode.xlua.utilities.RandomStringGenerator;

public class RandomAndroidID implements IRandomizer {
    @Override
    public boolean isSetting(String setting) {
        return setting.equalsIgnoreCase(getSettingName()) || setting.equalsIgnoreCase("unique.android.id");
    }

    @Override
    public String getSettingName() { return "value.android_id"; }

    @Override
    public String getName() {
        return "Android ID";
    }

    @Override
    public String getID() {
        return "%android_id%";
    }

    @Override
    public String generateString() {
        return RandomStringGenerator.generateRandomAlphanumericString(16, RandomStringGenerator.LOWER_LETTERS);
    }

    @Override
    public int generateInteger() { return 0; }

    @NonNull
    @Override
    public String toString() { return getName(); }
}
