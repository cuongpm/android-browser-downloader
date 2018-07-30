package com.browser.core.mvp;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import rx.Subscription;
import timber.log.Timber;

public class BaseTiPresenter<V extends BaseTiView> extends TiPresenter<V> {

    private final RxTiPresenterSubscriptionHandler rxHelper = new RxTiPresenterSubscriptionHandler(this);

    public void manageSubscription(Subscription subscription) {
        try {
            rxHelper.manageSubscription(subscription);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void manageViewSubscription(Subscription subscription) {
        try {
            rxHelper.manageViewSubscription(subscription);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
