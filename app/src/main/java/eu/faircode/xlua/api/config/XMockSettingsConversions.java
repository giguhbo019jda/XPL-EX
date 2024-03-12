package eu.faircode.xlua.api.config;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class XMockSettingsConversions {
    //make this a helper class
    //Converions helper universal but for JSON
    private static final String TAG = "XLua.MockSettingsConversions";

    public static LinkedHashMap<String, String> readSettingsFromString(String data) {
        if(data == null || data.isEmpty())
            return new LinkedHashMap<>();

        LinkedHashMap<String , String> settingsMap = new LinkedHashMap<>();
        try {
            JSONObject obj = new JSONObject(data);
            //return MockSettingsConversions.readSettingsFromJSON(obj); this will read settings field
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String value = obj.getString(key);
                settingsMap.put(key, value);
            }
        }catch (Exception e) {
            Log.e(TAG, "Failed to read settings from JSON String: " + e);
        }

        Log.i(TAG, "settings size=" + settingsMap.size());
        return settingsMap;
    }

    public static String createSettingsString(LinkedHashMap<String, String> settings) {
        JSONObject obj = createSettingsObject(settings);
        if(obj == null) {
            return "{}";
        }else {
            return obj.toString();
        }
    }

    public static LinkedHashMap<String, String> readSettingsFromJSON(JSONObject obj) throws JSONException {
        LinkedHashMap<String, String> settingsMap = new LinkedHashMap<>();

        JSONObject settings = obj.getJSONObject("settings");
        Iterator<String> keys = settings.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = settings.getString(key);
            settingsMap.put(key, value);
        }

        Log.i(TAG, "settings size=" + settingsMap.size());
        return settingsMap;
    }

    public static JSONObject createSettingsObject(LinkedHashMap<String, String> settings) {
        try {
            JSONObject settingsObj = new JSONObject();
            for(Map.Entry<String, String> r : settings.entrySet())
                settingsObj.put(r.getKey(), r.getValue());

            return settingsObj;
        }catch (Exception e) {
            Log.e(TAG, "Failed to write Settings to JSON: " + e);
            return null;
        }
    }

    public static void writeSettingsToJSON(JSONObject rootObject, LinkedHashMap<String, String> settings) {
        try {
            JSONObject settingsObj = new JSONObject();
            for(Map.Entry<String, String> r : settings.entrySet())
                settingsObj.put(r.getKey(), r.getValue());

            rootObject.put("settings", settingsObj);
        }catch (Exception e) {
            Log.e(TAG, "Failed to write Settings to JSON: " + e);
        }
    }
}
