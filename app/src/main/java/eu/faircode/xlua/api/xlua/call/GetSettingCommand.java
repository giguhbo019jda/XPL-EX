package eu.faircode.xlua.api.xlua.call;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import eu.faircode.xlua.BuildConfig;
import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.settingsex.LuaSettingPacket;
import eu.faircode.xlua.api.settingsex.LuaSettingsDatabase;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.api.settings.XLuaSettingPacket;
import eu.faircode.xlua.utilities.BundleUtil;


public class GetSettingCommand extends CallCommandHandler {
    public static GetSettingCommand create() { return new GetSettingCommand(); };
    public GetSettingCommand() {
        name = "getSetting";
        requiresPermissionCheck = false;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        LuaSettingPacket packet = commandData.read(LuaSettingPacket.class);

        if(BuildConfig.DEBUG)
            Log.i("XLua.GetSettingCommand", "handler packet=" + packet);

        if(packet.isGetObject()) {
            return LuaSettingsDatabase.getSetting(
                    commandData.getDatabase(),
                    packet.getName(),
                    packet.getUser(),
                    packet.getCategory()).toBundle();
        }else {
            return BundleUtil.createSingleString("value",
                    LuaSettingsDatabase.getSettingValueEx(
                            commandData.getContext(),
                            commandData.getDatabase(),
                            packet.getName(),
                            packet.getUser(),
                            packet.getCategory(),
                            packet.isEnsureGetValue()));
        }
    }

    public static Bundle invoke(Context context, LuaSettingPacket packet) {
        return XProxyContent.luaCall(
                        context,
                        "getSetting",
                        packet.toBundle());
    }
}
