package eu.faircode.xlua.api.settings;

import androidx.annotation.NonNull;

public class XLuaSettingBase {
    protected Integer user;
    protected String category;
    protected String name;
    protected String value;

    public XLuaSettingBase() { }
    public XLuaSettingBase(Integer user, String category, String name) { init(user, category, name, null); }
    public XLuaSettingBase(Integer user, String category, String name, String value) { init(user, category, name, value); }

    private void init(Integer user, String category, String name, String value) {
        setUser(user);
        setCategory(category);
        setName(name);
        setValue(value);
    }

    public Integer getUser() { return this.user; }
    public String getCategory() { return this.category; }
    public String getName() { return this.name; }
    public String getValue() { return this.value; }

    public XLuaSettingBase setUser(Integer user) {
        if(user != null) this.user = user;
        return this;
    }

    public XLuaSettingBase setCategory(String category) {
        if(category != null) this.category = category;
        return this;
    }

    public XLuaSettingBase setName(String name) {
        if(name != null) this.name = name;
        return this;
    }

    public XLuaSettingBase setValue(String value) {
        this.value = value;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(user != null) {
            sb.append("user=");
            sb.append(user);
        }

        if(category != null) {
            sb.append(" category=");
            sb.append(category);
        }

        if(name != null) {
            sb.append(" name=");
            sb.append(name);
        }

        if(value != null) {
            sb.append(" value=");
            sb.append(value);
        }

        return sb.toString();
    }
}
