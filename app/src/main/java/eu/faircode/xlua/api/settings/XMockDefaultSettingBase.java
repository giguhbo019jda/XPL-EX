package eu.faircode.xlua.api.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import eu.faircode.xlua.api.props.XMockPropSettingBase;

public class XMockDefaultSettingBase {
    protected String name;
    protected String value;

    protected XMockDefaultSettingBase() { }
    protected XMockDefaultSettingBase(String name, String value) {
        setName(name);
        setValue(value);
    }

    public String getName() { return name; }
    public XMockDefaultSettingBase setName(String name) {
        if(name != null) this.name = name;
        return this;
    }

    public String getValue() { return value;}
    public XMockDefaultSettingBase setValue(String value) {
        if(value != null) this.value = value;
        return this;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    @NonNull
    @Override
    public String toString() {
        return name + " " + value;
    }
}
