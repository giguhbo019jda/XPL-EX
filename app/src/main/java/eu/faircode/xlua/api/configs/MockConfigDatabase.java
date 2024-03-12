package eu.faircode.xlua.api.configs;

import android.content.Context;

import java.util.Collection;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.config.XMockConfig;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;
import eu.faircode.xlua.utilities.StringUtil;

public class MockConfigDatabase {
    private static final String JSON = "configs.json";
    private static final int COUNT = 3;

    public static boolean putMockConfig(Context context, MockConfig config, XDatabase db) {
        if(config == null || !StringUtil.isValidString(config.getName()))
            return false;

        return DatabaseHelp.insertItem(
                db,
                MockConfig.Table.name,
                config,
                prepareDatabaseTable(context, db));
    }

    public static Collection<MockConfig> getMockConfigs(Context context, XDatabase db) {
        return DatabaseHelp.getOrInitTable(
                context,
                db,
                MockConfig.Table.name,
                MockConfig.Table.columns,
                JSON,
                true,
                MockConfig.class,
                COUNT);
    }

    public static boolean prepareDatabaseTable(Context context, XDatabase db) {
        return DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                MockConfig.Table.name,
                MockConfig.Table.columns,
                JSON,
                true,
                MockConfig.class,
                COUNT);
    }

    public static boolean forceDatabaseCheck(Context context, XDatabase db) {
        return DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                MockConfig.Table.name,
                MockConfig.Table.columns,
                JSON,
                true,
                MockConfig.class,
                DatabaseHelp.DB_FORCE_CHECK);
    }
}
