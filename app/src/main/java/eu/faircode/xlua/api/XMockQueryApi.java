package eu.faircode.xlua.api;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import eu.faircode.xlua.api.objects.xmock.ConfigSetting;
import eu.faircode.xlua.api.objects.xmock.cpu.MockCpu;
import eu.faircode.xlua.api.objects.xmock.phone.MockConfigConversions;
import eu.faircode.xlua.api.objects.xmock.phone.MockPhoneConfig;
import eu.faircode.xlua.api.xmock.xquery.GetMockConfigsCommand;

public class XMockQueryApi {
    public static Collection<MockPhoneConfig> getConfigs(Context context, boolean marshall) {
        Log.i("XLua.XMockQueryApi", "Getting the Configs");
        return MockConfigConversions.configsFromCursor(
                GetMockConfigsCommand.invoke(context, marshall),
                marshall,
                true);
    }

    public static Collection<MockPhoneConfig> getConfigs(Context context, boolean marshall, boolean orderSettings) {
        Log.i("XLua.XMockQueryApi", "Getting the Configs");
        Collection<MockPhoneConfig> configs = MockConfigConversions.configsFromCursor(
                GetMockConfigsCommand.invoke(context, marshall),
                marshall,
                true);

        for(MockPhoneConfig conf : configs) {
            /*List<ConfigSetting> settings = new ArrayList<>(MockConfigConversions.hashMapToListSettings(conf.getSettings()));
            Collections.sort(settings, new Comparator<ConfigSetting>() {
                @Override
                public int compare(ConfigSetting o1, ConfigSetting o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });

            conf.setSettings(MockConfigConversions.listToHashMapSettings(settings, false));*/

            conf.orderSettings(true);
        }

        return configs;
    }
}
