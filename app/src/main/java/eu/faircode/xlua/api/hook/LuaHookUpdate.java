package eu.faircode.xlua.api.hook;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import eu.faircode.xlua.api.xstandard.interfaces.IJsonSerial;

public class LuaHookUpdate implements IJsonSerial {
    protected String newName;
    protected String oldName;
    protected String description;

    public String getNewId() { return this.newName; }
    public String getOldId() { return this.oldName; }
    public String getDescription() { return this.description; }
    public LuaHookUpdate() {  }

    @Override
    public ContentValues createContentValues() { return null; }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromContentValues(ContentValues contentValue) { }

    @Override
    public void fromCursor(Cursor cursor) { }

    @Override
    public Bundle toBundle() { return null; }

    @Override
    public void fromBundle(Bundle bundle) { }

    @Override
    public void fromParcel(Parcel in) { }

    @Override
    public void writeToParcel(Parcel dest, int flags) { }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = new JSONObject();
        jRoot.put("old", this.oldName);
        jRoot.put("new", this.newName);
        jRoot.put("description", this.description);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        if(obj != null) {
            this.oldName = obj.getString("old");
            this.newName = obj.getString("new");
            this.description = obj.getString("description");
        }
    }
}
