package com.browser.downloader;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.browser.core.logging.CrashlyticsLogExceptionTree;
import com.browser.core.logging.CrashlyticsLogTree;
import com.browser.core.logging.FileLoggingTree;
import com.browser.core.logging.FirebaseCrashLogExceptionTree;
import com.browser.core.logging.FirebaseCrashLogTree;
import com.browser.core.util.GlobalContext;
import com.browser.downloader.data.local.Constant;
import com.browser.downloader.injection.component.ApplicationComponent;
import com.browser.downloader.injection.component.DaggerApplicationComponent;
import com.browser.downloader.injection.module.ApplicationModule;
import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class AppApplication extends MultiDexApplication {

    private ApplicationComponent mApplicationComponent;

    private static AppApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        // singleton
        mApplication = this;

        // Init global context
        GlobalContext.setContext(this);

        // Init Firebase
        FirebaseApp.initializeApp(this);

        // Init Fabric
        Fabric.with(this, new Crashlytics());

        // Init Admob
        MobileAds.initialize(this, Constant.AD_APP_ID);

        // Init Fresco
        Fresco.initialize(this, ImagePipelineConfig.newBuilder(this).setDownsampleEnabled(true).build());

        // Init Timber
        initTimber();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initTimber() {
        Timber.plant(new Timber.DebugTree());
        Timber.plant(new FileLoggingTree(this));
        Timber.plant(new CrashlyticsLogExceptionTree());
        Timber.plant(new CrashlyticsLogTree(Log.INFO));
        Timber.plant(new FirebaseCrashLogExceptionTree());
        Timber.plant(new FirebaseCrashLogTree(Log.INFO));
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }

        return mApplicationComponent;
    }

    public static AppApplication getInstance() {
        return mApplication;
    }

}
