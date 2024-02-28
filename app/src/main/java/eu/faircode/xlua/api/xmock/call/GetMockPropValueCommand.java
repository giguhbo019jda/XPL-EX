package eu.faircode.xlua.api.xmock.call;

import android.content.Context;
import android.os.Bundle;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.standard.CallCommandHandler;
import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.props.XMockProp;
import eu.faircode.xlua.api.xmock.provider.XMockPropertiesProvider;

public class GetMockPropValueCommand extends CallCommandHandler {
    public static GetMockPropValueCommand create() { return new GetMockPropValueCommand(); };

    public GetMockPropValueCommand() {
        name = "getMockPropValue";
        requiresPermissionCheck = false;
    }

    @Override
    public Bundle handle(CallPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());
        XMockProp packet = commandData.read(XMockProp.class);
        return XMockPropertiesProvider.getMockPropValue(commandData.getContext(), commandData.getDatabase(), packet).toBundle();
    }

    public static Bundle invoke(Context context, XMockProp propPacket) {
        return XProxyContent.mockCall(
                context,
                "getMockPropValue", propPacket.toBundle());
    }
}
