package com.browser.downloader.videodownloader.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.adapter.VideoAdapter;
import com.browser.downloader.videodownloader.data.ConfigData;
import com.browser.downloader.videodownloader.data.Video;
import com.browser.downloader.videodownloader.databinding.FragmentVideoBinding;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import vd.core.util.AdUtil;
import vd.core.util.DialogUtil;
import vd.core.util.FileUtil;

public class VideoFragment extends BaseFragment {

    FragmentVideoBinding mBinding;

    VideoAdapter mVideoAdapter;

    private ArrayList<File> mListFiles;

    private InterstitialAd mInterstitialAd;

    private boolean isAdShowed = false;

    public static VideoFragment getInstance() {
        return new VideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_video, container, false);
        initUI();

        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.screen_video), "");

//        // Show ad banner
//        AdUtil.showBanner(this, mBinding.layoutBanner);

        // Load ad interstitial
        loadInterstitialAd();

        return mBinding.getRoot();
    }


//    @Override
//    public void onResume() {
//        trackView(getString(R.string.screen_video));
//        super.onResume();
//    }

    @Subscribe
    public void onDownloadVideo(Video video) {
        try {
            if (video.isDownloadCompleted()) {
                mListFiles.clear();
                mListFiles.addAll(FileUtil.getListFiles());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        mListFiles = FileUtil.getListFiles();
        mBinding.rvVideo.setLayoutManager(new LinearLayoutManager(getContext()));
        mVideoAdapter = new VideoAdapter(mListFiles, view -> {
            showInterstitlaAd();
            if (FileUtil.getListFiles().isEmpty()) {
                mBinding.tvNoVideo.setVisibility(View.VISIBLE);
            }
        });
        mBinding.rvVideo.setAdapter(mVideoAdapter);

        if (FileUtil.getListFiles().isEmpty()) {
            mBinding.tvNoVideo.setVisibility(View.VISIBLE);
        }
    }

    private void loadInterstitialAd() {
        // Empty data
        ArrayList<File> files = FileUtil.getListFiles();
        if (files == null || files.size() == 0) {
            return;
        }

        // Check show ad
        ConfigData configData = mPreferenceManager.getConfigData();
        boolean isShowAd = configData == null ? true : configData.isShowAdVideo();
        if (isShowAd) {
            DialogUtil.showSimpleProgressDialog(getContext());
            mInterstitialAd = new InterstitialAd(getContext());
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
