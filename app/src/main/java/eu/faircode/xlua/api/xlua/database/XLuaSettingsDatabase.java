package eu.faircode.xlua.api.xlua.database;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.XResult;
import eu.faircode.xlua.api.settings.XBulkSettingActionPacket;
import eu.faircode.xlua.api.xlua.provider.XLuaAppProvider;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;
import eu.faircode.xlua.api.settings.XLuaSettingPacket;

import eu.faircode.xlua.api.settings.XLuaLuaSetting;
import eu.faircode.xlua.api.settings.XLuaSettingCategory;
import eu.faircode.xlua.utilities.CollectionUtil;

public class XLuaSettingsDatabase {
    private static final String TAG = "XLua.XSettingsDatabase";
    public static final String DEFAULT_THEME = "dark";
    public static final String DEFAULT_COLLECTIONS = "Privacy,PrivacyEx";

    public static final String GLOBAL_NAMESPACE = "Global";
    public static final int GLOBAL_USER = 0;

    public static XResult putSetting(Context context, XDatabase db, String settingName) { return putSetting(context, db, settingName, null); }
    public static XResult putSetting(Context context, XDatabase db, String settingName, String value) { return putSetting(context, db, XLuaLuaSetting.create(GLOBAL_USER, GLOBAL_NAMESPACE, settingName, value), false); }
    public static XResult putSetting(Context context, XDatabase db, String settingName, Integer userId, String categoryOrPackageName, String value, Boolean kill) { return putSetting(context, db, XLuaLuaSetting.create(userId, categoryOrPackageName, settingName, value), kill); }
    public static XResult putSetting(Context context, XDatabase db, XLuaSettingPacket packet) { return putSetting(context, db, (XLuaLuaSetting)packet, packet.getKill()); }
    public static XResult putSetting(Context context, XDatabase db, XLuaLuaSetting setting, Boolean kill) {
        XResult res = XResult.create().setMethodName("putSetting").setExtra(setting.toString());
        Log.i(TAG, " putting setting internal [putSetting]=" + setting);
        if(!XLuaLuaSetting.isValid(setting))
            res.setFailed("Setting passed was Invalid!");
        boolean result =
                !setting.isDeleteAction() ?
                        DatabaseHelp.insertItem(
                                db,
                                XLuaLuaSetting.Table.name,
                                setting) :
                        DatabaseHelp.deleteItem(
                                SqlQuerySnake
                                        .create(db, XLuaLuaSetting.Table.name)
                                        .whereColumn("user", setting.getUser())
                                        .whereColumn("name", setting.getName()));

        if (!result && kill)
            XLuaAppProvider.forceStop(context, setting.getCategory(), setting.getUser(), res);

        return res.setResult(result);
    }

    public static XResult putSettings(Context context, XDatabase db, XBulkSettingActionPacket packet) { return putSettings(context, db, packet.getSettingsLua(), packet.getKill()); }
    public static XResult putSettings(Context context, XDatabase db, List<XLuaLuaSetting> settings) { return putSettings(context, db, settings, false); }
    public static XResult putSettings(Context context, XDatabase db, List<XLuaLuaSetting> settings, boolean kill) {
        XResult res = XResult.create().setMethodName("putSettings");
        if(!CollectionUtil.isValid(settings))
            return res.setFailed("Settings List was empty or null...");

        String category = settings.get(0).getCategory();
        int user = settings.get(0).getUser();
        boolean result = DatabaseHelp.insertItems(
                db,
                XLuaLuaSetting.Table.name,
                settings,
                prepareDatabaseTable(context, db));

        if (!result && kill)
            XLuaAppProvider.forceStop(context, category, user, res);

        return res.setResult(result);
    }

    public static boolean getSettingBoolean(Context context, XDatabase db, String settingName) { return Boolean.parseBoolean(getSettingValue(context, db, settingName, GLOBAL_USER, GLOBAL_NAMESPACE)); }
    public static boolean getSettingBoolean(Context context, XDatabase db, String settingName, int userId, String category) { return Boolean.parseBoolean(getSettingValue(context, db, settingName, userId, category)); }

