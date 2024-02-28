package eu.faircode.xlua.api.xlua.query;

import android.content.Context;
import android.database.Cursor;
import android.os.Binder;

import java.util.Map;

import eu.faircode.xlua.XUtil;
import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.QueryCommandHandler;
import eu.faircode.xlua.api.standard.command.QueryPacket;
import eu.faircode.xlua.api.xlua.provider.XLuaAppProvider;
import eu.faircode.xlua.utilities.CursorUtil;

import eu.faircode.xlua.api.app.XLuaApp;


public class GetAppsCommand extends QueryCommandHandler {
    public static GetAppsCommand create(boolean marshall) { return new GetAppsCommand(marshall); };

    private boolean marshall;
    public GetAppsCommand(boolean marshall) {
        name = marshall ? "getApps" : "getApps2";
        this.marshall = marshall;
        requiresPermissionCheck = false;
    }

    @Override
    public Cursor handle(QueryPacket commandData) throws Throwable {
        Map<String, XLuaApp> apps =
                XLuaAppProvider.getApps(
                        commandData.getContext(),
                        XUtil.getUserId(Binder.getCallingUid()),
                        commandData.getDatabase(),
                        true,
                        true);
        return CursorUtil.toMatrixCursor(
                apps.values(),
                marshall,
                0);
    }

    public static Cursor invoke(Context context) { return XProxyContent.luaQuery(context, "getApps"); }
    public static Cursor invokeEx(Context context) { return XProxyContent.luaQuery(context, "getApps2"); }
}
