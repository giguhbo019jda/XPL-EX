package eu.faircode.xlua.api.xmock;

import android.content.Context;
import android.util.Log;

import java.util.Collection;

import eu.faircode.xlua.api.config.XMockConfigConversions;
import eu.faircode.xlua.api.config.XMockConfig;
import eu.faircode.xlua.api.configs.MockConfig;
import eu.faircode.xlua.api.properties.MockPropSetting;
import eu.faircode.xlua.api.props.XMockGroupConversions;
import eu.faircode.xlua.api.props.XMockPropGroup;
import eu.faircode.xlua.api.settings.XMockMappedConversions;
import eu.faircode.xlua.api.settings.XMockMappedSetting;
import eu.faircode.xlua.api.settingsex.LuaSettingEx;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.api.xlua.query.GetSettingsCommand;
import eu.faircode.xlua.api.xmock.query.GetMockConfigsCommand;
import eu.faircode.xlua.api.xmock.query.GetMockPropertiesCommand;
import eu.faircode.xlua.api.xmock.query.GetMockSettingsCommand;
import eu.faircode.xlua.utilities.CursorUtil;

public class XMockQuery {
    public static Collection<MockPropSetting> getModifiedProperties(Context context) { return getModifiedProperties(context, true, XLuaSettingsDatabase.GLOBAL_USER, XLuaSettingsDatabase.GLOBAL_NAMESPACE); }
    public static Collection<MockPropSetting> getModifiedProperties(Context context, String packageName) { return getModifiedProperties(context, true, XLuaSettingsDatabase.GLOBAL_USER, packageName); }
    public static Collection<MockPropSetting> getModifiedProperties(Context context, boolean marshall, int userId, String packageName) {
        return CursorUtil.readCursorAs(
                GetMockPropertiesCommand.invoke(context, marshall, userId, packageName, false), marshall, MockPropSetting.class);
    }

    public static Collection<MockPropSetting> getAllProperties(Context context) { return getAllProperties(context, true, XLuaSettingsDatabase.GLOBAL_USER, XLuaSettingsDatabase.GLOBAL_NAMESPACE); }
    public static Collection<MockPropSetting> getAllProperties(Context context, String packageName) { return getAllProperties(context, true, XLuaSettingsDatabase.GLOBAL_USER, packageName); }
    public static Collection<MockPropSetting> getAllProperties(Context context, boolean marshall, int userId, String packageName) {
        return CursorUtil.readCursorAs(
                GetMockPropertiesCommand.invoke(context, marshall, userId, packageName, true), marshall, MockPropSetting.class);
    }

    public static Collection<MockConfig> getConfigs(Context context, boolean marshall, boolean orderSettings) {
        Log.i("XLua.XMockQuery", "Getting the Configs");
        /*Collection<XMockConfig> configs = XMockConfigConversions.configsFromCursor(
                GetMockConfigsCommand.invoke(context, marshall),
                marshall,
                true);*/


        Collection<MockConfig> configs = CursorUtil.readCursorAs(GetMockConfigsCommand.invoke(context, marshall) , true, MockConfig.class);

        /*if(orderSettings)
            for(XMockConfig conf : configs)
                conf.orderSettings(true);*/

        return configs;
    }

    public static Collection<LuaSettingEx> getAllSettings(Context context, boolean marshall, int user, String packageName) {
        return CursorUtil.readCursorAs(
                GetMockSettingsCommand.invoke(context, marshall, user, packageName),
                marshall,
                LuaSettingEx.class);
    }
}
