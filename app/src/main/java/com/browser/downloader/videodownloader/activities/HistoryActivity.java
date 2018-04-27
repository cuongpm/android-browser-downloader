package com.browser.downloader.videodownloader.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.adapter.HistoryAdapter;
import com.browser.downloader.videodownloader.databinding.ActivityHistoryBinding;
import com.browser.downloader.videodownloader.fragment.BrowserFragment;

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
                    overridePendingTransition(0, R.anim.exit_to_right);
                });
        mBinding.rvHistory.setAdapter(historyAdapter);

        if (mPreferenceManager.getHistory().isEmpty()) {
            mBinding.tvNoHistory.setVisibility(View.VISIBLE);
        }

        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.screen_history), "");

//        // Show ad banner
//        AdUtil.showBanner(this, mBinding.layoutBanner);
    }

    @Override
    public void onResume() {
        trackView(getString(R.string.screen_history));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, R.anim.exit_to_right);
    }
}
