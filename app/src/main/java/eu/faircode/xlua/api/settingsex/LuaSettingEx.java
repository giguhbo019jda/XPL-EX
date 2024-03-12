package eu.faircode.xlua.api.settingsex;

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
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;
import eu.faircode.xlua.utilities.ParcelUtil;
import eu.faircode.xlua.utilities.StringUtil;

public class LuaSettingEx extends LuaSettingDefault implements IJsonSerial, Parcelable {
    protected String defaultValue;

    public LuaSettingEx() { }
    public LuaSettingEx(Parcel in) { fromParcel(in); }
    public LuaSettingEx(LuaSettingDefault setting) {
        setUser(setting.getUser());
        setCategory(setting.getCategory());
        setName(setting.getName());
        setDescription(setting.getDescription());
        setDefaultValue(setting.getValue());
    }

    public LuaSettingEx(LuaSetting setting) {
        setUser(setting.getUser());
        setCategory(setting.getCategory());
        setName(setting.getName());
        setValue(setting.getValue());
    }

    public String getDefaultValue() { return defaultValue; }
    public LuaSettingEx setDefaultValue(String defaultValue) { if(defaultValue != null) this.defaultValue = defaultValue; return this; }

    public LuaSettingPacket generatePacket(boolean deleteSetting, boolean forceKill, String packageName) {
        LuaSettingPacket packet = LuaSettingPacket.create(getName(), getValue());
        packet.setUser(XLuaSettingsDatabase.GLOBAL_USER);
        packet.setCategory(packageName);
        packet.setKill(forceKill);
        packet.setCode(deleteSetting ? LuaSettingPacket.CODE_DELETE : LuaSettingPacket.CODE_INSERT_UPDATE);
        return packet;
    }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = super.createContentValues();
        if(this.defaultValue != null) cv.put("defaultValue", defaultValue);
        return cv;
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromContentValues(ContentValues contentValue) {
        if(contentValue != null) {
            super.fromContentValues(contentValue);
            this.defaultValue = contentValue.getAsString("defaultValue");
        }
    }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            super.fromCursor(cursor);
            this.defaultValue = CursorUtil.getString(cursor, "defaultValue");
        }
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = super.toJSONObject();
        if(this.defaultValue != null) jRoot.put("defaultValue", this.defaultValue);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        this.defaultValue = JSONUtil.getString(obj,"defaultValue");
    }

    @Override
    public Bundle toBundle() {
        Bundle b = super.toBundle();
        if(this.defaultValue != null) b.putString("defaultValue", this.defaultValue);
        return b;
    }

    @Override
    public void fromBundle(Bundle bundle) {
        if(bundle != null) {
            super.fromBundle(bundle);
            this.defaultValue = BundleUtil.readString(bundle, "defaultValue");
        }
    }

    @Override
    public void fromParcel(Parcel in) {
        if(in != null) {
            super.fromParcel(in);
            this.defaultValue = ParcelUtil.readString(in, null, ParcelUtil.IGNORE_VALUE);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(StringUtil.isValidString(name)) {
            super.writeToParcel(dest, flags);
            ParcelUtil.writeString(dest, this.defaultValue, ParcelUtil.IGNORE_VALUE, false);
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public boolean equals(@Nullable Object obj) { return super.equals(obj); }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" defaultValue=");
        sb.append(this.description);
        return sb.toString();
    }
}
