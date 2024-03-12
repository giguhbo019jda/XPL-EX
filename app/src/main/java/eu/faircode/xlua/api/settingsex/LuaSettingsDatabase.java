package eu.faircode.xlua.api.settingsex;

import android.content.Context;
import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.XGlobalCore;
import eu.faircode.xlua.api.XResult;
import eu.faircode.xlua.api.hook.XLuaHook;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;
import eu.faircode.xlua.api.xlua.provider.XLuaAppProvider;
import eu.faircode.xlua.utilities.CollectionUtil;

public class LuaSettingsDatabase {
    private static final String TAG = "XLua.LuaSettingsDatabase";
    private static final String JSON = "settingdefaults.json";
    private static final int COUNT = 116;

    public static final String DEFAULT_THEME = "dark";
    public static final String DEFAULT_COLLECTIONS = "Privacy,PrivacyEx";

    public static final String GLOBAL_NAMESPACE = "Global";
    public static final int GLOBAL_USER = 0;

    public static XResult putSetting(Context context, XDatabase db, String settingName) { return putSetting(context, db, settingName, null); }
    public static XResult putSetting(Context context, XDatabase db, String settingName, String value) { return putSetting(context, db, LuaSettingPacket.create(GLOBAL_USER, GLOBAL_NAMESPACE, settingName, value, null, false, null)); }
    public static XResult putSetting(Context context, XDatabase db, LuaSettingPacket setting) {
        XResult res = XResult.create().setMethodName("putSetting").setExtra(setting.toString());
        if(!LuaSetting.isValid(setting))
            return res.setFailed("Setting passed was Invalid!");
        boolean result =
                !setting.isDelete() ?
                        DatabaseHelp.insertItem(
                                db,
                                LuaSetting.Table.name,
                                LuaSetting.from(setting)) :
                        DatabaseHelp.deleteItem(
                                SqlQuerySnake
                                        .create(db, LuaSetting.Table.name)
                                        .whereColumn("user", setting.getUser())
                                        .whereColumn("category", setting.getCategory())
                                        .whereColumn("name", setting.getName()));//we should query via packageName

        if (!result && setting.isKill())
            XLuaAppProvider.forceStop(context, setting.getCategory(), setting.getUser(), res);

        return res.setResult(result);
    }

    public static XResult putSettings(Context context, XDatabase db, List<LuaSetting> settings) { return putSettings(context, db, settings, false); }
    public static XResult putSettings(Context context, XDatabase db, List<LuaSetting> settings, boolean kill) {
        XResult res = XResult.create().setMethodName("putSettings");
        if(!CollectionUtil.isValid(settings))
            return res.setFailed("Settings List was empty or null...");

        String category = settings.get(0).getCategory();
        int user = settings.get(0).getUser();
        boolean result = DatabaseHelp.insertItems(
                db,
                LuaSetting.Table.name,
                settings,
                prepareDatabaseTable(context, db));

        if (!result && kill)
            XLuaAppProvider.forceStop(context, category, user, res);

        return res.setResult(result);
    }

    public static boolean getSettingBoolean(Context context, XDatabase db, String settingName) { return Boolean.parseBoolean(getSettingValue(context, db, settingName, GLOBAL_USER, GLOBAL_NAMESPACE)); }
    public static boolean getSettingBoolean(Context context, XDatabase db, String settingName, int userId, String category) { return Boolean.parseBoolean(getSettingValue(context, db, settingName, userId, category)); }

    public static String getSettingValue(Context context, XDatabase db, LuaSetting setting) { return getSettingValue(context, db, setting.getName(), setting.getUser(), setting.getCategory()); }
    public static String getSettingValue(Context context, XDatabase db, String settingName, String category) { return getSettingValue(context, db, settingName, GLOBAL_USER, category); }
    public static String getSettingValue(Context context, XDatabase db, String settingName) { return getSettingValue(context, db, settingName, GLOBAL_USER, GLOBAL_NAMESPACE); }
    public static String getSettingValue(Context context, XDatabase db, String settingName, int userId, String category) {
        String v = SqlQuerySnake
                .create(db, LuaSetting.Table.name)
                .whereColumn("user", Integer.toString(userId))
                .whereColumn("category", category)
                .whereColumn("name", settingName)
                .queryGetFirstString("value", true);

        if(v == null) {
            if(settingName.equals("theme")) {
                LuaSettingPacket packet = LuaSettingPacket.create(settingName, DEFAULT_THEME);
                putSetting(context, db, packet);
                return DEFAULT_THEME;
            }
            else if(settingName.equals("collection")) {
                LuaSettingPacket packet = LuaSettingPacket.create(settingName, DEFAULT_COLLECTIONS);
                putSetting(context, db, packet);
                return DEFAULT_COLLECTIONS;
            }else if(!category.equals(GLOBAL_NAMESPACE)) {
                //ignore if they want to use global or not
                //Dont apply hooks if you wont be using it ? for now least :P
                return SqlQuerySnake
                        .create(db, LuaSetting.Table.name)
                        .whereColumn("user", GLOBAL_USER)
                        .whereColumn("category", GLOBAL_NAMESPACE)
                        .whereColumn("name", settingName)
                        .queryGetFirstString("value", true);
            }
        }

        return v;
    }

