package eu.faircode.xlua.api.config;

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

import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.CursorUtil;

public class XMockConfig extends XMockConfigBase implements IJsonSerial, Parcelable {
    public XMockConfig() { }
    public XMockConfig(Parcel p) { fromParcel(p); }
    public XMockConfig(Bundle b) { fromBundle(b); }
    public XMockConfig(String name, LinkedHashMap<String, String> settings) {
        setName(name);
        setSettings(settings);
    }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("settings", XMockSettingsConversions.createSettingsString(settings));
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
            this.settings = XMockSettingsConversions.readSettingsFromString(CursorUtil.getString(cursor, "settings"));
        }
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = new JSONObject();
        jRoot.put("name", name);
        XMockSettingsConversions.writeSettingsToJSON(jRoot, settings);
        //jRoot.put("settings", MockConfigConversions.convertMapToJson(settings).toString());
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        this.name = obj.getString("name");
        //this.settings =  MockConfigConversions.convertJsonToMap(obj.getString("settings"));
        this.settings = XMockSettingsConversions.readSettingsFromJSON(obj);
    }

    @Override
    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("name", name);
        XMockConfigConversions.writeSettingsToBundle(b, settings);
        return b;
    }

    @Override
    public void fromBundle(Bundle bundle) {
        if(bundle != null) {
            this.name = BundleUtil.readString(bundle, "name");
            this.settings = XMockConfigConversions.readSettingsFromBundle(bundle);
            Log.i(TAG, "Config from bundle, name=" + name + " settings size=" + settings.size());
        }
    }

    @Override
    public void fromParcel(Parcel in) {
        if(in != null) {
            this.name = in.readString();
            //this.settings = MockConfigConversions.convertJsonToMap(in.readString());
            this.settings = XMockSettingsConversions.readSettingsFromString(in.readString());
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(XMockSettingsConversions.createSettingsString(settings));
        //dest.writeString(MockConfigConversions.convertMapToJson(settings).toString());
    }

    public LinkedHashMap<String, String> orderSettings(boolean setInternal) {
        List<XMockConfigSetting> settings = new ArrayList<>(XMockConfigConversions.hashMapToListSettings(getSettings()));
        Collections.sort(settings, new Comparator<XMockConfigSetting>() {
            @Override
            public int compare(XMockConfigSetting o1, XMockConfigSetting o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        LinkedHashMap<String, String> newMap = XMockConfigConversions.listToHashMapSettings(settings, false);
        if(setInternal)
            setSettings(newMap);

        return newMap;
    }

    public static final Parcelable.Creator<XMockConfig> CREATOR = new Parcelable.Creator<XMockConfig>() {
        @Override
        public XMockConfig createFromParcel(Parcel source) {
            return new XMockConfig(source);
        }

        @Override
        public XMockConfig[] newArray(int size) {
            return new XMockConfig[size];
        }
    };

    public static class Table {
        public static final String name = "mockconfigs";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("name", "TEXT");
            put("settings", "TEXT");
        }};
    }
}
