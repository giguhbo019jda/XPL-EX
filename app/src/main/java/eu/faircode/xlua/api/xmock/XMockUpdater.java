package eu.faircode.xlua.api.xmock;

import android.content.Context;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.configs.MockConfigDatabase;
import eu.faircode.xlua.api.properties.MockPropDatabase;
import eu.faircode.xlua.api.properties.MockPropSetting;
import eu.faircode.xlua.api.settingsex.LuaSettingsDatabase;
import eu.faircode.xlua.api.xmock.database.XMockCpuDatabase;
import eu.faircode.xlua.api.xmock.database.XMockConfigDatabase;
import eu.faircode.xlua.api.xmock.database.XMockSettingsDatabase;
import eu.faircode.xlua.utilities.CollectionUtil;
import eu.faircode.xlua.utilities.DatabasePathUtil;

public class XMockUpdater {
    //private static final String TAG = "XLua.MockChecker";
    private static boolean check_1 = false;
    private static boolean check_2 = false;
    private static boolean check_3 = false;
    private static boolean check_4 = false;

    public static boolean initDatabase(Context context, XDatabase db) {
        if(!check_1)
            check_1 = MockConfigDatabase.forceDatabaseCheck(context, db);
        if(!check_2)
            check_2 = XMockCpuDatabase.forceDatabaseCheck(context, db);
        if(!check_3)
            check_3 = CollectionUtil.isValid(MockPropDatabase.forceCheckMapsDatabase(context, db));
        if(!check_4)
            check_4 = MockPropDatabase.ensurePropSettingsDatabase(context, db);

        DatabasePathUtil.log("Config Check=" + check_1 + " Cpu Config Check=" + check_2 + " properties=" + check_3 + " prop settings = " + check_4, false);

        return check_1 && check_2 && check_3 && check_4;
    }

    public static void reset() {
        check_1 = false;
        check_2 = false;
        check_3 = false;
        check_4 = false;
    }
}
