package eu.faircode.xlua.api.xlua.database;

import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.util.List;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.XGlobalCore;
import eu.faircode.xlua.api.settings.XLuaLuaSetting;

import eu.faircode.xlua.XUtil;
import eu.faircode.xlua.api.hook.assignment.XLuaAssignmentDataHelper;
import eu.faircode.xlua.api.hook.assignment.XLuaAssignment;
import eu.faircode.xlua.api.xlua.provider.XLuaAppProvider;
import eu.faircode.xlua.api.xlua.provider.XLuaHookProvider;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;

public class XLuaAppDatabase {
    private static final String TAG = "XLua.XAppDatabase";

    public static boolean initAppAssignments(
            Context context,
            String packageName,
            int uid,
            boolean kill,
            XDatabase db) {

        int userid = XUtil.getUserId(uid);
        List<String> collection = XLuaHookProvider.getCollections(context, db, userid);
        List<String> hookIds = XGlobalCore.getHookIds(packageName, collection);
        XLuaAssignmentDataHelper assignmentData = new XLuaAssignmentDataHelper(packageName, uid);

        try {
            if(!db.beginTransaction(true))
                return false;

            for(String hookId : hookIds) {
                if(!db.insert(XLuaAssignment.Table.name, assignmentData.createContentValues(hookId))) {
                    Log.e(TAG, "Error Inserting Assignment , hookId=" + hookId);
                    //throw new Throwable("Error inserting assignment");
                }
            }

            db.setTransactionSuccessful();
        }finally {
            db.endTransaction(true, false);
        }

        if (kill)
            XLuaAppProvider.forceStop(context, packageName, userid);

        Log.i(TAG, "Init app pkg=" + packageName + " uid=" + uid + " assignments=" + hookIds.size());
        return true;
    }

    public static boolean clearApp(
            Context context,
            String packageName,
            int uid,
            boolean full,
            boolean kill,
            XDatabase db)  {

        int userid = XUtil.getUserId(uid);
        SqlQuerySnake assSnake = SqlQuerySnake
                .create()
                .whereColumn("package", packageName)
                .whereColumn("uid", uid);

        try {
            if(!db.beginTransaction(true))
                return false;

            if(!db.delete(XLuaAssignment.Table.name, assSnake.getSelectionArgs(), assSnake.getSelectionCompareValues()))
                return false;

            if(full) {
                SqlQuerySnake setSnake = SqlQuerySnake
                        .create()
                        .whereColumn("user", userid)
                        .whereColumn("category", packageName);

                if(!db.delete(XLuaLuaSetting.Table.name, setSnake.getSelectionArgs(), setSnake.getSelectionCompareValues()))
                    return false;
            }

            if (kill)
                XLuaAppProvider.forceStop(context, packageName, userid);

            db.setTransactionSuccessful();
            return true;
        }finally {
            db.endTransaction(true, false);
        }
    }

    public static boolean clearData(int userid, XDatabase db)  {
        Log.i(TAG, "Clearing data user=" + userid);

        boolean result;

        if(userid == 0) {
            db.beginTransaction(true);
            result = db.delete(XLuaAssignment.Table.name) && db.delete(XLuaLuaSetting.Table.name);
            db.endTransaction(true, true);
        }else {
            int start = XUtil.getUserUid(userid, 0);
            int end = XUtil.getUserUid(userid, Process.LAST_APPLICATION_UID);

            result = DatabaseHelp.deleteItem(
                    SqlQuerySnake
                            .create(db, XLuaAssignment.Table.name)
                            .whereColumn("uid", start, ">=")
                            .whereColumn("uid", end, "<="));

            result = result && DatabaseHelp.deleteItem(
                    SqlQuerySnake
                            .create(db, XLuaLuaSetting.Table.name)
                            .whereColumn("user", userid));
        }

        return result;
    }
}
