package eu.faircode.xlua.api.xmock;

import android.content.Context;
import android.util.Log;

import java.util.Collection;

import eu.faircode.xlua.XDataBase;
import eu.faircode.xlua.api.objects.xmock.ConfigSetting;
import eu.faircode.xlua.api.objects.xmock.cpu.MockCpu;
import eu.faircode.xlua.api.objects.xmock.phone.MockPhoneConfig;
import eu.faircode.xlua.database.DatabaseHelperEx;
import eu.faircode.xlua.database.DatabaseQuerySnake;
import eu.faircode.xlua.utilities.StringUtil;

public class XMockPhoneDatabase {
    private static final int COUNT = 3;

    public static boolean putMockConfig(Context context, MockPhoneConfig config, XDataBase db) {
        if(config == null || !StringUtil.isValidString(config.getName()))
            return false;

        Log.i("XLua.XMockPhoneDatabase", "Config was valid: " + config);

        return DatabaseHelperEx.insertItem(
                db,
                MockPhoneConfig.Table.name,
                config,
                prepareDatabaseTable(context, db));
    }


    public static Collection<MockPhoneConfig> getMockConfigs(Context context, XDataBase db) {
        Log.i("XLua.XMockPhoneDatabase", "Getting Mock Configs");
        return DatabaseHelperEx.initDatabase(
                context,
                db,
                MockPhoneConfig.Table.name,
                MockPhoneConfig.Table.columns,
                MockPhoneConfig.JSON,
                true,
                MockPhoneConfig.class,
                COUNT);
    }

    public static boolean prepareDatabaseTable(Context context, XDataBase db) {
        return DatabaseHelperEx.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                MockPhoneConfig.Table.name,
                MockPhoneConfig.Table.columns,
                MockPhoneConfig.JSON,
                true,
                MockPhoneConfig.class,
                COUNT);
    }

    public static boolean forceDatabaseCheck(Context context, XDataBase db) {
        return DatabaseHelperEx.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                MockPhoneConfig.Table.name,
                MockPhoneConfig.Table.columns,
                MockPhoneConfig.JSON,
                true,
                MockPhoneConfig.class,
                DatabaseHelperEx.DB_FORCE_CHECK);
    }
}
