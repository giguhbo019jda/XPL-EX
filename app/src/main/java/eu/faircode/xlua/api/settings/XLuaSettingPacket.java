package eu.faircode.xlua.api.settings;

import android.os.Bundle;
import android.os.Parcel;

import androidx.annotation.NonNull;

import eu.faircode.xlua.utilities.BundleUtil;

public class XLuaSettingPacket extends XLuaLuaSetting {
    protected Boolean kill;
    protected Boolean useDefaultOrGlobal;

    public XLuaSettingPacket() { }
    public XLuaSettingPacket(Bundle b) { fromBundle(b); }
    public XLuaSettingPacket(Parcel in) { super(in); }
    public XLuaSettingPacket(Integer user, String category, String name) { super(user, category, name); }
    public XLuaSettingPacket(Integer user, String category, String name, String value) { super(user, category, name, value); }
    public XLuaSettingPacket(Integer user, String category, String name, String value, Boolean kill) {
        super(user, category, name, value);
        setKill(kill);
    }

    public XLuaSettingPacket setKill(Boolean kill) {
        if(kill != null) this.kill = kill;
        return this;
    }

    public Boolean getKill() { return this.kill; }

    @Override
    public Bundle toBundle() {
        Bundle b = super.toBundle();
        if(kill != null) b.putBoolean("kill", kill);
        if(useDefaultOrGlobal != null) b.putBoolean("useDefaultOrGlobal", useDefaultOrGlobal);
        return b;
    }

    @Override
    public void fromBundle(Bundle b) {
        super.fromBundle(b);
        if(b != null) {
            if(b.containsKey("kill")) kill = b.getBoolean("kill");
            kill = BundleUtil.readBoolean(b, "kill", false);
            useDefaultOrGlobal = BundleUtil.readBoolean(b, "useDefaultOrGlobal", false);
        }
    }

    public void setAsDeletePacket() {
        this.value = null;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if(kill != null) {
            sb.append("\nkill=");
            sb.append(kill);
        }

        if(useDefaultOrGlobal != null) {
            sb.append(" useDefaultOrGlobal=");
            sb.append(useDefaultOrGlobal);
        }

        return sb.toString();
    }
}