package eu.faircode.xlua.api.xmock.database;

import android.content.Context;

import java.util.Collection;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.props.XMockPropGroup;
import eu.faircode.xlua.api.props.XMockPropSetting;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;

public class XMockPropertiesDatabase {
    private static final String TAG = "XLua.XMockPropertiesDatabase";
    private static final String JSON = "propmaps.json";

    //we can also just still return :P group array
    //from the group array we create a list of all settings ez
    //if we needed the list ofc
    public static Collection<XMockPropSetting> initDatabase(Context context, XDatabase db) {
        return DatabaseHelp.initDatabaseLists(
                context,
                db,
                XMockPropSetting.Table.name,
                XMockPropSetting.Table.columns,
                JSON,
                true,
                XMockPropGroup.class,
                XMockPropSetting.class,
                true);
    }
}
