package eu.faircode.xlua.api.xlua.call;

import android.content.Context;
import android.os.Bundle;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.XResult;
import eu.faircode.xlua.api.settings.XBulkSettingActionPacket;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;


/*public class PutSettingsCommand extends CallCommandHandler {
    public static PutSettingsCommand create() { return new PutSettingsCommand(); };
    public PutSettingsCommand() {
        name = "putSettings";
        requiresPermissionCheck = true;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        if(XBulkSettingActionPacket.isValidCallCommand(commandData, true))
            return XResult.from(false)
                    .setMethodName("putSettings")
                    .appendErrorMessage("Command Packet received was Invalid").toBundle();

        XBulkSettingActionPacket packet = XBulkSettingActionPacket.from(commandData);
        return XLuaSettingsDatabase.putSettings(commandData.getContext(), commandData.getDatabase(), packet).toBundle();
    }

    public static Bundle invoke(Context context, Bundle bundle) {
        return XProxyContent.luaCall(
                context,
                "putSettings",
                bundle);
    }
}*/
