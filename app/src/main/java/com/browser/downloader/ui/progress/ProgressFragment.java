package com.browser.downloader.ui.progress;

import android.app.DownloadManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.core.R;
import com.browser.core.databinding.FragmentProgressBinding;
import com.browser.core.mvp.BaseTiFragment;
import com.browser.core.util.FileUtil;
import com.browser.downloader.data.model.ProgressInfo;
import com.browser.downloader.data.model.Video;
import com.browser.downloader.ui.adapter.ProgressAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

public class ProgressFragment extends BaseTiFragment<ProgressPresenter, ProgressView> implements ProgressView {

    FragmentProgressBinding mBinding;

    private ProgressAdapter mProgressAdapter;

    private DownloadManager mDownloadManager;

    public static ProgressFragment getInstance() {
        return new ProgressFragment();
    }

    @NonNull
    @Override
    public ProgressPresenter providePresenter() {
        return new ProgressPresenter();
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_progress, container, false);
        ButterKnife.bind(this, mBinding.getRoot());
        initUI();

        return mBinding.getRoot();
    }

    private void initUI() {
        // Check saved videos
        mDownloadManager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        for (ProgressInfo progressInfo : getPresenter().getProgressInfos()) {
            getPresenter().checkDownloadProgress(progressInfo, mDownloadManager);
        }

        mBinding.rvProgress.setLayoutManager(new LinearLayoutManager(mActivity));
        mProgressAdapter = new ProgressAdapter(getPresenter().getProgressInfos());
        mBinding.rvProgress.setAdapter(mProgressAdapter);

        showEmptyData();
    }

    private void showEmptyData() {
        if (getPresenter().getProgress().isEmpty()) {
            mBinding.layoutNoVideo.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutNoVideo.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onDownloadVideo(Video video) {
        if (!video.isDownloadCompleted()) {
            getPresenter().downloadVideo(video, mDownloadManager);
        }
    }

    @Override
    public void downloadDone(ProgressInfo progressInfo) {
        // Update badges & videos screen
        progressInfo.getVideo().setDownloadCompleted(true);
        EventBus.getDefault().post(progressInfo.getVideo());
        // Update progress screen
        getPresenter().getProgressInfos().remove(progressInfo);
        mProgressAdapter.notifyDataSetChanged();
        getPresenter().setProgress(getPresenter().getProgressInfos());
        showEmptyData();
        try {
            // google analytics
            String website = progressInfo.getVideo().getUrl();
            if (website.contains("/")) website = website.split("/")[2];
            trackEvent(getString(R.string.action_download_done), website, progressInfo.getVideo().getUrl());
        } catch (Exception e) {
            e.printStackTrace();
            trackEvent(mActivity.getString(R.string.action_download_done), progressInfo.getVideo().getUrl(), "");
        }
    }

    @Override
    public void downloadFailed(ProgressInfo progressInfo) {
        // Update progress screen
        getPresenter().getProgressInfos().remove(progressInfo);
        mProgressAdapter.notifyDataSetChanged();
        getPresenter().setProgress(getPresenter().getProgressInfos());
        showEmptyData();
        try {
            // google analytics
            String website = progressInfo.getVideo().getUrl();
            if (website.contains("/")) website = website.split("/")[2];
            trackEvent(getString(R.string.action_download_failed), website, progressInfo.getVideo().getUrl());
        } catch (Exception e) {
            e.printStackTrace();
            trackEvent(getString(R.string.action_download_failed), progressInfo.getVideo().getUrl(), "");
        }
    }

    @Override
    public void downloadProgress(ProgressInfo progressInfo) {
        double dlProgress = (progressInfo.getBytesDownloaded() * 100f / progressInfo.getBytesTotal());
        progressInfo.setProgress((int) dlProgress);
        progressInfo.setProgressSize(FileUtil.getFileSize(progressInfo.getBytesDownloaded())
                + "/" + FileUtil.getFileSize(progressInfo.getBytesTotal()));
        mProgressAdapter.notifyDataSetChanged();
        getPresenter().setProgress(getPresenter().getProgressInfos());
    }

    @Override
    public void updateProgress() {
        mProgressAdapter.notifyDataSetChanged();
        showEmptyData();
    }

}
