package com.browser.downloader.ui.videoplayer;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;

import com.browser.core.R;
import com.browser.core.databinding.ActivityVideoPlayerBinding;
import com.browser.core.mvp.BaseTiActivity;
import com.browser.core.util.IntentUtil;
import com.browser.core.util.TimeUtil;
import com.browser.downloader.callback.DialogListener;
import com.browser.downloader.data.local.PreferencesManager;
import com.browser.downloader.data.model.VideoState;
import com.browser.downloader.ui.dialog.RatingDialog;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoPlayerActivity extends BaseTiActivity<VideoPlayerPresenter, VideoPlayerView>
        implements VideoPlayerView, SeekBar.OnSeekBarChangeListener {

    ActivityVideoPlayerBinding mBinding;

    private MediaPlayer mMediaPlayer;

    private VideoState mVideoState = new VideoState();

    private Handler mHandler = new Handler();

    private boolean isPortraitOrientation = true;

    private boolean isVolumeOn = true;

    private boolean isPlaying = true;

    private int mCurrentTime;

    private final static int SEEK_INTERVAL = 5000;

    public final static String VIDEO_PATH = "video_path";

    public final static String VIDEO_NAME = "video_name";

    @NonNull
    @Override
    public VideoPlayerPresenter providePresenter() {
        return new VideoPlayerPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_player);
        setSupportActionBar(mBinding.toolbar);
        ButterKnife.bind(this);
        initUI();

        // google analytics
        trackEvent(getString(R.string.app_name), getString(R.string.screen_player),
                mVideoState.getPath().startsWith("http") ? "Online" : "Offline");
    }

    private void initUI() {
        Bundle extras = getIntent().getExtras();
        mVideoState.setPath(extras.getString(VIDEO_PATH));
        mVideoState.setFileName(extras.getString(VIDEO_NAME));

        Uri uri;
        if (mVideoState.getPath().startsWith("http")) {
            uri = Uri.parse(mVideoState.getPath());
        } else {
            File file = new File(mVideoState.getPath());
            uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        }
        mBinding.videoView.setVideoURI(uri);

        mBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        mBinding.toolbar.setTitle(mVideoState.getFileName());

        mBinding.seekBar.setOnSeekBarChangeListener(this);

        mBinding.getRoot().setOnClickListener(view -> {
            if (mBinding.toolbar.getVisibility() == View.VISIBLE) {
                Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
                mBinding.toolbar.startAnimation(anim);
                mBinding.layoutBottom.startAnimation(anim);
                mBinding.toolbar.setVisibility(View.GONE);
                mBinding.layoutBottom.setVisibility(View.GONE);
            } else {
                Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
                mBinding.toolbar.startAnimation(anim);
                mBinding.layoutBottom.startAnimation(anim);
                mBinding.toolbar.setVisibility(View.VISIBLE);
                mBinding.layoutBottom.setVisibility(View.VISIBLE);
            }
        });

        mBinding.videoView.setOnCompletionListener(mediaPlayer -> {
            mBinding.ivPlay.setImageResource(R.drawable.ic_play);
            mCurrentTime = 0;
            isPlaying = false;
            // google analytics
            trackEvent(getString(R.string.action_play_completed),
                    mVideoState.getPath().startsWith("http") ? "Online" : "Offline", mVideoState.getFileName());
        });

        mBinding.videoView.setOnPreparedListener(mediaPlayer -> {
            mMediaPlayer = mediaPlayer;
            setVolume(isVolumeOn);
            // google analytics
            trackEvent(getString(R.string.action_play_prepared),
                    mVideoState.getPath().startsWith("http") ? "Online" : "Offline", mVideoState.getFileName());
        });

        mBinding.videoView.setOnErrorListener((mediaPlayer, i, i1) -> {
            // google analytics
            trackEvent(getString(R.string.action_play_error),
                    mVideoState.getPath().startsWith("http") ? "Online" : "Offline", mVideoState.getFileName());
            return false;
        });

        mBinding.videoView.requestFocus();
        mBinding.videoView.start();
        updateSeekBar();
    }

    private void setVolume(boolean isVolumeOn) {
        if (mMediaPlayer == null) return;

        int amount = isVolumeOn ? 100 : 0;
        int max = 100;
        double numerator = max - amount > 0 ? Math.log(max - amount) : 0;
        float volume = (float) (1 - (numerator / Math.log(max)));
        mMediaPlayer.setVolume(volume, volume);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_volume) {
            isVolumeOn = !isVolumeOn;
            item.setIcon(isVolumeOn ? R.drawable.ic_volume_up_white_24dp : R.drawable.ic_volume_off_white_24dp);
            setVolume(isVolumeOn);
        } else if (item.getItemId() == R.id.menu_fullscreen) {
            isPortraitOrientation = !isPortraitOrientation;
            item.setIcon(isPortraitOrientation ? R.drawable.ic_fullscreen_white_24dp : R.drawable.ic_fullscreen_exit_white_24dp);
            setRequestedOrientation(isPortraitOrientation ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        boolean isShowRate = PreferencesManager.getInstance(this).isRateApp();
        if (!isShowRate && isPortraitOrientation) {
            RatingDialog.getDialog(this, new DialogListener() {
                @Override
                public void onPositiveButton(Dialog dialog) {
                    dialog.dismiss();
                    getPresenter().setRateApp(true);
                    IntentUtil.openGooglePlay(VideoPlayerActivity.this, getPackageName());
                    // google analytics
                    trackEvent(getString(R.string.app_name), getString(R.string.action_rate_us_video_player), "");
                    finish();
                }

                @Override
                public void onNegativeButton(Dialog dialog) {
                    dialog.dismiss();
                    finish();
                }
            }).show();
        } else {
            finish();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mVideoState;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.videoView.seekTo(mVideoState.getCurrentTime());
        // google analytics
        trackView(getString(R.string.screen_player));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoState.setCurrentTime(mBinding.videoView.getCurrentPosition());
    }

    @OnClick(R.id.iv_next)
    public void clickNext() {
        if (mBinding.videoView != null) {
            seekToTime(mBinding.videoView.getCurrentPosition() + SEEK_INTERVAL);
        }
    }

    @OnClick(R.id.iv_play)
    public void clickPlay() {
        if (mBinding.videoView != null) {
            if (isPlaying) {
                isPlaying = false;
                mBinding.ivPlay.setImageResource(R.drawable.ic_play);
                mCurrentTime = mBinding.videoView.getCurrentPosition();
                mBinding.videoView.pause();
            } else {
                isPlaying = true;
                mBinding.ivPlay.setImageResource(R.drawable.ic_pause);
                if (mCurrentTime < mBinding.videoView.getDuration()) {
                    mBinding.videoView.seekTo(mCurrentTime);
                    mBinding.videoView.start();
                } else {
                    mBinding.videoView.seekTo(0);
                    mBinding.videoView.start();
                }
            }
        }
    }

    @OnClick(R.id.iv_prev)
    public void clickPrev() {
        if (mBinding.videoView != null) {
            seekToTime(mBinding.videoView.getCurrentPosition() - SEEK_INTERVAL);
        }
    }

    private void seekToTime(int time) {
        if (time > mBinding.videoView.getDuration()) {
            time = mBinding.videoView.getDuration();
        }
        if (time <= 0) {
            time = 0;
        }
        mHandler.removeCallbacks(mTimerRunnable);
        mBinding.videoView.seekTo(time);
        mBinding.seekBar.setProgress(time);
        mBinding.tvCurrentTime.setText(TimeUtil.convertMilliSecondsToTimer(time));
        updateSeekBar();
    }

    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            long duration = (long) mBinding.videoView.getDuration();
            long currentPosition = (long) mBinding.videoView.getCurrentPosition();
            mBinding.tvTotalTime.setText(TimeUtil.convertMilliSecondsToTimer(duration));
            mBinding.tvCurrentTime.setText(TimeUtil.convertMilliSecondsToTimer(currentPosition));
            mBinding.seekBar.setMax(mBinding.videoView.getDuration());
            mBinding.seekBar.setProgress(mBinding.videoView.getCurrentPosition());
//            seekBar.setProgress(getProgressPercentage(currentPosition,duration));
            mHandler.postDelayed(this, 1000);
        }
    };

    public int getProgressPercentage(long j, long j2) {
        Double.valueOf(0.0d);
        return Double.valueOf((((double) ((long) ((int) (j / 1000)))) / ((double) ((long) ((int) (j2 / 1000))))) * 100.0d).intValue();
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mTimerRunnable);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mTimerRunnable);
        mBinding.videoView.seekTo(seekBar.getProgress());
        updateSeekBar();
    }

    public void updateSeekBar() {
        mHandler.postDelayed(mTimerRunnable, 1000);
    }

}