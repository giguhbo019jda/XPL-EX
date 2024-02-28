package eu.faircode.xlua.api.xlua.database;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.faircode.xlua.XDataBase;
import eu.faircode.xlua.XUtil;
import eu.faircode.xlua.api.xlua.provider.XLuaAppProvider;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;
import eu.faircode.xlua.api.settings.XLuaSettingPacket;

import eu.faircode.xlua.api.settings.XLuaLuaSetting;
import eu.faircode.xlua.api.settings.XLuaSettingCategory;

public class XLuaSettingsDatabase {
    private static final String TAG = "XLua.XSettingsDatabase";

    public static final String DEFAULT_THEME = "dark";
    public static final String DEFAULT_COLLECTIONS = "Privacy,PrivacyEx";


    public static boolean putSetting(XDataBase db, XLuaSettingPacket packet) {
        boolean result =
                packet.getValue() != null ?
                        DatabaseHelp.insertItem(
                                db,
                                XLuaLuaSetting.Table.name,
                                packet) :
                        DatabaseHelp.deleteItem(
                                SqlQuerySnake
                                    .create(db, XLuaLuaSetting.Table.name)
                                    .whereColumn("user", packet.getUser())
                                    .whereColumn("name", packet.getName()));

        return result;
    }

    public static boolean putSetting(Context context, String name, String value, boolean kill, XDataBase db) { return putSetting(context, 0, name, value, kill, db); }
    public static boolean putSetting(Context context, int user, String name, String value, boolean kill, XDataBase db) { return putSetting(context, user, "global", name, value, kill, db); }
    public static boolean putSetting(Context context, int user, String category, String name, String value, boolean kill, XDataBase db) {
        boolean result =
                DatabaseHelp.insertItem(
                        db,
                        XLuaLuaSetting.Table.name,
                        XLuaLuaSetting.create(user, category, name, value));
        if (!result && kill) {
            try {
                XLuaAppProvider.forceStop(context, category, user);
            }catch (Throwable e) {
                Log.e(TAG, "Failed to Kill user=" + user + "\n" + e);
            }
        }

        return result;
    }

    public static boolean putSetting(Context context, XLuaLuaSetting setting, boolean kill, XDataBase db) throws Throwable {
        Log.i(TAG, "[putSetting] " + setting);

        boolean result =
                setting.getValue() != null ?
                        DatabaseHelp.insertItem(db, XLuaLuaSetting.Table.name, setting) :
                        DatabaseHelp.deleteItem(SqlQuerySnake
                                .create(db, XLuaLuaSetting.Table.name)
                                .whereColumn("user", setting.getUser())
                                .whereColumn("name", setting.getName()));

        if (!result && kill)
            XLuaAppProvider.forceStop(context, setting.getCategory(), setting.getUser());

        return result;
    }

    public static boolean putSettings(Context context, XDataBase db, List<XLuaLuaSetting> settings) {
        return DatabaseHelp.insertItems(
                db,
                XLuaLuaSetting.Table.name,
                settings,
                prepareDatabaseTable(context, db));
    }

    public static boolean putSetting(Context context, XDataBase db, XLuaLuaSetting setting) {
        return DatabaseHelp.insertItem(
                db,
                XLuaLuaSetting.Table.name,
                setting,
                prepareDatabaseTable(context, db));
    }

    public static boolean prepareDatabaseTable(Context context, XDataBase db) {
        //if(context == null) return true;//Assume its handled
        return DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                XLuaLuaSetting.Table.name,
                XLuaLuaSetting.Table.columns,
                XLuaLuaSetting.class);
    }

    public static Collection<XLuaLuaSetting> getSettingsFromName(XDataBase db, String settingsName) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .whereColumn("name", settingsName)
                .queryAs(XLuaLuaSetting.class, true);
    }

    public static Collection<XLuaLuaSetting> getSettings(XDataBase db, int userId, String categoryName) { return getSettings(db, new XLuaSettingCategory(userId, categoryName)); }
    public static Collection<XLuaLuaSetting> getSettings(XDataBase db, XLuaSettingCategory category) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .whereColumns("user", "category")
                .whereColumnValues(Integer.toString(category.getUserId()), category.getName())
                .queryAs(XLuaLuaSetting.class, true);
    }

    public static Collection<XLuaLuaSetting> getSettingsByName(XDataBase db, int userId, String settingName) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .whereColumns("user", "name")
                .whereColumnValues(Integer.toString(userId), settingName)
                .queryAs(XLuaLuaSetting.class, true);
    }

    public static boolean getSettingBoolean(XDataBase db, String category, String settingName) { return getSettingBoolean(db, XUtil.getUserId(Process.myUid()), category, settingName); }
    public static boolean getSettingBoolean(XDataBase db,  int userId, String category, String settingName) {
        return Boolean.parseBoolean(getSettingValue(db, userId, category, settingName));
    }

    public static String getSettingValue(XDataBase db, XLuaLuaSetting setting) { return getSettingValue(db, setting.getUser(), setting.getCategory(), setting.getName()); }

    public static String getSettingValue(XDataBase db, String category, String settingName) { return getSettingValue(db, 0, category, settingName); }
    public static String getSettingValue(XDataBase db, int userId, String category, String settingName) {
        String v = SqlQuerySnake.
                create(db, XLuaLuaSetting.Table.name)
                .whereColumns("user", "category", "name")
                .whereColumnValues(Integer.toString(userId), category, settingName)
                .onlyReturnColumn("value")
                .queryGetFirstAs(XLuaLuaSetting.class, true)
                .getValue();

        if(v == null) {
            if(settingName.equals("theme")) {
                XLuaSettingPacket packet = new XLuaSettingPacket(userId, category, settingName);
                packet.setValue(DEFAULT_THEME);
                putSetting(db, packet);
                return DEFAULT_THEME;
            }
            else if(settingName.equals("collection")) {
                XLuaSettingPacket packet = new XLuaSettingPacket(userId, category, settingName);
                packet.setValue(DEFAULT_COLLECTIONS);
                putSetting(db, packet);
                return DEFAULT_COLLECTIONS;
            }
        }

        return v;
    }

    public static XLuaLuaSetting getSetting(XDataBase db, XLuaSettingCategory category, String settingName) { return getSetting(db, category.getUserId(), category.getName(), settingName);  }
    public static XLuaLuaSetting getSetting(XDataBase db, int userId, String category, String settingName) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .whereColumns("user", "category", "name")
                .whereColumnValues(Integer.toString(userId), category, settingName)
                .queryGetFirstAs(XLuaLuaSetting.class, true);
    }

    public static Collection<String> getCategoriesFromUID(XDataBase db, int userId) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .whereColumn("user", Integer.toString(userId))
                .queryAsStringList("category", true);
    }

    public static Collection<XLuaSettingCategory> getCategories(XDataBase db) {
        return SqlQuerySnake
                .create(db, XLuaLuaSetting.Table.name)
                .onlyReturnColumns("user", "category")
                .queryAll(XLuaSettingCategory.class, true);
    }

    public static Map<Integer, List<String>> getCategoriesFromUid(XDataBase db){
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
}
