package eu.faircode.xlua;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import eu.faircode.xlua.api.app.XLuaApp;
import eu.faircode.xlua.api.hook.assignment.XLuaAssignment;
import eu.faircode.xlua.utilities.StringUtil;

public class AppGeneric {
    private static final String TAG = "XLua.AppGeneric";

    private int icon;
    private int uid;
    private String name;
    private String packageName;

    public static AppGeneric from(Bundle b, Context context) {
        if(b == null || !b.containsKey("packageName"))
            return new AppGeneric(null, context);

        return new AppGeneric(b.getString("packageName"), context);
    }


    public AppGeneric(String packageName, Context context) {
        if(!StringUtil.isValidString(packageName) ||  packageName.equalsIgnoreCase("global")) {
            this.name = "Global";
            this.packageName = "Global";
            this.uid = 0;
            this.icon = -1;
        }else {
            try {
                Log.i(TAG, "Grabbing app info: " + packageName);
                this.packageName = packageName;
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
                Log.i(TAG, "Getting ICON");
                this.icon = appInfo.icon;
                Log.i(TAG, "Grabbed ICON=" + this.icon);
                this.uid = appInfo.uid;
                this.name = (String) packageManager.getApplicationLabel(appInfo);
                //this.name = appInfo.loadLabel(packageManager);
                // Get the application icon
                //Drawable appIcon = pm.getApplicationIcon(appInfo);
            }catch (Exception e) {
                Log.e(TAG, "Failed to grab Application Info: " + packageName + " " + e);
            }
        }
    }

    public int getIcon() { return icon; }
    public int getUid() { return uid; }
    public String getName() { return name; }
    public String getPackageName() { return packageName; }

    public void initIcon(ImageView ivAppIcon, Context context) {
        Log.i(TAG, "Setting icon s=" + toString());
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.listPreferredItemHeight, typedValue, true);
        int height = TypedValue.complexToDimensionPixelSize(typedValue.data, context.getResources().getDisplayMetrics());
        int iconSize = Math.round(height * context.getResources().getDisplayMetrics().density + 0.5f);

        Log.i(TAG, "ICON HEIGHT=" + height + " SIZE=" + iconSize);

        // App icon
        try {
            if (icon <= 0)
                ivAppIcon.setImageResource(android.R.drawable.sym_def_app_icon);
            else {
                Log.i(TAG, "Setting with glid app");
                Uri uri = Uri.parse("android.resource://" + packageName + "/" + icon);
                GlideApp.with(context)
                        .applyDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565))
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .override(iconSize, iconSize)
                        .into(ivAppIcon);
            }
        }catch (Exception e) {
            Log.e(TAG, "Failed to set AppIcon: " + packageName + " " + e);
        }
    }

    public boolean isGlobal() {
        if(!StringUtil.isValidString(packageName))
            return false;

        return packageName.equalsIgnoreCase("global");
    }

    @NonNull
    @Override
    public String toString() {
        return "pkg=" + packageName + " uid=" + uid + " name=" + name + " ico=" + icon;
    }
}
