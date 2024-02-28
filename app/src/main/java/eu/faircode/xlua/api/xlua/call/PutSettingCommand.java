package eu.faircode.xlua.api.xlua.call;

import android.content.Context;
import android.os.Bundle;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.xlua.provider.XLuaAppProvider;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.api.settings.XLuaSettingPacket;
import eu.faircode.xlua.utilities.BundleUtil;

public class PutSettingCommand extends CallCommandHandler {
    public static PutSettingCommand create() { return new PutSettingCommand(); };

    public PutSettingCommand() {
        name = "putSetting";
        requiresPermissionCheck = true;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        XLuaSettingPacket packet = commandData.read(XLuaSettingPacket.class);

        boolean result = XLuaSettingsDatabase.putSetting(commandData.getDatabase(), packet);

        if(result && (packet.getKill() != null && packet.getKill()))
            XLuaAppProvider.forceStop(commandData.getContext(), packet.getCategory(), packet.getUser());

        return BundleUtil.createResultStatus(result);
    }

    public static Bundle invoke(Context context, Integer userId, String category, String name, String value) { return invoke(context, userId, category, name, value, false); }
    public static Bundle invoke(Context context, Integer userId, String category, String name, String value, Boolean kill) {
        XLuaSettingPacket packet = new XLuaSettingPacket(userId, category, name, value, kill);
        return invoke(context, packet);
    }

    public static Bundle invoke(Context context, XLuaSettingPacket packet) {
        return XProxyContent.luaCall(
                context,
                "putSetting",
                packet.toBundle());
    }
}
