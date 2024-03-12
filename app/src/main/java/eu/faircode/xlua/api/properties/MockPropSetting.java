package eu.faircode.xlua.api.properties;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.props.XMockPropSetting;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;
import eu.faircode.xlua.api.standard.interfaces.IDBQuery;
import eu.faircode.xlua.api.standard.interfaces.IJsonSerial;
import eu.faircode.xlua.api.standard.interfaces.IPacket;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.ContentValuesUtil;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;
import eu.faircode.xlua.utilities.ParcelUtil;
import eu.faircode.xlua.utilities.StringUtil;

public class MockPropSetting extends MockPropMap implements IJsonSerial, IDBQuery, IPacket, Parcelable {
    public static final int PROP_NOTHING_COMMAND = -8;
    public static final int PROP_DELETE = -1;
    public static final int PROP_HIDE = 0x0;
    public static final int PROP_SKIP = 0x1;
    public static final int PROP_NULL = 0x2;
    public static final int PROP_GET_ALL = 0x6;
    public static final int PROP_GET_MODIFIED = 0x7;

    public static final int USER_PACKET_ONE = 0x0;
    public static final int USER_PACKET_TWO = 0x1;

    public static MockPropSetting create() { return new MockPropSetting(); }
    public static MockPropSetting create(String propName) { return new MockPropSetting(propName, null, null, null); }
    public static MockPropSetting create(String propName, Integer userId, String packageName) { return new MockPropSetting(propName, userId, packageName, null); }
    public static MockPropSetting create(String propName, Integer userId, String packageName, int value) { return new MockPropSetting(propName, userId, packageName, value); }
    public static MockPropSetting create(String propName, String settingName, Integer userId, String packageName, int value) { return new MockPropSetting(propName, settingName, userId, packageName, value); }

    protected Integer user;
    protected String category;
    protected Integer value;

    public MockPropSetting() { }
    public MockPropSetting(Parcel in) { fromParcel(in); }
    public MockPropSetting(ContentValues cv) { fromContentValues(cv); }
    public MockPropSetting(MockPropGroupHolder holder, String name, Integer value) { this(name, holder.getSettingName(), holder.getUser(), holder.getPackageName(), value); }
    public MockPropSetting(String name, Integer user, String category, Integer value) { this(name, null, user, category, value); }
    public MockPropSetting(String name, String settingName, Integer user, String category, Integer value) {
        setName(name);
        setSettingName(settingName);
        setUser(user);
        setCategory(category);
        setValue(value);
    }

    public Integer getUser() { return user; }
    public MockPropSetting setUser(Integer user) { if(user != null) this.user = user; return this; }

    public String getCategory() { return category; }
    public MockPropSetting setCategory(String category) { if(category != null) this.category = category; return this; }

    public Integer getValue() { return value; }
    public MockPropSetting setValue(Integer value) { if(value != null) this.value = value; return this; }

    public boolean isDelete() { return value == PROP_DELETE; }
    public boolean isSkip() { return value == PROP_SKIP; }
    public boolean isHide() { return value == PROP_HIDE; }
    public boolean isNULL() { return value == null || value == PROP_NULL; }
    public boolean isGetAll() { return value == PROP_GET_ALL; }
    public boolean isGlobal() {
        if(category == null)
            return true;

        return category.equalsIgnoreCase(XLuaSettingsDatabase.GLOBAL_NAMESPACE);
    }

