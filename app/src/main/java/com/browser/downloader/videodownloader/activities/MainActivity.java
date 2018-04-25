package com.browser.downloader.videodownloader.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.adapter.HomeAdapter;
import com.browser.downloader.videodownloader.data.Video;
import com.browser.downloader.videodownloader.databinding.ActivityMainBinding;
import com.google.android.gms.ads.MobileAds;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import vd.core.common.Constant;
import vd.core.util.AdUtil;
import vd.core.util.AppUtil;
import vd.core.util.DialogUtil;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mBinding;

    private int mPagePosition = 0;

    private IOnBackPressed mIOnBackPressed;

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
                mPreferenceManager.setTabBadge(tabId, 0);
                mBinding.bottomBar.getTabWithId(tabId).removeBadge();
            } else {
                mBinding.viewPager.setCurrentItem(3, true);
            }
        });

        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.screen_browser), "");
        trackView(getString(R.string.screen_browser));
    }

    @Subscribe
    public void onDownloadVideo(Video video) {
        if (video.isDownloadCompleted()) {
            // hide badge in progress tab
            int progressBadge = mPreferenceManager.getTabBadge(R.id.tab_progress) - 1;
            if (progressBadge > 0) {
                mPreferenceManager.setTabBadge(R.id.tab_progress, progressBadge);
                mBinding.bottomBar.getTabWithId(R.id.tab_progress).setBadgeCount(progressBadge);
            } else {
                mPreferenceManager.setTabBadge(R.id.tab_progress, 0);
                mBinding.bottomBar.getTabWithId(R.id.tab_progress).removeBadge();
            }
            // show badge in video tab
            mPreferenceManager.setTabBadge(R.id.tab_video, mPreferenceManager.getTabBadge(R.id.tab_video) + 1);
            mBinding.bottomBar.getTabWithId(R.id.tab_video).setBadgeCount(mPreferenceManager.getTabBadge(R.id.tab_video));
        } else {
            // show badge in progress tab
            mPreferenceManager.setTabBadge(R.id.tab_progress, mPreferenceManager.getTabBadge(R.id.tab_progress) + 1);
            mBinding.bottomBar.getTabWithId(R.id.tab_progress).setBadgeCount(mPreferenceManager.getTabBadge(R.id.tab_progress));
        }
    }

    @Override
    public void onBackPressed() {
        if (mIOnBackPressed == null || !mIOnBackPressed.onBackPressed()) {
            if (mPagePosition != 0) {
                mBinding.viewPager.setCurrentItem(0, true);
                return;
            }

            if (!mPreferenceManager.isRateApp() && AppUtil.isDownloadVideo) {
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
}