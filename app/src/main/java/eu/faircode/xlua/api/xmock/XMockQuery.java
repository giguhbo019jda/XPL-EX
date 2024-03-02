package eu.faircode.xlua.api.xmock;

import android.content.Context;
import android.util.Log;

import java.util.Collection;

import eu.faircode.xlua.api.config.XMockConfigConversions;
import eu.faircode.xlua.api.config.XMockConfig;
import eu.faircode.xlua.api.props.XMockGroupConversions;
import eu.faircode.xlua.api.props.XMockPropGroup;
import eu.faircode.xlua.api.xmock.query.GetMockConfigsCommand;
import eu.faircode.xlua.api.xmock.query.GetMockPropGroupsCommand;

public class XMockQuery {
    public static Collection<XMockPropGroup> getPropertiesGroup(Context context) { return getPropertiesGroup(context, "Global"); }
    public static Collection<XMockPropGroup> getPropertiesGroup(Context context, String packageName) { return getPropertiesGroup(context, packageName, 0); }
    public static Collection<XMockPropGroup> getPropertiesGroup(Context context, String packageName, int uid) { return getPropertiesGroup(context, true, packageName, uid); }
    public static Collection<XMockPropGroup> getPropertiesGroup(Context context, boolean marshall, String packageName, int uid) {
        return XMockGroupConversions.fromCursor(
                GetMockPropGroupsCommand.invoke(context, marshall, packageName, uid), marshall, true);
    }

    public static Collection<XMockConfig> getConfigs(Context context, boolean marshall) {
        Log.i("XLua.XMockQueryApi", "Getting the Configs");
        return XMockConfigConversions.configsFromCursor(
                GetMockConfigsCommand.invoke(context, marshall),
                marshall,
                true);
    }

    public static Collection<XMockConfig> getConfigs(Context context, boolean marshall, boolean orderSettings) {
        Log.i("XLua.XMockQueryApi", "Getting the Configs");
        Collection<XMockConfig> configs = XMockConfigConversions.configsFromCursor(
                GetMockConfigsCommand.invoke(context, marshall),
                marshall,
                true);

        for(XMockConfig conf : configs)
            conf.orderSettings(true);

        return configs;
    }
}
