package com.browser.downloader.videodownloader.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.adapter.ProgressAdapter;
import com.browser.downloader.videodownloader.data.ProgressInfo;
import com.browser.downloader.videodownloader.data.Video;
import com.browser.downloader.videodownloader.databinding.FragmentProgressBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import vd.core.util.FileUtil;

public class ProgressFragment extends BaseFragment {

    FragmentProgressBinding mBinding;

    private ProgressAdapter mProgressAdapter;

    private ArrayList<ProgressInfo> mProgressInfos;

    private DownloadManager mDownloadManager;

    public static ProgressFragment getInstance() {
        return new ProgressFragment();
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

//        // Load ad interstitial
//        loadInterstitialAd();

        return mBinding.getRoot();
    }

    private void initUI() {
        // Check saved videos
        mDownloadManager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        for (ProgressInfo progressInfo : getProgressInfos()) {
            checkDownloadProgress(progressInfo, mDownloadManager);
        }

        mBinding.rvProgress.setLayoutManager(new LinearLayoutManager(mActivity));
        mProgressAdapter = new ProgressAdapter(getProgressInfos());
        mBinding.rvProgress.setAdapter(mProgressAdapter);

        showEmptyData();
    }

    private void showEmptyData() {
        if (mPreferenceManager.getProgress().isEmpty()) {
            mBinding.layoutNoVideo.setVisibility(View.VISIBLE);
        } else {
            mBinding.layoutNoVideo.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onDownloadVideo(Video video) {
        if (!video.isDownloadCompleted()) {
            downloadVideo(video);
        }
    }

    private void downloadVideo(Video video) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(video.getUrl()));

        File localFile = FileUtil.getFolderDir();
        if (!localFile.exists() && !localFile.mkdirs()) return;

        request.setDestinationInExternalPublicDir(FileUtil.FOLDER_NAME, video.getFileName());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long downloadId = mDownloadManager.enqueue(request);


        // Save progress info
        ProgressInfo progressInfo = new ProgressInfo();
        progressInfo.setDownloadId(downloadId);
        progressInfo.setVideo(video);
        getProgressInfos().add(progressInfo);
        mProgressAdapter.notifyDataSetChanged();
        mPreferenceManager.setProgress(getProgressInfos());
        showEmptyData();

        // Check progress info
        checkDownloadProgress(progressInfo, mDownloadManager);
    }

    private void checkDownloadProgress(ProgressInfo progressInfo, DownloadManager downloadManager) {

        new Thread(() -> {

            try {
                boolean isDownloading = true;

                while (isDownloading) {

                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(progressInfo.getDownloadId());

                    Cursor cursor = downloadManager.query(query);
                    cursor.moveToFirst();

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        isDownloading = false;
                        mActivity.runOnUiThread(() -> {
                            // Update badges & videos screen
                            progressInfo.getVideo().setDownloadCompleted(true);
                            EventBus.getDefault().post(progressInfo.getVideo());
                            // Update progress screen
                            getProgressInfos().remove(progressInfo);
                            mProgressAdapter.notifyDataSetChanged();
                            mPreferenceManager.setProgress(getProgressInfos());
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
                        });
                    } else if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
                        isDownloading = false;
                        mActivity.runOnUiThread(() -> {
                            // Update progress screen
                            getProgressInfos().remove(progressInfo);
                            mProgressAdapter.notifyDataSetChanged();
                            mPreferenceManager.setProgress(getProgressInfos());
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
                        });
                    } else if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_RUNNING) {
                        int bytesDownloaded = cursor.getInt(cursor
                                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        double dlProgress = (bytesDownloaded * 100f / bytesTotal);

                        mActivity.runOnUiThread(() -> {
                            progressInfo.setProgress((int) dlProgress);
                            progressInfo.setProgressSize(FileUtil.getFileSize(bytesDownloaded) + "/" + FileUtil.getFileSize(bytesTotal));
                            mProgressAdapter.notifyDataSetChanged();
                            mPreferenceManager.setProgress(getProgressInfos());
                        });
                    }

                    cursor.close();

                }
            } catch (Exception e) {
                e.printStackTrace();
                mActivity.runOnUiThread(() -> {
                    // Update progress screen
                    getProgressInfos().remove(progressInfo);
                    mProgressAdapter.notifyDataSetChanged();
                    mPreferenceManager.setProgress(getProgressInfos());
                    showEmptyData();
                });
            }
        }).start();
    }

    private ArrayList<ProgressInfo> getProgressInfos() {
        if (mProgressInfos == null) {
            mProgressInfos = mPreferenceManager.getProgress();
        }
        return mProgressInfos;
    }

}
