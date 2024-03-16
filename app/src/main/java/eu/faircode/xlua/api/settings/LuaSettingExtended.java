package eu.faircode.xlua.api.settings;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;
import eu.faircode.xlua.utilities.ParcelUtil;
import eu.faircode.xlua.utilities.StringUtil;

public class LuaSettingExtended extends LuaSettingDefault implements IJsonSerial, Parcelable {
    public static LuaSettingExtended create() { return new LuaSettingExtended(); }
    public static LuaSettingExtended create(LuaSettingPacket packet) { return new LuaSettingExtended(packet); }
    public static LuaSettingExtended create(LuaSetting setting) { return new LuaSettingExtended(setting); }
    public static LuaSettingExtended create(LuaSetting setting, String description) { return new LuaSettingExtended(setting, description); }
    public static LuaSettingExtended create(LuaSetting setting, String description, String defaultValue) { return new LuaSettingExtended(setting, description, defaultValue); }
    public static LuaSettingExtended create(LuaSettingDefault defaultSetting) { return new LuaSettingExtended(defaultSetting); }
    public static LuaSettingExtended create(LuaSettingDefault defaultSetting, Integer user, String category) { return new LuaSettingExtended(defaultSetting, user, category); }
    public static LuaSettingExtended create(LuaSettingDefault defaultSetting, Integer user, String category, String value) { return new LuaSettingExtended(defaultSetting, user, category, value); }

    public static LuaSettingExtended create(Integer user, String category, String name, String value) { return new LuaSettingExtended(user, category, name, value, null, null, null); }
    public static LuaSettingExtended create(Integer user, String category, String name, String value, String description) { return new LuaSettingExtended(user, category, name, value, description, null, null); }
    public static LuaSettingExtended create(Integer user, String category, String name, String value, String description, String defaultValue) { return new LuaSettingExtended(user, category, name, value, description, defaultValue, null); }
    public static LuaSettingExtended create(Integer user, String category, String name, String value, String description, String defaultValue, Boolean isEnabled) { return new LuaSettingExtended(user, category, name, value, description, defaultValue, isEnabled); }


    public LuaSettingExtended(LuaSettingPacket packet) { this(packet.getUser(), packet.getCategory(), packet.getName(), packet.getValue(), packet.getDescription(), packet.getDefaultValue(), packet.isEnabled()); }

    public LuaSettingExtended(LuaSetting setting) { this(setting.getUser(), setting.getCategory(), setting.getName(), setting.getValue(), null, null, null); }
    public LuaSettingExtended(LuaSetting setting, String description) { this(setting.getUser(), setting.getCategory(), setting.getName(), setting.getValue(), description, null, null); }
    public LuaSettingExtended(LuaSetting setting, String description, String defaultValue) { this(setting.getUser(), setting.getCategory(), setting.getName(), setting.getValue(), description, defaultValue, null); }

    public LuaSettingExtended(LuaSettingDefault defaultSetting) { this(defaultSetting.getUser(), defaultSetting.getCategory(), defaultSetting.getName(), defaultSetting.getValue(), defaultSetting.getDescription(), defaultSetting.getDefaultValue(true), null); }
    public LuaSettingExtended(LuaSettingDefault defaultSetting, Integer user, String category) { this(user, category, defaultSetting.getName(), null, defaultSetting.getDescription(), defaultSetting.getDefaultValue(true), null); }
    public LuaSettingExtended(LuaSettingDefault defaultSetting, Integer user, String category, String value) { this(user, category, defaultSetting.getName(), value, defaultSetting.getDescription(), defaultSetting.getDefaultValue(true), null); }

    protected Boolean enabled = true;

    public LuaSettingExtended() { setUseUserIdentity(true); }
    public LuaSettingExtended(Parcel in) { this(); fromParcel(in); }

    public LuaSettingExtended(Integer user, String category, String name, String value) { this(user, category, name, value, null, null, null); }
    public LuaSettingExtended(Integer user, String category, String name, String value, String description) { this(user, category, name, value, description, null, null); }
    public LuaSettingExtended(Integer user, String category, String name, String value, String description, String defaultValue) { this(user, category, name, value, description, defaultValue, null); }
    public LuaSettingExtended(Integer user, String category, String name, String value, String description, String defaultValue, Boolean isEnabled) {
        this();
        setUser(user);
        setCategory(category);
        setName(name);
        setValue(value);
        setDescription(description);
        setDefaultValue(defaultValue);
        setIsEnabled(isEnabled);
    }

    public Boolean isEnabled() { return this.enabled; }
    public LuaSettingExtended setIsEnabled(Boolean enabled) { /*if(enabled != null)*/ this.enabled = enabled; return this; }

    public LuaSetting createSetting() { return LuaSetting.create(this); }
    public LuaSettingDefault createDefaultSetting() { return LuaSettingDefault.create(this); }
    public LuaSettingPacket createPacket() { return LuaSettingPacket.create(this); }
    public LuaSettingPacket createPacket(Integer code) { return LuaSettingPacket.create(this, code); }
    public LuaSettingPacket createPacket(Integer code, Boolean kill) { return LuaSettingPacket.create(this, code, kill); }

    @Override
    public ContentValues createContentValues() { return super.createContentValues(); }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromContentValues(ContentValues contentValue) { super.fromContentValues(contentValue); }

    @Override
    public void fromCursor(Cursor cursor) { super.fromCursor(cursor); }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException { return  super.toJSONObject(); }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException { super.fromJSONObject(obj); }

    @Override
    public Bundle toBundle() { return super.toBundle(); }

    @Override
    public void fromBundle(Bundle bundle) { super.fromBundle(bundle); }

    @Override
    public void fromParcel(Parcel in) { super.fromParcel(in); }

    @Override
    public void writeToParcel(Parcel dest, int flags) { super.writeToParcel(dest, flags); }

    @Override
    public int describeContents() { return 0; }

    @Override
    public boolean equals(@Nullable Object obj) { return super.equals(obj); }

    @NonNull
    @Override
    public String toString() { return new StringBuilder(super.toString()).append(" enabled=").append(this.enabled).toString(); }
}
