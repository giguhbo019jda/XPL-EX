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

import java.util.LinkedHashMap;
import java.util.List;

import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;
import eu.faircode.xlua.utilities.ParcelUtil;
import eu.faircode.xlua.utilities.StringUtil;

public class LuaSettingDefault extends LuaSetting implements IJsonSerial, Parcelable {
    public static LuaSettingDefault create() { return new LuaSettingDefault(); }
    public static LuaSettingDefault create(String name, String description) { return new LuaSettingDefault(null, null, name, null, description); }
    public static LuaSettingDefault create(Integer userId, String category, String name, String description) { return new LuaSettingDefault(userId, category, name, null, description); }
    public static LuaSettingDefault create(Integer userId, String category, String name, String value, String description) { return new LuaSettingDefault(userId, category, name, value, description); }

    protected String description;

    public LuaSettingDefault() { }
    public LuaSettingDefault(Parcel in) { fromParcel(in); }
    public LuaSettingDefault(Integer userId, String category, String name, String value, String description) {
        super(userId, category, name, value);
        setDescription(description);
    }

    public String getDescription() { return this.description; }
    public LuaSettingDefault setDescription(String description) { if(description != null) this.description = description; return this; }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = super.createContentValues();
        if(this.description != null) cv.put("description", this.description);
        return cv;
    }

    @Override
    public void fromContentValues(ContentValues contentValue) {
        if(contentValue != null) {
            super.fromContentValues(contentValue);
            this.description = contentValue.getAsString("description");
        }
    }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            super.fromCursor(cursor);
            this.description = CursorUtil.getString(cursor, "description");
        }
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = super.toJSONObject();
        if(this.description != null) jRoot.put("description", this.description);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        this.description = JSONUtil.getString(obj,"description");
    }

    @Override
    public Bundle toBundle() {
        Bundle b = super.toBundle();
        if(this.description != null) b.putString("description", this.description);
        return b;
    }

    @Override
    public void fromBundle(Bundle bundle) {
        if(bundle != null) {
            super.fromBundle(bundle);
            this.description = BundleUtil.readString(bundle, "description");
        }
    }

    @Override
    public void fromParcel(Parcel in) {
        if(in != null) {
            super.fromParcel(in);
            this.description = ParcelUtil.readString(in, null, ParcelUtil.IGNORE_VALUE);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(StringUtil.isValidString(name)) {
            super.writeToParcel(dest, flags);
            ParcelUtil.writeString(dest, this.description, ParcelUtil.IGNORE_VALUE, false);
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public boolean equals(@Nullable Object obj) { return super.equals(obj); }

    @NonNull
    @Override
    public String toString() {
        return new StringBuilder(super.toString())
                .append(" description=")
                .append(this.description).toString();
    }

    public static class Table {
        public static final String name = "default_settings";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("user", "INTEGER");
            put("category", "TEXT");
            put("name", "TEXT PRIMARY KEY");
            put("value", "TEXT");
            put("description", "TEXT");
        }};
    }
}
