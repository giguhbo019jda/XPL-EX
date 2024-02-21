package eu.faircode.xlua.api.objects.xmock.phone;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.faircode.xlua.api.objects.xmock.ConfigSetting;
import eu.faircode.xlua.utilities.CursorUtil;

public class MockConfigConversions {
    private static final String TAG = "XLua.MockPhoneConversions";

    public static LinkedHashMap<String, String> convertJsonToMap(String jsonData) {
        LinkedHashMap<String, String> settings = new LinkedHashMap<>();
        try {
            JSONArray jArray = new JSONArray(jsonData);
            for(int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                if(jObject == null)
                    continue;

                String name = jObject.getString("name");
                String value = jObject.getString("value");
                settings.put(name, value);
            }

            Log.i(TAG, "phone config settings size=" + settings.size());
            return settings;
        }catch (Exception e) {
            Log.e(TAG, "Failed to read Config Settings! " + e);
            return settings;
        }
    }

    public static JSONArray convertMapToJson(LinkedHashMap<String, String> settings) {
        JSONArray jArray = new JSONArray();

        for(Map.Entry<String, String> r : settings.entrySet()) {
            JSONObject jObject = new JSONObject();
            try {
                jObject.put("name", r.getKey());
                jObject.put("value", r.getValue());
            }catch (Exception e) {
                Log.e(TAG, "Failed to write JSON element: name=" + r.getKey() + " value=" + r.getValue() + "\n" + e);
            }
        }
        return jArray;
    }

    public static void writeSettingsToBundle(Bundle b, LinkedHashMap<String, String> settings) {
        String[] names = new String[settings.size()];
        String[] values = new String[settings.size()];

        int i = 0;
        for(Map.Entry<String, String> r : settings.entrySet()) {
            names[i] = r.getKey();
            values[i] = r.getValue();
            i++;
        }

        b.putStringArray("names", names);
        b.putStringArray("values", values);
    }

    public static LinkedHashMap<String, String> readSettingsFromBundle(Bundle b) {
        LinkedHashMap<String, String> settings = new LinkedHashMap<>();
        String[] names = b.getStringArray("names");
        String[] values = b.getStringArray("values");
        if(names == null || values == null)
            return settings;

        for(int i = 0; i < names.length; i++)
            settings.put(names[i], values[i]);

        return settings;
    }

    public static Collection<MockPhoneConfig> configsFromCursor(Cursor cursor, boolean marshall, boolean close) {
        Collection<MockPhoneConfig> ps = new ArrayList<>();
        try {
            if(marshall) {
                while (cursor != null && cursor.moveToNext()) {
                    byte[] marshaled = cursor.getBlob(0);
                    Parcel parcel = Parcel.obtain();
                    parcel.unmarshall(marshaled, 0, marshaled.length);
                    parcel.setDataPosition(0);
                    MockPhoneConfig config = MockPhoneConfig.CREATOR.createFromParcel(parcel);
                    parcel.recycle();
                    ps.add(config);
                }
            }else {

            }
        }finally {
            if(close) CursorUtil.closeCursor(cursor);
        }

        return ps;
    }

    public static LinkedHashMap<String, String> listToHashMapSettings(List<ConfigSetting> settings, boolean worryAboutEnabledState) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if(worryAboutEnabledState) {
            for (ConfigSetting setting : settings)
                if (setting.isEnabled())
                    map.put(setting.getName(), setting.getValue());
        }
        else {
            for(ConfigSetting setting : settings)
                map.put(setting.getName(), setting.getValue());
        }

        return map;
    }

    public static Collection<ConfigSetting> hashMapToListSettings(LinkedHashMap<String, String> settingsMap) {
        Collection<ConfigSetting> settings = new ArrayList<>();
        for(Map.Entry<String, String> r : settingsMap.entrySet()) {
            ConfigSetting setting = new ConfigSetting(r.getKey(), r.getValue(), true);
            settings.add(setting);
        }

        return settings;
    }
}
