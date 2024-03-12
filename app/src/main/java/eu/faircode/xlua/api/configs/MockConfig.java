package eu.faircode.xlua.api.configs;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import eu.faircode.xlua.api.config.XMockConfig;
import eu.faircode.xlua.api.config.XMockConfigConversions;
import eu.faircode.xlua.api.config.XMockSettingsConversions;
import eu.faircode.xlua.api.settingsex.LuaSettingEx;
import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.CollectionUtil;
import eu.faircode.xlua.utilities.CursorUtil;

public class MockConfig implements IJsonSerial, Parcelable {
    protected String name;
    protected List<LuaSettingEx> settings;

    public MockConfig() { }
    public MockConfig(Parcel in) { fromParcel(in); }
    public MockConfig(String name, List<LuaSettingEx> settings) {
        setName(name);
        setSettings(settings);
    }

    public String getName() { return name; }
    public MockConfig setName(String name) {  if(name != null) this.name = name; return  this; }

    public List<LuaSettingEx> getSettings() { return settings; }
    public MockConfig setSettings(List<LuaSettingEx> settings) { if(settings != null) this.settings = settings; return this; }

    public void addSetting(LuaSettingEx setting) {
        if(settings == null)
            settings = new ArrayList<>();

        if(!settings.isEmpty()) {
            for(LuaSettingEx set : settings) {
                if(set.getName().equalsIgnoreCase(setting.getName()))
                    return;
            }
        }

        settings.add(setting);
    }

    public void pairSettingMaps(List<LuaSettingEx> settings) {
        if(!CollectionUtil.isValid(this.settings) && !CollectionUtil.isValid(settings)) {
            for(LuaSettingEx localSet : this.settings) {
                for(LuaSettingEx set : settings) {
                    if(set.getName().equalsIgnoreCase(localSet.getName()))
                        localSet.setDescription(set.getDescription());
                }
            }
        }
    }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("settings", MockConfigConversions.getSettingsToJSONObjectString(settings));
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
            this.settings = MockConfigConversions.readSettingsFromJSON(CursorUtil.getString(cursor, "settings"), true);
        }
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = new JSONObject();
        jRoot.put("name", name);
        MockConfigConversions.writeSettingsToJSON(jRoot, settings);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        this.name = obj.getString("name");
        this.settings = MockConfigConversions.readSettingsFromJSON(obj, false);
    }

    @Override
    public Bundle toBundle() {
        Bundle b = new Bundle();
        b.putString("name", name);
        b.putString("settings", MockConfigConversions.getSettingsToJSONObjectString(settings));
        return b;
    }

    @Override
    public void fromBundle(Bundle bundle) {
        if(bundle != null) {
            this.name = BundleUtil.readString(bundle, "name");
            this.settings = MockConfigConversions.readSettingsFromJSON(bundle.getString("settings"), true);
        }
    }

    @Override
    public void fromParcel(Parcel in) {
        if(in != null) {
            this.name = in.readString();
            this.settings = MockConfigConversions.readSettingsFromJSON(in.readString(), true);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(MockConfigConversions.getSettingsToJSONObjectString(settings));
    }

    @Override
    public int describeContents() { return 0; }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }

    public static final Parcelable.Creator<MockConfig> CREATOR = new Parcelable.Creator<MockConfig>() {
        @Override
        public MockConfig createFromParcel(Parcel source) {
            return new MockConfig(source);
        }

        @Override
        public MockConfig[] newArray(int size) {
            return new MockConfig[size];
        }
    };

    public static class Table {
        public static final String name = "mock_configs";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("name", "TEXT PRIMARY KEY");
            put("settings", "TEXT");
        }};
    }
}
