package com.browser.downloader.ui.bookmark;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.browser.core.R;
import com.browser.core.databinding.ActivityBookmarkBinding;
import com.browser.core.mvp.BaseTiActivity;
import com.browser.downloader.ui.adapter.BookmarkAdapter;
import com.browser.downloader.ui.home.BrowserFragment;

import butterknife.ButterKnife;

public class BookmarkActivity extends BaseTiActivity<BookmarkPresenter, BookmarkView> implements BookmarkView {

    ActivityBookmarkBinding mBinding;

    @NonNull
    @Override
    public BookmarkPresenter providePresenter() {
        return new BookmarkPresenter();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bookmark);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        mBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        mBinding.rvBookmark.setLayoutManager(new LinearLayoutManager(this));
        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(getPresenter().getBookmark(),
                bookmark -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(BrowserFragment.RESULT_URL, bookmark.getUrl());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                });
        mBinding.rvBookmark.setAdapter(bookmarkAdapter);

        if (getPresenter().getBookmark().isEmpty()) {
            mBinding.tvNoBookmark.setVisibility(View.VISIBLE);
        }

        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.screen_bookmark), getPresenter().getBookmark().size() + "");
    }

    @Override
    public void onResume() {
        trackView(getString(R.string.screen_bookmark));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
