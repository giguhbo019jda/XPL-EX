package eu.faircode.xlua.api.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import eu.faircode.xlua.api.standard.command.CallPacket;
import eu.faircode.xlua.api.xlua.database.XLuaSettingsDatabase;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.CollectionUtil;
import eu.faircode.xlua.utilities.StringUtil;

//An IPACKET only Bundle or Cursor, base will have either verify or code set function whatevre
//Introduce this system
//Move things to packets
//Also we can do something like if setting values is null then means delete type thing :P
//ALso have constructors to take in CommandData call packet
public class XBulkSettingActionPacket {
    private static final String TAG = "XLua.XBulkSettingActionPacket";

    public static XBulkSettingActionPacket from(CallPacket p) { return new XBulkSettingActionPacket(p.getExtras()); }
    public static XBulkSettingActionPacket from(Bundle b) { return new XBulkSettingActionPacket(b); }
    public static XBulkSettingActionPacket from(List<XMockMappedSetting> settingsMapped) { return new XBulkSettingActionPacket(settingsMapped, false, 0, "Global", null); }
    public static XBulkSettingActionPacket from(List<XMockMappedSetting> settingsMapped, Boolean kill, Integer userId, String packageName) { return new XBulkSettingActionPacket(settingsMapped, kill, userId, packageName, null); }
    public static XBulkSettingActionPacket from(List<XMockMappedSetting> settingsMapped, Boolean kill, Integer userId, String packageName, Integer actionCode) { return new XBulkSettingActionPacket(settingsMapped, kill, userId, packageName, actionCode); }


    public static boolean isValidCallCommand(CallPacket packet, boolean requireValues) { return isValidCallCommand(packet.getExtras(), requireValues); }
    public static boolean isValidCallCommand(Bundle b, boolean requireValues) {
        if(b == null)
            return false;

        if(!b.containsKey("settingNames"))
            return false;

        if(requireValues && !b.containsKey("settingValues"))
            return false;

        return true;
    }

    protected List<XMockMappedSetting> settingsMapped = new ArrayList<>();
    protected List<XLuaLuaSetting> settingsLua = new ArrayList<>();

    protected Boolean kill;
    protected Integer userId;
    protected String packageName;
    protected Integer actionCode;

    public XBulkSettingActionPacket(Bundle b) {  fromBundle(b); }

    public XBulkSettingActionPacket(List<XMockMappedSetting> settingsMapped) { this(settingsMapped, false, 0, "Global", null); }
    public XBulkSettingActionPacket(List<XMockMappedSetting> settingsMapped, Boolean kill, Integer userId, String packageName) { this(settingsMapped, kill, userId, packageName, null); }
    public XBulkSettingActionPacket(List<XMockMappedSetting> settingsMapped, Boolean kill, Integer userId, String packageName, Integer actionCode) {
        setSettingsMapped(settingsMapped);
        setKill(kill);
        setUserId(userId);
        setPackageName(packageName);
        setActionCode(actionCode);
    }

    public void fromBundle(Bundle b) {
        if(b != null) {
            kill = BundleUtil.readBoolean(b, "kill", false);
            userId = BundleUtil.readInteger(b, "user", XLuaSettingsDatabase.GLOBAL_USER);
            packageName = BundleUtil.readString(b, "packageName", XLuaSettingsDatabase.GLOBAL_NAMESPACE);
            //actionCode = BundleUtil.readInt(b, "code", -1);
            setSettingsLua(XMockMappedConversions.fromBundleArrayToLuaSettings(b, userId, packageName));
        }
    }

    public Bundle toBundle() {
        return XMockMappedConversions.toBundleArray(settingsMapped, userId, packageName, kill);
    }

    public List<XLuaLuaSetting> getSettingsLua() { return settingsLua; }
    public XBulkSettingActionPacket setSettingsLua(List<XLuaLuaSetting> settingsLua) {
        if(CollectionUtil.isValid(settingsLua)) this.settingsLua = settingsLua;
        return this;
    }

    public List<XMockMappedSetting> getSettingsMapped() { return settingsMapped; }
    public XBulkSettingActionPacket setSettingsMapped(List<XMockMappedSetting> settingsMapped) {
        if(CollectionUtil.isValid(settingsMapped)) this.settingsMapped = settingsMapped;
        return this;
    }

    public Boolean getKill() { return kill; }
    public XBulkSettingActionPacket setKill(Boolean kill) {
        if(kill != null) this.kill = kill;
        return this;
    }

    public Integer getUserId() { return userId; }
    public XBulkSettingActionPacket setUserId(Integer userId) {
        if(userId != null) this.userId = userId;
        return this;
    }

    public String getPackageName() { return packageName; }
    public XBulkSettingActionPacket setPackageName(String packageName) {
        if(packageName != null) this.packageName = packageName;
        return this;
    }

    public Integer getActionCode() { return actionCode; }
    public XBulkSettingActionPacket setActionCode(Integer actionCode) {
        if(actionCode != null) this.actionCode = actionCode;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(CollectionUtil.isValid(settingsMapped)) {
            sb.append(" settings size=[");
            sb.append(settingsMapped.size());
            sb.append("]");
        }

        if(kill != null) {
            sb.append(" kill=[");
            sb.append(kill);
            sb.append("]");
        }

        if(userId != null) {
            sb.append(" user=p[");
            sb.append(userId);
            sb.append("]");
        }

        if(StringUtil.isValidString(packageName)) {
            sb.append(" pkg=[");
            sb.append(packageName);
            sb.append("]");
        }

        if(actionCode != null) {
            sb.append(" code=[");
            sb.append(actionCode);
            sb.append("]");
        }

        return sb.toString();
    }
}
