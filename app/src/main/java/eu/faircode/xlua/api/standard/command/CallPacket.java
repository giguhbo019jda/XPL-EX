package eu.faircode.xlua.api.standard.command;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.standard.interfaces.ISerial;

public class CallPacket {
    private Context context;
    private String method;
    private Bundle extras;
    private XDatabase db;

    public CallPacket(Context context, String method, Bundle extras, XDatabase db) {
        this.context = context;
        this.method = method;
        this.extras = extras;
        this.db = db;
    }

    public Context getContext() {
        return context;
    }

    public String getMethod() {
        return method;
    }

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
