package eu.faircode.xlua.api.props;

import androidx.annotation.NonNull;

public class XMockPropSettingBase {
    protected String name;
    protected String packageName;
    protected Integer user;
    protected Integer value;

    protected XMockPropSettingBase() { }
    protected XMockPropSettingBase(String name, Integer userId, String packageName, Integer value) {
        setName(name);
        setUserId(userId);
        setPackageName(packageName);
        setValue(value);
    }

    public String getName() { return name; }
    public XMockPropSettingBase setName(String name) {
        if(name != null) this.name = name;
        return this;
    }

    public String getPackageName() { return packageName; }
    public XMockPropSettingBase setPackageName(String packageName) {
        if(packageName != null) this.packageName = packageName;
        return this;
    }

    public Integer getUser() { return user; }
    public XMockPropSettingBase setUserId(Integer userId) {
        if(userId != null) this.user = userId;
        return this;
    }

    public int getValue() { return value; }
    public XMockPropSettingBase setValue(Integer value) {
        if(value != null) this.value = value;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return name + "::" + user + "::" + packageName + "::" + value;
    }
}
