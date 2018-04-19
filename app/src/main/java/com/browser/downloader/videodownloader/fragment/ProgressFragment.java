package com.browser.downloader.videodownloader.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.databinding.FragmentProgressBinding;

import butterknife.ButterKnife;
import butterknife.OnClick;
import vd.core.common.Constant;
import vd.core.util.FileUtil;
import vd.core.util.IntentUtil;

public class ProgressFragment extends BaseFragment {

    FragmentProgressBinding mBinding;

    public static ProgressFragment getInstance() {
        return new ProgressFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_progress, container, false);
        ButterKnife.bind(this, mBinding.getRoot());
        initUI();

////        // Show ad banner
////        AdUtil.showBanner(this, mBinding.layoutBanner);
//
//        // Load ad interstitial
//        loadInterstitialAd();

        return mBinding.getRoot();
    }

    private void initUI() {
//        mBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        mBinding.tvFolder.setText(FileUtil.getFolderDir().getPath());

        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.screen_progress), "");
    }

//    @Override
//    public void onResume() {
//        trackView(getString(R.string.screen_progress));
//        super.onResume();
//    }


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
}