    public static String getSettingValue(Context context, XDatabase db, XLuaLuaSetting setting) { return getSettingValue(context, db, setting.getName(), setting.getUser(), setting.getCategory()); }
    public static String getSettingValue(Context context, XDatabase db, String settingName, String category) { return getSettingValue(context, db, settingName, GLOBAL_USER, category); }
    public static String getSettingValue(Context context, XDatabase db, String settingName) { return getSettingValue(context, db, settingName, GLOBAL_USER, GLOBAL_NAMESPACE); }
    public static String getSettingValue(Context context, XDatabase db, String settingName, int userId, String category) {
        String v = SqlQuerySnake.
                create(db, XLuaLuaSetting.Table.name)
                .whereColumns("user", "category", "name")
                .whereColumnValues(Integer.toString(userId), category, settingName)
                .onlyReturnColumn("value")
                .queryGetFirstAs(XLuaLuaSetting.class, true)
                .getValue();

        if(v == null) {
            if(settingName.equals("theme")) {
                XLuaSettingPacket packet = new XLuaSettingPacket(GLOBAL_USER, GLOBAL_NAMESPACE, settingName);
                packet.setValue(DEFAULT_THEME);
                putSetting(context, db, packet);
                return DEFAULT_THEME;
            }
            else if(settingName.equals("collection")) {
                XLuaSettingPacket packet = new XLuaSettingPacket(GLOBAL_USER, GLOBAL_NAMESPACE, settingName);
                packet.setValue(DEFAULT_COLLECTIONS);
                putSetting(context, db, packet);
                return DEFAULT_COLLECTIONS;
            }
        }

        return v;
    }

    public static XLuaLuaSetting getSetting(XDatabase db, XLuaSettingCategory category, String settingName) { return getSetting(db, category.getName(), category.getUserId(), settingName);  }
    public static XLuaLuaSetting getSetting(XDatabase db, String category,  int userId, String settingName) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .whereColumns("user", "category", "name")
                .whereColumnValues(Integer.toString(userId), category, settingName)
                .queryGetFirstAs(XLuaLuaSetting.class, true);
    }

    public static Collection<String> getCategoriesFromUserId(XDatabase db, int userId) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .whereColumn("user", Integer.toString(userId))
                .queryAsStringList("category", true);
    }

    public static Collection<XLuaSettingCategory> getCategories(XDatabase db) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .onlyReturnColumns("user", "category")
                .queryAll(XLuaSettingCategory.class, true);
    }

    public static Map<Integer, List<String>> getCategoriesFromUid(XDatabase db){
        //return DatabaseHelper.getFromDatabase(db, "setting", XCategory.class);
        final Map<Integer, List<String>> categories = new HashMap<>();

        for(XLuaSettingCategory c : getCategories(db)) {
            List<String> names = categories.get(c.getUserId());
            if(names == null) {
                List<String> vs = new ArrayList<>();
                vs.add(c.getName());
                categories.put(c.getUserId(), vs);
            }else {
                if(!names.contains(c.getName()))
                    names.add(c.getName());
            }
        }

        return categories;
    }

    public static Collection<XLuaLuaSetting> getSettingsByCategory(XDatabase db, int userId, String categoryName) { return getSettingsByCategory(db, new XLuaSettingCategory(userId, categoryName)); }
    public static Collection<XLuaLuaSetting> getSettingsByCategory(XDatabase db, XLuaSettingCategory category) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .whereColumns("user", "category")
                .whereColumnValues(Integer.toString(category.getUserId()), category.getName())
                .queryAs(XLuaLuaSetting.class, true);
    }

    public static Collection<XLuaLuaSetting> getSettingsByNameAndUser(XDatabase db, int userId, String settingName) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .whereColumns("user", "name")
                .whereColumnValues(Integer.toString(userId), settingName)
                .queryAs(XLuaLuaSetting.class, true);
    }

    public static Collection<XLuaLuaSetting> getSettingsByName(XDatabase db, String settingsName) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .whereColumn("name", settingsName)
                .queryAs(XLuaLuaSetting.class, true);
    }

    public static boolean prepareDatabaseTable(Context context, XDatabase db) {
        //if(context == null) return true;//Assume its handled
        return DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                XLuaLuaSetting.Table.name,
                XLuaLuaSetting.Table.columns,
                XLuaLuaSetting.class);
    }
}
