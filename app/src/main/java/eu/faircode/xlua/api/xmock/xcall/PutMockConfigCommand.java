package eu.faircode.xlua.api.xmock.xcall;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.objects.CallCommandHandler;
import eu.faircode.xlua.api.objects.CallPacket;
import eu.faircode.xlua.api.objects.xmock.cpu.MockCpu;
import eu.faircode.xlua.api.objects.xmock.phone.MockPhoneConfig;
import eu.faircode.xlua.api.xmock.XMockCpuProvider;
import eu.faircode.xlua.api.xmock.XMockPhoneDatabase;
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
        Log.i("XLua.PutMockConfigCommand", "Putting Config");
        MockPhoneConfig config = commandData.read(MockPhoneConfig.class);
        Log.i("XLua.PutMockConfigCommand", "Putting Config=" + config + " Size=" + config.getSettings().size());
        return BundleUtil.createResultStatus(
                XMockPhoneDatabase.putMockConfig(
                        commandData.getContext(),
                        config,
                        commandData.getDatabase()));
    }

    public static Bundle invoke(Context context, MockPhoneConfig packet) {
        return XProxyContent.mockCall(
                context,
                "putMockConfig",
                packet.toBundle());
    }
}
