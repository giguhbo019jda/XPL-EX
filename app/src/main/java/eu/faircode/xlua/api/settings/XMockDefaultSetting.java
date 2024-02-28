package eu.faircode.xlua.api.settings;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import eu.faircode.xlua.api.config.XMockConfig;
import eu.faircode.xlua.api.config.XMockConfigConversions;
import eu.faircode.xlua.api.config.XMockConfigSetting;
import eu.faircode.xlua.api.config.XMockSettingsConversions;
import eu.faircode.xlua.api.props.XMockPropSettingBase;
import eu.faircode.xlua.api.standard.interfaces.IDBSerial;
import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.CursorUtil;

public class XMockDefaultSetting extends XMockDefaultSettingBase  implements IJsonSerial, Parcelable {
    public XMockDefaultSetting() { }
    public XMockDefaultSetting(Parcel p) { fromParcel(p); }
    public XMockDefaultSetting(Bundle b) { fromBundle(b); }

    public XMockDefaultSetting(String name, String value) { super(name, value); }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("value", value);
        return cv;
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromContentValues(ContentValues contentValue) { }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            this.name = CursorUtil.getString(cursor, "name");
            this.value = CursorUtil.getString(cursor, "value");
        }
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = new JSONObject();
        jRoot.put("name", name);
        jRoot.put("value", value);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        this.name = obj.getString("name");
        this.value = obj.getString("value");
    }

    @Override
    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("name", name);
        b.putString("value", value);
        return b;
    }

    @Override
    public void fromBundle(Bundle bundle) {
        if(bundle != null) {
            this.name = BundleUtil.readString(bundle, "name");
            this.value = BundleUtil.readString(bundle, "value");
        }
    }

    @Override
    public void fromParcel(Parcel in) {
        if(in != null) {
            this.name = in.readString();
            this.value = in.readString();
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(value);
    }

    public static final Parcelable.Creator<XMockDefaultSetting> CREATOR = new Parcelable.Creator<XMockDefaultSetting>() {
        @Override
        public XMockDefaultSetting createFromParcel(Parcel source) {
            return new XMockDefaultSetting(source);
        }

        @Override
        public XMockDefaultSetting[] newArray(int size) {
            return new XMockDefaultSetting[size];
        }
    };

    public static class Table {
        public static final String name = "settingsdefault";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("name", "TEXT");
            put("value", "TEXT");
        }};
    }
}
