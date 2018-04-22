package com.browser.downloader.videodownloader.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.data.Video;
import com.browser.downloader.videodownloader.databinding.FragmentProgressBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import butterknife.ButterKnife;
import vd.core.util.FileUtil;

public class ProgressFragment extends BaseFragment {

    FragmentProgressBinding mBinding;

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

////        // Show ad banner
////        AdUtil.showBanner(this, mBinding.layoutBanner);
//
//        // Load ad interstitial
//        loadInterstitialAd();

        return mBinding.getRoot();
    }

    private void initUI() {
        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.screen_progress), "");
    }

//    @Override
//    public void onResume() {
//        trackView(getString(R.string.screen_progress));
//        super.onResume();
//    }

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
        DownloadManager dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = dm.enqueue(request);

        new Thread(() -> {

            boolean downloading = true;

            while (downloading) {

                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(downloadId);

                Cursor cursor = dm.query(q);
                cursor.moveToFirst();
                int bytes_downloaded = cursor.getInt(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false;
                    mActivity.runOnUiThread(() -> {
                        video.setDownloadCompleted(true);
                        EventBus.getDefault().post(video);
                    });
                }

                double dl_progress = (bytes_downloaded * 100f / bytes_total);

                mActivity.runOnUiThread(() -> {
                    mBinding.tvProgress.setText(FileUtil.getFileSize(bytes_downloaded) + "/" + FileUtil.getFileSize(bytes_total));
                    mBinding.progressBar.setProgress((int) dl_progress);
                });

                Log.d("test", statusMessage(cursor));
                cursor.close();
            }

        }).start();
    }

    private String statusMessage(Cursor c) {
        String msg = "???";

        switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg = "Download failed!";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg = "Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg = "Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg = "Download in progress!";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg = "Download complete!";
                break;

            default:
                msg = "Download is nowhere in sight";
                break;
        }

        return (msg);
    }

}
