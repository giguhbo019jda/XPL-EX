package eu.faircode.xlua.api.props;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.utilities.JSONUtil;
import eu.faircode.xlua.utilities.ParcelUtil;

public class XMockPropGroup extends XMockPropGroupBase implements IJsonSerial, Parcelable  {
    public XMockPropGroup() { }
    public XMockPropGroup(Parcel in) { fromParcel(in); }
    public XMockPropGroup(Bundle bundle) { fromBundle(bundle);}

    public XMockPropGroup(String settingName) { this(settingName, null); }
    public XMockPropGroup(String settingName, String value) { this(settingName, value, null); }
    public XMockPropGroup(String settingName, String value, List<XMockPropSetting> settings) { super(settingName, value, settings); }

    @Override
    public Bundle toBundle() {

        Bundle b = new Bundle();
        if(settingName != null) b.putString("settingName", settingName);
        if(value != null) b.putString("value", value);
        if(properties != null) {
            b.putBoolean("enabled", properties.get(0).isEnabled());
            b.putStringArrayList("propNames", XMockGroupConversions.propsListToStringList(properties));
        }
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        this.settingName = b.getString("settingName");
        this.value = b.getString("value");
        boolean isEnabled = b.getBoolean("enabled", true);
        this.properties = XMockGroupConversions.stringListToPropsList(Objects.requireNonNull(b.getStringArrayList("propNames")), this.settingName, isEnabled);
    }

    @Override
    public void fromContentValues(ContentValues contentValue) { }

    @Override
    public ContentValues createContentValues() { return null;}

    @Override
    public List<ContentValues> createContentValuesList() {
        List<ContentValues> cvs = new ArrayList<>();
        for(XMockPropSetting setting : properties)
            cvs.add(setting.createContentValues());

        return cvs;
    }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) {
        List<XMockPropSetting> props = new ArrayList<>();
        for(ContentValues cv : contentValues)
            props.add(new XMockPropSetting(cv));

        properties = props;
    }

    @Override
    public void fromCursor(Cursor cursor) { }

    @Override
    public void fromParcel(Parcel in) {
        this.settingName = in.readString();
        this.value = in.readString();
        boolean isEnabled = ParcelUtil.readBool(in);
        this.properties = XMockGroupConversions.stringListToPropsList(Objects.requireNonNull(in.createStringArrayList()), this.settingName, isEnabled);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.settingName);
        if(this.value == null)
            this.value = "";

        dest.writeString(this.value);
        ParcelUtil.writeBool(dest, properties.get(0).isEnabled());
        dest.writeStringList(XMockGroupConversions.propsListToStringList(properties));
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jRoot = new JSONObject();
        if(this.settingName != null) jRoot.put("settingName", settingName);
        //if(this.value != null) jRoot.put("value", value);
        if(this.properties != null) {
            boolean isEnabled = properties.get(0).isEnabled();
            jRoot.put("enabled", isEnabled);
            jRoot.put("propNames", new JSONArray(XMockGroupConversions.propsListToStringList(properties)));
        }
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        this.settingName = obj.getString("settingName");
        //this.value = JSONUtil.getString(obj, "value");
        //JSONUtil.getBoolean(obj, "enabled");
        this.properties = XMockGroupConversions.getPropertiesFromJSON(obj);
    }

    public static final Parcelable.Creator<XMockPropGroup> CREATOR = new Parcelable.Creator<XMockPropGroup>() {
        @Override
        public XMockPropGroup createFromParcel(Parcel source) {
            return new XMockPropGroup(source);
        }

        @Override
        public XMockPropGroup[] newArray(int size) {
            return new XMockPropGroup[size];
        }
    };
}
