package eu.faircode.xlua.api.xmock;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.Collection;

import eu.faircode.xlua.DebugUtil;
import eu.faircode.xlua.api.XResult;
import eu.faircode.xlua.api.cpu.MockCpu;
import eu.faircode.xlua.api.cpu.MockCpuConversions;
import eu.faircode.xlua.api.props.XMockProp;
import eu.faircode.xlua.api.props.XMockPropMapped;
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

    public static Collection<MockCpu> getCpuMaps(Context context) {
        return MockCpuConversions.fromBundleArray(
                GetMockCpusCommand.invoke(context));
    }

    public static MockCpu getSelectedMockCpu(Context context) {
        return MockCpuConversions.fromBundle(
                GetMockCpuCommand.invoke(context));
    }

    public static XResult putMockCpu(Context context, MockCpu mockCpu) {
        return XResult.from(PutMockCpuCommand.invoke(context, mockCpu));
    }

    //public static XResult setPropGroupState

    public static XResult setGroupState(Context context, String packageName, String settingName, boolean enabled) {
        //[prop maps] (property) => (setting)
        //[prop sets] (property) => (hide/skip)
        //Resolve prop setting, check if hidden or skip
        return XResult.create();
    }

    public static boolean setPropGroupState(Context context, String settingName, boolean enabled) {
        XMockPropMapped setting = new XMockPropMapped("global", settingName, enabled);
        return BundleUtil.readResultStatus(
                PutGroupStateCommand.invoke(context, setting));
    }
}
