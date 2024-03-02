package eu.faircode.xlua.api.xmock.database;

import android.content.Context;

import java.util.Collection;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.settings.XMockDefaultSetting;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;

public class XMockSettingsDatabase {
    private static final String TAG = "XLua.XMockSettingsDatabase";
    private static final String JSON = "settingdefaults.json";
    private static final int COUNT = 12;

    public static Collection<XMockDefaultSetting> getDefaultSettings(Context context, XDatabase db) {
        return DatabaseHelp.getOrInitTable(
                context,
                db,
                XMockDefaultSetting.Table.name,
                XMockDefaultSetting.Table.columns,
                JSON,
                true,
                XMockDefaultSetting.class,
                COUNT);
    }

    public static boolean forceDatabaseCheck(Context context, XDatabase db) {
        return DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                XMockDefaultSetting.Table.name,
                XMockDefaultSetting.Table.columns,
                JSON,
                true,
                XMockDefaultSetting.class,
                DatabaseHelp.DB_FORCE_CHECK);
    }
}
