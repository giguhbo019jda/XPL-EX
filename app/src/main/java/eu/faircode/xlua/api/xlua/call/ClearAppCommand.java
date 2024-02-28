package eu.faircode.xlua.api.xlua.call;

import android.content.Context;
import android.os.Bundle;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.xlua.database.XLuaAppDatabase;
import eu.faircode.xlua.api.app.XLuaAppPacket;
import eu.faircode.xlua.utilities.BundleUtil;

public class ClearAppCommand extends CallCommandHandler {
    public static ClearAppCommand create() { return new ClearAppCommand(); };

    public ClearAppCommand() {
        name = "clearApp";
        requiresPermissionCheck = true;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        XLuaAppPacket packet = commandData.read(XLuaAppPacket.class);
        return BundleUtil.createResultStatus(
                XLuaAppDatabase.clearApp(
                        commandData.getContext(),
                        packet.packageName,
                        packet.uid,
                        packet.settings,
                        packet.kill,
                        commandData.getDatabase()));
    }

    public static Bundle invoke(Context context, String packageName, Integer uid, Boolean full) {
        XLuaAppPacket packet = new XLuaAppPacket();
        packet.packageName = packageName;
        packet.uid = uid;
        packet.settings = full;
        return invoke(context, packet);
    }

    public static Bundle invoke(Context context, XLuaAppPacket packet) {
        return XProxyContent.luaCall(
                context,
                "clearApp",
                packet.toBundle());
    }
}
