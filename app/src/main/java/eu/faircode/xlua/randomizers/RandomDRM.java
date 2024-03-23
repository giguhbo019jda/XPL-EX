package eu.faircode.xlua.randomizers;

import androidx.annotation.NonNull;

import java.util.List;

import eu.faircode.xlua.randomizers.elements.ISpinnerElement;
import eu.faircode.xlua.utilities.RandomStringGenerator;

public class RandomDRM implements IRandomizer {
    @Override
    public boolean isSetting(String setting) {
        return setting.equalsIgnoreCase(getSettingName()) || setting.equalsIgnoreCase("drm") || setting.equalsIgnoreCase("unique.drm.id");
    }

    @Override
    public String getSettingName() {
        return "drm.id";
    }

    @Override
    public String getName() {
        return "DRM ID";
    }

    @Override
    public String getID() {
        return "%drm_id%";
    }

    @Override
    public String generateString() {
        //64
        return RandomStringGenerator.generateRandomAlphanumericString(64, RandomStringGenerator.LOWER_LETTERS);
    }

    @Override
    public int generateInteger() { return 0; }

    @Override
    public List<ISpinnerElement> getOptions() { return null; }

    @NonNull
    @Override
    public String toString() { return getName(); }
}
