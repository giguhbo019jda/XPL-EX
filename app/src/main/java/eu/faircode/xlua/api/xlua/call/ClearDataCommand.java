package eu.faircode.xlua.api.xlua.call;

import android.content.Context;
import android.os.Bundle;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.xlua.database.XLuaAppDatabase;
import eu.faircode.xlua.utilities.BundleUtil;

public class ClearDataCommand extends CallCommandHandler {
    public static ClearDataCommand create() { return new ClearDataCommand(); };
    public ClearDataCommand() {
        name = "clearData";
        requiresPermissionCheck = true;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        return BundleUtil.createResultStatus(
                XLuaAppDatabase.clearData(
                        commandData.getExtras().getInt("user"),
                        commandData.getDatabase()));
    }

    public static Bundle invoke(Context context, int userId) {
        return XProxyContent.luaCall(
                        context,
                        "clearData",
                        BundleUtil.createSingleInt("user", userId));
    }
}
