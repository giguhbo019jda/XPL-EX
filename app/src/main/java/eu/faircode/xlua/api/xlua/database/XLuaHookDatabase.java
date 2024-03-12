package eu.faircode.xlua.api.xlua.database;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.XGlobalCore;
import eu.faircode.xlua.XUtil;
import eu.faircode.xlua.api.hook.assignment.XLuaAssignment;
import eu.faircode.xlua.api.hook.XLuaHook;
import eu.faircode.xlua.api.xlua.provider.XLuaAppProvider;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;
import eu.faircode.xlua.api.hook.assignment.XLuaAssignmentDataHelper;
import eu.faircode.xlua.api.hook.group.XLuaGroupDataHelper;
import eu.faircode.xlua.hooks.XReport;

public class XLuaHookDatabase {
    private static final String TAG = "XLua.XHookDatabase";

    public static boolean updateHook(XDatabase db, XLuaHook hook, String extraId) {
        Log.i(TAG, "updating Hook, id=" + extraId);
        if(hook == null || !hook.isBuiltin())
            return hook == null ?
                    deleteHook(db, extraId) :
                    putHook(db, hook);

        return true;
    }

    public static boolean putHook(XDatabase db, XLuaHook hook) {
        //Make sure we do not need to prepare db what not
        return !DatabaseHelp.insertItem(
                db,
                XLuaHook.Table.name,
                hook);
    }

    public static boolean deleteHook(XDatabase db, String id) {
        return !DatabaseHelp.deleteItem(db, SqlQuerySnake
                .create("hook")
                .whereColumn("id", id));
    }

    public static boolean assignHooks(Context context, List<String> hookIds, String packageName, int uid, boolean delete, boolean kill, XDatabase db) {
        //Assign Hook(s) to a App (package name, uid)
        List<String> groups = new ArrayList<>();
        XLuaAssignmentDataHelper assignmentData = new XLuaAssignmentDataHelper(packageName, uid);
        XLuaGroupDataHelper groupData = new XLuaGroupDataHelper(packageName, uid);

        try {
            if(!db.beginTransaction(true))
                return false;

            if(!db.hasTable(XLuaAssignment.Table.name)) {
                Log.e(TAG, "Table does not exist [" + XLuaAssignment.Table.name + "] in Database [" + db + "]");
                return false;
            }

            for(String hookId : hookIds) {
                XLuaHook hook = XGlobalCore.getHook(hookId);

                //Add its Group to the group list
                if (hook != null && !groups.contains(hook.getGroup()))
                    groups.add(hook.getGroup());

                if(delete) {
                    Log.i(TAG, packageName + ":" + uid + "/" + hookId + " deleted");
                    if(!db.delete(XLuaAssignment.Table.name, assignmentData.getSelectionArgs(), assignmentData.createValueArgs(hookId))) {
                        Log.e(TAG, "Failed to Delete Assignment ID=" + hookId);
                        //return false;
                    }
                }else {
                    Log.i(TAG, packageName + ":" + uid + "/" + hookId + " added");
                    if(!db.insert(XLuaAssignment.Table.name, assignmentData.createContentValues(hookId))) {
                        Log.e(TAG, "Failed to Insert Assignment ID=" + hookId);
                        //return false , keep going ???
                    }
                }
            }

            if (!delete)
                for (String group : groups) {
                    if(!db.delete("`group`", groupData.getSelectionArgs(), groupData.createValueArgs(group))) {
                        Log.e(TAG, "Failed to Delete Group=" + group);
                        //return false;
                    }
                }

            db.setTransactionSuccessful();
        }finally {
            db.endTransaction(true, false);
        }

        if (kill)
            XLuaAppProvider.forceStop(context, packageName, XUtil.getUserId(uid));

        return true;
    }

    public static long report(XReport report, XLuaHook hook, XDatabase db) {
        Log.i(TAG , "Updating Assignment: " + report);

        //Update Assignment , make it a function ?
        long used = -1;
        if(!DatabaseHelp.updateItem(db, XLuaAssignment.Table.name, report.generateQuery(), report))
            Log.w(TAG, "Error updating Assignment: " + report);

        //Update Group
        if(hook != null && report.event.equals("use") && report.getRestricted() == 1 && report.getNotify(db)) {
            used = SqlQuerySnake
                    .create(db , "`group`")
                    .whereColumns("package", "uid", "name")
                    .whereColumnValues(report.packageName, Integer.toString(report.uid), hook.getGroup())
                    .queryGetFirstLong("used", true);

            //ahh fix this ugly code
            //dupes too many standardize query snake
            if(!DatabaseHelp.insertItem(db, "`group`",  report.createGroupObject(hook, used)))
                Log.e(TAG, "Error inserting group: " + report);
        }

        return used;
    }
}
