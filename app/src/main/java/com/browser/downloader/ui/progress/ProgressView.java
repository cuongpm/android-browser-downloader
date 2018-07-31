package com.browser.downloader.ui.progress;

import com.browser.core.mvp.BaseTiView;
import com.browser.downloader.data.model.ProgressInfo;

public interface ProgressView extends BaseTiView {

    void downloadDone(ProgressInfo progressInfo);

    void downloadFailed(ProgressInfo progressInfo);

    void downloadProgress(ProgressInfo progressInfo);

    void updateProgress();
}
