package eu.faircode.xlua.randomizers;

import androidx.annotation.NonNull;

import eu.faircode.xlua.utilities.RandomStringGenerator;

public class RandomIMEI implements IRandomizer {
    @Override
    public boolean isSetting(String setting) {
        return setting.equalsIgnoreCase(getSettingName()) || setting.equalsIgnoreCase("imei");
    }

    @Override
    public String getSettingName() {  return "value.imei"; }

    @Override
    public String getName() {
        return "IMEI";
    }

    @Override
    public String getID() {
        return "%imei%";
    }

    @Override
    public String generateString() {
        return RandomStringGenerator.generateRandomNumberString(15);
    }

    @Override
    public int generateInteger() { return 0; }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
