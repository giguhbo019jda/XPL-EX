package eu.faircode.xlua.api.xmock.call;

import android.content.Context;
import android.os.Bundle;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.config.XMockConfig;
import eu.faircode.xlua.api.xmock.database.XMockConfigDatabase;
import eu.faircode.xlua.utilities.BundleUtil;

public class PutMockConfigCommand extends CallCommandHandler {
    public static PutMockConfigCommand create() { return new PutMockConfigCommand(); };
    public PutMockConfigCommand() {
        name = "putMockConfig";
        requiresPermissionCheck = true;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        XMockConfig config = commandData.read(XMockConfig.class);
        return BundleUtil.createResultStatus(
                XMockConfigDatabase.putMockConfig(
                        commandData.getContext(),
                        config,
                        commandData.getDatabase()));
    }

    public static Bundle invoke(Context context, XMockConfig packet) {
        return XProxyContent.mockCall(
                context,
                "putMockConfig",
                packet.toBundle());
    }
}
