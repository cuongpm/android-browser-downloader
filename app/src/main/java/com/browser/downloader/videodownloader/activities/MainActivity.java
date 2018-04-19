package com.browser.downloader.videodownloader.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.adapter.HomeAdapter;
import com.browser.downloader.videodownloader.databinding.ActivityMainBinding;

import butterknife.ButterKnife;
import vd.core.common.PreferencesManager;
import vd.core.util.AdUtil;
import vd.core.util.AppUtil;
import vd.core.util.DialogUtil;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mBinding;

    private int mPagePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        ButterKnife.bind(this);
        initUI();

        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.screen_home), "");

        // Show ad banner
        AdUtil.showBanner(this, mBinding.layoutBanner);
    }

    @Override
    public void onResume() {
        trackView(getString(R.string.screen_home));
        super.onResume();
    }

    private void initUI() {
        HomeAdapter adapter = new HomeAdapter(getSupportFragmentManager());
        mBinding.viewPager.setAdapter(adapter);

        mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPagePosition = position;
                mBinding.bottomBar.setDefaultTabPosition(position);
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
            } else {
                mBinding.viewPager.setCurrentItem(2, true);
            }
        });
    }

//    @OnClick(R.id.btn_browser)
//    public void clickBrowser() {
//        startActivity(new Intent(this, BrowserFragment.class));
//    }
//
//    @OnClick(R.id.btn_video)
//    public void clickVideo() {
//        startActivity(new Intent(this, VideoFragment.class));
//    }
//
//    @OnClick(R.id.btn_settings)
//    public void clickSettings() {
//        startActivity(new Intent(this, SettingsFragment.class));
//    }

    @Override
    public void onBackPressed() {
        if (mPagePosition != 0) {
            mBinding.viewPager.setCurrentItem(0, true);
            return;
        }

        boolean isShowRate = PreferencesManager.getInstance(this).isRateApp();
        if (!isShowRate && AppUtil.isDownloadVideo) {
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