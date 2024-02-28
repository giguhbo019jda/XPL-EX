package eu.faircode.xlua.api.xmock;

import android.content.Context;

import eu.faircode.xlua.XDataBase;
import eu.faircode.xlua.api.xmock.database.XMockCpuDatabase;
import eu.faircode.xlua.api.xmock.database.XMockConfigDatabase;
import eu.faircode.xlua.api.xmock.database.XMockSettingsDatabase;
import eu.faircode.xlua.utilities.DatabasePathUtil;

public class XMockUpdater {
    //private static final String TAG = "XLua.MockChecker";
    private static boolean check_1 = false;
    private static boolean check_2 = false;
    private static boolean check_3 = false;

    public static boolean initDatabase(Context context, XDataBase db) {
        if(!check_1)
            check_1 = XMockConfigDatabase.forceDatabaseCheck(context, db);
        if(!check_2)
            check_2 = XMockCpuDatabase.forceDatabaseCheck(context, db);
        if(!check_3)
            check_3 = XMockSettingsDatabase.forceDatabaseCheck(context, db);

        DatabasePathUtil.log("Config Check=" + check_1 + " Cpu Config Check=" + check_2 + " default settings Check=" + check_3, false);

        return check_1 && check_2 && check_3;
    }

    public static void reset() {
        check_1 = false;
        check_2 = false;
        check_3 = false;
    }
}
