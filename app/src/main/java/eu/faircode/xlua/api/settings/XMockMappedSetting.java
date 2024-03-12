package eu.faircode.xlua.api.settings;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;
import eu.faircode.xlua.utilities.ParcelUtil;
import eu.faircode.xlua.utilities.StringUtil;

public class XMockMappedSetting extends XMockMappedSettingBase implements IJsonSerial, Parcelable {
    private static final String IGNORE_VALUE = "!";

    //We can also create a base class that stores modified value or something
    protected String modifiedValue = null;

    public XMockMappedSetting() { }
    public XMockMappedSetting(Parcel p) { fromParcel(p); }
    public XMockMappedSetting(Bundle b) { fromBundle(b); }
    public XMockMappedSetting(String name, String description, String value) { super(name, description, value); }
    public XMockMappedSetting(XMockMappedSetting setting) {
        //Ignore value as when we create a copy we assume you want it without the value
        super(setting.name, setting.description, null);

    }

    public void resetModifiedValue() { modifiedValue = null; }
    public String getModifiedValue() {
        if(modifiedValue != null) {
            value = modifiedValue;
            modifiedValue = null;
            return value;
        }

        return value;
    }
    public void setModifiedValue(String modifiedValue) { this.modifiedValue = modifiedValue; }
    public boolean requiresUpdate() {
        //if(modifiedValue == null || (!StringUtil.isValidString(value) && !StringUtil.isValidString(modifiedValue)) || !StringUtil.isValidString(value))
        //    return false;
        if(value == null && modifiedValue != null) {
            return true;
        }

        return !value.equalsIgnoreCase(modifiedValue);
    }

    public XLuaSettingPacket generatePacket(String categoryOrPackage, boolean delete, boolean kill) { return generatePacket(XLuaSettingsDatabase.GLOBAL_USER, categoryOrPackage, delete, kill); }
    public XLuaSettingPacket generatePacket(int userId, String categoryOrPackage, boolean delete, boolean kill) {
        XLuaSettingPacket packet = new XLuaSettingPacket(userId, categoryOrPackage, name, getModifiedValue(), kill);
        if(delete)
            packet.setAsDeletePacket();

        return packet;
    }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        if(this.name != null) cv.put("name", name);
        if(this.description != null) cv.put("description", description);
        if(this.value != null) cv.put("value", value);
        return cv;
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromContentValues(ContentValues contentValue) {
        if(contentValue != null) {
            this.name = contentValue.getAsString("name");
            this.description = contentValue.getAsString("description");
            this.value = contentValue.getAsString("value");
        }
    }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            this.name = CursorUtil.getString(cursor, "name");
            this.description = CursorUtil.getString(cursor, "description");
            this.value = CursorUtil.getString(cursor, "value");
        }
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = new JSONObject();
        if(this.name != null) jRoot.put("name", name);
        if(this.description != null) jRoot.put("description", description);
        if(this.value != null) jRoot.put("value", value);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        this.name = JSONUtil.getString(obj,"name");
        this.description = JSONUtil.getString(obj,"description");
        this.value = JSONUtil.getString(obj,"value");
    }

    @Override
    public Bundle toBundle() {
        Bundle b = new Bundle();
        if(this.name != null) b.putString("name", name);
        if(this.description != null) b.putString("description", description);
        if(this.value != null) b.putString("value", value);
        return b;
    }

    @Override
    public void fromBundle(Bundle bundle) {
        if(bundle != null) {
            this.name = BundleUtil.readString(bundle, "name");
            this.description = BundleUtil.readString(bundle, "description");
            this.value = BundleUtil.readString(bundle, "value");
        }
    }

    @Override
    public void fromParcel(Parcel in) {
        if(in != null) {
            this.name = in.readString();
            this.description = ParcelUtil.readString(in, null, IGNORE_VALUE);
            this.value = ParcelUtil.readString(in, null, IGNORE_VALUE);
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(StringUtil.isValidString(name)) {
            dest.writeString(name);
            ParcelUtil.writeString(dest, description, IGNORE_VALUE, false);
            ParcelUtil.writeString(dest, value, IGNORE_VALUE, false);
        }
    }

    public static final Parcelable.Creator<XMockMappedSetting> CREATOR = new Parcelable.Creator<XMockMappedSetting>() {
        @Override
        public XMockMappedSetting createFromParcel(Parcel source) {
            return new XMockMappedSetting(source);
        }

        @Override
        public XMockMappedSetting[] newArray(int size) {
            return new XMockMappedSetting[size];
        }
    };

    public static class Table {
        public static final String name = "mapped_settings";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("name", "TEXT");
            put("description", "TEXT");
            put("value", "TEXT");
        }};

        //user
        //category
        //name
        //value
        //
        //
    }
}
