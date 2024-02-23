package eu.faircode.xlua.randomizers;

import androidx.annotation.NonNull;

import eu.faircode.xlua.utilities.RandomStringGenerator;

public class RandomGSF implements IRandomizer {
    @Override
    public boolean isSetting(String setting) {
        return setting.equalsIgnoreCase(getSettingName());
    }

    @Override
    public String getSettingName() { return "gsf.id"; }

    @Override
    public String getName() {
        return "GSF ID";
    }

    @Override
    public String getID() {
        return "%gsf_id%";
    }

    @Override
    public String generateString() {
        return RandomStringGenerator.generateRandomAlphanumericString(16, RandomStringGenerator.UPPER_LETTERS);
    }

    @Override
    public int generateInteger() { return 0; }

    @NonNull
    @Override
    public String toString() { return getName(); }
}
