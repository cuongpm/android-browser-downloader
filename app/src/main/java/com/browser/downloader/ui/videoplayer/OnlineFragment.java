package com.browser.downloader.ui.videoplayer;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.core.R;
import com.browser.core.databinding.FragmentOnlineBinding;
import com.browser.core.mvp.BaseTiFragment;
import com.browser.downloader.data.model.SavedVideo;
import com.browser.downloader.data.model.Video;
import com.browser.downloader.ui.adapter.SavedVideoAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class OnlineFragment extends BaseTiFragment<OnlinePresenter, OnlineView> implements OnlineView {

    FragmentOnlineBinding mBinding;

    private SavedVideoAdapter mSavedVideoAdapter;

    private ArrayList<Video> mVideos;

    public static OnlineFragment getInstance() {
        return new OnlineFragment();
    }

    @NonNull
    @Override
    public OnlinePresenter providePresenter() {
        return new OnlinePresenter();
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
        mVideos = getPresenter().getSavedVideos();
        mBinding.rvVideo.setLayoutManager(new LinearLayoutManager(mActivity));
        mSavedVideoAdapter = new SavedVideoAdapter(mVideos, view -> {
            String tag = (String) view.getTag();
            if (!TextUtils.isEmpty(tag) && tag.equals(VideoPlayerActivity.class.getSimpleName())) {
                mActivity.showInterstitlaAd();
            }
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
