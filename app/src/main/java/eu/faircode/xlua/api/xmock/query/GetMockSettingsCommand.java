package eu.faircode.xlua.api.xmock.query;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.XUtil;
import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.settings.XLuaLuaSetting;
import eu.faircode.xlua.api.settingsex.LuaSettingsDatabase;
import eu.faircode.xlua.api.standard.QueryCommandHandler;
import eu.faircode.xlua.api.standard.command.QueryPacket;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;
import eu.faircode.xlua.api.xlua.query.GetSettingsCommand;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.StringUtil;

public class GetMockSettingsCommand extends QueryCommandHandler {
    public static GetMockSettingsCommand create() { return new GetMockSettingsCommand(true); }
    public static GetMockSettingsCommand create(boolean marshall) { return new GetMockSettingsCommand(marshall); }
    protected boolean marshall;

    public GetMockSettingsCommand(boolean marshall) {
        this.marshall = marshall;
        this.name = marshall ? "getMockSettings2" : "getMockSettings";
        this.requiresPermissionCheck = false;
    }

    @Override
    public Cursor handle(QueryPacket commandData) throws Throwable {
        String[] selection = commandData.getSelection();
        if(selection == null || selection.length == 0)
            return null;

        String packageName = selection[0];
        int uid = StringUtil.toInteger(selection[1], 0);
        int userid = XUtil.getUserId(uid);

        return CursorUtil.toMatrixCursor(
                LuaSettingsDatabase.getAllSettings(
                        commandData.getContext(),
                        commandData.getDatabase(),
                        userid,
                        packageName), marshall, 0);
    }

    public static Cursor invoke(Context context, boolean marshall, int user, String packageOrName) {
        SqlQuerySnake query = SqlQuerySnake.create()
                .whereColumn("packageName", packageOrName)
                .whereColumn("user", user);

        return XProxyContent.luaQuery(
                context,
                marshall ? "getMockSettings2" : "getMockSettings",
                query);
    }
}