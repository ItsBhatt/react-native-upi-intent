package com.reactnativeupiintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;  
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.List;

// Import the utility class from its new location
import com.spinnypay.util.DrawableToBase64Util;

public class UPIModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private static final int REQUEST_CODE = 123;
    private ReactApplicationContext mReactContext;

    public UPIModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;
        reactContext.addActivityEventListener(this);
    }

    @NonNull
    @Override
    public String getName() {
        return "UPIModule";
    }

    // Method from UPIModule to check if a specific app is available
    @ReactMethod
    public void checkAppAvailability(String packageName, Promise promise) {
        PackageManager pm = mReactContext.getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            promise.resolve(true);
        } catch (PackageManager.NameNotFoundException e) {
            promise.resolve(false);
        }
    }

    // Method from UPIModule to check if a specific app is UPI ready
    @ReactMethod
    public void checkAppUpiReady(String packageName, Promise promise) {
        boolean appUpiReady = false;
        Intent upiIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("upi://pay"));
        PackageManager pm = mReactContext.getPackageManager();
        List<ResolveInfo> upiActivities = pm.queryIntentActivities(upiIntent, 0);
        for (ResolveInfo a : upiActivities) {
            if (a.activityInfo.packageName.equals(packageName)) appUpiReady = true;
        }
        promise.resolve(appUpiReady);
    }

    // Method from UPIModule to initiate a UPI payment
    @ReactMethod
    public void initiateUPIPayment(String packageName, String uri, Promise promise) {
        Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.reject("Activity doesn't exist");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage(packageName);

        try {
            activity.startActivityForResult(intent, REQUEST_CODE);
            promise.resolve(true);
        } catch (Exception e) {
            promise.reject("UPI Payment initiation failed", e);
        }
    }

    // Method from UPIInstalledAppsModule to get the list of installed UPI apps
    @ReactMethod
    public void getInstalledUPIAppLists(Promise promise) {
        try {
            WritableArray installedAppList = Arguments.createArray();
            Uri uri = Uri.parse("upi://pay");
            Intent upiUriIntent = new Intent();
            upiUriIntent.setData(uri);
            PackageManager pm = mReactContext.getPackageManager();
            List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(upiUriIntent, PackageManager.MATCH_DEFAULT_ONLY);

            if (resolveInfoList != null) {
                for (ResolveInfo resolveInfo : resolveInfoList) {
                    WritableMap appInfoMap = Arguments.createMap();
                    appInfoMap.putString("name", resolveInfo.activityInfo.loadLabel(pm).toString());
                    appInfoMap.putString("code", resolveInfo.activityInfo.packageName);
                    appInfoMap.putString("icon", DrawableToBase64Util.drawableToBase64(resolveInfo.activityInfo.loadIcon(pm)));
                    installedAppList.pushMap(appInfoMap);
                }
            }
            promise.resolve(installedAppList);
        } catch (Exception ex) {
            promise.reject(ex);
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            WritableMap result = Arguments.createMap();
            if (data != null) {
                // String status = data.getStringExtra("Status");
                // result.putString("Status", status);
                // String response = data.getStringExtra("response");
                // result.putString("response", response != null ? response : ""); // Ensure response is not null
                Bundle extras = data.getExtras();
                if (extras != null) {
                    for (String key : extras.keySet()) {
                        Object value = extras.get(key);
                        if (value != null) {
                            result.putString(key, value.toString());
                        }
                    }
                }
            } else {
                result.putString("Status", "failed");
                result.putString("response", ""); // Set an empty response in case of null data
            }
            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("UPIResponse", result);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {}
}
