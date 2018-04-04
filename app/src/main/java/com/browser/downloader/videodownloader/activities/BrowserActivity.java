package com.browser.downloader.videodownloader.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.data.model.StaticData;
import com.browser.downloader.videodownloader.data.model.Video;
import com.browser.downloader.videodownloader.databinding.ActivityBrowserBinding;
import com.browser.downloader.videodownloader.service.DownloadService;

import java.net.URLDecoder;

import butterknife.ButterKnife;
import butterknife.OnClick;
import vd.core.common.Constant;
import vd.core.common.PreferencesManager;
import vd.core.util.AdUtil;
import vd.core.util.AppUtil;
import vd.core.util.DialogUtil;
import vd.core.util.ScriptUtil;

public class BrowserActivity extends BaseActivity {

    ActivityBrowserBinding mBinding;

    private InterstitialAd mInterstitialAd;

    private boolean isAdShowed = false;

    private LinkStatus mLinkStatus;

    private enum LinkStatus {
        SUPPORTED, GENERAL, UNSUPPORTED
    }

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
                    mBinding.tvNotSupport.setVisibility(View.GONE);
                    mBinding.webview.setVisibility(View.GONE);
                    mBinding.layoutBottom.setVisibility(View.GONE);
                    mBinding.layoutSocial.setVisibility(View.VISIBLE);
                    showInterstitlaAd();
                    return true;
                }
            }
            return false;
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
                mBinding.layoutSocial.setVisibility(View.GONE);
                return;
            } else {
                mBinding.webview.setVisibility(View.VISIBLE);
                mBinding.tvNotSupport.setVisibility(View.GONE);
            }

            mBinding.etSearch.setText(url);
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.layoutBottom.setVisibility(View.VISIBLE);
            mBinding.layoutSocial.setVisibility(View.GONE);

            checkLinkStatus(url);
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
            try {
                if (url.contains("facebook.com")) {
                    view.loadUrl(ScriptUtil.FACEBOOK_SCRIPT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mBinding.etSearch.setText(url);
            mBinding.progressBar.setVisibility(View.GONE);
            checkWebViewData(view);
            checkLinkStatus(url);
            super.onPageFinished(view, url);
        }
    };

    private void checkLinkStatus(String url) {
        StaticData staticData = PreferencesManager.getInstance(this).getStaticData();
        if (staticData != null) {
            // General sites
            if (staticData.getPagesGeneral() != null) {
                for (String link : staticData.getPagesGeneral()) {
                    if (url.startsWith(link)) {
                        mLinkStatus = LinkStatus.GENERAL;
                        disableDownloadBtn();
                        return;
                    }
                }
            }
            // Unsupported sites
            if (staticData.getPagesUnsupported() != null) {
                for (String link : staticData.getPagesUnsupported()) {
                    if (url.startsWith(link)) {
                        mLinkStatus = LinkStatus.UNSUPPORTED;
                        disableDownloadBtn();
                        return;
                    }
                }
            }
        }

        // Other sites
        mLinkStatus = LinkStatus.SUPPORTED;
        enableDownloadBtn();
    }

    private void checkWebViewData(WebView view) {
        mBinding.ivBack.setEnabled(view.canGoBack());
        mBinding.ivBack.setAlpha(view.canGoBack() ? 1f : 0.5f);
        mBinding.ivNext.setEnabled(view.canGoForward());
        mBinding.ivNext.setAlpha(view.canGoForward() ? 1f : 0.5f);
    }

    private void disableDownloadBtn() {
        mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_gray_1)));
    }

    private void enableDownloadBtn() {
        mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
    }

    private void enableDownloadBtnAndShake() {
        enableDownloadBtn();
        shakeButton(mBinding.fab);
    }

    private void shakeButton(View view) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.shake_btn_anim);
        anim.setDuration(50L);
        view.startAnimation(anim);
    }

    @JavascriptInterface
    public void getVideoData(String link) {
        runOnUiThread(() -> {
            try {
                String url = URLDecoder.decode(link, "UTF-8");
                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                    Video video = new Video(System.currentTimeMillis() + ".mp4", url);
                    DialogUtil.showAlertDialog(BrowserActivity.this,
                            video.getFileName(), getString(R.string.message_download_video),
                            (dialogInterface, i) -> {
                                showInterstitlaAd();
                                AppUtil.downloadVideo(BrowserActivity.this, video);
                            });
                    // google analytics
                    trackEvent(getString(R.string.app_name), getString(R.string.event_get_link_facebook), url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadWebView() {
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
    }

    @OnClick(R.id.iv_search)
    public void clickSearch() {
        loadWebView();
        // google analytics
        String content = mBinding.etSearch.getText().toString().trim();
        trackEvent(getString(R.string.app_name), getString(R.string.action_search), content);
    }

    @OnClick(R.id.btn_facebook)
    public void clickFacebook() {
        mBinding.etSearch.setText(mBinding.tvFacebook.getText().toString());
        loadWebView();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_facebook), "");
    }

    @OnClick(R.id.btn_twitter)
    public void clickTwitter() {
        mBinding.etSearch.setText(mBinding.tvTwitter.getText().toString());
        loadWebView();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_twitter), "");
    }

    @OnClick(R.id.btn_instagram)
    public void clickInstagram() {
        mBinding.etSearch.setText(mBinding.tvInstagram.getText().toString());
        loadWebView();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_instagram), "");
    }

    @OnClick(R.id.fab)
    public void downloadVideo() {

        if (mLinkStatus != null) {
            if (mLinkStatus == LinkStatus.GENERAL) {
                DialogUtil.showAlertDialog(this, getString(R.string.error_video_page));
                return;
            }
            if (mLinkStatus == LinkStatus.UNSUPPORTED) {
                DialogUtil.showAlertDialog(this, getString(R.string.error_unsupported_site));
                return;
            }
        }

        String data = mBinding.webview.getUrl();
        if (data == null || data.length() == 0 || !Patterns.WEB_URL.matcher(data).matches()) {
            DialogUtil.showAlertDialog(this, getString(R.string.error_valid_link));
            return;
        }

        if (data.contains("facebook.com")) {
            DialogUtil.showAlertDialog(this, getString(R.string.error_facebook));
            return;
        }

        if (data.startsWith("https://youtu.be/") || data.toLowerCase().contains("youtube.com")) {
            Toast.makeText(this, "Unsupported site!", Toast.LENGTH_LONG).show();
            // google analytics
            trackEvent(getString(R.string.app_name), getString(R.string.event_unsupported_site), data);
            return;
        }

        new DownloadService(this, video -> {
            DialogUtil.showAlertDialog(BrowserActivity.this,
                    video.getFileName(), getString(R.string.message_download_video),
                    (dialogInterface, i) -> {
                        showInterstitlaAd();
                        AppUtil.downloadVideo(BrowserActivity.this, video);
                    });
        }).execute(data);
    }

}
