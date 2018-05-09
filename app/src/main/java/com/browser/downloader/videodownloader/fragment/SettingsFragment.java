package com.browser.downloader.videodownloader.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.databinding.FragmentSettingsBinding;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import vd.core.common.Constant;
import vd.core.util.AppUtil;
import vd.core.util.FileUtil;
import vd.core.util.IntentUtil;

public class SettingsFragment extends BaseFragment {

    FragmentSettingsBinding mBinding;

    public static SettingsFragment getInstance() {
        return new SettingsFragment();
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
        IntentUtil.openFolder(getContext(), FileUtil.getFolderDir().getPath());
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_folder), "");
    }

    @OnClick(R.id.layout_rate_us)
    public void clickRate() {
        IntentUtil.openGooglePlay(getContext(), getContext().getPackageName());
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_rate_us), "");
    }

    @OnClick(R.id.layout_share)
    public void clickShare() {
        IntentUtil.shareLink(getContext(), String.format(Constant.GOOGLE_PLAY_LINK, getContext().getPackageName()));
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_share_app), "");
    }

    @OnClick(R.id.layout_clear_history)
    public void clickClearHistory() {
        mPreferenceManager.setHistory(new ArrayList<>());
        Toast.makeText(getContext(), "Deleted browser history", Toast.LENGTH_SHORT).show();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_clear_history), "");
    }

    @OnClick(R.id.layout_clear_bookmark)
    public void clickClearBookmark() {
        mPreferenceManager.setBookmark(new ArrayList<>());
        Toast.makeText(getContext(), "Deleted browser bookmark", Toast.LENGTH_SHORT).show();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_clear_bookmark), "");
    }

    @OnClick(R.id.layout_clear_cookie)
    public void clickClearCookie() {
        AppUtil.clearCookies(getContext());
        Toast.makeText(getContext(), "Deleted browser cookies", Toast.LENGTH_SHORT).show();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_clear_cookie), "");
    }
}
