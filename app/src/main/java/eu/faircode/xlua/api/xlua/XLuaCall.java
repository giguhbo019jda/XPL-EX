package eu.faircode.xlua.api.xlua;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.faircode.xlua.BuildConfig;
import eu.faircode.xlua.XUtil;

import eu.faircode.xlua.api.XResult;
import eu.faircode.xlua.api.hook.XLuaHookPacket;
import eu.faircode.xlua.api.settings.XBulkSettingActionPacket;
import eu.faircode.xlua.api.settings.XMockMappedConversions;
import eu.faircode.xlua.api.settings.XMockMappedSetting;
import eu.faircode.xlua.api.settingsex.LuaSettingPacket;
import eu.faircode.xlua.api.settingsex.LuaSettingsDatabase;
import eu.faircode.xlua.api.xlua.call.AssignHooksCommand;
import eu.faircode.xlua.api.xlua.call.ClearAppCommand;
import eu.faircode.xlua.api.xlua.call.ClearDataCommand;
import eu.faircode.xlua.api.xlua.call.GetGroupsCommand;
import eu.faircode.xlua.api.xlua.call.GetSettingCommand;
import eu.faircode.xlua.api.xlua.call.GetVersionCommand;
import eu.faircode.xlua.api.xlua.call.InitAppCommand;
import eu.faircode.xlua.api.xlua.call.PutHookCommand;
import eu.faircode.xlua.api.xlua.call.PutSettingCommand;
import eu.faircode.xlua.api.xlua.call.ReportCommand;
import eu.faircode.xlua.api.app.XLuaAppPacket;
import eu.faircode.xlua.api.hook.assignment.XLuaAssignmentPacket;
import eu.faircode.xlua.api.settings.XLuaSettingPacket;
import eu.faircode.xlua.hooks.XReport;
import eu.faircode.xlua.utilities.BundleUtil;

public class XLuaCall {
    private static final String TAG = "XLua.XLuaCallApi";

    /*public static XResult setSettings(Context context, List<XMockMappedSetting> settings, int userId, String packageName, boolean kill) {
         return XResult.from(
                 PutSettingsCommand.invoke(context,
                         XMockMappedConversions.toBundleArray(settings, userId, packageName, kill)));
    }*/


    public static boolean assignHooks(Context context, XLuaAssignmentPacket packet) { return BundleUtil.readResultStatus(AssignHooksCommand.invoke(context, packet)); }
    public static boolean assignHooks(Context context, List<String> hookIds, String packageName, int uid, Boolean delete, Boolean kill) {
        return BundleUtil.readResultStatus(
                AssignHooksCommand.invoke(
                        context,
                        hookIds,
                        packageName,
                        uid,
                        delete,
                        kill));
    }

    public static boolean initApp(Context context, String packageName, Integer userId) { return initApp(context, packageName, userId, false); }
    public static boolean initApp(Context context, String packageName, Integer userId, Boolean kill) {
        return BundleUtil.readResultStatus(
                InitAppCommand.invoke(
                        context,
                        packageName,
                        userId,
                        kill));
    }

    public static boolean clearData(Context context, int userId) { return BundleUtil.readResultStatus(ClearDataCommand.invoke(context, userId)); }

    public static boolean clearApp(Context context, XLuaAppPacket packet) { return BundleUtil.readResultStatus(ClearAppCommand.invoke(context, packet)); }
    public static boolean clearApp(Context context, String packageName, int uid) { return clearApp(context, packageName, uid, false); }
    public static boolean clearApp(Context context, String packageName, int uid, boolean full) {
        return BundleUtil.readResultStatus(
                ClearAppCommand.invoke(
                        context,
                        packageName,
                        uid,
                        full));
    }

    public static List<String> getGroups(Context context) {
        return BundleUtil.readStringList(
                GetGroupsCommand.invoke(context),
                "groups",
                true);
    }

    /*public static XLuaSettingPacket getSetting(Context context, Integer userId, String category, String name) {
        XLuaSettingPacket setting = new XLuaSettingPacket(userId, category, name);
        setting.fromBundle(GetSettingCommand.invoke(context, setting));
        return setting;
    }*/

    public static boolean getSettingBoolean(Context context, String name) { return getSettingBoolean(context, LuaSettingsDatabase.GLOBAL_NAMESPACE, name); }
    public static boolean getSettingBoolean(Context context, String category, String name) { return getSettingBoolean(context, XUtil.getUserId(Process.myUid()), category, name); }
    public static boolean getSettingBoolean(Context context, Integer user, String category, String name) { return Boolean.parseBoolean(getSettingValue(context, user, category, name)); }
    public static boolean getSettingBoolean(Context context, Integer user, String name) { return Boolean.parseBoolean(getSettingValue(context, user, LuaSettingsDatabase.GLOBAL_NAMESPACE, name)); }

