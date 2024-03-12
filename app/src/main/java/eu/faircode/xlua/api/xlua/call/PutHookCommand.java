package eu.faircode.xlua.api.xlua.call;

import android.content.Context;
import android.os.Bundle;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.hook.XLuaHookPacket;
import eu.faircode.xlua.api.xlua.provider.XLuaHookProvider;
import eu.faircode.xlua.utilities.BundleUtil;

public class PutHookCommand extends CallCommandHandler {
    public static PutHookCommand create() { return new PutHookCommand(); };
    public PutHookCommand() {
        name = "putHook";
        requiresPermissionCheck = true;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        XLuaHookPacket packet = commandData.read(XLuaHookPacket.class);
        return BundleUtil.createResultStatus(
                XLuaHookProvider.putHook(
                    commandData.getContext(),
                    commandData.getDatabase(),
                    packet.getId(),
                    packet.getDefinition()));
    }

    public static Bundle invoke(Context context, String id, String definition) {
        XLuaHookPacket packet = new XLuaHookPacket(id, definition);
        return invoke(context, packet);
    }

    public static Bundle invoke(Context context, XLuaHookPacket packet) {
        return XProxyContent.luaCall(
                context,
                "putHook",
                packet.toBundle());
    }
}
