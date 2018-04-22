package com.browser.downloader.videodownloader.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.adapter.SuggestionAdapter;
import com.browser.downloader.videodownloader.data.ConfigData;
import com.browser.downloader.videodownloader.data.Video;
import com.browser.downloader.videodownloader.databinding.FragmentBrowserBinding;
import com.browser.downloader.videodownloader.databinding.LayoutVideoDataBinding;
import com.browser.downloader.videodownloader.service.DownloadService;
import com.browser.downloader.videodownloader.service.SearchService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subjects.PublishSubject;
import vd.core.common.Constant;
import vd.core.util.AdUtil;
import vd.core.util.AppUtil;
import vd.core.util.DialogUtil;
import vd.core.util.ScriptUtil;
import vd.core.util.TimeUtil;
import vd.core.util.ViewUtil;

public class BrowserFragment extends BaseFragment {

    FragmentBrowserBinding mBinding;

    private InterstitialAd mInterstitialAd;

    private PublishSubject<String> mPublishSubject;

    private SuggestionAdapter mSuggestionAdapter;

    private boolean isAdShowed = false;

    private boolean isHasFocus = false;

    private LinkStatus mLinkStatus;

    private enum LinkStatus {
        SUPPORTED, GENERAL, UNSUPPORTED
    }

    public static BrowserFragment getInstance() {
        return new BrowserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_browser, container, false);
        ButterKnife.bind(this, mBinding.getRoot());
        setSupportActionBar(mBinding.toolbar);
        setHasOptionsMenu(true);

        initUI();

        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.screen_browser), "");

