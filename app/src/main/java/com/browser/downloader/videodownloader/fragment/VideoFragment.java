package com.browser.downloader.videodownloader.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.adapter.VideoAdapter;
import com.browser.downloader.videodownloader.data.Video;
import com.browser.downloader.videodownloader.databinding.FragmentVideoBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import vd.core.util.FileUtil;

public class VideoFragment extends BaseFragment {

    FragmentVideoBinding mBinding;

    VideoAdapter mVideoAdapter;

    private ArrayList<File> mListFiles;

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
        return mBinding.getRoot();
    }

    @Subscribe
    public void onDownloadVideo(Video video) {
        try {
            if (video.isDownloadCompleted()) {
                mListFiles.clear();
                mListFiles.addAll(FileUtil.getListFiles());
                mVideoAdapter.notifyDataSetChanged();
                showEmptyData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        mListFiles = FileUtil.getListFiles();
        mBinding.rvVideo.setLayoutManager(new LinearLayoutManager(mActivity));
        mVideoAdapter = new VideoAdapter(mListFiles, view -> {
            mActivity.showInterstitlaAd();
            showEmptyData();
        });
        mBinding.rvVideo.setAdapter(mVideoAdapter);
        showEmptyData();
    }

    private void showEmptyData() {
        if (FileUtil.getListFiles().isEmpty()) {
            mBinding.layoutNoVideo.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutNoVideo.setVisibility(View.GONE);
        }
    }
}
