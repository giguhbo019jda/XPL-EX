package eu.faircode.xlua.api.xlua.call;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;

import eu.faircode.xlua.BuildConfig;
import eu.faircode.xlua.XGlobals;
import eu.faircode.xlua.XNotify;
import eu.faircode.xlua.XUtil;
import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.hook.XLuaHook;
import eu.faircode.xlua.api.xlua.database.XLuaHookDatabase;
import eu.faircode.xlua.hooks.XReport;
import eu.faircode.xlua.utilities.BundleUtil;

public class ReportCommand extends CallCommandHandler {
    private static final String TAG = "XLua.ReportCommand";

    @SuppressWarnings("unused")
    public ReportCommand() {
        this.name = "report";
        this.requiresPermissionCheck = false;
        this.requiresSingleThread = true;
    }

    @Override
    @SuppressLint("MissingPermission")
    public Bundle handle(CallPacket commandData) throws Throwable {
        XReport report = new XReport(commandData.getExtras());
        if(report.uid !=  Binder.getCallingUid()) {
            Log.e(TAG, "Calling ID is not matched with Report User ID! " + report.uid);
            throw new SecurityException();//Ok how the fuck this can throw exception without a throws clause this JAVA is fucking dumb worst language
        }

        XLuaHook hook = XGlobals.getHook(report.hookId);
        long used = XLuaHookDatabase.report(report, hook, commandData.getDatabase());

        long identity = Binder.clearCallingIdentity();
        try {
            Context ctx = XUtil.createContextForUser(commandData.getContext(), report.getUserId());
            PackageManager pm = ctx.getPackageManager();
            Resources resources = pm.getResourcesForApplication(BuildConfig.APPLICATION_ID);

            boolean notify = report.getNotify(commandData.getDatabase());
            Log.i(TAG, "hook is null ? =" + (hook == null) + " use=" + report.event.equals("use") + " get restricted=" + report.getRestricted() + " report notify=" + notify + " used=" + used);

            if(hook != null) {
                Log.i(TAG, "hook do notify ? =" + hook.doNotify());
            }

            //Notify Usage
            if(hook != null && report.event.equals("use") && report.getRestricted() == 1
                    && (hook.doNotify() || (report.getNotify(commandData.getDatabase()) && used < 0))) {

                Log.i(TAG, "Usage notification invoking...");
                Notification.Builder builder = XNotify.buildUsageNotification(ctx, hook, report, pm, resources);
                XUtil.notifyAsUser(ctx, "xlua_use_" + hook.getGroup(), report.uid, builder.build(), report.getUserId());
            }

            //Notify Exception
            if(report.data.containsKey("exception")) {
                Notification.Builder builder = XNotify.buildExceptionNotification(ctx, report, pm, resources);
                XUtil.notifyAsUser(ctx, "xlua_exception", report.uid, builder.build(), report.getUserId());
            }
        }catch (Throwable e) {
            Log.e(TAG, "Internal Error for Report: \n" + e + "\n" + Log.getStackTraceString(e));
        }finally {
            Binder.restoreCallingIdentity(identity);
        }
        return BundleUtil.createResultStatus(true);
    }

    public static Bundle invoke(Context context, XReport report) {
        return XProxyContent.luaCall(
                context,
                "report",
                report.toBundle());
    }
}
