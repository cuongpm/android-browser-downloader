package com.browser.core.ui;

import android.support.annotation.NonNull;

import com.browser.core.mvp.BaseTiFragment;
import com.browser.core.mvp.BaseTiPresenter;
import com.browser.core.mvp.BaseTiView;

public class BaseFragment extends BaseTiFragment<BaseTiPresenter<BaseTiView>, BaseTiView> implements BaseTiView {

    @NonNull
    @Override
    public BaseTiPresenter providePresenter() {
        return new BaseTiPresenter();
    }

}
