package eu.faircode.xlua.api.xmock.query;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Collection;

import eu.faircode.xlua.api.XProxyContent;
import eu.faircode.xlua.api.props.XMockPropGroup;
import eu.faircode.xlua.api.standard.QueryCommandHandler;
import eu.faircode.xlua.api.standard.command.QueryPacket;
import eu.faircode.xlua.api.xmock.database.XMockPropertiesDatabase;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;
import eu.faircode.xlua.api.xmock.provider.XMockPropertiesProvider;
import eu.faircode.xlua.utilities.CursorUtil;
import eu.faircode.xlua.utilities.StringUtil;

/*public class GetMockPropGroupsCommand extends QueryCommandHandler {
    public static GetMockPropGroupsCommand create(boolean marshall) { return new GetMockPropGroupsCommand(marshall); };

    private boolean marshall;
    public GetMockPropGroupsCommand(boolean marshall) {
        this.name = marshall ? "getMockPropGroups2" : "getMockPropGroups";
        this.marshall = marshall;
        this.requiresPermissionCheck = false;
    }

    @Override
    public Cursor handle(QueryPacket commandData) throws Throwable {
        throwOnPermissionCheck(commandData.getContext());

        String[] selection = commandData.getSelection();
        String packageName = "Global";
        int uid = 0;
        if(selection != null && selection.length > 0) {
            if(StringUtil.isValidString(selection[0]))
                packageName = selection[0];

            if(selection.length > 1)
                uid = StringUtil.toInteger(selection[1], 0);
        }

        return CursorUtil.toMatrixCursor(XMockPropertiesProvider.getGroups(
                commandData.getContext(),
                commandData.getDatabase(),
                uid,
                packageName), marshall, 0);
    }

    public static Cursor invoke(Context context, boolean marshall, String packageName, int uid) {
        SqlQuerySnake snake = SqlQuerySnake
                .create()
                .whereColumn("pkg", packageName)
                .whereColumn("uid", uid);

        return XProxyContent.mockQuery(
                context,
                marshall ? "getMockPropGroups2" : "getMockPropGroups", snake.getSelectionCompareValues(), snake.getSelectionArgs());
    }
}*/
