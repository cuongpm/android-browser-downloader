package com.browser.downloader.videodownloader.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.adapter.VideoAdapter;
import com.browser.downloader.videodownloader.data.model.StaticData;
import com.browser.downloader.videodownloader.databinding.ActivityVideoBinding;

import java.io.File;
import java.util.ArrayList;

import vd.core.common.PreferencesManager;
import vd.core.util.AdUtil;
import vd.core.util.DialogUtil;
import vd.core.util.FileUtil;

public class VideoActivity extends BaseActivity {

    ActivityVideoBinding mBinding;

    VideoAdapter mVideoAdapter;

    private InterstitialAd mInterstitialAd;

    private boolean isAdShowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video);
        initUI();

        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.screen_video), "");

        // Show ad banner
        AdUtil.showBanner(this, mBinding.layoutBanner);

        // Load ad interstitial
        loadInterstitialAd();
    }

    @Override
    public void onResume() {
        trackView(getString(R.string.screen_video));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        showInterstitlaAd();
        finish();
    }

    private void initUI() {
        mBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        mBinding.rvVideo.setLayoutManager(new LinearLayoutManager(this));
        mVideoAdapter = new VideoAdapter(FileUtil.getListFiles(), view -> showInterstitlaAd());
        mBinding.rvVideo.setAdapter(mVideoAdapter);
    }

    private void loadInterstitialAd() {
        // Empty data
        ArrayList<File> files = FileUtil.getListFiles();
        if (files == null || files.size() == 0) {
            return;
        }

        // Check show ad
        StaticData staticData = PreferencesManager.getInstance(this).getStaticData();
        boolean isShowAd = staticData == null ? true : staticData.isShowAdVideo();
        if (isShowAd) {
            DialogUtil.showSimpleProgressDialog(this);
            mInterstitialAd = new InterstitialAd(this);
            AdUtil.showInterstitialAd(mInterstitialAd, new AdListener() {
                @Override
                public void onAdLoaded() {
                    DialogUtil.closeProgressDialog();
                    super.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    DialogUtil.closeProgressDialog();
                    super.onAdFailedToLoad(i);
                }
            });
        }
    }

    private void showInterstitlaAd() {
        if (!isAdShowed && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            isAdShowed = true;
            mInterstitialAd.show();
            // google analytics
            trackEvent(getResources().getString(R.string.app_name), getString(R.string.action_show_ad_video), "");
        }
    }
}
