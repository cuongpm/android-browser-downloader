package com.browser.downloader.ui.history;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.browser.core.R;
import com.browser.downloader.ui.BaseActivity;
import com.browser.downloader.ui.adapter.HistoryAdapter;
import com.browser.core.databinding.ActivityHistoryBinding;
import com.browser.downloader.ui.home.BrowserFragment;

import butterknife.ButterKnife;

public class HistoryActivity extends BaseActivity {

    ActivityHistoryBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_history);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        mBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        mBinding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        HistoryAdapter historyAdapter = new HistoryAdapter(mPreferenceManager.getHistory(),
                history -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(BrowserFragment.RESULT_URL, history.getUrl());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                });
        mBinding.rvHistory.setAdapter(historyAdapter);

        if (mPreferenceManager.getHistory().isEmpty()) {
            mBinding.tvNoHistory.setVisibility(View.VISIBLE);
        }

        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.screen_history), mPreferenceManager.getHistory().size() + "");
    }

    @Override
    public void onResume() {
        trackView(getString(R.string.screen_history));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
