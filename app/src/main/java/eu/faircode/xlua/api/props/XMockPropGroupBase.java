package eu.faircode.xlua.api.props;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import eu.faircode.xlua.utilities.CollectionUtil;

public class XMockPropGroupBase {
    protected String settingName;
    protected String value;
    protected List<XMockPropSetting> properties = new ArrayList<>();

    protected XMockPropGroupBase() { }
    protected XMockPropGroupBase(String settingName, String value, List<XMockPropSetting> properties) {
        setSettingName(settingName);
        setValue(value);
        setProperties(properties);
    }

    public String getSettingName() { return settingName; }
    public XMockPropGroupBase setSettingName(String settingName) {
        if(settingName != null) this.settingName = settingName;
        return this;
    }

    public String getValue() { return value; }
    public XMockPropGroupBase setValue(String value) {
        if(value != null) this.value = value;
        return this;
    }

    public List<XMockPropSetting> getProperties() { return properties; }
    public XMockPropGroupBase setProperties(List<XMockPropSetting> properties) {
        if(CollectionUtil.isValid(properties)) this.properties = properties;
        return this;
    }

    public boolean containsProperty(XMockPropSetting pSetting) {
        for (XMockPropSetting setting : properties) {
            if(setting.getPropertyName().equalsIgnoreCase(pSetting.getPropertyName())) {
                return true;
            }
        }

        return false;
    }

    public void addProperty(XMockPropSetting pSetting) {
        if(properties == null)
            properties = new ArrayList<>();

        properties.add(pSetting);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XMockPropGroupBase)) {
            try {
                List<XMockPropSetting> pSettings = (List<XMockPropSetting>) obj;
                if(pSettings != null) {
                    //check if given settings has all of our settings :P simple nigga cmon
                    List<XMockPropSetting> pCopy = new ArrayList<>(properties);
                    for(XMockPropSetting pSetting : pSettings) {
                        for(int i = 0; i < pCopy.size(); i++) {
                            if(pCopy.get(i).equals(pSetting)) {
                                pCopy.remove(i);
                                break;
                            }
                        }
                    }

                    return pCopy.isEmpty();
                }
            }catch (Exception e) { return false; }
            return false;
        }

        XMockPropGroupBase other = (XMockPropGroupBase) obj;
        return this.getSettingName().equalsIgnoreCase(other.getSettingName());
    }

    @NonNull
    @Override
    public String toString() {
        return settingName;
    }
}
