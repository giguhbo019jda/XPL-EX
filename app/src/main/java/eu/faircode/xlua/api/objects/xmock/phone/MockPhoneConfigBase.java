package eu.faircode.xlua.api.objects.xmock.phone;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class MockPhoneConfigBase {
    protected static final String TAG = "XLua.MockUniqueIdBase";

    protected String name;
    protected LinkedHashMap<String, String> settings;

    public MockPhoneConfigBase() { }
    public MockPhoneConfigBase(String name, LinkedHashMap<String, String> settings) {
        setName(name);
        setSettings(settings);
    }

    public String getName() { return this.name; }
    public MockPhoneConfigBase setName(String name) {
        if(name != null) this.name = name;
        return this;
    }

    public LinkedHashMap<String, String> getSettings() { return this.settings; }
    public MockPhoneConfigBase setSettings(LinkedHashMap<String, String> settings) {
        if(settings != null) this.settings = settings;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
