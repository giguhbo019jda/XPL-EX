package eu.faircode.xlua.api.properties;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.props.XMockPropMapped;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;
import eu.faircode.xlua.api.standard.interfaces.IDBQuery;
import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.ContentValuesUtil;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;
import eu.faircode.xlua.utilities.ParcelUtil;

public class MockPropMap implements IJsonSerial, IDBQuery, Parcelable {
    protected String name;
    protected String settingName;

    public MockPropMap() { }
    public MockPropMap(Parcel in) { fromParcel(in); }
    public MockPropMap(ContentValues cv) { fromContentValues(cv); }
    public MockPropMap(String name, String settingName) {
        setName(name);
        setSettingName(settingName);
    }

    public String getName() { return name; }
    public MockPropMap setName(String name) { if(name != null) this.name = name; return this; }

    public String getSettingName() { return settingName; }
    public MockPropMap setSettingName(String settingName) { if(settingName != null) this.settingName = settingName; return this; }

    @Override
    public Bundle toBundle() {
        Bundle b = new Bundle();
        if(this.name != null) b.putString("name", name);
        if(this.settingName != null) b.putString("settingName", settingName);
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        this.name = BundleUtil.readString(b, "name");
        this.settingName = BundleUtil.readString(b, "settingName");
    }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        if(name != null) cv.put("name", name);
        if(settingName != null) cv.put("settingName", settingName);
        return cv;
    }

    @Override
    public void fromContentValues(ContentValues contentValue) {
        this.name = ContentValuesUtil.getString(contentValue, "name");
        this.settingName = ContentValuesUtil.getString(contentValue, "settingName");
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null;  }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromCursor(Cursor cursor) {
        this.name = CursorUtil.getString(cursor, "name");
        this.settingName = CursorUtil.getString(cursor, "settingName");
    }

    @Override
    public void fromParcel(Parcel in) {
        this.name = in.readString();
        this.settingName = in.readString();
        //this.settingName = ParcelUtil.readString(in, null, ParcelUtil.IGNORE_VALUE);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.settingName);
        //ParcelUtil.writeString(dest, this.settingName, ParcelUtil.IGNORE_VALUE, true);
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = new JSONObject();
        if(this.name != null) jRoot.put("name", name);
        if(this.settingName != null) jRoot.put("settingName", settingName);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        this.name = JSONUtil.getString(obj, "name");
        this.settingName = JSONUtil.getString(obj, "settingName");
    }

    @Override
    public SqlQuerySnake createQuery(XDatabase db) {
        return SqlQuerySnake.create(db, Table.name)
                .whereColumn("name", this.name);
    }

    public static final Parcelable.Creator<MockPropMap> CREATOR = new Parcelable.Creator<MockPropMap>() {
        @Override
        public MockPropMap createFromParcel(Parcel source) {
            return new MockPropMap(source);
        }

        @Override
        public MockPropMap[] newArray(int size) {
            return new MockPropMap[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if(name != null) {
            sb.append(" name=");
            sb.append(name);
        }

        if(settingName != null) {
            sb.append(" setting=");
            sb.append(settingName);
        }

        return sb.toString();
    }

    public static class Table {
        public static final String name = "prop_maps";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("name", "TEXT PRIMARY KEY");
            put("settingName", "TEXT");
        }};
    }
}
