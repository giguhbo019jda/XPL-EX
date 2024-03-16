package eu.faircode.xlua.api.xlua.query;

import android.database.Cursor;
import android.os.Binder;
import android.os.Process;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.XUtil;
import eu.faircode.xlua.api.standard.QueryCommandHandler;
import eu.faircode.xlua.api.standard.command.QueryPacket;
import eu.faircode.xlua.api.hook.assignment.LuaAssignment;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;

public class GetLogCommand extends QueryCommandHandler {
    public static GetLogCommand create() { return new GetLogCommand(); };

    public GetLogCommand() {
        name = "getLog";
        requiresPermissionCheck = true;
    }

    @Override
    public Cursor handle(QueryPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());

        XDatabase db = commandData.getDatabase();
        if (commandData.getSelection() != null)
            throw new IllegalArgumentException("selection invalid");

        int userid = XUtil.getUserId(Binder.getCallingUid());
        int start = XUtil.getUserUid(userid, 0);
        int end = XUtil.getUserUid(userid, Process.LAST_APPLICATION_UID);

        SqlQuerySnake snake = SqlQuerySnake
                .create(db, LuaAssignment.Table.name)
                .onlyReturnColumns("package", "uid", "hook", "used", "old", "new")
                .whereColumn("restricted", "1")
                .whereColumn("uid", start, ">=")
                .whereColumn("uid", end, "<=")
                .orderBy("used DESC");

        db.readLock();
        Cursor c = snake.query();
        snake.clean(null);
        db.readUnlock();
        return c;
    }
}
