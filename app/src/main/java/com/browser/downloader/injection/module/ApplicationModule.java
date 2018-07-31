package com.browser.downloader.injection.module;

import android.app.Application;
import android.content.Context;

import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.remote.DataService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    Context provideApplicationContext() {
        return mApplication;
    }


    @Provides
    @Singleton
    PreferencesManager provicePreferencesManager() {
        return new PreferencesManager(provideApplicationContext());
    }

    @Provides
    @Singleton
    DataService provideDataService() {
        return DataService.Factory.create();
    }

}
