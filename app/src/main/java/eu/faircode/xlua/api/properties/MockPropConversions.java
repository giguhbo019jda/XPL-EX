package eu.faircode.xlua.api.properties;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import eu.faircode.xlua.DebugUtil;
import eu.faircode.xlua.api.settingsex.LuaSettingEx;
import eu.faircode.xlua.api.xlua.XLuaCall;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;

public class MockPropConversions {
    private static final String TAG = "XLua.MockPropConversions";

    public static Collection<MockPropGroupHolder> createHolders(Context context, Collection<MockPropSetting> properties, Collection<LuaSettingEx> settings) {
        Log.i(TAG, " properties returned =" + properties.size());

        HashMap<String, MockPropGroupHolder> groups = new HashMap<>();
        for(MockPropSetting prop : properties) {
            String sName = prop.getSettingName();
            MockPropGroupHolder holder = groups.get(sName);
            if(holder == null) {
                holder = new MockPropGroupHolder(sName);
                holder.setUser(prop.getUser());
                holder.setPackageName(prop.getCategory());
                //holder.setValue(XLuaCall.getSettingValue(context, prop.getUser(), prop.getCategory(), prop.getSettingName()));
                holder.addProperty(prop);
                groups.put(sName, holder);
                if(DebugUtil.isDebug())
                    Log.i(TAG, "Group does not exist=" + holder + " property=" + prop);

                for(LuaSettingEx setting : settings) {
                    if(setting.getName().equalsIgnoreCase(sName)) {
                        holder.setDescription(setting.getDescription());
                        holder.setValue(setting.getValue());
                        holder.setSetting(setting);
                    }
                }
            }else {
                holder.addProperty(prop);
                if(DebugUtil.isDebug())
                    Log.i(TAG, "Group exists=" + holder + " property=" + prop);
            }
        }

        Log.i(TAG, "groups = " + groups.size());
        return groups.values();
    }


    public static ArrayList<String> propsListToStringList(Collection<MockPropSetting> settings) {
        ArrayList<String> props = new ArrayList<>();
        for(MockPropSetting pSetting : settings)
            props.add(pSetting.getName());

        return props;
    }

    public static ArrayList<MockPropSetting> getPropertiesFromJSON(JSONObject rootObject) {
        try {
            String settingName = rootObject.getString("settingName");
            return getPropertiesFromJSONArray(rootObject.getJSONArray("propNames"), settingName, XLuaSettingsDatabase.GLOBAL_USER, XLuaSettingsDatabase.GLOBAL_NAMESPACE);
        }catch (JSONException ex) {
            Log.e(TAG, "JSON getJSONArray(propNames) or getString(settingName) failed: " + ex);
        }

        return new ArrayList<>();
    }

    public static ArrayList<MockPropSetting> getPropertiesFromJSONArray(JSONArray jsonArray, String settingName, int userId, String packageName) {
        ArrayList<MockPropSetting> props = new ArrayList<>();
        try {
            for(int i = 0; i < jsonArray.length(); i++)
                props.add(new MockPropSetting(jsonArray.getString(i), settingName, userId, packageName, MockPropSetting.PROP_NULL));

            if(DebugUtil.isDebug())
                Log.i(TAG, "[getPropertiesFromJSONArray] size=" + props.size());

        }catch (JSONException ex) {
            Log.e(TAG, "JSON array.getString(i) failed: " + ex);
        }

        return props;
    }
}
