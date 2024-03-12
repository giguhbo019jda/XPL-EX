package eu.faircode.xlua.interceptors;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import eu.faircode.xlua.DebugUtil;
import eu.faircode.xlua.api.xmock.XMockCall;
import eu.faircode.xlua.api.cpu.MockCpu;
import eu.faircode.xlua.utilities.MemoryUtil;
import eu.faircode.xlua.utilities.MockUtils;
import eu.faircode.xlua.utilities.StringUtil;

public class ShellIntercept {
    private static final String TAG = "XLua.ShellIntercept";

    public static String interceptOne(String command, Context context) {
        if(!StringUtil.isValidString(command))
            return null;
        if(DebugUtil.isDebug())
            Log.i(TAG, "Filtering (" + command + ")");

        String lowered = command.toLowerCase();
        lowered.trim();

        String fakeValue = null;
        String propCommand = "getprop";
        String propFile = "build.prop";
        String cpuFile = "cpuinfo";
        String memFile = "meminfo";


        if(lowered.contains(propCommand)) {
            int index = lowered.indexOf(propCommand);
            //String propValue = lowered.substring(index + lowered.length()).trim();
            int start = index + propCommand.length();

            if(DebugUtil.isDebug())
                Log.i(TAG, "start index=" + start + " length=" + lowered.length() + " begin index=" + index);

            if(lowered.length() <= start)
                return null;

            String propValue = lowered.substring(start).trim();
            if(DebugUtil.isDebug())
                Log.i(TAG, "prop after=" + propValue);

            int whiteSpaceIndex = lowered.indexOf(' ');
            if(whiteSpaceIndex != -1)
                propValue = propValue.substring(0, whiteSpaceIndex).trim();

            if(DebugUtil.isDebug())
                Log.i(TAG, "cleaned prop after=" + propValue);

            if(!StringUtil.isValidString(propValue) || MockUtils.isPropVxpOrLua(propValue))
                return null;

            if(DebugUtil.isDebug())
                Log.i(TAG, "Checking Property=" + propValue);

            //XMockCall.getPropertyValue(getApplicationContext(), property, getPackageName(), 0);
            //fakeValue = MockUtils.filterProperty(propValue, XMockCall.getMockProps(context));
            fakeValue = XMockCall.getPropertyValue(context, propValue);// work on this
            if(fakeValue.equalsIgnoreCase(MockUtils.NOT_BLACKLISTED))
                return null;
        }
        else if(lowered.contains(propFile)) {
            //propname=value
            //StringBuilder sb = new StringBuilder();
            ///for(MockProp prop : XMockCallApi.getMockProps(context)) {
            //}
            fakeValue = "ro.vendor.display.ad=0";
            //note this can be used to detect odd environment
        }
        else if(lowered.contains(cpuFile)) {
            MockCpu mockCpu = XMockCall.getSelectedMockCpu(context);
            fakeValue = mockCpu.getContents();
        }
        else if(lowered.contains(memFile)) {
            int total = ThreadLocalRandom.current().nextInt(5, 100);
            fakeValue = MemoryUtil.generateFakeMeminfoContents(total, total - 5);
        }

        //   /proc/version
        //   uname -r or -a
        //   top
        //   ps -a
        //   VD Info


        //Have so in the build prop things you can select a config
        //each prop is linked to a config tag group in attempt to change / spoof
        //properties can be directly identifed via configs as well
        //maybe also now start cacheing it if possible ?
        //cache can change internally , as in stop grabbibng it from the db
        //matter in fact we can use query to select the specific property :P instead of grabbing the whole list ++
        //so we can avoid cache just use queries

        //each category will be linked to the "SETTING"
        //

        //if(!StringUtil.isValidString(fakeValue))
        //    return null;

        return fakeValue;
    }

    public static String interceptTwo(String[] args, Context context) {
        return interceptOne(StringUtil.joinDelimiter(" ", args), context);
    }

    public static String interceptThree(List<String> commands, Context context) {
        return interceptOne(StringUtil.joinDelimiter(" ", commands), context);
    }

    public static Process echo(String command) {
        try {
            String[] cmdline = { "sh", "-c", "echo " + command};
            return Runtime.getRuntime().exec(cmdline);
        }catch (Exception e) {
            Log.e(TAG, "Failed to start Dummy Process: " + e);
            return null;
        }
    }

    //public static Process interceptThree(ProcessBuilder builder, Context context) {
    //    return interceptOne(StringUtil.joinDelimiter(" ", builder.command()), context);
    //}
}
