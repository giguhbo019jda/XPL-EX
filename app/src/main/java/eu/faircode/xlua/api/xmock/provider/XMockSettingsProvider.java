package eu.faircode.xlua.api.xmock.provider;

import android.content.Context;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.settings.XMockDefaultSetting;
import eu.faircode.xlua.api.xmock.database.XMockSettingsDatabase;

public class XMockSettingsProvider {
    private static final Object lock = new Object();
    private static final String TAG = "XLua.XMockSettingsProvider";
    private static Map<String, XMockDefaultSetting> defaultSettings = new HashMap<>();

    public static String getDefaultSettingValue(Context context, XDatabase db, String settingName) {
        if(defaultSettings == null || defaultSettings.isEmpty()) {
            synchronized (lock) {
                if(defaultSettings == null || defaultSettings.isEmpty()) {
                    Map<String, XMockDefaultSetting> cDefaultSettings = new HashMap<>();

                    Collection<XMockDefaultSetting> defaults = XMockSettingsDatabase.getDefaultSettings(context, db);
                    for(XMockDefaultSetting def : defaults)
                        cDefaultSettings.put(def.getName(), def);

                    defaultSettings = cDefaultSettings;
                    Log.i(TAG, "Internal Cache Default Settings size=" + defaultSettings.size());
                }
            }
        }

        XMockDefaultSetting defSetting = defaultSettings.get(settingName);
        if(defSetting != null)
            return defSetting.getValue();

        return null;
    }
}
