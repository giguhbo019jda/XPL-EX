package eu.faircode.xlua.api.standard;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.robv.android.xposed.XC_MethodHook;
import eu.faircode.xlua.DebugUtil;
import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.standard.command.QueryPacket;
import eu.faircode.xlua.api.standard.command.TryCallWrapper;
import eu.faircode.xlua.api.standard.command.TryQueryWrapper;
import eu.faircode.xlua.api.standard.interfaces.IInitDatabase;
import eu.faircode.xlua.utilities.DatabasePathUtil;

public class CommanderService {
    private static final String TAG = "XLua.CommandHandler";

    private final HashMap<String, CallCommandHandler> calls = new HashMap<>();
    private final HashMap<String, QueryCommandHandler> queries = new HashMap<>();

    private final String commandPrefix;
    private final ExecutorService executor;
    private final IInitDatabase database;

    public CommanderService(String commandPrefix, IInitDatabase database, int maxThreads) {
        this.commandPrefix = commandPrefix;
        this.database = database;
        executor = Executors.newFixedThreadPool(maxThreads);//Executors.newSingleThreadExecutor();
    }

    public boolean isCommandPrefix(String methodOrCommandPrefix) { return commandPrefix.equalsIgnoreCase(methodOrCommandPrefix); }
    public XDatabase getDatabase(Context context) { return database.getDatabase(context); }

    public Bundle handleCall(CallPacket packet) {
        if(packet == null)
            return null;

        try {
            CallCommandHandler command = calls.get(packet.getSubMethod());
            if(command == null) {
                Log.e(TAG, "Call Command could not be found! " + packet);
                return null;
            }

            if(DebugUtil.isDebug())
                Log.i(TAG, "Found Command Handler for Call = " + command + " packet=" + packet);

            return executor.submit(TryCallWrapper.create(packet, command)).get();
        }catch (Exception e) {
            DatabasePathUtil.log("Failed to execute command call: " + packet + "\n" + e, true);
            return null;
        }
    }

    public Cursor handleQuery(QueryPacket packet) {
        if(packet == null)
            return null;

        try {
            QueryCommandHandler command = queries.get(packet.getSubMethod());
            if(command == null) {
                Log.e(TAG, "Query Command could not be found! " + packet);
                return null;
            }

            if(DebugUtil.isDebug())
                Log.i(TAG, "Found Command Handler for Query = " + command + " packet=" + packet);

            return executor.submit(TryQueryWrapper.create(packet, command)).get();
        }catch (Exception e) {
            DatabasePathUtil.log("Failed to execute command query: " + packet + "\n" + e, true);
            return null;
        }
    }

    public CallPacket createCallPacket(XC_MethodHook.MethodHookParam param, String packageName) {
        try {
            String method = (String)param.args[0];
            if(!method.equalsIgnoreCase(commandPrefix))
                return null;

            Method mGetContext = param.thisObject.getClass().getMethod("getContext");
            Context context = (Context) mGetContext.invoke(param.thisObject);

            String arg = (String) param.args[1];        //sub method being invoked like "getSetting"
            Bundle extras = (Bundle) param.args[2];
            return new CallPacket(context, method, arg, extras, database.getDatabase(context), packageName);
        }catch (Exception e) {
            DatabasePathUtil.log("Failed to generate a Call packet\n" + e, true);
            return null;
        }
    }

    public QueryPacket createQueryPacket(XC_MethodHook.MethodHookParam param, String packageName) {
        try {
            String[] projection = (String[]) param.args[1];
            if (!(projection != null && projection.length > 0 && projection[0] != null))
                return null;

            if(!projection[0].startsWith(commandPrefix))
                return null;

            String[] selection = (String[]) param.args[3];

            Method mGetContext = param.thisObject.getClass().getMethod("getContext");
            Context context = (Context) mGetContext.invoke(param.thisObject);

            String[] split = projection[0].split("\\.");
            String method = split[0];
            String arg = split[1];      //sub method being invoked like "getSettings"

            return new QueryPacket(context, method, arg, selection, database.getDatabase(context), packageName);
        }catch (Exception e) {
            DatabasePathUtil.log("Failed to generate a Call packet\n" + e, true);
            return null;
        }
    }

    public <TCall extends CallCommandHandler> CommanderService registerCall(Class<TCall> callCom) {
        try {
            TCall inst = callCom.newInstance();
            calls.put(inst.getName(), inst);
            return this;
        }catch (Exception e) {
            DatabasePathUtil.log("Failed to register Call Class Command: " + callCom.getName(), true);
            return this;
        }
    }

    public <TQuery extends QueryCommandHandler> CommanderService registerQuery(Class<TQuery> queryCom) { return registerQuery(queryCom, false); }

    public <TQuery extends QueryCommandHandler> CommanderService registerQuery(Class<TQuery> queryCom, boolean useMarshallAsWell) {
        try {
            TQuery inst = queryCom.newInstance();
            queries.put(inst.getName(), inst);
            if(useMarshallAsWell) {
                TQuery inst2 = queryCom.newInstance();
                inst2.setAsMarshallCommand();
                queries.put(inst2.getName(), inst2);
            }

            return this;
        }catch (Exception e) {
            DatabasePathUtil.log("Failed to register Query Class Command: " + queryCom.getName(), true);
            return this;
        }
    }
}
