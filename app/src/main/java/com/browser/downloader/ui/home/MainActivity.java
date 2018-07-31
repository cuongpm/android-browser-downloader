package com.browser.downloader.ui.home;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;

import com.browser.core.BuildConfig;
import com.browser.core.R;
import com.browser.core.databinding.ActivityMainBinding;
import com.browser.core.mvp.BaseTiActivity;
import com.browser.core.util.AdUtil;
import com.browser.core.util.AppUtil;
import com.browser.core.util.DialogUtil;
import com.browser.core.util.FileUtil;
import com.browser.core.util.IntentUtil;
import com.browser.downloader.callback.DialogListener;
import com.browser.downloader.data.model.ConfigData;
import com.browser.downloader.data.model.Video;
import com.browser.downloader.ui.adapter.HomeAdapter;
import com.browser.downloader.ui.dialog.RatingDialog;
import com.browser.downloader.ui.dialog.UpdateDialog;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

public class MainActivity extends BaseTiActivity<MainPresenter, MainView> implements MainView {

    ActivityMainBinding mBinding;

    private int mPagePosition = 0;

    private InterstitialAd mInterstitialAd;

    private boolean isGetLinkSuccess = false;

    private boolean isAdShowed = false;

    @NonNull
    @Override
    public MainPresenter providePresenter() {
        return new MainPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);
        initUI();

        // Show ad banner
        AdUtil.loadBanner(this, mBinding.layoutBanner, AdSize.BANNER, true);

        // Load ad interstitial
        mInterstitialAd = new InterstitialAd(this);
        getPresenter().loadInterstitialAd(mInterstitialAd);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initUI() {
        HomeAdapter adapter = new HomeAdapter(getSupportFragmentManager());
        mBinding.viewPager.setAdapter(adapter);
        mBinding.viewPager.setOffscreenPageLimit(5);

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPagePosition = position;
                mBinding.bottomBar.setDefaultTabPosition(position);
                // google analytics
                String category = getString(position == 0 ? R.string.screen_browser
                        : position == 1 ? R.string.screen_progress : position == 2 ? R.string.screen_video
                        : position == 3 ? R.string.screen_online : R.string.screen_settings);
                String action = (position == 1 ? (getPresenter().getProgress().size() + "")
                        : position == 2 ? (FileUtil.getListFiles().size() + "")
                        : position == 3 ? (getPresenter().getSavedVideos().size() + "") : "");
                trackEvent(getString(R.string.app_name), category, action);
                trackView(category);
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
                getPresenter().setTabVideoBadge(0);
                mBinding.bottomBar.getTabWithId(tabId).removeBadge();
            } else if (tabId == R.id.tab_online) {
                mBinding.viewPager.setCurrentItem(3, true);
                getPresenter().setTabOnlineBadge(0);
                mBinding.bottomBar.getTabWithId(tabId).removeBadge();
            } else {
                mBinding.viewPager.setCurrentItem(4, true);
            }
        });

        // show badge in progress tab
        showProgressBadge();

        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.screen_browser), "");
        trackView(getString(R.string.screen_browser));

        // Check retention
        trackEvent(getString(R.string.app_name), getString(R.string.action_check_user), AppUtil.getRetentionTime(this));

        // Update dialog
        showUpdateDialog();
    }

    @Subscribe
    public void onDownloadVideo(Video video) {
        // show badge in progress tab
        showProgressBadge();

        // show badge in video tab
        if (video.isDownloadCompleted()) {
            getPresenter().setTabVideoBadge(getPresenter().getTabVideoBadge() + 1);
            mBinding.bottomBar.getTabWithId(R.id.tab_video).setBadgeCount(getPresenter().getTabVideoBadge());
        }
    }

    public void showUpdateDialog() {
        ConfigData configData = getPresenter().getConfigData();
        if (configData != null && configData.isUpdateApp() && !TextUtils.isEmpty(configData.getAppVersion())
                && !configData.getAppVersion().equals(BuildConfig.VERSION_NAME)) {
            UpdateDialog.getDialog(this, configData.getAppVersion(), view -> {
                IntentUtil.openGooglePlay(MainActivity.this, getPackageName());
                // google analytics
                trackEvent(getString(R.string.app_name), getString(R.string.action_update), BuildConfig.VERSION_NAME);
            }).show();
        }
    }

    private void showProgressBadge() {
        new Handler().postDelayed(() -> {
            int progressBadge = getPresenter().getProgress().size();
            if (progressBadge > 0) {
                mBinding.bottomBar.getTabWithId(R.id.tab_progress).setBadgeCount(progressBadge);
            } else {
                mBinding.bottomBar.getTabWithId(R.id.tab_progress).removeBadge();
            }
        }, 1000);
    }

    public void showOnlineTabBadge() {
        getPresenter().setTabOnlineBadge(getPresenter().getTabOnlineBadge() + 1);
        mBinding.bottomBar.getTabWithId(R.id.tab_online).setBadgeCount(getPresenter().getTabOnlineBadge());
    }

    @Override
    public void onBackPressed() {
        if (mPagePosition != 0) {
            mBinding.viewPager.setCurrentItem(0, true);
            return;
        }

        if ((isGetLinkSuccess || !getPresenter().isFirstTime()) && !getPresenter().isRateApp()) {
            RatingDialog.getDialog(this, new DialogListener() {
                @Override
                public void onPositiveButton(Dialog dialog) {
                    dialog.dismiss();
                    getPresenter().setRateApp(true);
                    IntentUtil.openGooglePlay(MainActivity.this, getPackageName());
                    // google analytics
                    trackEvent(getString(R.string.app_name), getString(R.string.action_rate_us_exit_app), "");
                    finish();
                }

                @Override
                public void onNegativeButton(Dialog dialog) {
                    dialog.dismiss();
                    finish();
                }
            }).show();
        } else {
            DialogUtil.showAlertDialog(this, getString(R.string.app_name), getString(R.string.message_exit),
                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
                    });
        }

        // set first time data
        getPresenter().setFirstTime(false);
    }

    public void showInterstitlaAd() {
        if (!isAdShowed && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            isAdShowed = true;
            // google analytics
            trackEvent(getString(R.string.app_name), getString(R.string.action_show_ad_app), "Admob");
        }
    }

    public void showInterstitialAdFullPosition() {
        // Get config
        ConfigData configData = getPresenter().getConfigData();
        // Check show ad
        boolean isShowAdFullPosition = configData == null ? true : configData.isShowAdAppFull();
        // Show ad full position
        if (isShowAdFullPosition) showInterstitlaAd();
    }

    public boolean isGetLinkSuccess() {
        return isGetLinkSuccess;
    }

    public void setGetLinkSuccess(boolean getLinkSuccess) {
        isGetLinkSuccess = getLinkSuccess;
    }
}