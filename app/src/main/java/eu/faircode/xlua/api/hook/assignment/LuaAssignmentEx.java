package eu.faircode.xlua.api.hook.assignment;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import eu.faircode.xlua.api.hook.XLuaHook;
import eu.faircode.xlua.api.xstandard.interfaces.IDBSerial;
import eu.faircode.xlua.api.xstandard.interfaces.IJsonSerial;
import eu.faircode.xlua.api.xstandard.interfaces.ISerial;
import eu.faircode.xlua.utilities.CursorUtil;

public class LuaAssignmentEx implements ISerial, IDBSerial, IJsonSerial, Parcelable {
    protected String packageName;
    protected int uid;
    protected long installed = -1;
    protected String hookId;
    protected long used = -1;
    protected Boolean restricted = false;
    protected String exception;
    protected String old;
    protected String nNew;

    public LuaAssignmentEx() { }
    public LuaAssignmentEx(Parcel p) { fromParcel(p); }

    public String getPackageName() { return this.packageName; }
    public int getUid() { return this.uid; }
    public String getHookId() { return this.hookId; }
    public long getInstalled() { return this.installed; }
    public long getUsed() { return this.used; }
    public boolean getRestricted() { return this.restricted; }
    public String getException() { return this.exception; }
    public String getOld() { return this.old; }
    public String getNew() { return this.nNew; }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("package", this.packageName);
        cv.put("uid", this.uid);
        cv.put("hook", this.hookId);
        cv.put("installed", this.installed);
        cv.put("used", this.used);
        cv.put("restricted", this.restricted ? 1 : 0);
        cv.put("exception", this.exception);
        cv.put("old", this.old);
        cv.put("new", this.nNew);
        return cv;
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromContentValues(ContentValues contentValue) { }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            this.packageName = CursorUtil.getString(cursor,"package");
            this.uid = CursorUtil.getInteger(cursor, "uid");
            this.hookId = CursorUtil.getString(cursor, "hook");
            this.installed = CursorUtil.getLong(cursor, "installed", (long)-1);
            this.used = CursorUtil.getLong(cursor, "used", (long) -1);
            this.restricted = CursorUtil.getBoolean(cursor, "restricted");
            this.exception = CursorUtil.getString(cursor, "exception");
            this.old = CursorUtil.getString(cursor, "old");
            this.nNew = CursorUtil.getString(cursor, "new");
        }
    }

    @Override
    public Bundle toBundle() { return null; }

    @Override
    public void fromBundle(Bundle bundle) { }

    @Override
    public void fromParcel(Parcel in) { }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) { }

    public static final Parcelable.Creator<LuaAssignmentEx> CREATOR = new Parcelable.Creator<LuaAssignmentEx>() {
        @Override
        public LuaAssignmentEx createFromParcel(Parcel source) { return new LuaAssignmentEx(source); }

        @Override
        public LuaAssignmentEx[] newArray(int size) {
            return new LuaAssignmentEx[size];
        }
    };

    @Override
    public String toJSON() throws JSONException {
        return toJSONObject().toString(2);
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        /*JSONObject jRoot = new JSONObject();
        jRoot.put("hook", this.hook.toJSONObject());
        jRoot.put("installed", this.installed);
        jRoot.put("used", this.used);
        jRoot.put("restricted", this.restricted);
        jRoot.put("exception", this.exception);
        return jRoot;*/
        return null;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        //XLuaHook hook = new XLuaHook();
        //hook.fromJSONObject(obj.getJSONObject("hook"));
        //this.hook = hook;
        //this.installed = obj.getLong("installed");
        //this.used = obj.getLong("used");
        //this.restricted = obj.getBoolean("restricted");
        //this.exception = (obj.has("exception") ? obj.getString("exception") : null);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LuaAssignmentEx))
            return false;
        LuaAssignmentEx other = (LuaAssignmentEx) obj;
        return this.hookId.equals(other.hookId);
    }

    @Override
    public int hashCode() { return this.hookId.hashCode(); }
}
