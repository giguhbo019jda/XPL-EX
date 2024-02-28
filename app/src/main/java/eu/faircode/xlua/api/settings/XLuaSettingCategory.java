package eu.faircode.xlua.api.settings;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import eu.faircode.xlua.api.standard.interfaces.IDBSerial;

public class XLuaSettingCategory extends XLuaSettingCategoryBase implements IDBSerial {
    protected Collection<XLuaLuaSetting> settings = new ArrayList<>();

    public XLuaSettingCategory() { }
    public XLuaSettingCategory(Integer userId, String name) { super(userId, name); }

    public Collection<XLuaLuaSetting> getSettings(String name) {
        Collection<XLuaLuaSetting> sets = new ArrayList<>();
        if(settings == null || settings.isEmpty())
            return sets;

        for(XLuaLuaSetting s : settings) {
            if(s.getName().equals(name)) {
                sets.add(s);
            }
        }

        return sets;
    }

    public XLuaSettingCategory addSetting(XLuaLuaSetting setting) {
        if(setting != null)
            settings.add(setting);

        return this;
    }

    public XLuaSettingCategory setSettings(Collection<XLuaLuaSetting> settings) {
        if(settings != null) this.settings = settings;
        return this;
    }

    @Override
    public ContentValues createContentValues() {
        ContentValues cv = new ContentValues();
        cv.put("user", userId);
        cv.put("name", name);
        return cv;
    }

    @Override
    public List<ContentValues> createContentValuesList() { return null; }

    @Override
    public void fromContentValuesList(List<ContentValues> contentValues) { }

    @Override
    public void fromContentValues(ContentValues contentValue) { }

    @Override
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            userId = cursor.getInt(cursor.getColumnIndex("user"));
            name = cursor.getString(cursor.getColumnIndex("category"));
        }
    }
}
