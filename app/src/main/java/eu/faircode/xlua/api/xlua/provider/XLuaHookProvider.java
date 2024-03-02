package eu.faircode.xlua.api.xlua.provider;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import eu.faircode.xlua.BuildConfig;
import eu.faircode.xlua.DebugUtil;
import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.XGlobalCore;
import eu.faircode.xlua.XUiGroup;
import eu.faircode.xlua.api.xlua.XLuaCall;

import eu.faircode.xlua.api.hook.XLuaHook;
import eu.faircode.xlua.api.xlua.call.GetVersionCommand;
import eu.faircode.xlua.api.xlua.database.XLuaHookDatabase;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;

public class XLuaHookProvider {
    private static final String TAG = "XLua.XHookProvider";

    public static List<String> getCollections(XDatabase db, int userId) {
        String value = XLuaSettingsDatabase.getSettingValue(db, userId, "global", "collection");
        List<String> result = new ArrayList<>();

        if(DebugUtil.isDebug())
            Log.i(TAG, "collection=" + value);

        if(value.contains(",")) Collections.addAll(result, value.split(","));
        else result.add(value);

        if(DebugUtil.isDebug())
            Log.i(TAG, "collection size=" + result.size());

        return result;
    }

    public static boolean putHook(Context context, String id, String definition, XDatabase database) throws Throwable {
        if (id == null) {
            Log.e("XHookIO.Convert", "ID Missing from Hook!");
            return false;
        }

        XLuaHook hook = null;
        if(definition != null) {
            hook = new XLuaHook();
            hook.fromJSONObject(new JSONObject(definition));
        }

        if(hook != null) {
            hook.validate();
            if(!id.equals(hook.getId())) {
                Log.e(TAG, "ID Mismatch: Given=" + id + "  Parsed=" + hook.getId());
                return false;
            }
        }

        if(!XGlobalCore.updateHookCache(context, hook, id)) {
            Log.e(TAG, "Failed at Updating Hook Cache, id=" + id);
            return false;
        }

        return XLuaHookDatabase.updateHook(database, hook, id);
    }

    public static boolean isAvailable(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, 0);
            Bundle b = GetVersionCommand.invoke(context);
            //return XLuaCallApi.getVersion(context) == pi.versionCode;
            return (b != null && pi.versionCode == b.getInt("version"));
        } catch (Throwable ex) {
            Log.e(TAG, Log.getStackTraceString(ex));
            XposedBridge.log(ex);
            return false;
        }
    }

    public static List<XUiGroup> getUiGroups(Context context) {
        List<String> groups = XLuaCall.getGroups(context);
        List<XUiGroup> uiGroups = new ArrayList<>();

        if(groups == null)
            return uiGroups;

        Resources res = context.getResources();
        for(String name : groups) {
            String g = name.toLowerCase().replaceAll("[^a-z]", "_");
            int id = res.getIdentifier("group_" + g, "string", context.getPackageName());

            XUiGroup group = new XUiGroup();
            group.name = name;
            group.title = (id > 0 ? res.getString(id) : name);
            uiGroups.add(group);
        }

        return uiGroups;
    }
}