//        // Show ad banner
//        AdUtil.showBanner(this, mBinding.layoutBanner);

        // Load ad interstitial
        loadInterstitialAd();

        return mBinding.getRoot();
    }

    private void loadInterstitialAd() {
        // Check show ad
        ConfigData configData = mPreferenceManager.getConfigData();
        boolean isShowAd = configData == null ? true : configData.isShowAdBrowser();
        if (isShowAd) {
            DialogUtil.showSimpleProgressDialog(getContext());
            mInterstitialAd = new InterstitialAd(getContext());
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_browser, menu);
//        AlertConfiguration configuration = AlertPresenter.getAlertConfiguration();
//        try {
//            menu.getItem(0).getSubMenu().getItem(0).setIcon(configuration.isShowExtMessage() ? R.drawable.ic_check_green : 0);
//            menu.getItem(0).getSubMenu().getItem(1).setIcon(configuration.isShowHiddenAlert() ? R.drawable.ic_check_green : 0);
//        } catch (Exception e) {
//            Timber.e(e);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_bookmark) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, 0, 0, R.anim.exit_to_right)
                    .add(android.R.id.content, BookmarkFragment.getInstance())
                    .addToBackStack(null)
                    .commit();
        } else if (item.getItemId() == R.id.menu_history) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, 0, 0, R.anim.exit_to_right)
                    .add(android.R.id.content, HistoryFragment.getInstance())
                    .addToBackStack(null)
                    .commit();
        } else if (item.getItemId() == R.id.menu_share) {
        } else if (item.getItemId() == R.id.menu_copy_link) {
        }
        return super.onOptionsItemSelected(item);
    }


    private void initUI() {
        // Grant permission
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        ConfigData configData = mPreferenceManager.getConfigData();
        mBinding.layoutSocial.layoutMostVisited.setVisibility(configData != null && configData.isShowAllPages() ? View.VISIBLE : View.GONE);

        mBinding.webview.getSettings().setJavaScriptEnabled(true);
        mBinding.webview.addJavascriptInterface(this, "browser");
        mBinding.webview.setWebViewClient(webViewClient);
        mBinding.webview.setWebChromeClient(webChromeClient);
        checkWebViewData(mBinding.webview);

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

        mBinding.etSearch.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        // clear focus & hide keyboard
                        focusSearchView(false);
                        // load data
                        loadWebView();
                        // google analytics
                        String content = mBinding.etSearch.getText().toString().trim();
                        trackEvent(getString(R.string.app_name), getString(R.string.action_search), content);
                        return true;
                    }
                    return false;
                }
        );

        mBinding.etSearch.setOnClickListener(view -> focusSearchView(true));

        mBinding.etSearch.setOnFocusChangeListener((view, isHasFocus) -> {
            this.isHasFocus = isHasFocus;
            if (isHasFocus) {
                mBinding.ivCloseRefresh.setImageResource(R.drawable.ic_close_gray_24dp);
            } else {
                mBinding.ivCloseRefresh.setImageResource(R.drawable.ic_refresh_gray_24dp);
//                String data = mBinding.webview.getUrl();
//                if (data != null && data.length() > 0) {
//                    mBinding.etSearch.setText(data);
//                } else {
//                    mBinding.etSearch.setText("");
//                }
            }
        });

        mBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPublishSubject.onNext(editable.toString());
            }
        });

        mPublishSubject = PublishSubject.create();
        mPublishSubject.debounce(300, TimeUnit.MILLISECONDS).subscribe(searchValue -> {
            if (searchValue.length() > 0 && !searchValue.startsWith("http://") && !searchValue.startsWith("https://")) {
                getActivity().runOnUiThread(() -> {
                    new SearchService(suggestions -> {
                        showSuggestion(suggestions);
                    }).execute(String.format(Constant.SUGGESTION_URL, searchValue));
                });
            }
        });
    }

    private void focusSearchView(boolean isFocus) {
        mBinding.etSearch.setFocusable(isFocus);
        mBinding.etSearch.setFocusableInTouchMode(isFocus);
        if (isFocus) {
            ViewUtil.showSoftKeyboard(getContext(), mBinding.etSearch);
        } else {
            ViewUtil.hideSoftKeyboard(getContext(), mBinding.etSearch);
        }
    }

    private void showSuggestion(List<String> suggestions) {
        mSuggestionAdapter = new SuggestionAdapter(getContext(), R.layout.item_suggestion, suggestions);
        mBinding.etSearch.setAdapter(mSuggestionAdapter);
        mBinding.etSearch.showDropDown();
        mBinding.etSearch.setOnItemClickListener((parent, view, position, id) -> {
            // clear focus & hide keyboard
            focusSearchView(false);
            // Search keyword
            loadWebView();
            // google analytics
            String content = mBinding.etSearch.getText().toString().trim();
            trackEvent(getString(R.string.app_name), getString(R.string.action_search_suggestion), content);
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
            if (url.toLowerCase().startsWith("https://youtu.be/") || url.toLowerCase().contains("youtube.com")) {
                mBinding.webview.setVisibility(View.GONE);
                mBinding.tvNotSupport.setVisibility(View.VISIBLE);
                mBinding.layoutSocial.layoutRoot.setVisibility(View.GONE);
                return;
            }

            mBinding.webview.setVisibility(View.VISIBLE);
            mBinding.tvNotSupport.setVisibility(View.GONE);
            mBinding.etSearch.setText(url);
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.layoutBottom.setVisibility(View.VISIBLE);
            mBinding.layoutSocial.layoutRoot.setVisibility(View.GONE);

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

    private void showVideoDataDialog(Video video) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        LayoutVideoDataBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.layout_video_data, null, false);

        binding.ivThumbnail.setImageURI(Uri.parse(video.getThumbnail()));
        binding.tvName.setText(video.getFileName());
        if (video.getDuration() != 0) {
            binding.tvTime.setVisibility(View.VISIBLE);
            binding.tvTime.setText(TimeUtil.convertMilliSecondsToTimer(video.getDuration() * 1000));
        } else {
            binding.tvTime.setVisibility(View.GONE);
        }

        binding.tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());

        binding.tvOk.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
//            AppUtil.downloadVideo(getContext(), video);
            EventBus.getDefault().post(video);
            showInterstitlaAd();
        });

        bottomSheetDialog.setContentView(binding.getRoot());
        bottomSheetDialog.show();
    }

    private void checkLinkStatus(String url) {
        ConfigData configData = mPreferenceManager.getConfigData();
        if (configData != null) {
            // General sites
            if (configData.getPagesGeneral() != null) {
                for (String link : configData.getPagesGeneral()) {
                    if (url.startsWith(link) || url.equals(link)) {
                        mLinkStatus = LinkStatus.GENERAL;
                        disableDownloadBtn();
                        return;
                    }
                }
            }
            // General sites with specific link
            if (configData.getPagesGeneral1() != null) {
                for (String link : configData.getPagesGeneral1()) {
                    if (url.equals(link)) {
                        mLinkStatus = LinkStatus.GENERAL;
                        disableDownloadBtn();
                        return;
                    }
                }
            }
            // Unsupported sites
            if (configData.getPagesUnsupported() != null) {
                for (String link : configData.getPagesUnsupported()) {
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
        mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.color_gray_1)));
    }

    private void enableDownloadBtn() {
        mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
    }

    private void enableDownloadBtnAndShake() {
        enableDownloadBtn();
        shakeButton(mBinding.fab);
    }

    private void shakeButton(View view) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.shake_btn_anim);
        anim.setDuration(50L);
        view.startAnimation(anim);
    }

    @JavascriptInterface
    public void getVideoData(String link) {
        getActivity().runOnUiThread(() -> {
            try {
                String url = URLDecoder.decode(link, "UTF-8");
                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                    Video video = new Video(System.currentTimeMillis() + ".mp4", url);
                    DialogUtil.showAlertDialog(getContext(), video.getFileName(), getString(R.string.message_download_video),
                            (dialogInterface, i) -> {
                                showInterstitlaAd();
                                AppUtil.downloadVideo(getContext(), video);
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

    @OnClick(R.id.btn_facebook)
    public void clickFacebook() {
        mBinding.etSearch.setText(mBinding.layoutSocial.tvFacebook.getText().toString());
        loadWebView();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_facebook), "");
    }

    @OnClick(R.id.btn_twitter)
    public void clickTwitter() {
        mBinding.etSearch.setText(mBinding.layoutSocial.tvTwitter.getText().toString());
        loadWebView();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_twitter), "");
    }

    @OnClick(R.id.btn_instagram)
    public void clickInstagram() {
        mBinding.etSearch.setText(mBinding.layoutSocial.tvInstagram.getText().toString());
        loadWebView();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_instagram), "");
    }

    @OnClick(R.id.btn_dailymotion)
    public void clickDailymotion() {
        mBinding.etSearch.setText(mBinding.layoutSocial.tvDailymotion.getText().toString());
        loadWebView();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_dailymotion), "");
    }

    @OnClick(R.id.btn_vimeo)
    public void clickVimeo() {
        mBinding.etSearch.setText(mBinding.layoutSocial.tvVimeo.getText().toString());
        loadWebView();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_vimeo), "");
    }

    @OnClick(R.id.iv_close_refresh)
    public void clickCloseOrRefresh() {
        if (mBinding.etSearch.getText().toString().trim().length() > 0) {
            if (isHasFocus) {
                mBinding.etSearch.setText("");
                mBinding.tvNotSupport.setVisibility(View.GONE);
                mBinding.webview.setVisibility(View.GONE);
                mBinding.layoutBottom.setVisibility(View.GONE);
                mBinding.layoutSocial.layoutRoot.setVisibility(View.VISIBLE);
                showInterstitlaAd();
            } else {
                mBinding.webview.reload();
            }
        }
    }

    @OnClick(R.id.fab)
    public void downloadVideo() {

        if (mLinkStatus != null) {
            if (mLinkStatus == LinkStatus.GENERAL) {
                DialogUtil.showAlertDialog(getContext(), getString(R.string.error_video_page));
                return;
            }
            if (mLinkStatus == LinkStatus.UNSUPPORTED) {
                DialogUtil.showAlertDialog(getContext(), getString(R.string.error_unsupported_site));
                return;
            }
        }

        String data = mBinding.webview.getUrl();
        if (data == null || data.length() == 0 || !Patterns.WEB_URL.matcher(data).matches()) {
            DialogUtil.showAlertDialog(getContext(), getString(R.string.error_valid_link));
            return;
        }

        if (data.contains("facebook.com")) {
            DialogUtil.showAlertDialog(getContext(), getString(R.string.error_facebook));
            return;
        }

        if (data.toLowerCase().startsWith("https://youtu.be/") || data.toLowerCase().contains("youtube.com")) {
            Toast.makeText(getContext(), "Unsupported site!", Toast.LENGTH_LONG).show();
            // google analytics
            trackEvent(getString(R.string.app_name), getString(R.string.event_unsupported_site), data);
            return;
        }

        new DownloadService(getContext(), video -> showVideoDataDialog(video)).execute(data);
    }
}
