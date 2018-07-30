package com.browser.downloader.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.browser.core.R;
import com.browser.downloader.adapter.BookmarkAdapter;
import com.browser.core.databinding.ActivityBookmarkBinding;
import com.browser.downloader.fragment.BrowserFragment;

import butterknife.ButterKnife;

public class BookmarkActivity extends BaseActivity {

    ActivityBookmarkBinding mBinding;

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
        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(mPreferenceManager.getBookmark(),
                bookmark -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(BrowserFragment.RESULT_URL, bookmark.getUrl());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                });
        mBinding.rvBookmark.setAdapter(bookmarkAdapter);

        if (mPreferenceManager.getBookmark().isEmpty()) {
            mBinding.tvNoBookmark.setVisibility(View.VISIBLE);
        }

        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.screen_bookmark), mPreferenceManager.getBookmark().size() + "");
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
