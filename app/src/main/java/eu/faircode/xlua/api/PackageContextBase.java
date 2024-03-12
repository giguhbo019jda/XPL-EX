package eu.faircode.xlua.api;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import eu.faircode.xlua.api.settingsex.LuaSetting;
import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.ContentValuesUtil;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;

public class PackageContextBase implements IJsonSerial {
    protected Integer user;
    protected String category;

    public PackageContextBase() { }
    public PackageContextBase(Integer user, String category) {
        setUser(user);
        setCategory(category);
    }

    public Integer getUser() { return user; }
    public PackageContextBase setUser(Integer user) { if(user != null) this.user = user; return this; }

    public String getCategory() { return category; }
    public PackageContextBase setCategory(String category) { if(category != null) this.category = category; return this; }

    public boolean isGlobal() {
        if(category == null)
            return true;

        return category.equalsIgnoreCase(XLuaSettingsDatabase.GLOBAL_NAMESPACE);
    }

    @Override
    public Bundle toBundle() {
        ensureIdentification();
        Bundle b = new Bundle();
        if(this.user != null) b.putInt("user", this.user);
        if(this.category != null) b.putString("category", this.category);
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        this.user = BundleUtil.readInteger(b, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.category = BundleUtil.readString(b, "category", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
    }

    @Override
    public ContentValues createContentValues() {
        ensureIdentification();
        ContentValues cv = new ContentValues();
        if(this.user != null) cv.put("user", this.user);
        if(this.category != null) cv.put("category", this.category);
        return cv;
    }

    @Override
    public void fromContentValues(ContentValues contentValue) {
        this.user = ContentValuesUtil.getInteger(contentValue, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.category = ContentValuesUtil.getString(contentValue, "category", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null;  }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromCursor(Cursor cursor) {
        this.user = CursorUtil.getInteger(cursor, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.category = CursorUtil.getString(cursor, "category", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
    }

    @Override
    public void fromParcel(Parcel in) {
        this.user = in.readInt();
        this.category = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ensureIdentification();
        dest.writeInt(this.user);
        dest.writeString(this.category);
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        ensureIdentification();
        JSONObject jRoot = new JSONObject();
        if(this.user != null) jRoot.put("user", this.user);
        if(this.category != null) jRoot.put("category", this.category);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        this.user = JSONUtil.getInteger(obj, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.category = JSONUtil.getString(obj, "packageName", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
    }

    public void ensureIdentification() {
        if(this.user == null)
            this.user = XLuaSettingsDatabase.GLOBAL_USER;
        if(this.category == null)
            this.category = XLuaSettingsDatabase.GLOBAL_NAMESPACE;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" user=");
        sb.append(user);
        sb.append(" category=");
        sb.append(category);
        return sb.toString();
    }
}
