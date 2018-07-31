package com.browser.downloader.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.browser.core.R;
import com.browser.core.databinding.DialogDownloadVideoBinding;
import com.browser.core.databinding.FragmentBrowserBinding;
import com.browser.core.mvp.BaseTiFragment;
import com.browser.core.util.AdUtil;
import com.browser.core.util.AppUtil;
import com.browser.core.util.DialogUtil;
import com.browser.core.util.IntentUtil;
import com.browser.core.util.ScriptUtil;
import com.browser.core.util.TimeUtil;
import com.browser.core.util.ViewUtil;
import com.browser.downloader.data.local.Constant;
import com.browser.downloader.data.model.ConfigData;
import com.browser.downloader.data.model.Format;
import com.browser.downloader.data.model.PagesSupported;
import com.browser.downloader.data.model.SavedVideo;
import com.browser.downloader.data.model.Suggestion;
import com.browser.downloader.data.model.Video;
import com.browser.downloader.data.remote.DailymotionService;
import com.browser.downloader.data.remote.DownloadService;
import com.browser.downloader.data.remote.SearchService;
import com.browser.downloader.ui.adapter.SuggestionAdapter;
import com.browser.downloader.ui.bookmark.BookmarkActivity;
import com.browser.downloader.ui.dialog.GuidelineDialog;
import com.browser.downloader.ui.dialog.YoutubeDialog;
import com.browser.downloader.ui.history.HistoryActivity;
import com.browser.downloader.ui.videoplayer.VideoPlayerActivity;
import com.google.android.gms.ads.AdSize;

import org.greenrobot.eventbus.EventBus;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subjects.PublishSubject;

public class BrowserFragment extends BaseTiFragment<BrowserPresenter, BrowserView> implements BrowserView {

    FragmentBrowserBinding mBinding;

    private PublishSubject<String> mPublishSubject;

    private SuggestionAdapter mSuggestionAdapter;

    private Video mCurrentVideo;

    private Menu mMenu;

    private boolean isHasFocus = false;

    private final static int ACTIVITY_HISTORY = 0;

    private final static int ACTIVITY_BOOKMARK = 1;

    public final static String RESULT_URL = "RESULT_URL";

    private LinkStatus mLinkStatus = LinkStatus.SUPPORTED;

    private enum LinkStatus {
        SUPPORTED, GENERAL, UNSUPPORTED, YOUTUBE
    }

    public static BrowserFragment getInstance() {
        return new BrowserFragment();
    }

