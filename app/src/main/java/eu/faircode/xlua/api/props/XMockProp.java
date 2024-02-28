package eu.faircode.xlua.api.props;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;

public class XMockProp extends XMockPropBase implements IJsonSerial, Parcelable {
    public XMockProp() { }
    public XMockProp(Parcel in) { fromParcel(in); }
    public XMockProp(Bundle bundle) { fromBundle(bundle); }
    public XMockProp(Cursor cursor) { fromCursor(cursor); }

    public XMockProp(String propertyName) { this(propertyName, 0, "Global", null); }
    public XMockProp(String propertyName, String packageName) { this(propertyName, 0, packageName, null); }
    public XMockProp(String propertyName, Integer userId, String packageName) { this(propertyName, userId, packageName, null); }
    public XMockProp(String propertyName, Integer userId, String packageName, String value) {
        super(propertyName, userId, packageName, value);
    }

    @Override
    public Bundle toBundle() {
        Bundle b = new Bundle();
        if(this.propertyName != null) b.putString("propertyName", propertyName);
        if(this.userId != null) b.putInt("user", userId);
        if(this.packageName != null) b.putString("packageName", packageName);
        if(this.value != null) b.putString("value", value);
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        this.propertyName = b.getString("propertyName");
        this.userId = b.getInt("user");
        this.packageName = b.getString("packageName");
        this.value = b.getString("value");
    }

    @Override
    public ContentValues createContentValues() { return null; }

    @Override
    public void fromContentValues(ContentValues contentValue) { }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromCursor(Cursor cursor) {
        this.propertyName = CursorUtil.getString(cursor, "propertyName");
        this.userId = CursorUtil.getInteger(cursor, "user");
        this.packageName = CursorUtil.getString(cursor, "packageName");
        this.value = CursorUtil.getString(cursor, "value");
    }

    @Override
    public void fromParcel(Parcel in) {
        this.propertyName = in.readString();
        this.userId = in.readInt();
        this.packageName = in.readString();
        this.value = in.readString();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(this.propertyName != null) dest.writeString(this.propertyName);
        if(this.userId != null) dest.writeInt(this.userId);
        if(this.packageName != null) dest.writeString(this.packageName);
        if(this.value != null) dest.writeString(this.value);
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = new JSONObject();
        if(this.propertyName != null) jRoot.put("propertyName", propertyName);
        if(this.userId != null) jRoot.put("user", userId);
        if(this.packageName != null) jRoot.put("packageName", packageName);
        if(this.value != null) jRoot.put("value", value);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        this.propertyName = obj.getString("propertyName");
        this.userId = obj.getInt("user");
        this.packageName = obj.getString("packageName");
        this.value = JSONUtil.getString(obj, "value");
    }

    public static final Parcelable.Creator<XMockProp> CREATOR = new Parcelable.Creator<XMockProp>() {
        @Override
        public XMockProp createFromParcel(Parcel source) {
            return new XMockProp(source);
        }

        @Override
        public XMockProp[] newArray(int size) {
            return new XMockProp[size];
        }
    };
}
