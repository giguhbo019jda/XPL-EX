package eu.faircode.xlua.api.xmock.call;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.cpu.XMockCpu;
import eu.faircode.xlua.api.xmock.provider.XMockCpuProvider;
import eu.faircode.xlua.utilities.BundleUtil;

public class GetMockCpuCommand extends CallCommandHandler {
    public static GetMockCpuCommand create() { return new GetMockCpuCommand(); };
    public GetMockCpuCommand() {
        name = "getMockCpu";
        requiresPermissionCheck = false;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        XMockCpu packet = commandData.read(XMockCpu.class);
        if(packet.getName() == null) {
            return BundleUtil.createFromISerial(
                    XMockCpuProvider.getSelectedCpuMap(
                            commandData.getContext(),
                            commandData.getDatabase()), true);
        }else {
            /*return BundleUtil.createFromISerial(
                    XMockCpuProvider.getSelectedCpuMap(
                            commandData.getContext(),
                            commandData.getDatabase()), true);

            return BundleUtil.createFromISerial(
                    XMockPropDatabase.getMockProp(
                            commandData.getDatabase(),
                            packet.getName()), true);*/
        }

        return null;
    }

    public static Bundle invoke(Context context, String name) {
        return XProxyContent.mockCall(
                context,
                "getMockCpu",
                BundleUtil.createSingleString("name", name));
    }

    public static Bundle invoke(Context context) {
        return XProxyContent.mockCall(
                context,
                "getMockCpu",
                new Bundle());
    }
}
