package vd.core.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import vd.core.common.Constant;

public class AdUtil {

    public static void showBanner(Context context, ViewGroup layoutBanner) {
        final AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(Constant.AD_BANNER_ID);
        AdRequest adRequest =
                new AdRequest.Builder().addTestDevice("9B52FAA26A296F8A9D559226B0DB9F2E").build();
        adView.loadAd(adRequest);
        layoutBanner.addView(adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                adView.setVisibility(View.GONE);
                super.onAdFailedToLoad(i);
            }
        });
    }

    public static void showInterstitialAd(InterstitialAd interstitialAd, AdListener adListener) {
        interstitialAd.setAdUnitId(Constant.AD_INTERSTITIAL_ID);
        AdRequest adRequest =
                new AdRequest.Builder().addTestDevice("9B52FAA26A296F8A9D559226B0DB9F2E").build();
        interstitialAd.loadAd(adRequest);
        interstitialAd.setAdListener(adListener);
    }

}