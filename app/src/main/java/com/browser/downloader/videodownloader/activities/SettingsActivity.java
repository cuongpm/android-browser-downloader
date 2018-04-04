package com.browser.downloader.videodownloader.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.databinding.ActivitySettingsBinding;

import butterknife.ButterKnife;
import butterknife.OnClick;
import vd.core.common.Constant;
import vd.core.util.FileUtil;
import vd.core.util.IntentUtil;

public class SettingsActivity extends BaseActivity {

    ActivitySettingsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        mBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        mBinding.tvFolder.setText(FileUtil.getFolderDir().getPath());

        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.screen_settings), "");
    }

    @Override
    public void onResume() {
        trackView(getString(R.string.screen_settings));
        super.onResume();
    }


    @OnClick(R.id.layout_folder)
    public void clickFolder() {
        IntentUtil.openFolder(this, FileUtil.getFolderDir().getPath());
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_folder), "");
    }

    @OnClick(R.id.layout_rate_us)
    public void clickRate() {
        IntentUtil.openGooglePlay(this, getPackageName());
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_rate_us), "");
    }

    @OnClick(R.id.layout_share)
    public void clickShare() {
        IntentUtil.shareLink(this, String.format(Constant.GOOGLE_PLAY_LINK, getPackageName()));
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_share_app), "");
    }
}
