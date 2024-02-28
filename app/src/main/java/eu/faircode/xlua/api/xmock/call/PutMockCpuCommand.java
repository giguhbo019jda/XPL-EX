package eu.faircode.xlua.api.xmock.call;

import android.content.Context;
import android.os.Bundle;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.cpu.XMockCpu;
import eu.faircode.xlua.api.xmock.provider.XMockCpuProvider;
import eu.faircode.xlua.utilities.BundleUtil;

public class PutMockCpuCommand extends CallCommandHandler {
    public static PutMockCpuCommand create() { return new PutMockCpuCommand(); };
    public PutMockCpuCommand() {
        name = "putMockCpu";
        requiresPermissionCheck = true;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        XMockCpu packet = commandData.read(XMockCpu.class);
        return BundleUtil.createResultStatus(
                XMockCpuProvider.putCpuMap(
                        commandData.getDatabase(),
                        packet.getName(),
                        packet.getSelected()));
    }

    public static Bundle invoke(Context context, String cpuMapName, boolean selected) {
        XMockCpu map = new XMockCpu(cpuMapName, null, null, null, selected);
        return invoke(context, map);
    }

    public static Bundle invoke(Context context, XMockCpu packet) {
        return XProxyContent.mockCall(
                context,
                "putMockCpu",
                packet.toBundle());
    }
}
