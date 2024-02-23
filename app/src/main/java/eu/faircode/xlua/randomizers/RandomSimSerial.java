package eu.faircode.xlua.randomizers;

import androidx.annotation.NonNull;

import eu.faircode.xlua.utilities.RandomStringGenerator;

public class RandomSimSerial implements IRandomizer {
    @Override
    public boolean isSetting(String setting) {
        return setting.equalsIgnoreCase(getSettingName());
    }

    @Override
    public String getSettingName() {  return "phone.simserial"; }

    @Override
    public String getName() {
        return "SIM Serial";
    }

    @Override
    public String getID() {
        return "%sim_serial%";
    }

    @Override
    public String generateString() {
        return RandomStringGenerator.generateRandomNumberString(20);
    }

    @Override
    public int generateInteger() { return 0; }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
