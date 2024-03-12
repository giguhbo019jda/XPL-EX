package eu.faircode.xlua.api.settingsex;

import android.os.Bundle;

import androidx.annotation.NonNull;

import eu.faircode.xlua.XUtil;
import eu.faircode.xlua.api.standard.interfaces.IPacket;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.StringUtil;

public class LuaSettingPacket extends LuaSettingDefault implements IPacket {
    public static final int CODE_GET_VALUE = 0x0;
    public static final int CODE_INSERT_UPDATE = 0x1;
    public static final int CODE_DELETE = 0x2;
    public static final int CODE_GET_OBJECT = 0x3;
    public static final int CODE_ENSURE_GET_VALUE = 0x4;
    public static final int CODE_VERSION_ONE = 0x5;

    public static LuaSettingPacket create() { return new LuaSettingPacket(); }
    public static LuaSettingPacket create(String name, String value) { return new LuaSettingPacket(LuaSettingsDatabase.GLOBAL_USER, LuaSettingsDatabase.GLOBAL_NAMESPACE, name, value, null, false, CODE_INSERT_UPDATE); }
    public static LuaSettingPacket create(Integer userId, String category, String name, String value, String description) { return new LuaSettingPacket(userId, category, name, value, description, false, null); }
    public static LuaSettingPacket create(Integer userId, String category, String name, String value, String description, Boolean kill, Integer code) { return new LuaSettingPacket(userId, category, name, value, description, kill, code); }

    protected Integer code;
    protected Boolean kill;

    public LuaSettingPacket() { }
    public LuaSettingPacket(Integer userId, String category, String name, String value, String description, Boolean kill, Integer code) {
        super(userId, category, name, value, description);
        setKill(kill);
        setCode(code);
    }

    public Integer getCode() { return code; }
    public LuaSettingPacket setCode(Integer code) { if(code != null) this.code = code; return this; }

    public Boolean isKill() { return kill; }
    public LuaSettingPacket setKill(Boolean kill) { if(kill != null) this.kill = kill; return this; }

    public boolean isGetValue() { return code == CODE_GET_VALUE;  }
    public boolean isInsertOrUpdate() { return code == CODE_INSERT_UPDATE; }
    public boolean isGetObject() { return code == CODE_GET_OBJECT; }
    public boolean isEnsureGetValue() { return code == CODE_ENSURE_GET_VALUE; }
    public boolean isOriginalCall() {  return code == CODE_VERSION_ONE; }
    public boolean isDelete() {
        if(code == null && value == null)
            return true;

        if(code == null)
            return false;

        return code == CODE_DELETE;
    }

    @Override
    public Bundle toBundle() {
        Bundle b = super.toBundle();
        if(this.kill != null) b.putBoolean("kill", this.kill);
        if(this.code != null) b.putInt("code", this.code);
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        super.fromBundle(b);
        if(b != null) {
            this.kill = BundleUtil.readBoolean(b, "kill", false);
            int def = value == null ? CODE_DELETE : CODE_INSERT_UPDATE;
            this.code = BundleUtil.readInteger(b, "code", def);
        }
    }

    @Override
    public int getSecretKey() { return 0; }

    @Override
    public void readSelectionArgs(String[] selection, int flags) {
        if(selection == null)
            return;
        //Fill in if empty

        if(selection.length > 1) {
            int uid = Integer.parseInt(selection[1]);
            user = XUtil.getUserId(uid);
            if(selection.length > 2)
                StringUtil.toInteger(selection[2], CODE_GET_VALUE);
            else code = CODE_VERSION_ONE;
        }
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if(this.code != null) {
            sb.append(" code=");
            sb.append(code);
        }

        return sb.toString();
    }
}
