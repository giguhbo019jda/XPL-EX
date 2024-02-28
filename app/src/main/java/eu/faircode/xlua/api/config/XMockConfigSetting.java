package eu.faircode.xlua.api.config;

import androidx.annotation.NonNull;

public class XMockConfigSetting {
    protected String name;
    protected String value;
    protected Boolean enabled;

    public XMockConfigSetting() { }
    public XMockConfigSetting(String name, String value) { this(name, value, true); }
    public XMockConfigSetting(String name, String value, Boolean enabled) {
        setName(name);
        setValue(value);
        setEnabled(enabled);
    }

    public String getName() { return this.name; }
    public XMockConfigSetting setName(String name) {
        if(name != null) this.name = name;
        return this;
    }

    public String getValue() { return this.value; }
    public XMockConfigSetting setValue(String value) {
        if(value != null) this.value = value;
        return this;
    }

    public Boolean isEnabled() { return this.enabled; }
    public XMockConfigSetting setEnabled(Boolean enabled) {
        if(enabled != null) this.enabled = enabled;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if(name != null) {
            sb.append(" name=");
            sb.append(name);
        }

        if(value != null) {
            sb.append(" value=");
            sb.append(value);
        }

        if(enabled == null) {
            sb.append(" enabled=false");
        }else {
            sb.append(" enabled=");
            sb.append(Boolean.toString(enabled));
        }

        return sb.toString();
    }
}