    @NonNull
    @Override
    public BrowserPresenter providePresenter() {
        return new BrowserPresenter();
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

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_browser, menu);
        mMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_bookmark) {
            if (mBinding.webview != null) {
                if (getPresenter().isBookmarkLink(mBinding.webview)) {
                    getPresenter().removeBookmark(mBinding.webview);
                } else {
                    getPresenter().saveWebViewBookmark(mBinding.webview);
                }
                updateBookmarkMenu(mBinding.webview);
            }

        } else if (item.getItemId() == R.id.menu_bookmark) {
            startActivityForResult(new Intent(mActivity, BookmarkActivity.class), ACTIVITY_BOOKMARK);

        } else if (item.getItemId() == R.id.menu_history) {
            startActivityForResult(new Intent(mActivity, HistoryActivity.class), ACTIVITY_HISTORY);

        } else if (item.getItemId() == R.id.menu_share) {
            if (mBinding.webview != null && !TextUtils.isEmpty(mBinding.webview.getUrl())) {
                IntentUtil.shareLink(mActivity, mBinding.webview.getUrl());
                // google analytics
                trackEvent(getString(R.string.app_name), getString(R.string.action_share_link), "");
            }

        } else if (item.getItemId() == R.id.menu_copy_link) {
            if (mBinding.webview != null && !TextUtils.isEmpty(mBinding.webview.getUrl())) {
                AppUtil.copyClipboard(mActivity, mBinding.webview.getUrl());
                Toast.makeText(mActivity, "Copied link", Toast.LENGTH_SHORT).show();
                // google analytics
                trackEvent(getString(R.string.app_name), getString(R.string.action_copy_link), "");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_HISTORY:
                if (resultCode == Activity.RESULT_OK) {
                    String url = data.getStringExtra(RESULT_URL);
                    mBinding.webview.loadUrl(url);
                }
                break;
            case ACTIVITY_BOOKMARK:
                if (resultCode == Activity.RESULT_OK) {
                    String url = data.getStringExtra(RESULT_URL);
                    mBinding.webview.loadUrl(url);
                }
                break;
        }
    }

    private void initUI() {
        // Grant permission
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        ConfigData configData = getPresenter().getConfigData();
        mBinding.layoutSocial.layoutMostVisited.setVisibility(configData != null && configData.isShowAllPages() ? View.VISIBLE : View.GONE);

        mBinding.webview.getSettings().setJavaScriptEnabled(true);
        mBinding.webview.addJavascriptInterface(this, "browser");
        mBinding.webview.setWebViewClient(webViewClient);
        mBinding.webview.setWebChromeClient(webChromeClient);

        mBinding.etSearch.setOnKeyListener((v, keyCode, event) -> {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
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
            List<Suggestion> listSuggestions = getPresenter().addAllSupportedPages(searchValue);
            if (searchValue.length() > 0 && !searchValue.startsWith("http://") && !searchValue.startsWith("https://")) {
                getActivity().runOnUiThread(() -> {
                    new SearchService(suggestions -> {
                        showSuggestion(listSuggestions, suggestions);
                    }).execute(String.format(Constant.SUGGESTION_URL, searchValue));
                });
            }
        });
    }

    private void focusSearchView(boolean isFocus) {
        mBinding.etSearch.setFocusable(isFocus);
        mBinding.etSearch.setFocusableInTouchMode(isFocus);
        if (isFocus) {
            ViewUtil.showSoftKeyboard(mActivity, mBinding.etSearch);
        } else {
            ViewUtil.hideSoftKeyboard(mActivity, mBinding.etSearch);
        }
    }

    private void showSuggestion(List<Suggestion> suggestionList, List<String> suggestions) {

        List<Suggestion> listSuggestions = getPresenter().addAllSuggestions(suggestionList, suggestions);

        mSuggestionAdapter = new SuggestionAdapter(mActivity, R.layout.item_suggestion, listSuggestions);
        mBinding.etSearch.setAdapter(mSuggestionAdapter);
        mBinding.etSearch.showDropDown();
        mBinding.etSearch.setOnItemClickListener((parent, view, position, id) -> {
            // Update text for search box
            mBinding.etSearch.setText(listSuggestions.get(position).getSuggestion());
            // google analytics
            trackEvent(getString(R.string.app_name), getString(R.string.action_search_suggestion), listSuggestions.get(position).getSuggestion());
            // Search keyword
            loadWebView();
            // Show ad
            mActivity.showInterstitialAdFullPosition();
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
            mBinding.webview.setVisibility(View.VISIBLE);
            mBinding.etSearch.setText(view.getUrl());
            mBinding.fab.setVisibility(View.VISIBLE);
            mBinding.progressBar.setVisibility(View.VISIBLE);
            mBinding.layoutSocial.layoutRoot.setVisibility(View.GONE);

            checkLinkStatus(view.getUrl());
            updateBookmarkMenu(view);
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
                mBinding.etSearch.setText(view.getUrl());
                checkLinkStatus(view.getUrl());
                if (url.contains("facebook.com")) {
                    view.loadUrl(ScriptUtil.FACEBOOK_SCRIPT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mBinding.etSearch.setText(view.getUrl());
            mBinding.progressBar.setVisibility(View.GONE);
            checkLinkStatus(view.getUrl());
            getPresenter().saveWebViewHistory(view);
            updateBookmarkMenu(view);
            super.onPageFinished(view, url);
        }
    };

    private void updateBookmarkMenu(WebView webView) {
        try {
            mMenu.getItem(0).getSubMenu().getItem(0).setIcon(getPresenter().isBookmarkLink(webView)
                    ? R.drawable.ic_star_yellow_24dp : R.drawable.ic_star_border_gray_24dp);
            mMenu.getItem(0).getSubMenu().getItem(0).setTitle(getPresenter().isBookmarkLink(webView)
                    ? "Remove bookmark" : "Add bookmark");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showVideoDataDialog(Video video) {
        mActivity.setGetLinkSuccess(true);
        mCurrentVideo = video;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mActivity);
        DialogDownloadVideoBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_download_video, null, false);

        // Show ad banner
        AdUtil.loadBanner(mActivity, binding.layoutBanner, AdSize.SMART_BANNER, false);

        // Layout video menu
        binding.layoutVideoMenu.tvPreview.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.VIDEO_PATH, video.getUrl());
            intent.putExtra(VideoPlayerActivity.VIDEO_NAME, video.getFileName());
            startActivity(intent);
            // Show ad
            mActivity.showInterstitlaAd();
            // google analytics
            logEventGA(video.getUrl(), getString(R.string.action_video_preview));
        });

        binding.layoutVideoMenu.tvSave.setOnClickListener(view -> {
            openDialogAnimation(binding.layoutVideoMenu.getRoot(), binding.layoutVideoSave.getRoot(), true);
            // google analytics
            logEventGA(video.getUrl(), getString(R.string.action_video_save));
        });

        binding.layoutVideoMenu.tvDownload.setOnClickListener(view -> {
            openDialogAnimation(binding.layoutVideoMenu.getRoot(), binding.layoutVideoDownload.getRoot(), true);
            // google analytics
            logEventGA(video.getUrl(), getString(R.string.action_video_download));
        });

        binding.layoutVideoMenu.tvCancel.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            // google analytics
            logEventGA(video.getUrl(), getString(R.string.action_video_cancel));
        });

        // Layout video save
        binding.layoutVideoSave.tvName.setText(video.getFileName());
        if (!TextUtils.isEmpty(video.getThumbnail())) {
            binding.layoutVideoSave.ivThumbnail.setImageURI(Uri.parse(video.getThumbnail()));
        }
        if (video.getDuration() != 0) {
            binding.layoutVideoSave.tvTime.setVisibility(View.VISIBLE);
            binding.layoutVideoSave.tvTime.setText(TimeUtil.convertMilliSecondsToTimer(video.getDuration() * 1000));
        } else {
            binding.layoutVideoSave.tvTime.setVisibility(View.GONE);
        }
        binding.layoutVideoSave.tvCancel.setOnClickListener(view -> {
            openDialogAnimation(binding.layoutVideoSave.getRoot(), binding.layoutVideoMenu.getRoot(), false);
            // google analytics
            logEventGA(video.getUrl(), getString(R.string.action_video_save_cancel));
        });
        binding.layoutVideoSave.tvOk.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            // Save video
            ArrayList<Video> currentVideos = getPresenter().getSavedVideos();
            currentVideos.add(video);
            getPresenter().setSavedVideos(currentVideos);
            // Show badge for tab online
            mActivity.showOnlineTabBadge();
            // Send event to Online screen to update list saved video
            EventBus.getDefault().post(new SavedVideo(video));
            // google analytics
            logEventGA(video.getUrl(), getString(R.string.action_video_save_ok));
            // Show ad
            mActivity.showInterstitialAdFullPosition();
        });

        // Layout video download
        binding.layoutVideoDownload.tvName.setText(video.getFileName());
        if (!TextUtils.isEmpty(video.getThumbnail())) {
            binding.layoutVideoDownload.ivThumbnail.setImageURI(Uri.parse(video.getThumbnail()));
        }
        if (video.getDuration() != 0) {
            binding.layoutVideoDownload.tvTime.setVisibility(View.VISIBLE);
            binding.layoutVideoDownload.tvTime.setText(TimeUtil.convertMilliSecondsToTimer(video.getDuration() * 1000));
        } else {
            binding.layoutVideoDownload.tvTime.setVisibility(View.GONE);
        }

        binding.layoutVideoDownload.tvCancel.setOnClickListener(view -> {
            openDialogAnimation(binding.layoutVideoDownload.getRoot(), binding.layoutVideoMenu.getRoot(), false);
            // google analytics
            logEventGA(video.getUrl(), getString(R.string.action_video_download_cancel));
        });

        binding.layoutVideoDownload.tvOk.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            EventBus.getDefault().post(video);
            // google analytics
            logEventGA(video.getUrl(), getString(R.string.action_video_download_ok));
            // Show ad
            mActivity.showInterstitialAdFullPosition();
        });

        // Show dialog
        bottomSheetDialog.setContentView(binding.getRoot());
        bottomSheetDialog.show();
    }

    private void openDialogAnimation(View viewExit, View viewEnter, boolean isNext) {
        Animation enterAnimation = AnimationUtils.loadAnimation(mActivity, isNext ? R.anim.enter_from_right : R.anim.enter_from_left);
        Animation exitAnimation = AnimationUtils.loadAnimation(mActivity, isNext ? R.anim.exit_to_left : R.anim.exit_to_right);
        viewExit.startAnimation(exitAnimation);
        viewExit.setVisibility(View.GONE);
        viewEnter.startAnimation(enterAnimation);
        viewEnter.setVisibility(View.VISIBLE);
    }

    private void logEventGA(String url, String event) {
        try {
            // google analytics
            String website = url;
            if (website.contains("/")) website = website.split("/")[2];
            trackEvent(event, website, url);
        } catch (Exception e) {
            e.printStackTrace();
            trackEvent(event, url, "");
        }
    }

    private void checkLinkStatus(String url) {

        // show appbar
        mBinding.appBar.setExpanded(true, true);

        // Clear current data
        mCurrentVideo = null;

        // Check url
        if (TextUtils.isEmpty(url)) {
            return;
        } else {
            url = url.toLowerCase();
        }

        // Detect youtube
        if (url.startsWith("https://youtu.be/") || url.contains("youtube.com")) {
            mLinkStatus = LinkStatus.YOUTUBE;
            disableDownloadBtn();
            return;
        }

        ConfigData configData = getPresenter().getConfigData();
        if (configData != null) {
            // Check site and pattern
            if (configData.getPagesSupported() != null) {
                for (PagesSupported pagesSupported : configData.getPagesSupported()) {
                    if (url.contains(pagesSupported.getName())) {
                        if (url.matches(pagesSupported.getPattern()) || url.contains(pagesSupported.getPattern())) {
                            mLinkStatus = LinkStatus.SUPPORTED;
                            enableDownloadBtnAndShake();
                        } else {
                            mLinkStatus = LinkStatus.GENERAL;
                            disableDownloadBtn();
                        }
                        return;
                    }
                }
            }

            // General sites
            if (configData.getPagesGeneral() != null) {
                for (String link : configData.getPagesGeneral()) {
                    if (url.startsWith(link) || url.contains(link)) {
                        mLinkStatus = LinkStatus.GENERAL;
                        disableDownloadBtn();
                        return;
                    }
                }
            }
        }

        // Other sites
        mLinkStatus = LinkStatus.UNSUPPORTED;
        disableDownloadBtn();
    }

    private void disableDownloadBtn() {
        try {
            mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.color_gray_1)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableDownloadBtn() {
        try {
            mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mActivity, R.color.colorAccent)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableDownloadBtnAndShake() {
        enableDownloadBtn();
        shakeButton(mBinding.fab);
    }

    private void shakeButton(View view) {
        Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.shake_btn_anim);
        anim.setDuration(50L);
        view.startAnimation(anim);
    }

    @JavascriptInterface
    public void getVideoData(String link) {
        getActivity().runOnUiThread(() -> {
            try {
                if (mCurrentVideo != null) {
                    showVideoDataDialog(mCurrentVideo);
                    return;
                }

                String url = URLDecoder.decode(link, "UTF-8");
                if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
                    Video video = new Video(System.currentTimeMillis() + ".mp4", url);
                    showVideoDataDialog(video);
                    // google analytics
                    trackEvent(getString(R.string.app_name), getString(R.string.event_get_link_facebook), url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadWebView() {
        // clear focus & hide keyboard
        focusSearchView(false);

        String content = mBinding.etSearch.getText().toString().trim();
        if (content.length() > 0) {
            if (content.toLowerCase().contains("youtu.be") || content.toLowerCase().contains("youtube.com")) {
                YoutubeDialog.getDialog(mActivity, true).show();
            } else if (content.startsWith("http://") || content.startsWith("https://")) {
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
        // Show ad
        mActivity.showInterstitialAdFullPosition();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_facebook), "");
    }

    @OnClick(R.id.btn_twitter)
    public void clickTwitter() {
        mBinding.etSearch.setText(mBinding.layoutSocial.tvTwitter.getText().toString());
        loadWebView();
        // Show ad
        mActivity.showInterstitialAdFullPosition();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_twitter), "");
    }

    @OnClick(R.id.btn_instagram)
    public void clickInstagram() {
        mBinding.etSearch.setText(mBinding.layoutSocial.tvInstagram.getText().toString());
        loadWebView();
        // Show ad
        mActivity.showInterstitialAdFullPosition();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_instagram), "");
    }

    @OnClick(R.id.btn_dailymotion)
    public void clickDailymotion() {
        mBinding.etSearch.setText(mBinding.layoutSocial.tvDailymotion.getText().toString());
        loadWebView();
        // Show ad
        mActivity.showInterstitialAdFullPosition();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_dailymotion), "");
    }

    @OnClick(R.id.btn_vimeo)
    public void clickVimeo() {
        mBinding.etSearch.setText(mBinding.layoutSocial.tvVimeo.getText().toString());
        loadWebView();
        // Show ad
        mActivity.showInterstitialAdFullPosition();
        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.action_open_vimeo), "");
    }

    @OnClick(R.id.iv_close_refresh)
    public void clickCloseOrRefresh() {
        if (mBinding.etSearch.getText().toString().trim().length() > 0) {
            if (isHasFocus) {
                mBinding.etSearch.setText("");
                mBinding.webview.setVisibility(View.GONE);
                mBinding.fab.setVisibility(View.GONE);
                mBinding.layoutSocial.layoutRoot.setVisibility(View.VISIBLE);
            } else {
                mBinding.webview.reload();
            }
        }
    }

    @OnClick(R.id.fab)
    public void downloadVideo() {
        String data = mBinding.webview.getUrl();
        if (TextUtils.isEmpty(data) || !Patterns.WEB_URL.matcher(data).matches()) {
            DialogUtil.showAlertDialog(mActivity, getString(R.string.error_valid_link));
            return;
        } else if (mLinkStatus == LinkStatus.YOUTUBE) {
            YoutubeDialog.getDialog(mActivity, true).show();
            return;
        } else if (mLinkStatus == LinkStatus.UNSUPPORTED) {
            YoutubeDialog.getDialog(mActivity, false).show();
            return;
        } else if (mLinkStatus == LinkStatus.GENERAL) {
            GuidelineDialog.getDialog(mActivity).show();
            return;
        }

        if (mCurrentVideo == null) {
            downloadVideoService(data);
        } else {
            showVideoDataDialog(mCurrentVideo);
        }
    }

    private void downloadVideoService(String data) {
        new DownloadService(mActivity, new DownloadService.DownloadCallback() {
            @Override
            public void onDownloadCompleted(Video video) {
                if (data.contains("dailymotion.com")) {
                    // Download video formats for Dailymotion
                    new DailymotionService(mActivity, new DailymotionService.DailymotionCallback() {
                        @Override
                        public void onDownloadCompleted(ArrayList<Format> formats) {
                            video.setFormats(formats);
                            // set default url is last format (best quality)
                            if (formats.size() > 0) {
                                video.setUrl(formats.get(formats.size() - 1).getUrl());
                            }
                            showVideoDataDialog(video);
                        }

                        @Override
                        public void onDownloadFailed(String url) {
                            showTryAgainDialog(data);
                        }
                    }).execute(data);
                } else {
                    // Show download video dialog for other sites
                    showVideoDataDialog(video);
                }
            }

            @Override
            public void onDownloadFailed(String url) {
                showTryAgainDialog(data);
            }
        }).execute(data);
    }

    private void showTryAgainDialog(String url) {
        DialogUtil.showAlertDialog(mActivity, "Try again", "Report link", getString(R.string.error_video_page),
                (dialog, i) -> {
                    dialog.dismiss();
                    downloadVideoService(url);
                }, (dialog, i) -> {
                    dialog.dismiss();
                    DialogUtil.showAlertDialog(mActivity, getString(R.string.message_report));
                    try {
                        // google analytics
                        String website = url;
                        if (url.contains("/")) website = url.split("/")[2];
                        trackEvent(getString(R.string.event_report_link), website, url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
