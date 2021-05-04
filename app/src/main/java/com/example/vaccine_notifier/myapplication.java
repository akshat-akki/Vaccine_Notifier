package com.example.vaccine_notifier;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

final public class myapplication extends Application {
    public void onCreate() {
        super.onCreate();
        ComponentName receiver = new ComponentName((Context)this, Myservice.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting( receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}

