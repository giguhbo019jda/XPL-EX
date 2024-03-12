package eu.faircode.xlua.api.xmock.call;

import android.content.Context;
import android.os.Bundle;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.props.XMockPropMapped;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.xmock.provider.XMockPropertiesProvider;
import eu.faircode.xlua.utilities.BundleUtil;

public class PutGroupStateCommand extends CallCommandHandler {

    public static PutGroupStateCommand create() { return new PutGroupStateCommand(); };

    public PutGroupStateCommand() {
        name = "putGroupState";
        requiresPermissionCheck = true;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        XMockPropMapped packet = commandData.read(XMockPropMapped.class);
        return BundleUtil.createResultStatus(
                XMockPropertiesProvider.setGroupState(commandData.getContext(), commandData.getDatabase(), packet));
    }

    public static Bundle invoke(Context context, XMockPropMapped settingPacket) {
        return XProxyContent.mockCall(
                context,
                "putGroupState", settingPacket.toBundle());
    }
}
