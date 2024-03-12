package eu.faircode.xlua.api.xmock.query;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.properties.MockPropProvider;
import eu.faircode.xlua.api.properties.MockPropSetting;
import eu.faircode.xlua.api.standard.QueryCommandHandler;
import eu.faircode.xlua.api.standard.command.QueryPacket;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;
import eu.faircode.xlua.utilities.CursorUtil;

public class GetMockPropertiesCommand extends QueryCommandHandler {
    public static GetMockPropertiesCommand create(boolean marshall) { return new GetMockPropertiesCommand(marshall); };
    protected boolean marshall;
    public GetMockPropertiesCommand(boolean marshall) {
        this.marshall = marshall;
        this.name = marshall ? "getModifiedProperties2" : "getModifiedProperties";
        this.requiresPermissionCheck = false;
    }

    @Override
    public Cursor handle(QueryPacket commandData) throws Throwable {
        MockPropSetting packet = commandData.readFrom(MockPropSetting.class, MockPropSetting.USER_PACKET_TWO);
        packet.ensureIdentification();
        Log.i("XLua.GetMockPropertiesCommand", "packet=" + packet + " marshall=" + marshall);
        return CursorUtil.toMatrixCursor(
                MockPropProvider.getSettingsForPackage(
                        commandData.getContext(),
                        commandData.getDatabase(),
                        packet.getUser(),
                        packet.getCategory(),
                        packet.isGetAll()), marshall, 0);
    }

    public static Cursor invoke(Context context, boolean marshall, int user, String packageOrName, boolean getAllProperties) { return invoke(context, marshall, user, packageOrName, getAllProperties ? MockPropSetting.PROP_GET_ALL : MockPropSetting.PROP_GET_MODIFIED); }
    public static Cursor invoke(Context context, boolean marshall, int user, String packageOrName, int commandCode) {
        SqlQuerySnake query = SqlQuerySnake.create()
                .whereColumn("packageName", packageOrName)
                .whereColumn("user", user)
                .whereColumn("code", commandCode);

        return XProxyContent.mockQuery(
                context,
                marshall ? "getModifiedProperties2" : "getModifiedProperties",
                query);
    }
}
