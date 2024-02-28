package eu.faircode.xlua.api.xmock.database;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.faircode.xlua.XDataBase;
import eu.faircode.xlua.api.cpu.XMockCpu;
import eu.faircode.xlua.api.standard.database.DatabaseHelp;
import eu.faircode.xlua.api.standard.database.SqlQuerySnake;

public class XMockCpuDatabase {
    private static final String TAG = "XLua.XMockCpuDatabase";
    public static final int COUNT = 43;
    public static final String JSON = "cpumaps.json";

    public static boolean insertCpuMap(XDataBase db, XMockCpu map) {
        return DatabaseHelp.insertItem(
                db,
                XMockCpu.Table.name,
                map);
    }

    public static boolean updateCpuMap(XDataBase db, String name, boolean selected) {
        XMockCpu map = new XMockCpu(name, null, null, null, selected);
        SqlQuerySnake snake = SqlQuerySnake.create(db, XMockCpu.Table.name)
                .whereColumn("name", name)
                .whereColumn("selected", Boolean.toString(!selected));

        return DatabaseHelp.updateItem(snake, map);
    }

    public static boolean putCpuMaps(XDataBase db, Collection<XMockCpu> maps) {
       return DatabaseHelp.insertItems(db, XMockCpu.Table.name, maps);
    }

    public static XMockCpu getSelectedMap(XDataBase db, boolean getContents) {
        SqlQuerySnake snake = SqlQuerySnake.create(db, XMockCpu.Table.name)
                .whereColumn("selected", "true");

        if(!getContents)
            avoidContents(snake);

        return snake.queryGetFirstAs(XMockCpu.class, true);
    }

    public static XMockCpu getMap(XDataBase db, String name, boolean getContents) {
        SqlQuerySnake snake = SqlQuerySnake
                .create(db, XMockCpu.Table.name)
                .whereColumn("name", name);

        if(!getContents)
            snake.onlyReturnColumns("name", "model", "manufacturer", "selected");

        return snake.queryGetFirstAs(XMockCpu.class, true);
    }

    public static void enforceOneSelected(XDataBase db, String keepMapName, boolean keepFirstSelected) {
        SqlQuerySnake selectedSnake = SqlQuerySnake.create(db, XMockCpu.Table.name)
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

            DatabaseHelp.updateItems(db, XMockCpu.Table.name, selected, selectedSnake);
        }
    }

    public static Collection<XMockCpu> getSelectedMaps(XDataBase db) {
        SqlQuerySnake selectedSnake = SqlQuerySnake.create(db, XMockCpu.Table.name)
                .whereColumn("selected", "true")
                .onlyReturnColumns("name", "selected");

        return selectedSnake.queryAs(XMockCpu.class, true);
    }

    public static Collection<String> getSelectedMapNames(XDataBase db) {
        SqlQuerySnake selectedSnake = SqlQuerySnake.create(db, XMockCpu.Table.name)
                .whereColumn("selected", "true")
                .onlyReturnColumns("name", "selected");

        return selectedSnake.queryAsStringList("name", true);
    }

    public static Collection<XMockCpu> getCpuMaps(Context context, XDataBase db) {
        return DatabaseHelp.initDatabase(
                context,
                db,
                XMockCpu.Table.name,
                XMockCpu.Table.columns,
                JSON,
                true,
                XMockCpu.class,
                COUNT);
    }

    public static boolean forceDatabaseCheck(Context context, XDataBase db) {
        return DatabaseHelp.prepareTableIfMissingOrInvalidCount(
                context,
                db,
                XMockCpu.Table.name,
                XMockCpu.Table.columns,
                JSON,
                true,
                XMockCpu.class,
                DatabaseHelp.DB_FORCE_CHECK);
    }

    private static void avoidContents(SqlQuerySnake snake) {
        snake.onlyReturnColumns("name", "model", "manufacturer", "selected");
    }
}
