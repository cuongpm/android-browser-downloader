package com.browser.downloader.videodownloader.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.adapter.HomeAdapter;
import com.browser.downloader.videodownloader.data.ConfigData;
import com.browser.downloader.videodownloader.data.Video;
import com.browser.downloader.videodownloader.databinding.ActivityMainBinding;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import vd.core.common.Constant;
import vd.core.util.AdUtil;
import vd.core.util.DialogUtil;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mBinding;

    private int mPagePosition = 0;

    private IOnBackPressed mIOnBackPressed;

    private InterstitialAd mInterstitialAd;

    private boolean isAdShowed = false;

    private int numberActionShowAd = 0;

    private int totalActionShowAd = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);
        initUI();

        // Init Admob
        MobileAds.initialize(this, Constant.AD_APP_ID);

        // Show ad banner
        AdUtil.showBanner(this, mBinding.layoutBanner);

        // Load ad interstitial
        loadInterstitialAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initUI() {
        HomeAdapter adapter = new HomeAdapter(getSupportFragmentManager());
        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setOffscreenPageLimit(4);

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                showInterstitlaAd();
                mPagePosition = position;
                mBinding.bottomBar.setDefaultTabPosition(position);
                // google analytics
                trackEvent(getString(R.string.app_name), getString(position == 0
                        ? R.string.screen_browser : position == 1 ? R.string.screen_progress : position == 2
                        ? R.string.screen_video : R.string.screen_settings), "");
                trackView(getString(position == 0 ? R.string.screen_browser : position == 1
                        ? R.string.screen_progress : position == 2 ? R.string.screen_video : R.string.screen_settings));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mBinding.bottomBar.setOnTabSelectListener(tabId -> {
            if (tabId == R.id.tab_browser) {
                mBinding.viewPager.setCurrentItem(0, true);
            } else if (tabId == R.id.tab_progress) {
                mBinding.viewPager.setCurrentItem(1, true);
            } else if (tabId == R.id.tab_video) {
                mBinding.viewPager.setCurrentItem(2, true);
                mPreferenceManager.setTabVideoBadge(0);
                mBinding.bottomBar.getTabWithId(tabId).removeBadge();
            } else {
                mBinding.viewPager.setCurrentItem(3, true);
            }
        });

        // show badge in progress tab
        showProgressBadge();

        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.screen_browser), "");
        trackView(getString(R.string.screen_browser));
    }

    @Subscribe
    public void onDownloadVideo(Video video) {
        // show badge in progress tab
        showProgressBadge();

        // show badge in video tab
        if (video.isDownloadCompleted()) {
            mPreferenceManager.setTabVideoBadge(mPreferenceManager.getTabVideoBadge() + 1);
            mBinding.bottomBar.getTabWithId(R.id.tab_video).setBadgeCount(mPreferenceManager.getTabVideoBadge());
        }
    }

    private void showProgressBadge() {
        new Handler().postDelayed(() -> {
            int progressBadge = mPreferenceManager.getProgress().size();
            if (progressBadge > 0) {
                mBinding.bottomBar.getTabWithId(R.id.tab_progress).setBadgeCount(progressBadge);
            } else {
                mBinding.bottomBar.getTabWithId(R.id.tab_progress).removeBadge();
            }
        }, 1000);
    }

    @Override
    public void onBackPressed() {
        if (mIOnBackPressed == null || !mIOnBackPressed.onBackPressed()) {
            if (mPagePosition != 0) {
                mBinding.viewPager.setCurrentItem(0, true);
                return;
            }

            if (!mPreferenceManager.isRateApp()) {
                DialogUtil.showRateDialog(this);
            } else {
                DialogUtil.showAlertDialog(this, getString(R.string.app_name), getString(R.string.message_exit),
                        (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            finish();
                        });
            }
        }
    }

    public IOnBackPressed getIOnBackPressed() {
        return mIOnBackPressed;
    }

    public void setIOnBackPressed(IOnBackPressed mIOnBackPressed) {
        this.mIOnBackPressed = mIOnBackPressed;
    }

    public interface IOnBackPressed {
        boolean onBackPressed();
    }

    private void loadInterstitialAd() {
        // Check show ad
        ConfigData configData = mPreferenceManager.getConfigData();
        totalActionShowAd = configData == null ? totalActionShowAd : configData.getTotalActionShowAd();
        boolean isShowAd = configData == null ? true : configData.isShowAdApp();
        if (isShowAd) {
            mInterstitialAd = new InterstitialAd(this);
            AdUtil.showInterstitialAd(mInterstitialAd, null);
        }
    }

    public void showInterstitlaAd() {
        numberActionShowAd++;
        if (!isAdShowed && mInterstitialAd != null && mInterstitialAd.isLoaded() && (numberActionShowAd % totalActionShowAd == 0)) {
            isAdShowed = true;
            mInterstitialAd.show();
            // google analytics
            trackEvent(getString(R.string.app_name), getString(R.string.action_show_ad_app), "");
        }
    }
}