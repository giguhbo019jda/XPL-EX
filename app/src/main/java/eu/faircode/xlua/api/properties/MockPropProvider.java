package eu.faircode.xlua.api.properties;

import android.content.Context;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.api.xmock.provider.XMockSettingsProvider;
import eu.faircode.xlua.utilities.CollectionUtil;
import eu.faircode.xlua.utilities.MockUtils;

public class MockPropProvider {
    private static String TAG = "XLua.MockPropProvider";

    //KEY=(Property Name)
    private static HashMap<String, MockPropMap> mappedProperties = new HashMap<>();
    private static final Object lock = new Object();

    public static String getPropertyValue(Context context, XDatabase db, String propertyName,  int userId, String packageName) {
        MockPropMap map = mappedProperties.get(propertyName);
        if(map == null)
            return MockUtils.NOT_BLACKLISTED;

        String sName = map.getSettingName();
        String value = XLuaSettingsDatabase.getSettingValue(context, db, sName, userId, packageName);
        if(value == null)
            value = XMockSettingsProvider.getDefaultSettingValue(context, db, sName);

        if(value == null)
            return MockUtils.NOT_BLACKLISTED;

        return value;
    }

    public static Collection<MockPropSetting> getSettingsForPackage(Context context, XDatabase db, int userId, String packageName, boolean getAll) {
        Log.i(TAG, "[getSettingsForPackage] db=" + db.getName() + " user=" + userId + " pkg=" + packageName + " all=" + getAll);
        initCache(context, db);
        Collection<MockPropSetting> userSettings = MockPropDatabase.getPropertySettingsForUser(db, userId, packageName);
        Log.i(TAG, "user settings=" + userSettings.size());
        if(!getAll)
            return userSettings;

        HashMap<String, MockPropSetting> users = new HashMap<>(userSettings.size());
        if(!CollectionUtil.isValid(userSettings)) {
            for(MockPropSetting s : userSettings)
                users.put(s.getName(), s);
        }

        Log.i(TAG, "user settings (2) =" + users.size() + " mapped properties=" + mappedProperties.size());

        for(Map.Entry<String, MockPropMap> e : mappedProperties.entrySet()) {
            String k = e.getKey();
            MockPropMap m = e.getValue();
            if(!users.containsKey(k)) {
                Log.d(TAG, "mock map before to user [" + k + "] = " + m);
                MockPropSetting ss = MockPropSetting.create(k, m.getSettingName(), userId, packageName, MockPropSetting.PROP_NULL);
                Log.d(TAG, "ss from [MockPropSetting.create] = " + ss);
                users.put(k, ss);
            }else {
                Log.w(TAG, "user settings contains=" + k + " map=" + m);
            }
        }

        Log.i(TAG, "total user settings=" + users.size());
        return users.values();
    }

    public static void initCache(Context context, XDatabase db) {
        if (!CollectionUtil.isValid(mappedProperties)) {
            synchronized (lock) {
                if (!CollectionUtil.isValid(mappedProperties)) {
                    //Still null even after lock que
                    HashMap<String, MockPropMap> maps = new HashMap<>();
                    Collection<MockPropMap> settings = MockPropDatabase.forceCheckMapsDatabase(context, db);
                    for (MockPropMap set : settings)
                        maps.put(set.getName(), set);

                    mappedProperties = maps;
                    Log.i(TAG, "mapped settings =" + maps.size());
                }
            }
        }
    }
}
