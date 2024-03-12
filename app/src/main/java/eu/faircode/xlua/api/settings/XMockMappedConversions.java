package eu.faircode.xlua.api.settings;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.faircode.xlua.api.config.XMockConfig;
import eu.faircode.xlua.utilities.CursorUtil;

public class XMockMappedConversions {
    private static final String TAG = "XLua.XMockMappedConversions";

    public static List<XLuaLuaSetting> fromBundleArrayToLuaSettings(Bundle b, int user, String packageName) {
        try {
            Log.i(TAG, "Converting bundle Array to Lua Settings... user=" + user + " pgk=" + packageName);
            String[] settingNames = b.getStringArray("settingNames");
            String[] settingValues = b.getStringArray("settingValues");
            if((settingNames == null || settingValues == null) || (settingNames.length != settingValues.length))
                return new ArrayList<>();

            List<XLuaLuaSetting> settings = new ArrayList<>(settingNames.length);
            for(int i = 0; i < settingNames.length; i++) {
                XLuaLuaSetting set = new XLuaSettingPacket(user, packageName, settingNames[i], settingValues[i]);
                settings.add(set);
            }

            return settings;
        }catch (Exception e) {
            Log.e(TAG, "Failed to read bundle settings array! " + e);
        }

        return new ArrayList<>();
    }

    public static Bundle toBundleArray(List<XMockMappedSetting> settings, int userId, String packageName, boolean kill) {
        Bundle b = new Bundle();
        b.putInt("user", userId);
        b.putString("packageName", packageName);
        b.putBoolean("kill", kill);

        String[] settingNames = new String[settings.size()];
        String[] settingValues = new String[settings.size()];

        for(int i = 0 ; i < settings.size(); i++) {
            XMockMappedSetting set = settings.get(i);
            settingNames[i] = set.getName();
            settingValues[i] = set.getValue();
        }

        b.putStringArray("settingNames", settingNames);
        b.putStringArray("settingValues", settingValues);
        return b;
    }


    public static Collection<XMockMappedSetting> settingsFromCursor(Cursor cursor, boolean marshall, boolean close) {
        Collection<XMockMappedSetting> ps = new ArrayList<>();
        Log.i(TAG, "We are getting XMockMappedSetting (s) from cursor, marshall=" + marshall);
        int ij = 0;
        try {
            if(marshall) {
                while (cursor != null && cursor.moveToNext()) {
                    Log.d(TAG, "IJ [from] Index=(" + ij + ")");
                    ij++;
                    byte[] marshaled = cursor.getBlob(0);
                    Parcel parcel = Parcel.obtain();
                    parcel.unmarshall(marshaled, 0, marshaled.length);
                    parcel.setDataPosition(0);
                    XMockMappedSetting setting = XMockMappedSetting.CREATOR.createFromParcel(parcel);
                    Log.d(TAG, "Got setting = [" + setting + "]");
                    parcel.recycle();
                    ps.add(setting);
                }
            }else {

            }
        }finally {
            if(close) CursorUtil.closeCursor(cursor);
        }

        return ps;
    }
}
