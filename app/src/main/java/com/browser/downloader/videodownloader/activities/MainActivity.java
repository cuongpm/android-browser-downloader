package com.browser.downloader.videodownloader.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.data.model.StaticData;
import com.browser.downloader.videodownloader.databinding.ActivityMainBinding;

import butterknife.ButterKnife;
import butterknife.OnClick;
import vd.core.common.PreferencesManager;
import vd.core.util.AdUtil;
import vd.core.util.AppUtil;
import vd.core.util.DialogUtil;
import vd.core.util.ViewUtil;

public class MainActivity extends BaseActivity {

    ActivityMainBinding mBinding;

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
        mBinding.layoutItem.getLayoutParams().height = (ViewUtil.getScreenWidth() - ViewUtil.dpToPx(64)) / 3;
    }

    @OnClick(R.id.btn_browser)
    public void clickBrowser() {
        startActivity(new Intent(this, BrowserActivity.class));
    }

    @OnClick(R.id.btn_video)
    public void clickVideo() {
        startActivity(new Intent(this, VideoActivity.class));
    }

    @OnClick(R.id.btn_settings)
    public void clickSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public void onBackPressed() {
        StaticData staticData = PreferencesManager.getInstance(this).getStaticData();
        if (staticData != null && staticData.isShowRateApp()) {
            DialogUtil.showRateDialog(this);
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