    public static Collection<LuaSettingEx> getAllSettings(Context context, XDatabase db, int userId, String category) {
        HashMap<String, LuaSettingEx> allSettings = new HashMap<>();

        Collection<LuaSettingDefault> mappedSettings = getMappedSettings(context, db);
        Collection<LuaSetting> userSettings = SqlQuerySnake
                .create(db, LuaSetting.Table.name)
                .whereColumn("user", userId)
                .whereColumn("category", category)
                .queryAll(LuaSetting.class, true);

        for(LuaSettingDefault s : mappedSettings)
            allSettings.put(s.getName(), new LuaSettingEx(s));

        Log.i(TAG, "current map settings=" + allSettings.size() + " mapped settings=" + mappedSettings.size() + " user settings=" + userSettings.size());

        if(CollectionUtil.isValid(userSettings)) {
            for(LuaSetting s : userSettings) {
                String sName = s.getName();
                LuaSettingDefault set = allSettings.get(sName);
                if(set != null) {
                    set.setValue(s.getValue());
                }else {
                    allSettings.put(s.getName(), new LuaSettingEx(s));
                }
            }
        }

        Log.i(TAG, "current map settings=" + allSettings.size() + " [before hook settings parse]");

        Collection<XLuaHook> hooks = XGlobalCore.getHooks(context, db, true);
        for(XLuaHook hook : hooks) {
            String[] settings = hook.getSettings();
            if(settings == null || settings.length < 1)
                continue;

            for(String s : settings) {
                if(!allSettings.containsKey(s)) {
                    LuaSettingEx obj = new LuaSettingEx();
                    obj.setUser(userId);
                    obj.setCategory(category);
                    obj.setName(s);

                    allSettings.put(s, obj);
                }
            }
        }

        Log.i(TAG, "current map settings=" + allSettings.size() + "  after hook settings parsed!");
        return allSettings.values();
    }

    public static LuaSetting getSetting(XDatabase db, String settingName, int userId, String category) {
        return SqlQuerySnake
                .create(db, LuaSetting.Table.name)
                .whereColumns("user", "category", "name")
                .whereColumnValues(Integer.toString(userId), category, settingName)
                .queryGetFirstAs(LuaSetting.class, true);
    }

    public static XResult putDefaultMappedSetting(Context context, XDatabase db, LuaSettingPacket setting) {
        XResult res = XResult.create().setMethodName("putDefaultSetting").setExtra(setting.toString());
        if(!LuaSetting.isValid(setting))
            res.setFailed("Setting passed was Invalid!");
        boolean result =
                !setting.isDelete() ?
                        DatabaseHelp.insertItem(
                                db,
                                LuaSettingDefault.Table.name,
                                (LuaSettingDefault)setting) :
                        DatabaseHelp.deleteItem(
                                SqlQuerySnake
                                        .create(db, LuaSettingDefault.Table.name)
                                        .whereColumn("user", setting.getUser())
                                        .whereColumn("name", setting.getName()));

        return res.setResult(result);
    }

    public static String getSettingValueEx(Context context, XDatabase db, String settingName, int userId, String category, boolean ensureValue) {
        String v = getSettingValue(context, db, settingName, userId, category);
        if(v == null && ensureValue)
            return getDefaultMappedSettingValue(db, settingName);

        return v;
    }

    public static String getDefaultMappedSettingValue(XDatabase db, String settingName) {
        return SqlQuerySnake
                .create(db, LuaSettingDefault.Table.name)
                .ensureDatabaseIsReady()
                .whereColumn("name", settingName)
                .queryGetFirstString("value", true);
    }

    public static LuaSettingDefault getMappedSetting(Context context, XDatabase db, String settingName) {
        return SqlQuerySnake
                .create(db, LuaSettingDefault.Table.name)
                .ensureDatabaseIsReady()
                .whereColumn("name", settingName)
                .queryGetFirstAs(LuaSettingDefault.class, true);
    }

    public static Collection<LuaSettingDefault> getMappedSettings(Context context, XDatabase db) {
        return DatabaseHelp.getOrInitTable(
                context,
                db,
                LuaSettingDefault.Table.name,
                LuaSettingDefault.Table.columns,
                JSON,
                true,
                LuaSettingDefault.class,
                COUNT);
    }

    public static boolean prepareDatabaseTable(Context context, XDatabase db) {
        return DatabaseHelp.prepareDatabase(db, LuaSetting.Table.name, LuaSetting.Table.columns);
        /*if(!db.hasTable(LuaSetting.Table.name)) {
            boolean res = DatabaseHelp.prepareDatabase(db, LuaSetting.Table.name, LuaSetting.Table.columns);
            if(res) {
                //prepare the table with elements within it
                DatabaseHelp.getOrInitTable(
                        context,
                        db,
                        LuaSetting.Table.name,
                        LuaSetting.Table.columns,
                        JSON,
                        true,
                        LuaSetting.class,
                        true);
            }else return false;
        }

        return true;*/
    }

    public static boolean forceDatabaseCheck(Context context, XDatabase db) {
        //ensure globals are init ?
        /*boolean low = DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                LuaSetting.Table.name,
                LuaSetting.Table.columns,
                JSON,
                true,
                LuaSetting.class,
                DatabaseHelp.DB_FORCE_CHECK);

        if(!low) return false;*/
        return DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                LuaSettingDefault.Table.name,
                LuaSettingDefault.Table.columns,
                JSON,
                true,
                LuaSettingDefault.class,
                DatabaseHelp.DB_FORCE_CHECK);
    }
}
