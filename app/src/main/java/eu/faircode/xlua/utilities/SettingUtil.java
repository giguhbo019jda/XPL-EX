package eu.faircode.xlua.utilities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import eu.faircode.xlua.api.settings.LuaSettingExtended;

public class SettingUtil {
    private static final List<String> allUpperNames = Arrays.asList("sms", "icc", "no", "sys", "isp", "cid", "lac", "mac", "net", "ad", "drm", "gsf", "lcc", "meid", "imei", "bssid", "ssid", "esim", "sim", "sku", "lac", "cid", "msin", "mnc", "mcc", "adb", "os", "utc", "abi", "gps", "dns", "vm", "id", "gsm", "cpu", "gpu", "fp", "rom", "nfc", "soc", "url", "dev", "sdk", "iso");

    public static String cleanSettingName(String settingName) {
        if(!StringUtil.isValidString(settingName))
            return "NULL";

        String lowered = settingName.toLowerCase().trim();
        StringBuilder name = new StringBuilder();
        if(settingName.contains(".") && !lowered.contains(" ")) {
            String[] parts = settingName.split("\\.");

            for(String s : parts) {
                if(!StringUtil.isValidString(s))
                    continue;

                if(allUpperNames.contains(s))
                    name.append(s.toUpperCase());
                else {
                    if(s.length() > 1) {
                        char f = s.charAt(0);
                        name.append(Character.toUpperCase(f));
                        name.append(s.substring(1));
                    }else
                        name.append(s);
                }

                name.append(" ");
            }
        }else {
            name.append(lowered);
        }

        return name.toString().trim();
    }

    public static void updateSetting(LuaSettingExtended setting, String newValue, HashMap<LuaSettingExtended, String> modified) {
        String originalValue = modified.get(setting);
        if(originalValue == null) {
            if((setting.getValue() != null && newValue != null) && !newValue.equalsIgnoreCase(setting.getValue())) {
                modified.put(setting, setting.getValue());
                setting.setValueForce(newValue);
            }
            else if(setting.getValue() == null && newValue != null) {
                modified.put(setting, "<nil>");
                setting.setValueForce(newValue);
            }
        }else if(originalValue.equalsIgnoreCase("<nil>") && newValue == null) {
            modified.remove(setting);
            setting.setValueForce(null);
        } else if(newValue == null) {
            setting.setValueForce(null);
        }
        else if(!originalValue.equalsIgnoreCase(newValue)) {
            setting.setValueForce(newValue);
        }else {
            modified.remove(setting);
            setting.setValueForce(originalValue);
        }
    }
}
