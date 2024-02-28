package eu.faircode.xlua.api.xmock;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.Collection;

import eu.faircode.xlua.DebugUtil;
import eu.faircode.xlua.api.cpu.XMockCpu;
import eu.faircode.xlua.api.cpu.XMockCpuConversions;
import eu.faircode.xlua.api.props.XMockProp;
import eu.faircode.xlua.api.props.XMockPropSetting;
import eu.faircode.xlua.api.xmock.call.GetMockCpuCommand;
import eu.faircode.xlua.api.xmock.call.GetMockCpusCommand;
import eu.faircode.xlua.api.xmock.call.GetMockPropValueCommand;
import eu.faircode.xlua.api.xmock.call.PutGroupStateCommand;
import eu.faircode.xlua.api.xmock.call.PutMockCpuCommand;
import eu.faircode.xlua.utilities.BundleUtil;
import eu.faircode.xlua.utilities.MockUtils;

public class XMockCall {
    private static final String TAG = "XLua.XMockCall";

    public static String getPropertyValue(Context context, String propertyName) { return getPropertyValue(context, propertyName, "Global"); }
    public static String getPropertyValue(Context context, String propertyName, String packageName) { return getPropertyValue(context, propertyName, packageName, 0); }
    public static String getPropertyValue(Context context, String propertyName, String packageName, int uid) {
        XMockProp packet = new XMockProp(propertyName, uid, packageName);
        Bundle bundle = GetMockPropValueCommand.invoke(context, packet);
        if(bundle == null) {
            if(DebugUtil.isDebug())
                Log.e(TAG, "Bundle from [getPropertyValue] is null: " + packet);

            return MockUtils.NOT_BLACKLISTED;
        }

        return new XMockProp(bundle).getValue();
    }

    public static Collection<XMockCpu> getCpuMaps(Context context) {
        return XMockCpuConversions.fromBundleArray(
                GetMockCpusCommand.invoke(context));
    }

    public static XMockCpu getSelectedMockCpu(Context context) {
        return XMockCpuConversions.fromBundle(
                GetMockCpuCommand.invoke(context));
    }

    public static boolean putMockCpu(Context context, XMockCpu mockCpu) {
        return BundleUtil.readResultStatus(
                PutMockCpuCommand.invoke(context, mockCpu));
    }

    public static boolean setPropGroupState(Context context, String settingName, boolean enabled) {
        XMockPropSetting setting = new XMockPropSetting("global", settingName, enabled);
        return BundleUtil.readResultStatus(
                PutGroupStateCommand.invoke(context, setting));
    }
}
