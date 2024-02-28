package eu.faircode.xlua.api.config;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;

public class XMockConfigBase {
    protected static final String TAG = "XLua.MockUniqueIdBase";

    protected String name;
    protected LinkedHashMap<String, String> settings;

    protected XMockConfigBase() { }
    protected XMockConfigBase(String name, LinkedHashMap<String, String> settings) {
        setName(name);
        setSettings(settings);
    }

    public String getName() { return this.name; }
    public XMockConfigBase setName(String name) {
        if(name != null) this.name = name;
        return this;
    }

    public LinkedHashMap<String, String> getSettings() { return this.settings; }
    public XMockConfigBase setSettings(LinkedHashMap<String, String> settings) {
        if(settings != null) this.settings = settings;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