    public static String getSettingValue(Context context, String name) { return getSettingValue(context,LuaSettingsDatabase.GLOBAL_NAMESPACE, name); }
    public static String getSettingValue(Context context, String category, String name) { return getSettingValue(context, XUtil.getUserId(Process.myUid()), category, name); }
    public static String getSettingValue(Context context, Integer userId, String category, String name) {
        if(BuildConfig.DEBUG)
            Log.i(TAG, "[getSettingValue] user=" + userId + "  category=" + category + "  name=" + name);

        LuaSettingPacket packet = new LuaSettingPacket();
        packet.setUser(userId);
        packet.setCategory(category);
        packet.setName(name);
        packet.setCode(LuaSettingPacket.CODE_GET_VALUE);

        return BundleUtil.readString(GetSettingCommand.invoke(
                context, packet), "value");
    }

    public static boolean getSettingBooleanEx(Context context, String name) { return getSettingBooleanEx(context, LuaSettingsDatabase.GLOBAL_NAMESPACE, name); }
    public static boolean getSettingBooleanEx(Context context, String category, String name) { return getSettingBooleanEx(context, XUtil.getUserId(Process.myUid()), category, name); }
    public static boolean getSettingBooleanEx(Context context, Integer user, String category, String name) { return Boolean.parseBoolean(getSettingValueEx(context, user, category, name)); }
    public static boolean getSettingBooleanEx(Context context, Integer user, String name) { return Boolean.parseBoolean(getSettingValueEx(context, user, LuaSettingsDatabase.GLOBAL_NAMESPACE, name)); }

    public static String getSettingValueEx(Context context, String name) { return getSettingValueEx(context,LuaSettingsDatabase.GLOBAL_NAMESPACE, name); }
    public static String getSettingValueEx(Context context, String category, String name) { return getSettingValueEx(context, XUtil.getUserId(Process.myUid()), category, name); }
    public static String getSettingValueEx(Context context, Integer userId, String category, String name) {
        if(BuildConfig.DEBUG)
            Log.i(TAG, "[getSettingValue] user=" + userId + "  category=" + category + "  name=" + name);

        LuaSettingPacket packet = new LuaSettingPacket();
        packet.setUser(userId);
        packet.setCategory(category);
        packet.setName(name);
        packet.setCode(LuaSettingPacket.CODE_ENSURE_GET_VALUE);

        return BundleUtil.readString(GetSettingCommand.invoke(
                context, packet), "value");
    }

    public static List<String> getCollections(Context context) {
        List<String> collections = new ArrayList<>();
        String collectionValue = getSettingValue(context, "collection");
        if(collectionValue == null || collectionValue.isEmpty()) {
            collections.add("Privacy");
            collections.add("PrivacyEx");
            //Should not happen
        }
        else if(!collectionValue.contains(",")) collections.add(collectionValue);
        else Collections.addAll(collections, collectionValue.split(","));

        return collections;
    }

    public static String getTheme(Context context) {
        String theme = getSettingValue(context, "theme");
        if(theme == null)
            theme = "dark";

        return theme;
    }

    public static boolean report(Context context, XReport report) { return BundleUtil.readResultStatus(ReportCommand.invoke(context, report)); }
    public static int getVersion(Context context) { return BundleUtil.readInteger(GetVersionCommand.invoke(context), "version");}

    public static boolean putHook(Context context, String id, String definition) { return BundleUtil.readResultStatus(PutHookCommand.invoke(context, id, definition)); }
    public static boolean putHook(Context context, XLuaHookPacket packet) { return BundleUtil.readResultStatus(PutHookCommand.invoke(context, packet)); }

