package eu.faircode.xlua.api.objects.xlua.setting;

import android.os.Bundle;

import eu.faircode.xlua.api.objects.xlua.packets.SettingPacket;

public class xSettingConversions {
    public static XSetting fromBundle(Bundle b) {
        XSetting setting = new SettingPacket();
        setting.fromBundle(b);
        return setting;
    }
}
