package com.browser.downloader.ui.settings;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.browser.core.R;
import com.browser.core.databinding.FragmentSettingsBinding;
import com.browser.core.mvp.BaseTiFragment;
import com.browser.core.util.AppUtil;
import com.browser.core.util.FileUtil;
import com.browser.core.util.IntentUtil;
import com.browser.downloader.data.local.Constant;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends BaseTiFragment<SettingsPresenter, SettingsView> implements SettingsView {

    FragmentSettingsBinding mBinding;

    public static SettingsFragment getInstance() {
        return new SettingsFragment();
    }

    @NonNull
    @Override
    public SettingsPresenter providePresenter() {
        return new SettingsPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, mBinding.getRoot());
        initUI();

        return mBinding.getRoot();
    }

    private void initUI() {
        mBinding.tvFolder.setText(FileUtil.getFolderDir().getPath());
    }


    @OnClick(R.id.layout_folder)
    public void clickFolder() {
        IntentUtil.openFolder(mActivity, FileUtil.getFolderDir().getPath());
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_folder), "");
    }

    @OnClick(R.id.layout_rate_us)
    public void clickRate() {
        IntentUtil.openGooglePlay(mActivity, mActivity.getPackageName());
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_rate_us), "");
    }

    @OnClick(R.id.layout_share)
    public void clickShare() {
        IntentUtil.shareLink(mActivity, String.format(Constant.GOOGLE_PLAY_LINK, mActivity.getPackageName()));
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_share_app), "");
    }

    @OnClick(R.id.layout_clear_history)
    public void clickClearHistory() {
        getPresenter().setHistory(new ArrayList<>());
        Toast.makeText(mActivity, "Deleted browser history", Toast.LENGTH_SHORT).show();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_clear_history), "");
    }

    @OnClick(R.id.layout_clear_bookmark)
    public void clickClearBookmark() {
        getPresenter().setBookmark(new ArrayList<>());
        Toast.makeText(mActivity, "Deleted browser bookmark", Toast.LENGTH_SHORT).show();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_clear_bookmark), "");
    }

    @OnClick(R.id.layout_clear_cookie)
    public void clickClearCookie() {
        AppUtil.clearCookies(mActivity);
        Toast.makeText(mActivity, "Deleted browser cookies", Toast.LENGTH_SHORT).show();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_clear_cookie), "");
    }
}
