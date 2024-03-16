package eu.faircode.xlua.api.standard.command;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.standard.interfaces.ISerial;

public class CallPacket {
    private static final String TAG = "XLua.CallPacket";

    private final Context context;
    private final String method;
    private final String subMethod;
    private final Bundle extras;
    private final String packageName;
    private final XDatabase db;

    private boolean isVxp = false;

    public static CallPacket create(XC_MethodHook.MethodHookParam param, XDatabase database, String packageName) {
        try {
            String method = (String) param.args[0];
            String arg = (String) param.args[1];        //sub method being invoked like "getSetting"
            Bundle extras = (Bundle) param.args[2];

            Method mGetContext = param.thisObject.getClass().getMethod("getContext");
            Context context = (Context) mGetContext.invoke(param.thisObject);

            return new CallPacket(context, method, arg, extras, database, packageName);
        }catch (Exception e) {
            Log.e(TAG, "Failed to create call packet! " + e);
            return null;
        }
    }

    public boolean isVXP() { return this.isVxp; };
    public void setIsVXP(boolean isVxp) { this.isVxp = isVxp; }

    public CallPacket(Context context, String subMethod, Bundle extras, XDatabase db) { this(context, null, subMethod, extras, db, null); }
    public CallPacket(Context context, String methodPrefix, String subMethod, Bundle extras, XDatabase db) { this(context, methodPrefix, subMethod, extras, db, null); }
    public CallPacket(Context context, String methodPrefix, String subMethod, Bundle extras, XDatabase db, String packageName) {
        this.context = context;
        this.method = methodPrefix;
        this.subMethod = subMethod;
        this.extras = extras;
        this.db = db;
        this.packageName = packageName;
        this.isVxp = packageName == null;
    }


    public String getMethodPrefix() { return method;  }
    public Context getContext() {
        return context;
    }
    public String getSubMethod() { return subMethod; }
    public Bundle getExtras() {
        return extras;
    }
    public XDatabase getDatabase() {
        return db;
    }

    public String getSecretKey() {
        return extras.getString("sKey", null);
    }

    public <T extends ISerial> T read(Class<T> obj) throws IllegalAccessException, InstantiationException {
        T inst = obj.newInstance();
        inst.fromBundle(extras);
        return inst;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(subMethod != null) {
            sb.append("method=");
            sb.append(subMethod);
        }

        if(db != null) {
            sb.append(" db=");
            sb.append(db);
        }

        return sb.toString();
    }
}
