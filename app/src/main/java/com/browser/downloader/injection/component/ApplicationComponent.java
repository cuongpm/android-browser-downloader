package com.browser.downloader.injection.component;

import android.content.Context;

import com.browser.downloader.injection.ApplicationContext;
import com.browser.downloader.injection.module.ApplicationModule;
import com.browser.downloader.ui.splash.SplashPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(SplashPresenter splashPresenter);

    @ApplicationContext
    Context context();
}
