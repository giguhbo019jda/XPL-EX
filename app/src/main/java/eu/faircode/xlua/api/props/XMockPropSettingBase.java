package eu.faircode.xlua.api.props;

import androidx.annotation.NonNull;

public class XMockPropSettingBase {
    protected String propertyName;
    protected String settingName;
    protected Boolean enabled;

    protected XMockPropSettingBase() { }
    protected XMockPropSettingBase(String propertyName, String settingName) { this(propertyName, settingName, true); }
    protected XMockPropSettingBase(String propertyName, String settingName, Boolean enabled) {
        setPropertyName(propertyName);
        setSettingName(settingName);
        setIsEnabled(enabled);
    }

    public String getPropertyName() { return propertyName; }
    public XMockPropSettingBase setPropertyName(String propertyName) {
        if(propertyName != null) this.propertyName = propertyName;
        return this;
    }

    public String getSettingName() { return settingName; }
    public XMockPropSettingBase setSettingName(String settingName) {
        if(settingName != null) this.settingName = settingName;
        return this;
    }

    public Boolean isEnabled() { return enabled; }
    public XMockPropSettingBase setIsEnabled(Boolean isEnabled) {
        if(isEnabled != null) this.enabled = isEnabled;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XMockPropSettingBase))
            return false;

        XMockPropSettingBase other = (XMockPropSettingBase) obj;
        return this.getSettingName().equalsIgnoreCase(other.getSettingName()) && this.getPropertyName().equalsIgnoreCase(other.getPropertyName());
    }

    @NonNull
    @Override
    public String toString() {
        return propertyName;
    }
}
