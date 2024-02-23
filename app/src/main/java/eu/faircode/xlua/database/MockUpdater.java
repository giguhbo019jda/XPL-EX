package eu.faircode.xlua.database;

import android.content.Context;

import eu.faircode.xlua.XDataBase;
import eu.faircode.xlua.api.xmock.XMockCpuDatabase;
import eu.faircode.xlua.api.xmock.XMockPhoneDatabase;
import eu.faircode.xlua.api.xmock.XMockPropDatabase;
import eu.faircode.xlua.utilities.DatabasePathUtil;

public class MockUpdater {
    //private static final String TAG = "XLua.MockChecker";
    private static boolean check_1 = false;
    private static boolean check_2 = false;
    private static boolean check_3 = false;

    public static boolean initDatabase(Context context, XDataBase db) {
        if(!check_1)
            check_1 = XMockPhoneDatabase.forceDatabaseCheck(context, db);
        if(!check_2)
            check_2 = XMockCpuDatabase.forceDatabaseCheck(context, db);
        if(!check_3)
            check_3 = XMockPropDatabase.forceDatabaseCheck(context, db);

        DatabasePathUtil.log("Phone Config Check=" + check_1 + " Cpu Config Check=" + check_2 + " Prop Config Check=" + check_3, false);

        return check_1 && check_2 && check_3;
    }

    public static void reset() {
        check_1 = false;
        check_2 = false;
        check_3 = false;
    }
}