    /*public static boolean putSettingBoolean(Context context, String name, Boolean value) { return putSetting(context, "global", name, Boolean.toString(value)); }
    public static boolean putSettingBoolean(Context context, String category, String name, Boolean value) { return putSetting(context, category, name, Boolean.toString(value)); }
    public static boolean putSettingBoolean(Context context, Integer userId, String category, String name, Boolean value) { return putSetting(context, userId, category, name, Boolean.toString(value)); }


    public static boolean putSetting(Context context, XLuaSettingPacket packet) { return BundleUtil.readResultStatus(PutSettingCommand.invoke(context, packet)); }
    public static boolean putSetting(Context context, String name, String value) { return putSetting(context, XUtil.getUserId(Process.myUid()), "global", name, value); }
    public static boolean putSetting(Context context, String category, String name, String value) { return putSetting(context, XUtil.getUserId(Process.myUid()), category, name, value); }
    public static boolean putSetting(Context context, Integer userId, String category, String name, String value) {
        return BundleUtil.readResultStatus(
                PutSettingCommand.invoke(context, userId, category, name, value));
    }

    public static boolean putSetting(Context context, Integer userId, String category, String name, String value, Boolean kill) {
        return BundleUtil.readResultStatus(
                PutSettingCommand.invoke(context, userId, category, name, value, kill));
    }*/

    public static XResult putSetting(Context context, LuaSettingPacket packet) { return XResult.from(PutSettingCommand.invoke(context, packet)); }

    public static XResult deleteSetting(Context context, String settingName) { return deleteSetting(context, settingName, false, LuaSettingsDatabase.GLOBAL_USER, LuaSettingsDatabase.GLOBAL_NAMESPACE); }
    public static XResult deleteSetting(Context context, String settingName, int userId, String category)  { return deleteSetting(context, settingName, false, userId, category); }
    public static XResult deleteSetting(Context context, String settingName, boolean kill, int userId, String category) {
        return putSetting(context,
                LuaSettingPacket.create(userId, category, settingName, null, null, kill, LuaSettingPacket.CODE_DELETE));
    }

    public static XResult putSettingBoolean(Context context, String settingName, boolean value) { return putSetting(context, settingName, Boolean.toString(value) ,LuaSettingsDatabase.GLOBAL_USER, LuaSettingsDatabase.GLOBAL_NAMESPACE); }
    public static XResult putSettingBoolean(Context context, String settingName, boolean value, int userId, String category)  { return putSetting(context, settingName, Boolean.toString(value), false, userId, category); }
    public static XResult putSettingBoolean(Context context, String settingName, boolean value, boolean kill, int userId, String category) { return putSetting(context, settingName, Boolean.toString(value), kill, userId, category); }


    public static XResult putSetting(Context context, String settingName, String value) { return putSetting(context, settingName, value, false, LuaSettingsDatabase.GLOBAL_USER, LuaSettingsDatabase.GLOBAL_NAMESPACE); }
    public static XResult putSetting(Context context, String settingName, String value, int userId, String category)  { return putSetting(context, settingName, value, false, userId, category); }
    public static XResult putSetting(Context context, String settingName, String value, boolean kill, int userId, String category) {
        int code = value == null ? LuaSettingPacket.CODE_DELETE : LuaSettingPacket.CODE_INSERT_UPDATE;
        return putSetting(context,
                LuaSettingPacket.create(userId, category, settingName, value, null, kill, code));
    }

    //We will have XMockMappedSettings
    /*public static XResult putSettingsEx(Context context, Integer userId, String categoryOrPackage, List<XMockMappedSetting> pushSettings, boolean kill) {
        XBulkSettingActionPacket packet = XBulkSettingActionPacket.from(pushSettings, kill, userId, categoryOrPackage);
        Log.i(TAG, "packet sending size of = (" + packet.getSettingsMapped().size() + ")");
        return XResult.from(
                PutSettingsCommand.invoke(context, packet.toBundle()));
    }


    //public static XResult putSettingEx(Context context, String name, String value) { return putSettingEx(context, XUtil.getUserId(Process.myUid()), "global", name, value); }
    //public static XResult putSettingEx(Context context, String category, String name, String value) { return putSettingEx(context, XUtil.getUserId(Process.myUid()), category, name, value); }
    public static XResult putSettingEx(Context context, String name, Integer userId, String categoryOrPackage, String value, boolean kill) {
        //Integer user, String category, String name, String value, Boolean kill
        XLuaSettingPacket setting = new XLuaSettingPacket(userId, categoryOrPackage, name, value, kill);
        Log.i(TAG, "pitSettingEx=" + setting);
        return XResult.from(
                PutSettingCommand.invoke(context, setting));
    }*/

    /*public static XResult putSettingEx(Context context, XLuaLuaSetting packet) {
        //Integer user, String category, String name, String value, Boolean kill
        //XLuaSettingPacket setting = new XLuaSettingPacket(userId, categoryOrPackage, name, value, kill);
        Log.i(TAG, "pitSettingEx=" + setting);
        return XResult.from(
                PutSettingCommand.invoke(context, setting));
    }*/
}
