package eu.faircode.xlua.api.props;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;
import eu.faircode.xlua.api.standard.interfaces.IDBQuery;
import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;

public class XMockPropSetting extends XMockPropSettingBase implements IJsonSerial, IDBQuery, Parcelable {
    public static final int PROP_DELETE = -1;
    public static final int PROP_HIDE = 0x0;
    public static final int PROP_SKIP = 0x1;
    public static final int PROP_NULL = 0x2;

    public static XMockPropSetting create() { return new XMockPropSetting(); }
    public static XMockPropSetting create(String propName) { return new XMockPropSetting(propName, null, null, null); }
    public static XMockPropSetting create(String propName, Integer userId, String packageName) { return new XMockPropSetting(propName, userId, packageName, null); }
    public static XMockPropSetting create(String propName, Integer userId, String packageName, int value) { return new XMockPropSetting(propName, userId, packageName, value); }

    public XMockPropSetting() { }
    public XMockPropSetting(Parcel p) { fromParcel(p); }
    public XMockPropSetting(String name, Integer userId, String packageName, Integer value) { super(name, userId, packageName, value); }

    @Override
    public Bundle toBundle() {
        Bundle b = new Bundle();
        ensureIdentification();
        b.putInt("user", this.user);
        b.putString("packageName", this.packageName);
        b.putString("name", name);
        b.putInt("value", value);
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        this.user = BundleUtil.readInteger(b, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.packageName = BundleUtil.readString(b, "category", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
        this.name = BundleUtil.readString(b, "name");
        this.value = BundleUtil.readInteger(b, "value", PROP_HIDE);
    }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        ensureIdentification();
        cv.put("user", this.user);
        cv.put("packageName", this.packageName);
        cv.put("name", this.name);
        cv.put("value", this.value);
        return cv;
    }

    @Override
    public void fromContentValues(ContentValues contentValue) {
        this.user = contentValue.getAsInteger("user");
        this.packageName = contentValue.getAsString("packageName");
        this.name = contentValue.getAsString("name");
        this.value = contentValue.getAsInteger("value");
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null;  }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromCursor(Cursor cursor) {
        this.user = CursorUtil.getInteger(cursor, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.packageName = CursorUtil.getString(cursor, "packageName", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
        this.name = CursorUtil.getString(cursor, "name");
        this.value = CursorUtil.getInteger(cursor, "value", PROP_HIDE);
    }

    @Override
    public void fromParcel(Parcel in) {
        this.user = in.readInt();
        this.packageName = in.readString();
        this.name = in.readString();
        this.value = in.readInt();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ensureIdentification();
        dest.writeInt(this.user);
        dest.writeString(this.packageName);
        dest.writeString(this.name);
        dest.writeInt(this.value);
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = new JSONObject();
        ensureIdentification();
        jRoot.put("user", this.user);
        jRoot.put("packageName", this.packageName);
        jRoot.put("name", this.name);
        jRoot.put("value", this.value);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        this.user = JSONUtil.getInteger(obj, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.packageName = JSONUtil.getString(obj, "packageName", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
        this.name = obj.getString("name");
        this.value = JSONUtil.getInteger(obj, "value", PROP_HIDE);
    }

    public static final Parcelable.Creator<XMockPropSetting> CREATOR = new Parcelable.Creator<XMockPropSetting>() {
        @Override
        public XMockPropSetting createFromParcel(Parcel source) {
            return new XMockPropSetting(source);
        }

        @Override
        public XMockPropSetting[] newArray(int size) {
            return new XMockPropSetting[size];
        }
    };

    @Override
    public SqlQuerySnake createQuery(XDatabase db) {
        ensureIdentification();
        return SqlQuerySnake.create(db, Table.name)
                .whereColumn("user", this.user)
                .whereColumn("packageName", this.packageName)
                .whereColumn("name", this.name);
    }

    private void ensureIdentification() {
        if(this.user == null)
            this.user = XLuaSettingsDatabase.GLOBAL_USER;
        if(this.packageName == null)
            this.packageName = XLuaSettingsDatabase.GLOBAL_NAMESPACE;
    }

    public static class Table {
        public static final String name = "prop_settings";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("user", "TEXT");
            put("packageName", "TEXT");
            put("name", "TEXT");
            put("value", "INTEGER");
        }};
    }
}
