package eu.faircode.xlua.api.xlua;

import android.content.Context;
import android.database.Cursor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import eu.faircode.xlua.api.app.XLuaApp;
import eu.faircode.xlua.api.app.XLuaAppConversions;
import eu.faircode.xlua.api.hook.XLuaHook;
import eu.faircode.xlua.api.hook.XLuaHookConversions;
import eu.faircode.xlua.api.xlua.query.GetAppsCommand;
import eu.faircode.xlua.api.xlua.query.GetAssignedHooksCommand;
import eu.faircode.xlua.api.xlua.query.GetHooksCommand;
import eu.faircode.xlua.api.xlua.query.GetSettingsCommand;
import eu.faircode.xlua.utilities.CursorUtil;

public class XLuaQuery {
    //Clean this class as well as security class
    public static Collection<XLuaApp> getApps(Context context, boolean marshall) { return XLuaAppConversions.fromCursor(GetAppsCommand.invoke(context, marshall), marshall, true); }
    public static Collection<XLuaHook> getHooks(Context context, boolean marshall) { return XLuaHookConversions.fromCursor(GetHooksCommand.invoke(context, marshall), marshall, true); }

    public static Map<String, String> getGlobalSettings(Context context, int uid) { return getSettings(context, "global", uid); }
    public static Map<String, String> getSettings(Context context, String packageOrName, int uid) {
        final Map<String, String> settings = new HashMap<>();
        Cursor c = GetSettingsCommand.invoke(context, packageOrName, uid);

        try {
            while (c != null && c.moveToNext())
                settings.put(c.getString(0), c.getString(1));
        }finally {
            //if(c != null) c.close();
            CursorUtil.closeCursor(c);
        }

        return settings;
    }

    public static Collection<XLuaHook> getAssignments(Context context, String packageName, int uid, boolean marshall) {
        return XLuaHookConversions.fromCursor(
                !marshall ?
                GetAssignedHooksCommand.invoke(context, packageName, uid) :
                GetAssignedHooksCommand.invokeEx(context, packageName, uid),
                marshall, true);
    }
}
