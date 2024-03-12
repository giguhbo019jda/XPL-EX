package eu.faircode.xlua.api.settings;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;


import eu.faircode.xlua.api.standard.interfaces.IDBSerial;
import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.api.standard.interfaces.ISerial;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.StringUtil;

public class XLuaLuaSetting extends XLuaSettingBase implements ISerial, IDBSerial, IJsonSerial, Parcelable {
    public static XLuaLuaSetting create(Integer user, String category, String name, String value) { return new XLuaSettingPacket(user, category, name, value); }

    public XLuaLuaSetting() { }
    public XLuaLuaSetting(Parcel in) { fromParcel(in); }
    public XLuaLuaSetting(Integer user, String category, String name) { super(user, category, name, null); }
    public XLuaLuaSetting(Integer user, String category, String name, String value) { super(user, category, name, value); }

    public boolean isDeleteAction() { return !StringUtil.isValidString(value); }

    @Override
    public Bundle toBundle() {
        Log.w("XLua.XLuaLuaSetting", " toBundle (putSetting) s=" + toString());
        Bundle b = new Bundle();
        if(user != null) b.putInt("user", user);
        if(category != null) b.putString("category", category);
        if(name != null) b.putString("name", name);
        if(value != null) b.putString("value", value);

        //if(kill != null) b.putBoolean("kill", kill);
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        if(b != null) {
            user = b.getInt("user");
            category = b.getString("category");
            name = b.getString("name");
            value = b.getString("value");

            Log.w("XLua.XLuaLuaSetting", " fromBundle [putSetting] s=" + toString());

            //if(b.containsKey("kill")) kill = b.getBoolean("kill");
        }
    }

    @Override
    public void fromParcel(Parcel in) {
        if(in != null) {
            this.user = in.readInt();
            this.category = in.readString();
            this.name = in.readString();
            this.value = in.readString();
        }
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(this.user != null) dest.writeInt(this.user);
        if(this.category != null) dest.writeString(this.category);
        if(this.name != null) dest.writeString(this.name);
        if(this.value != null) dest.writeString(this.value);
    }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            this.user = CursorUtil.getInteger(cursor, "user");
            this.category = CursorUtil.getString(cursor, "category");
            this.name = CursorUtil.getString(cursor, "name");
            this.value = CursorUtil.getString(cursor, "value");
            if(this.value == null) {
                int v = cursor.getColumnIndex("value");
                Log.i("XLua.xSetting.fromCursor", "oops is null ? value, here is my index=" + v);
                if(v != -1) {
                    String vv = cursor.getString(v);
                    Log.i("XLua.xSetting.fromCursor", "Got the value ...." + vv);
                }
            }
        }

        //int killIx = cursor.getColumnIndex("kill");
        //this.kill = killIx == -1 ? null : cursor.getInt(killIx);
    }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        if(user != null) cv.put("user", user);
        if(category != null) cv.put("category", category);
        if(name != null) cv.put("name", name);
        if(value != null) cv.put("value", value);
        return cv;
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromContentValues(ContentValues contentValue) { }

    public static final Parcelable.Creator<XLuaLuaSetting> CREATOR = new Parcelable.Creator<XLuaLuaSetting>() {
        @Override
        public XLuaLuaSetting createFromParcel(Parcel source) {
            return new XLuaLuaSetting(source);
        }

        @Override
        public XLuaLuaSetting[] newArray(int size) {
            return new XLuaLuaSetting[size];
        }
    };

    @Override
    public String toJSON() throws JSONException { return null; }

    @Override
    public JSONObject toJSONObject() throws JSONException { return null; }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException { }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(name != null) {
            sb.append("name=");
            sb.append(name);
        }

        if(value != null) {
            sb.append(" value=");
            sb.append(value);
        }

        if(category != null) {
            sb.append(" category=");
            sb.append(category);
        }

        if(user != null) {
            sb.append(" user=");
            sb.append(user);
        }

        return sb.toString();
    }

    public static boolean isValid(XLuaLuaSetting setting) {
        if(setting == null)
            return false;
        if(setting.user == null)
            setting.user = 0;
        if(!StringUtil.isValidString(setting.category))
            setting.category = "Global";

        return StringUtil.isValidString(setting.name);
    }


    public static class Table {
        public static final String name = "setting";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("user", "INTEGER");
            put("category", "TEXT");
            put("name", "TEXT");
            put("value", "TEXT");
        }};
    }
}
