package com.browser.core.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import com.browser.downloader.data.local.Constant;

public class AdUtil {

    public static void loadBanner(Context context, ViewGroup layoutBanner, AdSize adSize, boolean isGoneWhileLoading) {
        AdView adView = new AdView(context);
        adView.setAdSize(adSize);
        adView.setAdUnitId(Constant.AD_BANNER_ID);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        if (isGoneWhileLoading) {
            adView.setVisibility(View.GONE);
        }
        layoutBanner.addView(adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (isGoneWhileLoading) {
                    adView.setVisibility(View.VISIBLE);
                }
                super.onAdLoaded();
            }
        });
    }

    public static void loadInterstitialAd(InterstitialAd interstitialAd, AdListener adListener) {
        interstitialAd.setAdUnitId(Constant.AD_INTERSTITIAL_ID);
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(adListener);
    }

}