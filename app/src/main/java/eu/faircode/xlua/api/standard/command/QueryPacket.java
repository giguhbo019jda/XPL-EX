package eu.faircode.xlua.api.standard.command;

import android.content.Context;

import androidx.annotation.NonNull;

import eu.faircode.xlua.XDatabase;

public class QueryPacket {
    private Context context;
    private String method;
    private String[] selection;
    private XDatabase db;

    public QueryPacket(Context context, String method, String[] selection, XDatabase db) {
        this.context = context;
        this.method = method;
        this.selection = selection;
        this.db = db;
    }

    public Context getContext() { return this.context; }
    public String getMethod() { return this.method; }
    public String[] getSelection() { return this.selection; }
    public XDatabase getDatabase() { return this.db; }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(method != null) {
            sb.append("method=");
            sb.append(method);
        }

        if(db != null) {
            sb.append(" db=");
            sb.append(db);
        }

        return sb.toString();
    }
}
