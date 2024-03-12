package eu.faircode.xlua.api.props;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import eu.faircode.xlua.utilities.CollectionUtil;

public class XMockPropGroupBase {
    protected String settingName;
    protected String value;
    protected List<XMockPropMapped> properties = new ArrayList<>();

    protected XMockPropGroupBase() { }
    protected XMockPropGroupBase(String settingName, String value, List<XMockPropMapped> properties) {
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

    public List<XMockPropMapped> getProperties() { return properties; }
    public XMockPropGroupBase setProperties(List<XMockPropMapped> properties) {
        if(CollectionUtil.isValid(properties)) this.properties = properties;
        return this;
    }

    public boolean containsProperty(XMockPropMapped pSetting) {
        for (XMockPropMapped setting : properties) {
            if(setting.getPropertyName().equalsIgnoreCase(pSetting.getPropertyName())) {
                return true;
            }
        }

        return false;
    }

    public void addProperty(XMockPropMapped pSetting) {
        if(properties == null)
            properties = new ArrayList<>();

        properties.add(pSetting);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof XMockPropGroupBase)) {
            try {
                List<XMockPropMapped> pSettings = (List<XMockPropMapped>) obj;
                if(pSettings != null) {
                    //check if given settings has all of our settings :P simple nigga cmon
                    List<XMockPropMapped> pCopy = new ArrayList<>(properties);
                    for(XMockPropMapped pSetting : pSettings) {
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
