package com.browser.downloader.injection.component;

import android.content.Context;

import com.browser.downloader.injection.module.ApplicationModule;
import com.browser.downloader.ui.bookmark.BookmarkPresenter;
import com.browser.downloader.ui.history.HistoryPresenter;
import com.browser.downloader.ui.home.BrowserPresenter;
import com.browser.downloader.ui.home.MainPresenter;
import com.browser.downloader.ui.progress.ProgressPresenter;
import com.browser.downloader.ui.settings.SettingsPresenter;
import com.browser.downloader.ui.splash.SplashPresenter;
import com.browser.downloader.ui.videoplayer.OfflinePresenter;
import com.browser.downloader.ui.videoplayer.OnlinePresenter;
import com.browser.downloader.ui.videoplayer.VideoPlayerPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(SplashPresenter splashPresenter);

    void inject(BookmarkPresenter bookmarkPresenter);

    void inject(HistoryPresenter historyPresenter);

    void inject(MainPresenter mainPresenter);

    void inject(VideoPlayerPresenter videoPlayerPresenter);

    void inject(BrowserPresenter browserPresenter);

    void inject(ProgressPresenter progressPresenter);

    void inject(OnlinePresenter onlinePresenter);

    void inject(OfflinePresenter offlinePresenter);

    void inject(SettingsPresenter settingsPresenter);

    Context context();
}
