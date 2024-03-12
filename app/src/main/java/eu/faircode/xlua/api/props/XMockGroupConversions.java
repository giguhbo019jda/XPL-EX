package eu.faircode.xlua.api.props;

import android.database.Cursor;
import android.os.Parcel;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.faircode.xlua.DebugUtil;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.JSONUtil;

public class XMockGroupConversions {
    private static final String TAG = "XLua.MockGroupConversions";

    public static Collection<XMockPropGroup> fromCursor(Cursor cursor, boolean marshall, boolean close) {
        Collection<XMockPropGroup> ps = new ArrayList<>();
        try {
            if(marshall) {
                while (cursor != null && cursor.moveToNext()) {
                    byte[] marshaled = cursor.getBlob(0);
                    Parcel parcel = Parcel.obtain();
                    parcel.unmarshall(marshaled, 0, marshaled.length);
                    parcel.setDataPosition(0);
                    XMockPropGroup group = XMockPropGroup.CREATOR.createFromParcel(parcel);
                    parcel.recycle();
                    ps.add(group);
                }
            }else {

            }
        }finally {
            if(close) CursorUtil.closeCursor(cursor);
        }

        return ps;
    }

    public static List<XMockPropMapped> stringListToPropsList(ArrayList<String> props, String settingName, boolean isEnabled) {
        List<XMockPropMapped> pSettings = new ArrayList<>();
        for(String s : props)
            pSettings.add(new XMockPropMapped(s, settingName, isEnabled));

        return pSettings;
    }

    public static ArrayList<String> propsListToStringList(Collection<XMockPropMapped> settings) {
        ArrayList<String> props = new ArrayList<>();
        for(XMockPropMapped pSetting : settings)
            props.add(pSetting.getPropertyName());

        return props;
    }

    public static ArrayList<XMockPropMapped> getPropertiesFromJSON(JSONObject rootObject) {
        try {
            String settingName = rootObject.getString("settingName");
            boolean isEnabled = JSONUtil.getBoolean(rootObject, "enabled", true);
            return getPropertiesFromJSONArray(rootObject.getJSONArray("propNames"), settingName, isEnabled);
        }catch (JSONException ex) {
            Log.e(TAG, "JSON getJSONArray(propNames) or getString(settingName) failed: " + ex);
        }

        return new ArrayList<>();
    }

    public static ArrayList<XMockPropMapped> getPropertiesFromJSONArray(JSONArray jsonArray, String settingName, boolean enabled) {
        ArrayList<XMockPropMapped> props = new ArrayList<>();
        try {
            for(int i = 0; i < jsonArray.length(); i++)
                props.add(new XMockPropMapped(jsonArray.getString(i), settingName));

            if(DebugUtil.isDebug())
                Log.i(TAG, "[getPropertiesFromJSONArray] size=" + props.size());

        }catch (JSONException ex) {
            Log.e(TAG, "JSON array.getString(i) failed: " + ex);
        }

        return props;
    }
}
