package eu.faircode.xlua.api.xmock.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import eu.faircode.xlua.XDatabase;
import eu.faircode.xlua.api.cpu.XMockCpu;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;

public class XMockCpuDatabase {
    private static final String TAG = "XLua.XMockCpuDatabase";
    public static final int COUNT = 43;
    public static final String JSON = "cpumaps.json";

    public static final String TABLE_NAME = "cpumaps";
    public static final LinkedHashMap<String, String> TABLE_COLUMNS = new LinkedHashMap<String, String>() {{
        put("name", "TEXT");
        put("model", "TEXT");
        put("manufacturer", "TEXT");
        put("contents", "TEXT");
        put("selected", "BOOLEAN");
    }};

    public static boolean insertCpuMap(XDatabase db, XMockCpu map) {
        return DatabaseHelp.insertItem(
                db,
                TABLE_NAME,
                map);
    }

    public static boolean updateCpuMap(XDatabase db, String name, boolean selected) {
        XMockCpu map = new XMockCpu(name, null, null, null, selected);
        SqlQuerySnake snake = SqlQuerySnake.create(db, TABLE_NAME)
                .whereColumn("name", name)
                .whereColumn("selected", Boolean.toString(!selected));

        return DatabaseHelp.updateItem(snake, map);
    }

    public static boolean putCpuMaps(XDatabase db, Collection<XMockCpu> maps) {
       return DatabaseHelp.insertItems(db, TABLE_NAME, maps);
    }

    public static XMockCpu getSelectedMap(XDatabase db, boolean getContents) {
        SqlQuerySnake snake = SqlQuerySnake.create(db, TABLE_NAME)
                .whereColumn("selected", "true");

        if(!getContents)
            avoidContents(snake);

        return snake.queryGetFirstAs(XMockCpu.class, true);
    }

    public static XMockCpu getMap(XDatabase db, String name, boolean getContents) {
        SqlQuerySnake snake = SqlQuerySnake
                .create(db, TABLE_NAME)
                .whereColumn("name", name);

        if(!getContents)
            snake.onlyReturnColumns("name", "model", "manufacturer", "selected");

        return snake.queryGetFirstAs(XMockCpu.class, true);
    }

    public static void enforceOneSelected(XDatabase db, String keepMapName, boolean keepFirstSelected) {
        SqlQuerySnake selectedSnake = SqlQuerySnake.create(db, TABLE_NAME)
                .whereColumn("selected", "true")
                .onlyReturnColumns("name", "selected");

        List<XMockCpu> selected = new ArrayList(selectedSnake.queryAs(XMockCpu.class, true));
        if(selected.size() > 1) {
            if(keepMapName != null) {
                for(int i = 0; i < selected.size(); i++) {
                    XMockCpu map = selected.get(i);
                    if(map.getName().equals(keepMapName)) {
                        selected.remove(map);
                        break;
                    }
                }
            }
            else if(keepFirstSelected)
                selected.remove(selected.get(0));

            for (XMockCpu m : selected)
                m.setSelected(false);

            DatabaseHelp.updateItems(db, TABLE_NAME, selected, selectedSnake);
        }
    }

    public static Collection<XMockCpu> getSelectedMaps(XDatabase db) {
        SqlQuerySnake selectedSnake = SqlQuerySnake.create(db, TABLE_NAME)
                .whereColumn("selected", "true")
                .onlyReturnColumns("name", "selected");

        return selectedSnake.queryAs(XMockCpu.class, true);
    }

    public static Collection<String> getSelectedMapNames(XDatabase db) {
        SqlQuerySnake selectedSnake = SqlQuerySnake.create(db, TABLE_NAME)
                .whereColumn("selected", "true")
                .onlyReturnColumns("name", "selected");

        return selectedSnake.queryAsStringList("name", true);
    }

    public static Collection<XMockCpu> getCpuMaps(Context context, XDatabase db) {
        return DatabaseHelp.getOrInitTable(
                context,
                db,
                TABLE_NAME,
                TABLE_COLUMNS,
                JSON,
                true,
                XMockCpu.class,
                COUNT);
    }

    public static boolean forceDatabaseCheck(Context context, XDatabase db) {
        return DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                TABLE_NAME,
                TABLE_COLUMNS,
                JSON,
                true,
                XMockCpu.class,
                DatabaseHelp.DB_FORCE_CHECK);
    }

    private static void avoidContents(SqlQuerySnake snake) {
        snake.onlyReturnColumns("name", "model", "manufacturer", "selected");
    }
}
