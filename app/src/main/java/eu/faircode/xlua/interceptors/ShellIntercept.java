package eu.faircode.xlua.interceptors;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import eu.faircode.xlua.DebugUtil;
import eu.faircode.xlua.api.XMockCallApi;
import eu.faircode.xlua.api.objects.xmock.cpu.MockCpu;
import eu.faircode.xlua.api.objects.xmock.prop.MockProp;
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

            if(!StringUtil.isValidString(propValue))
                return null;

            if(MockUtils.isPropVxpOrLua(propValue)) {
                Log.i(TAG, "Skipping Property avoid Stack Overflow / Recursion");
                return null;
            }

            if(DebugUtil.isDebug())
                Log.i(TAG, "Checking Property=" + propValue);

            fakeValue = MockUtils.filterProperty(propValue, XMockCallApi.getMockProps(context));
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
            MockCpu mockCpu = XMockCallApi.getSelectedMockCpu(context);
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
        //   ro.treble.enabled
        //   ro.boot.dynamic_partitions
        //   ro.boot.verifiedbootstate
        //   ro.boot.flash.locked
        //   ro.allow.mock.location
        //   ro.adv.secure
        //   ro.baseband
        //   ro.boot.hardware.sku
        //   ro.boot.product.hardware.sku
        //   ro.bootmode
        //   ro.control_privapp_permissions




        //   ro.build.fingerprint                       =OnePlus/yaap_guacamoles/OnePlus7ProNR:13/TQ3A.230805.001/eng.ido.20231003.173955:user/release-keys
        //   ro.build.description                       =yaap_guacamoles-user 13 TQ3A.230805.001
        //   ro.build.id                                =TQ3A.230805.001
        //   ro.build.host                              =ido-X3999A
        //   ro.build.product                           =OnePlus7ProNR
        //   ro.build.user                              =ido
        //
        //   ro.build.version.codename                  =REL
        //   ro.build.version.incremental               =eng.ido.20231003.173955
        //   ro.build.version.release                   =13
        //   ro.build.version.release_or_codename       =13
        //   ro.build.version.release_or_preview_display=13
        //   ro.build.version.security_patch            =2023-10-05
        //
        //   ro.hardware                                =qcom
        //   ro.hardware.egl                            =adreno
        //   ro.hardware.keystore_desede                =true
        //   ro.hardware.vulkan                         =adreno
        //
        //   ro.vendor.build.fingerprint                =OnePlus/yaap_guacamoles/guacamoles:13/TQ3A.230805.001/ido10031738:user/release-keys
        //
        //   ro.product.board                           =msmnile
        //   ro.product.cpu.abi                         =arm64-v8a
        //   ro.product.cpu.abilist                     =arm64-v8a,armeabi-v7a,armeabi
        //   ro.product.cpu.abilist32                   =armeabi-v7a,armeabi
        //   ro.product.cpu.abilist64                   =arm64-v8a
        //
        //   ro.product.brand                           =OnePlus
        //   ro.product.device                          =OnePlus7ProNR
        //   ro.product.manufacturer                    =OnePlus
        //   ro.product.model                           =GM1920
        //   ro.product.name                            =yaap_guacamoles
        //
        //   ro.build.type                              =user
        //   ro.build.tags                              =release-keys
        //   ro.build.display.id                        =TQ3A.230805.001 release-keys
        //
        //   ro.debuggable                              =0
        //   ro.secure                                  =1
        //
        //   dalvik.vm.isa.arm.variant                  =cortext-a76
        //   dalvik.vm.isa.arm64.variant                =cortex-a76
        //
        //   gsm.network.type                           =Unknown
        //   gsm.operator.iso-country                   =us
        //   gsm.version.baseband                       =017-SM8150_GENFUSION_PACK-1.299167.1.399259.3
        //   gsm.version.ril-impl                       =Qualcomm RIL 1.0
        //
        //   net.bt.name                                =Android
        //   persist.sys.timezone                       =America/New_York
        //
        //   ro.adb.secure                              =1
        //   ro.allow.mock.location                     =0
        //   ro.baseband                                =mdm
        //   ro.board.platform                          =msmnile
        //
        //   ro.boot.flash.locked                       =1
        //   ro.boot.hardware                           =qcom
        //   ro.boot.hardware.sku                       =pn553
        //   ro.boot.mode                               =normal
        //   ro.boot.product.hardware.sku               =noese
        //
        //   ro.boot.verifiedbootstate                  =green
        //   ro.bootloader                              =unknown
        //   ro.bootmode                                =normal
        //
        //   ro.build.date                              =Tue Oct 3 17:38:39 IDT 2023
        //   ro.build.date.utc                          =1696343919
        //   ro.build.flavor                            =yaap_guacamoles-user
        //   ro.build.system_root_image                 =true
        //   ro.build.version.min_supported_target_sdk  =23
        //   ro.build.version.sdk                       =33
        //
        //   ro.carrier                                 =unknown
        //
        //   ro.com.google.clientidbase                 =android-oneplus
        //
        //   ro.control_privapp_permissions             =enforce
        //
        //   ro.crypto.state                            =encrypted
        //   ro.crypto.type                             =file
        //   ro.crypto.uses_fs_ioc_add_encryption_key   =true
        //   ro.crypto.volume.filenames_mode            =aes-256-cts
        //
        //   ro.frp.pst                                 =/dev/block/bootdevice/by-name/config
        //   ro.netflix.bsp_rev                         =Q855-16947-1
        //
        //   ro.odm.build.date.utc                      =1696343919
        //   ro.odm.build.date                          =Tue Oct 3 17:38:39 IDT 2023
        //   ro.odm.build.fingerprint                   =OnePlus/yaap_guacamoles/guacamoles:13/TQ3A.230805.001/ido10031738:user/release-keys
        //   ro.odm.build.version.incremental           =eng.ido.20231003.173995
        //
        //   ro.opengls.version                         =19660
        //
        //   ro.product.first_api_level                 =28
        //   ro.product.locale                          =en-US
        //
        //   ro.product.odm.brand                       =OnePlus
        //   ro.product.odm.device                      =OnePlus7ProNR
        //   ro.product.odm.manufacturer                =OnePlus
        //   ro.product.odm.model                       =GM1920
        //   ro.product.odm.name                        =yaap_guacamoles
        //
        //   ro.product.system.brand                    =OnePlus
        //   ro.product.system.device                   =OnePlus7ProNR
        //   ro.product.system.manufacturer             =OnePlus
        //   ro.product.system.model                    =GM1920
        //   ro.product.system.name                     =OnePlus7ProNR
        //
        //   ro.product.system_ext.brand                =OnePlus
        //   ro.product.system_ext.device               =OnePlus7ProNR
        //   ro.product.system_ext.manufacturer         =OnePlus
        //   ro.product.system_ext.model                =GM1920
        //   ro.product.system_ext.name                 =yaap_guacamoles
        //
        //   ro.product.vendor.brand                    =OnePlus
        //   ro.product.vendor.device                   =OnePlus7ProNR
        //   ro.product.vendor.manufacturer             =OnePlus
        //   ro.product.vendor.model                    =GM1920
        //   ro.product.vendor.name                     =yaap_guacamoles
        //
        //   ro.system.build.date                       =Tue Oct 3 17:38:39 IDT 2023
        //   ro.system.build.date.utc                   =1696343919
        //   ro.system.build.fingerprint                =OnePlus/yaap_guacamoles/guacamoles:13/TQ3A.230805.001/ido10031738:user/release-keys
        //   ro.system.build.id                         =TQ3A.230805.001
        //   ro.system.build.tags                       =release-keys
        //   ro.system.build.type                       =user
        //   ro.system.build.version.incremental        =eng.ido.20231003.173955
        //   ro.system.build.version.release            =13
        //   ro.system.build.version.release_or_codename=13
        //   ro.system.build.version.sdk                =33
        //
        //   ro.system_ext.build.date                   =Tue Oct 3 17:38:39 IDT 2023
        //   ro.system_ext.build.date.utc               =1696343919
        //   ro.system_ext.build.fingerprint            =OnePlus/yaap_guacamoles/guacamoles:13/TQ3A.230805.001/ido10031738:user/release-keys
        //   ro.system_ext.build.id                     =TQ3A.230805.001
        //   ro.system_ext.build.tags                   =release-keys
        //   ro.system_ext.build.type                   =user
        //   ro.system_ext.build.version.incremental    =eng.ido.20231003.173955
        //   ro.system_ext.build.version.release        =13
        //   ro.system_ext.build.version.release_or_codename=13
        //   ro.system_ext.build.version.sdk            =33
        //
        //   ro.vendor.build.date                       =Tue oct 3 17:38:39 IDT 2023
        //   ro.vendor.build.date.utc                   =1696343919
        //   ro.vendor.build.id                         =TQ3A.230805.001
        //   ro.vendor.build.security_patch             =2022-06-01
        //   ro.vendor.build.tags                       =release-keys
        //   ro.vendor.build.type                       =user
        //   ro.vendor.build.version.incremental        =eng.ido.20231003.173955
        //   ro.vendor.build.version.release            =13
        //   ro.vendor.build.version.release_or_codename=13
        //   ro.vendor.build.version.sdk                =33
        //   ro.vendor.product.cpu.abilist              =arm64-v8a,armeabi-v7a,armeabi
        //   ro.vendor.product.cpu.abilist32            =armeabi-v7a,armeabi
        //   ro.vendor.product.cpu.abilist64            =arm64-v8a


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
        /*try {
            String[] cmdline = { "sh", "-c", "echo " + fakeValue};
            return Runtime.getRuntime().exec(cmdline);
        }catch (Exception e) {
            Log.e(TAG, "Failed to start Dummy Process: " + e);
            return null;
        }*/
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
