package eu.faircode.xlua.api.settings;

import android.os.Bundle;

public class XLuaSettingConversions {
    public static XLuaLuaSetting fromBundle(Bundle b) {
        XLuaLuaSetting setting = new XLuaSettingPacket();
        setting.fromBundle(b);
        return setting;
    }
}
