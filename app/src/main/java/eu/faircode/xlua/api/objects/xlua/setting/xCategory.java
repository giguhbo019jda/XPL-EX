package eu.faircode.xlua.api.objects.xlua.setting;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collection;

import eu.faircode.xlua.api.objects.IDBSerial;

public class xCategory extends xCategoryBase implements IDBSerial {
    protected Collection<XSetting> settings = new ArrayList<>();

    public xCategory() { }
    public xCategory(Integer userId, String name) { super(userId, name); }

    public Collection<XSetting> getSettings(String name) {
        Collection<XSetting> sets = new ArrayList<>();
        if(settings == null || settings.isEmpty())
            return sets;

        for(XSetting s : settings) {
            if(s.getName().equals(name)) {
                sets.add(s);
            }
        }

        return sets;
    }

    public xCategory addSetting(XSetting setting) {
        if(setting != null)
            settings.add(setting);

        return this;
    }

    public xCategory setSettings(Collection<XSetting> settings) {
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
    public void fromCursor(Cursor cursor) {
        if(cursor != null) {
            userId = cursor.getInt(cursor.getColumnIndex("user"));
            name = cursor.getString(cursor.getColumnIndex("category"));
        }
    }
}
