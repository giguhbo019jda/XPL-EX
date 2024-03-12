package eu.faircode.xlua.api.xlua.call;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.XResult;
import eu.faircode.xlua.api.settings.XLuaLuaSetting;
import eu.faircode.xlua.api.settingsex.LuaSettingPacket;
import eu.faircode.xlua.api.settingsex.LuaSettingsDatabase;
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
        LuaSettingPacket packet = commandData.read(LuaSettingPacket.class);
        if(packet.getDescription() == null) {
            return LuaSettingsDatabase.putSetting(
                    commandData.getContext(),
                    commandData.getDatabase(),
                    packet).toBundle();
        }else {
            return LuaSettingsDatabase.putDefaultMappedSetting(
                    commandData.getContext(),
                    commandData.getDatabase(),
                    packet).toBundle();
        }
    }

    public static Bundle invoke(Context context, LuaSettingPacket packet) {
        Log.i("XLua.PutSettingCommand", "packet=" + packet);
        return XProxyContent.luaCall(
                context,
                "putSetting",
                packet.toBundle());
    }
}
