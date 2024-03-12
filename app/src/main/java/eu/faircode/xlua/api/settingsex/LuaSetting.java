package eu.faircode.xlua.api.settingsex;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

import eu.faircode.xlua.api.PackageContextBase;
import eu.faircode.xlua.api.cpu.MockCpu;
import eu.faircode.xlua.api.settings.XLuaLuaSetting;
import eu.faircode.xlua.api.settings.XMockMappedSettingBase;
import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.ContentValuesUtil;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;
import eu.faircode.xlua.utilities.ParcelUtil;
import eu.faircode.xlua.utilities.StringUtil;

public class LuaSetting extends PackageContextBase implements IJsonSerial, Parcelable {
    public static LuaSetting create() { return new LuaSetting(); }
    public static LuaSetting create(String name) { return new LuaSetting(null, null, name, null); }
    public static LuaSetting create(Integer userId, String category, String name) { return new LuaSetting(userId, category, name, null); }
    public static LuaSetting create(Integer userId, String category, String name, String value) { return new LuaSetting(userId, category, name, value); }

    public static LuaSetting from(LuaSettingPacket packet) {
        LuaSetting s = new LuaSetting();
        s.setUser(packet.getUser());
        s.setCategory(packet.getCategory());
        s.setName(packet.getName());
        s.setValue(packet.getValue());
        return s;
    }

    protected String name;
    protected String value;

    public LuaSetting() { }
    public LuaSetting(Parcel p) { fromParcel(p); }
    public LuaSetting(Integer userId, String category, String name, String value) {
        super(userId, category);
        setName(name);
        setValue(value);
    }

    public String getName() { return name; }
    public LuaSetting setName(String name) { if(name != null) this.name = name; return this; }

    public String getValue() { return value; }
    public LuaSetting setValue(String value) { if(value != null) this.value = value; return this; }
    public LuaSetting setValueForce(String value) { this.value = value; return this; }

    @Override
    public Bundle toBundle() {
        Bundle b = super.toBundle();
        if(name != null) b.putString("name", name);
        if(value != null) b.putString("value", value);
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        if(b != null) {
            super.fromBundle(b);
            name = b.getString("name");
            value = b.getString("value");
        }
    }

    @Override
    public void fromParcel(Parcel in) {
        if(in != null) {
            super.fromParcel(in);
            this.name = in.readString();
            this.value = ParcelUtil.readString(in, null, ParcelUtil.IGNORE_VALUE);
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        ParcelUtil.writeString(dest, this.value, ParcelUtil.IGNORE_VALUE, false);
    }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            super.fromCursor(cursor);
            this.name = CursorUtil.getString(cursor, "name");
            this.value = CursorUtil.getString(cursor, "value");
        }
    }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = super.createContentValues();
        if(this.name != null) cv.put("name", this.name);
        if(this.value != null) cv.put("value", this.value);
        return cv;
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromContentValues(ContentValues contentValue) {
        super.fromContentValues(contentValue);
        this.name = ContentValuesUtil.getString(contentValue, "name");
        this.value = ContentValuesUtil.getString(contentValue, "value");
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject obj = super.toJSONObject();
        if(this.name != null) obj.put("name", this.name);
        if(this.value != null) obj.put("value", this.value);
        return obj;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        this.name = JSONUtil.getString(obj, "name");
        this.value = JSONUtil.getString(obj, "value");
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        String name = null;
        if(obj instanceof String)
            name = (String)obj;
        else if(obj instanceof LuaSetting)
            name = ((LuaSetting) obj).getName();

        return this.getName().equalsIgnoreCase(name);
    }

    @NonNull
    @Override
    public String toString() {
        return new StringBuilder(super.toString())
                .append(" name=")
                .append(this.name)
                .append(" value=")
                .append(this.value).toString();
    }

    public static boolean isValid(LuaSetting setting) {
        if(setting == null)
            return false;
        if(setting.user == null)
            setting.user = 0;
        if(!StringUtil.isValidString(setting.category))
            setting.category = "Global";

        return StringUtil.isValidString(setting.name);
    }

    public static final Parcelable.Creator<LuaSetting> CREATOR = new Parcelable.Creator<LuaSetting>() {
        @Override
        public LuaSetting createFromParcel(Parcel source) {
            return new LuaSetting(source);
        }

        @Override
        public LuaSetting[] newArray(int size) {
            return new LuaSetting[size];
        }
    };

    public static class Table {
        public static final String name = "setting";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("user", "INTEGER");
            put("category", "TEXT");
            put("name", "TEXT");
            put("value", "TEXT");
        }};
    }
}
