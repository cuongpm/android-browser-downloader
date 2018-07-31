package com.browser.downloader.injection.component;

import com.browser.downloader.injection.PerActivity;
import com.browser.downloader.injection.module.ActivityModule;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)

public interface ActivityComponent {
}
