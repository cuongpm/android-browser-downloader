package com.browser.downloader.videodownloader.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.adapter.SavedVideoAdapter;
import com.browser.downloader.videodownloader.data.SavedVideo;
import com.browser.downloader.videodownloader.data.Video;
import com.browser.downloader.videodownloader.databinding.FragmentOnlineBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class OnlineFragment extends BaseFragment {

    FragmentOnlineBinding mBinding;

    private SavedVideoAdapter mSavedVideoAdapter;

    private ArrayList<Video> mVideos;

    public static OnlineFragment getInstance() {
        return new OnlineFragment();
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_online, container, false);
        initUI();
        return mBinding.getRoot();
    }

    @Subscribe
    public void onDownloadVideo(SavedVideo savedVideo) {
        mVideos.add(savedVideo.getVideo());
        mSavedVideoAdapter.notifyDataSetChanged();
        showEmptyData();
    }

    private void initUI() {
        mVideos = mPreferenceManager.getSavedVideos();
        mBinding.rvVideo.setLayoutManager(new LinearLayoutManager(getContext()));
        mSavedVideoAdapter = new SavedVideoAdapter(mVideos, view -> {
            mActivity.showInterstitlaAd();
            showEmptyData();
        });
        mBinding.rvVideo.setAdapter(mSavedVideoAdapter);
        showEmptyData();
    }

    private void showEmptyData() {
        if (mVideos.isEmpty()) {
            mBinding.layoutNoVideo.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutNoVideo.setVisibility(View.GONE);
        }
    }
}
