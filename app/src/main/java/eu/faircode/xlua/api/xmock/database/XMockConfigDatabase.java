package eu.faircode.xlua.api.xmock.database;

import android.content.Context;
import android.util.Log;

import java.util.Collection;

import eu.faircode.xlua.XDataBase;
import eu.faircode.xlua.api.config.XMockConfig;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;
import eu.faircode.xlua.utilities.StringUtil;

public class XMockConfigDatabase {
    private static final String JSON = "configs.json";
    private static final int COUNT = 3;

    public static boolean putMockConfig(Context context, XMockConfig config, XDataBase db) {
        if(config == null || !StringUtil.isValidString(config.getName()))
            return false;

        return DatabaseHelp.insertItem(
                db,
                XMockConfig.Table.name,
                config,
                prepareDatabaseTable(context, db));
    }


    public static Collection<XMockConfig> getMockConfigs(Context context, XDataBase db) {
        return DatabaseHelp.initDatabase(
                context,
                db,
                XMockConfig.Table.name,
                XMockConfig.Table.columns,
                JSON,
                true,
                XMockConfig.class,
                COUNT);
    }

    public static boolean prepareDatabaseTable(Context context, XDataBase db) {
        return DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                XMockConfig.Table.name,
                XMockConfig.Table.columns,
                JSON,
                true,
                XMockConfig.class,
                COUNT);
    }

    public static boolean forceDatabaseCheck(Context context, XDataBase db) {
        return DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                XMockConfig.Table.name,
                XMockConfig.Table.columns,
                JSON,
                true,
                XMockConfig.class,
                DatabaseHelp.DB_FORCE_CHECK);
    }
}
