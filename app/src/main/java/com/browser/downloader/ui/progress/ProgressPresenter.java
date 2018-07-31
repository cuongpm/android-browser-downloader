package com.browser.downloader.ui.progress;

import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;

import com.browser.core.mvp.BaseTiPresenter;
import com.browser.core.util.FileUtil;
import com.browser.downloader.AppApplication;
import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.model.ProgressInfo;
import com.browser.downloader.data.model.Video;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

public class ProgressPresenter extends BaseTiPresenter<ProgressView> {

    @Inject
    PreferencesManager mPreferencesManager;

    private ArrayList<ProgressInfo> mProgressInfos;

    public ProgressPresenter() {
        AppApplication.getInstance().getComponent().inject(this);
    }

    public ArrayList<ProgressInfo> getProgress() {
        return mPreferencesManager.getProgress();
    }

    public void setProgress(ArrayList<ProgressInfo> progressInfos) {
        mPreferencesManager.setProgress(progressInfos);
    }

    ArrayList<ProgressInfo> getProgressInfos() {
        if (mProgressInfos == null) {
            mProgressInfos = mPreferencesManager.getProgress();
        }
        return mProgressInfos;
    }

    void checkDownloadProgress(ProgressInfo progressInfo, DownloadManager downloadManager) {

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
                        sendToView(view -> view.downloadDone(progressInfo));
                    } else if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_FAILED) {
                        isDownloading = false;
                        sendToView(view -> view.downloadFailed(progressInfo));
                    } else if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_RUNNING) {
                        int bytesDownloaded = cursor.getInt(cursor
                                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        int bytesTotal = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        progressInfo.setBytesDownloaded(bytesDownloaded);
                        progressInfo.setBytesTotal(bytesTotal);

                        sendToView(view -> view.downloadProgress(progressInfo));
                    }

                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendToView(view -> view.downloadFailed(progressInfo));
            }
        }).start();
    }

    void downloadVideo(Video video, DownloadManager downloadManager) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(video.getUrl()));

        File localFile = FileUtil.getFolderDir();
        if (!localFile.exists() && !localFile.mkdirs()) return;

        request.setDestinationInExternalPublicDir(FileUtil.FOLDER_NAME, video.getFileName());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long downloadId = downloadManager.enqueue(request);


        // Save progress info
        ProgressInfo progressInfo = new ProgressInfo();
        progressInfo.setDownloadId(downloadId);
        progressInfo.setVideo(video);
        getProgressInfos().add(progressInfo);
        mPreferencesManager.setProgress(getProgressInfos());

        sendToView(view -> view.updateProgress());

        // Check progress info
        checkDownloadProgress(progressInfo, downloadManager);
    }
}