    @Override
    public Bundle toBundle() {
        ensureIdentification();
        Bundle b = super.toBundle();
        if(this.user != null) b.putInt("user", this.user);
        if(this.category != null) b.putString("category", this.category);
        if(this.value != null) b.putInt("value", this.value);
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        this.user = BundleUtil.readInteger(b, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.category = BundleUtil.readString(b, "category", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
        super.fromBundle(b);
        this.value = BundleUtil.readInteger(b, "value", PROP_HIDE);
    }

    @Override
    public ContentValues createContentValues() {
        ensureIdentification();
        ContentValues cv = super.createContentValues();
        if(this.user != null) cv.put("user", this.user);
        if(this.category != null) cv.put("category", this.category);
        if(this.value != null) cv.put("value", this.value);
        return cv;
    }

    @Override
    public void fromContentValues(ContentValues contentValue) {
        this.user = ContentValuesUtil.getInteger(contentValue, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.category = ContentValuesUtil.getString(contentValue, "category", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
        super.fromContentValues(contentValue);
        this.value = ContentValuesUtil.getInteger(contentValue, "value", PROP_NULL);
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null;  }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromCursor(Cursor cursor) {
        this.user = CursorUtil.getInteger(cursor, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.category = CursorUtil.getString(cursor, "category", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
        super.fromCursor(cursor);
        this.value = CursorUtil.getInteger(cursor, "value", PROP_NULL);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void fromParcel(Parcel in) {
        this.user = in.readInt();
        this.category = in.readString();
        super.fromParcel(in);
        this.value = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ensureIdentification();
        dest.writeInt(this.user);
        dest.writeString(this.category);
        super.writeToParcel(dest, flags);

        if(this.value == null)
            this.value = PROP_NOTHING_COMMAND;

        dest.writeInt(this.value);
    }

    @Override
    public String toJSON() throws JSONException { return toJSONObject().toString(2); }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        //with json we dont need to WORRY about order in sense of like parcel
        ensureIdentification();
        JSONObject jRoot = super.toJSONObject();
        if(this.user != null) jRoot.put("user", this.user);
        if(this.category != null) jRoot.put("category", this.category);
        if(this.value != null) jRoot.put("value", this.value);
        return jRoot;
    }

    @Override
    public void fromJSONObject(JSONObject obj) throws JSONException {
        super.fromJSONObject(obj);
        this.user = JSONUtil.getInteger(obj, "user", XLuaSettingsDatabase.GLOBAL_USER);
        this.category = JSONUtil.getString(obj, "category", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
        this.value = JSONUtil.getInteger(obj, "value", PROP_HIDE);
    }

    public static final Parcelable.Creator<MockPropSetting> CREATOR = new Parcelable.Creator<MockPropSetting>() {
        @Override
        public MockPropSetting createFromParcel(Parcel source) {
            return new MockPropSetting(source);
        }

        @Override
        public MockPropSetting[] newArray(int size) {
            return new MockPropSetting[size];
        }
    };

    @Override
    public SqlQuerySnake createQuery(XDatabase db) {
        ensureIdentification();
        return SqlQuerySnake.create(db, XMockPropSetting.Table.name)
                .whereColumn("user", this.user)
                .whereColumn("category", this.category)
                .whereColumn("name", this.name);
    }

    @Override
    public int getSecretKey() { return 0; }

    @Override
    public void readSelectionArgs(String[] selection, int flags) {
        if(selection != null && selection.length > 0) {
            if(flags == USER_PACKET_ONE) {
                this.user = StringUtil.toInteger(selection[0], XLuaSettingsDatabase.GLOBAL_USER);
                if(selection.length > 1) {
                    this.category = selection[1];
                    if(selection.length > 2)
                        this.value = StringUtil.toInteger(selection[2], PROP_NULL);//ensure null
                }
            }else if(flags == USER_PACKET_TWO) {
                this.category = selection[0];
                if(selection.length > 1) {
                    this.user =  StringUtil.toInteger(selection[1], XLuaSettingsDatabase.GLOBAL_USER);
                    if(selection.length > 2)
                        this.value = StringUtil.toInteger(selection[2], PROP_NULL);//ensure null
                }
            }
        }
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
        return new StringBuilder(super.toString())
                .append(" user=")
                .append(user)
                .append(" pkg=")
                .append(category)
                .append(" value=")
                .append(value).toString();
    }

    public static class Table {
        //uhh we r not even reading the last two fields ?????????
        ////ye uh we dont init this
        //I forgot why we need this I see "prop_maps" help us map
        //wait no this is incase properties have custom behaviour
        //Uhh ye this class is jus off how did I make this


        //we should
        //
        //LOW           (user)
        //MID(super)    (name + settingName)
        //HIGH          (value)
        //
        public static final String name = "prop_settings";
        public static final LinkedHashMap<String, String> columns = new LinkedHashMap<String, String>() {{
            put("user", "INTEGER");
            put("packageName", "TEXT");
            put("name", "TEXT PRIMARY KEY");
            put("settingName", "TEXT");
            put("value", "INTEGER");
        }};
    }
}
