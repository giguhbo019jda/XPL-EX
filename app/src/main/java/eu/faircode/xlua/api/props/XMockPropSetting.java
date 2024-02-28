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

import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;
import eu.faircode.xlua.utilities.ParcelUtil;

public class XMockPropSetting extends XMockPropSettingBase implements IJsonSerial, Parcelable {
    public XMockPropSetting() { }
    public XMockPropSetting(Parcel in) { fromParcel(in); }
    public XMockPropSetting(Bundle bundle) { fromBundle(bundle); }

    public XMockPropSetting(ContentValues cv) { fromContentValues(cv); }
    public XMockPropSetting(String propertyName, String settingName) { super(propertyName, settingName); }
    public XMockPropSetting(String propertyName, String settingName, Boolean enabled) { super(propertyName, settingName, enabled); }

    @Override
    public Bundle toBundle() {
        Bundle b = new Bundle();
        if(this.propertyName != null) b.putString("propertyName", propertyName);
        if(this.settingName != null) b.putString("settingName", settingName);
        if(this.enabled != null) b.putBoolean("enabled", enabled);
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        this.propertyName = b.getString("propertyName");
        this.settingName = b.getString("settingName");
        this.enabled = b.getBoolean("enabled");
    }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        if(propertyName != null) cv.put("propertyName", propertyName);
        if(settingName != null) cv.put("settingName", settingName);
        if(enabled != null) cv.put("enabled", enabled);
        return cv;
    }

    @Override
    public void fromContentValues(ContentValues contentValue) {
        this.propertyName = contentValue.getAsString("propertyName");
        this.settingName = contentValue.getAsString("settingName");
        this.enabled = contentValue.getAsBoolean("enabled");
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null;  }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromCursor(Cursor cursor) {
        this.propertyName = CursorUtil.getString(cursor, "propertyName");
        this.settingName = CursorUtil.getString(cursor, "settingName");
        this.enabled = CursorUtil.getBoolean(cursor, "enabled");
    }

    @Override
    public void fromParcel(Parcel in) {
        this.settingName = in.readString();
        this.propertyName = in.readString();
        this.enabled = ParcelUtil.readBool(in);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.settingName);
        dest.writeString(this.propertyName);
        ParcelUtil.writeBool(dest, this.enabled);
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = new JSONObject();
        jRoot.put("settingName", settingName);
        jRoot.put("propertyName", propertyName);
        jRoot.put("enabled", enabled);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        this.settingName = obj.getString("settingName");
        this.propertyName = obj.getString("propertyName");
        this.enabled = JSONUtil.getBoolean(obj, "enabled", true);
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

    public static class Table {
        public static final String name = "propmaps";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("propertyName", "TEXT");
            put("settingName", "TEXT");
            put("enabled", "BOOLEAN");
        }};
    }
}
