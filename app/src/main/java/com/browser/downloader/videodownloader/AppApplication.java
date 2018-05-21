package com.browser.downloader.videodownloader;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.airpush.AirPush;
import com.applovin.sdk.AppLovinSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.google.android.gms.ads.MobileAds;

import vd.core.common.Constant;

public class AppApplication extends Application {

    private static AppLovinSdk mAppLovinSdk;

    @Override
    public void onCreate() {
        super.onCreate();

        // Init AppLovin
        AppLovinSdk.initializeSdk(this);
        mAppLovinSdk = AppLovinSdk.getInstance(this);
        mAppLovinSdk.getSettings().setTestAdsEnabled(false);

        // Init Airpush
        AirPush.init(this, "1526153644312398977", "391990");
//        AirPush.enableTestMode();

        // Init Admob
        MobileAds.initialize(this, Constant.AD_APP_ID);

        // Init Fresco
        Fresco.initialize(this, ImagePipelineConfig.newBuilder(this).setDownsampleEnabled(true).build());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static AppLovinSdk getAppLovinSdk() {
        return mAppLovinSdk;
    }
}
