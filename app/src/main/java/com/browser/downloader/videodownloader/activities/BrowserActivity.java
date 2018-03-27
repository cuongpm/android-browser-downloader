package com.browser.downloader.videodownloader.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.data.model.StaticData;
import com.browser.downloader.videodownloader.data.model.Video;
import com.browser.downloader.videodownloader.databinding.ActivityBrowserBinding;
import com.browser.downloader.videodownloader.service.DownloadService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import java.net.URLDecoder;

import butterknife.ButterKnife;
import butterknife.OnClick;
import core.common.Constant;
import core.common.PreferencesManager;
import core.util.AdUtil;
import core.util.AppUtil;
import core.util.DialogUtil;

public class BrowserActivity extends BaseActivity {

    ActivityBrowserBinding mBinding;

    private InterstitialAd mInterstitialAd;

    private boolean isAdShowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_browser);
        ButterKnife.bind(this, mBinding.getRoot());
        initUI();

        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.screen_browser), "");

        // Show ad banner
        AdUtil.showBanner(this, mBinding.layoutBanner);

        // Load ad interstitial
        loadInterstitialAd();
    }

    private void loadInterstitialAd() {
        // Check show ad
        StaticData staticData = PreferencesManager.getInstance(this).getStaticData();
        boolean isShowAd = staticData == null ? true : staticData.isShowAdBrowser();
        if (isShowAd) {
            DialogUtil.showSimpleProgressDialog(this);
            mInterstitialAd = new InterstitialAd(this);
            AdUtil.showInterstitialAd(mInterstitialAd, new AdListener() {
                @Override
                public void onAdLoaded() {
                    DialogUtil.closeProgressDialog();
                    super.onAdLoaded();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    DialogUtil.closeProgressDialog();
                    super.onAdFailedToLoad(i);
                }
            });
        }
    }

    private void showInterstitlaAd() {
        if (!isAdShowed && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            isAdShowed = true;
            mInterstitialAd.show();
            // google analytics
            trackEvent(getResources().getString(R.string.app_name), getString(R.string.action_show_ad_browser), "");
        }
    }

    @Override
    public void onResume() {
        trackView(getString(R.string.screen_browser));
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        showInterstitlaAd();
        finish();
    }

    private void initUI() {
        // Grant permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        mBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());

        mBinding.webview.getSettings().setJavaScriptEnabled(true);
        mBinding.webview.addJavascriptInterface(this, "browser");
        mBinding.webview.setWebViewClient(webViewClient);
        mBinding.webview.setWebChromeClient(webChromeClient);
        checkWebViewData(mBinding.webview);

        mBinding.etSearch.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getX() >= (mBinding.etSearch.getRight()
                        - mBinding.etSearch.getCompoundDrawables()[2].getBounds().width())) {
                    mBinding.etSearch.setText("");
                    showInterstitlaAd();
                    return true;
                }
            }
            return false;
        });

        mBinding.ivSearch.setOnClickListener(view -> {
            String content = mBinding.etSearch.getText().toString().trim();
            if (content.length() > 0) {
                if (content.startsWith("http://") || content.startsWith("https://")) {
                    mBinding.webview.loadUrl(content);
                } else if (Patterns.WEB_URL.matcher(content).matches()) {
                    mBinding.webview.loadUrl("http://" + content);
                    mBinding.etSearch.setText("http://" + content);
                } else {
                    mBinding.webview.loadUrl(String.format(Constant.SEARCH_URL, content));
                    mBinding.etSearch.setText(String.format(Constant.SEARCH_URL, content));
                }
            }
        });

        mBinding.ivNext.setOnClickListener(view -> {
            if (mBinding.webview.canGoForward()) {
                mBinding.webview.goForward();
                showInterstitlaAd();
            }
        });

        mBinding.ivBack.setOnClickListener(view -> {
            if (mBinding.webview.canGoBack()) {
                mBinding.webview.goBack();
                showInterstitlaAd();
            }
        });
    }

    WebChromeClient webChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mBinding.progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    };

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.startsWith("https://youtu.be/") || url.toLowerCase().contains("youtube.com")) {
                mBinding.webview.setVisibility(View.GONE);
                mBinding.tvNotSupport.setVisibility(View.VISIBLE);
                return;
            } else {
                mBinding.webview.setVisibility(View.VISIBLE);
                mBinding.tvNotSupport.setVisibility(View.GONE);
            }

            mBinding.etSearch.setText(url);
            mBinding.progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mBinding.etSearch.setText(url);
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            if (view.getUrl().contains("m.facebook.com")) {
                view.loadUrl(Constant.VIDEO_SCRIPT);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mBinding.etSearch.setText(url);
            mBinding.progressBar.setVisibility(View.GONE);
            checkWebViewData(view);
            super.onPageFinished(view, url);
        }
    };

    private void checkWebViewData(WebView view) {
        mBinding.ivBack.setEnabled(view.canGoBack());
        mBinding.ivBack.setAlpha(view.canGoBack() ? 1f : 0.5f);
        mBinding.ivNext.setEnabled(view.canGoForward());
        mBinding.ivNext.setAlpha(view.canGoForward() ? 1f : 0.5f);
    }

    @JavascriptInterface
    public void getVideoData(String link, String name) {
        try {
            String url = URLDecoder.decode(link, "UTF-8");
            if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                Video video = new Video(name + ".mp4", url);
                DialogUtil.showAlertDialog(BrowserActivity.this,
                        video.getFileName(), "Do you want to download this video?",
                        (dialogInterface, i) -> {
                            showInterstitlaAd();
                            AppUtil.downloadVideo(BrowserActivity.this, video);
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.fab)
    public void downloadVideo() {
        String data = mBinding.webview.getUrl();
        if (data == null || data.length() == 0 || !Patterns.WEB_URL.matcher(data).matches()) {
            Toast.makeText(this, "Please enter a valid video link!", Toast.LENGTH_LONG).show();
            return;
        }

        if (data.contains("m.facebook.com")) {
            Toast.makeText(this, "Please click on video to download it!", Toast.LENGTH_LONG).show();
            return;
        }

        if (data.startsWith("https://youtu.be/") || data.toLowerCase().contains("youtube.com")) {
            Toast.makeText(this, "Unsupported site!", Toast.LENGTH_LONG).show();
            return;
        }

        new DownloadService(this, video -> {
            DialogUtil.showAlertDialog(BrowserActivity.this,
                    video.getFileName(), "Do you want to download this video?",
                    (dialogInterface, i) -> {
                        showInterstitlaAd();
                        AppUtil.downloadVideo(BrowserActivity.this, video);
                    });
        }).execute(AppUtil.buildUrl(this, data));
    }

}
