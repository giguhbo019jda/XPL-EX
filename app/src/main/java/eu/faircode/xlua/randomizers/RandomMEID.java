package eu.faircode.xlua.randomizers;

import androidx.annotation.NonNull;

import eu.faircode.xlua.utilities.RandomStringGenerator;

public class RandomMEID implements IRandomizer {
    @Override
    public boolean isSetting(String setting) {
        return setting.equalsIgnoreCase(getSettingName()) || setting.equalsIgnoreCase("meid") || setting.equalsIgnoreCase("unique.gsm.meid");
    }

    @Override
    public String getSettingName() {  return "value.meid"; }

    @Override
    public String getName() {
        return "MEID";
    }

    @Override
    public String getID() {
        return "%meid%";
    }

    @Override
    public String generateString() {
        return RandomStringGenerator.generateRandomNumberString(14);
    }

    @Override
    public int generateInteger() { return 0; }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
