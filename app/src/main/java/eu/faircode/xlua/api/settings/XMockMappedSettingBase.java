package eu.faircode.xlua.api.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class XMockMappedSettingBase {
    protected String name;
    protected String description;
    protected String value;

    protected XMockMappedSettingBase() { }
    protected XMockMappedSettingBase(String name, String description, String value) {
        setName(name);
        setDescription(description);
        setValue(value);
    }

    public String getDescription() { return description; }
    public XMockMappedSettingBase setDescription(String description) {
        if(description != null) this.description = description;
        return this;
    }

    public String getName() { return name; }
    public XMockMappedSettingBase setName(String name) {
        if(name != null) this.name = name;
        return this;
    }

    public String getValue() { return value;}
    public XMockMappedSettingBase setValue(String value) {
        if(value != null) this.value = value;
        return this;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof XMockMappedSettingBase))
            return false;
        XMockMappedSettingBase other = (XMockMappedSettingBase) obj;
        return this.getName().equals(other.getName());
    }

    @NonNull
    @Override
    public String toString() {
        return name + " " + description + " " + value;
    }
}
