package eu.faircode.xlua.api.configs;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.faircode.xlua.api.settingsex.LuaSettingEx;

public class MockConfigConversions {
    private static final String TAG = "XLua.MockConfigConversions";

    public static void writeSettingsToJSON(JSONObject obj, List<LuaSettingEx> settings) {
        try {
            JSONObject settingsObj = new JSONObject();
            for(LuaSettingEx setting : settings)
                settingsObj.put(setting.getName(), setting.getValue());

            obj.put("settings", settingsObj);
        }catch (JSONException ex) {
            Log.e(TAG, "Failed to write settings: " + ex + "\n" + Log.getStackTraceString(ex));
        }
    }

    public static String getSettingsToJSONObjectString(List<LuaSettingEx> settings) {
        try {
            return getSettingsToJSONObject(settings).toString(2);
        }catch (JSONException ex) {
            Log.e(TAG, "JSON Object to String failed: " + ex + "\n" + Log.getStackTraceString(ex));
            return "{}";
        }
    }

    public static JSONObject getSettingsToJSONObject(List<LuaSettingEx> settings) {
        JSONObject settingsObj = new JSONObject();
        try {
            for(LuaSettingEx setting : settings)
                settingsObj.put(setting.getName(), setting.getValue());

            return settingsObj;
        }catch (JSONException ex) {
            Log.e(TAG, "Failed to write settings: " + ex + "\n" + Log.getStackTraceString(ex));
            return settingsObj;
        }
    }

    public static List<LuaSettingEx> readSettingsFromJSON(String jsonText, boolean isRootObject) {
        try {
            return readSettingsFromJSON(new JSONObject(jsonText), isRootObject);
        }catch (JSONException ex) {
            Log.e(TAG, "Failed to String to JSON: " + ex + "\n" + Log.getStackTraceString(ex));
            return new ArrayList<>();
        }
    }

    public static List<LuaSettingEx> readSettingsFromJSON(JSONObject obj, boolean isRootObject) {
        List<LuaSettingEx> settingsList = new ArrayList<>();
        try {
            JSONObject baseObj = obj;
            Iterator<String> keys = null;
            if(!isRootObject) {
                baseObj = obj.getJSONObject("settings");
                keys = baseObj.keys();
            }else {
                keys = obj.keys();
            }

            while (keys.hasNext()) {
                String key = keys.next();
                String value = baseObj.getString(key);
                LuaSettingEx setting = new LuaSettingEx();
                setting.setName(key);
                setting.setValue(value);
                settingsList.add(setting);
            }

            return settingsList;
        }catch (JSONException ex) {
            Log.e(TAG, "Failed to read JSON object: " + ex + "\n" + Log.getStackTraceString(ex));
            return settingsList;
        }
    }
}
