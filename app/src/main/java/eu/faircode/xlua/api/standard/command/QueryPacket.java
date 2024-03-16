package eu.faircode.xlua.api.standard.command;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.standard.interfaces.IPacket;
import eu.faircode.xlua.api.standard.interfaces.IUserPacket;

public class QueryPacket {
    private static final String TAG = "XLua.QueryPacket";

    private final Context context;
    private final String methodPrefix;
    private final String subMethod;
    private final String[] selection;
    private final XDatabase db;
    private final String packageName;

    private boolean isVxp = false;

    public QueryPacket(Context context, String methodPrefix, String subMethod, String[] selection, XDatabase db) { this(context, methodPrefix, subMethod, selection, db, null); }
    public QueryPacket(Context context, String methodPrefix, String subMethod, String[] selection, XDatabase db, String packageName) {
        this.context = context;
        this.subMethod = subMethod;
        this.selection = selection;
        this.db = db;
        this.methodPrefix = methodPrefix;
        this.packageName = packageName;
        this.isVxp = packageName == null;
    }

    public boolean isVXP() { return this.isVxp; };
    public void setIsVXP(boolean isVxp) { this.isVxp = isVxp; }

    public String getPackageName() { return this.packageName; }
    public Context getContext() { return this.context; }
    public String getMethodPrefix() { return this.methodPrefix; }
    public String getSubMethod() { return this.subMethod; }
    public String[] getSelection() { return this.selection; }
    public XDatabase getDatabase() { return this.db; }


    public <T extends IUserPacket> T readFrom(Class<T> classType, int flags) {
        try {
            T itm = classType.newInstance();
            itm.readSelectionArgsFromQuery(selection, flags);
            return itm;
        }catch (Exception e) {
            Log.e(TAG, "Failed to read Selections args! " + e);
            return null;
        }
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
