package eu.faircode.xlua.api.xlua.query;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import eu.faircode.xlua.XGlobals;
import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.QueryCommandHandler;
import eu.faircode.xlua.api.standard.command.QueryPacket;
import eu.faircode.xlua.api.hook.XLuaHook;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.DatabasePathUtil;
import eu.faircode.xlua.utilities.ReflectUtil;

public class GetHooksCommand extends QueryCommandHandler {
    public static GetHooksCommand create(boolean marshall) { return new GetHooksCommand(marshall); };

    public GetHooksCommand() { this(false); }
    public GetHooksCommand(boolean marshall) {
        this.marshall = marshall;
        this.name = marshall ? "getHooks2" : "getHooks";
        requiresPermissionCheck = false;
    }

    @Override
    public Cursor handle(QueryPacket commandData) throws Throwable {
        String[] selection = commandData.getSelection();
        boolean all = (selection != null && selection.length == 1 && "all".equals(selection[0]));
        return CursorUtil.toMatrixCursor(
                XGlobals.getHooks(commandData.getContext(), commandData.getDatabase(), all),
                marshall,
                XLuaHook.FLAG_WITH_LUA);
    }

    public static Cursor invoke(Context context) { return XProxyContent.luaQuery(context, "getHooks"); }
    public static Cursor invokeEx(Context context) { return XProxyContent.luaQuery(context, "getHooks2"); }
}
