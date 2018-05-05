package com.browser.downloader.videodownloader.activities;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.callback.DialogListener;
import com.browser.downloader.videodownloader.data.VideoState;
import com.browser.downloader.videodownloader.databinding.ActivityVideoPlayerBinding;
import com.browser.downloader.videodownloader.dialog.RatingDialog;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import vd.core.common.PreferencesManager;
import vd.core.util.IntentUtil;
import vd.core.util.TimeUtil;

public class VideoPlayerActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

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
        trackEvent(getString(R.string.app_name), getString(R.string.screen_player), "");
    }

    private void initUI() {
        Bundle extras = getIntent().getExtras();
        mVideoState.setFileName(extras.getString(VIDEO_PATH));

        File file = new File(mVideoState.getFileName());
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        mBinding.videoView.setVideoURI(uri);

        mBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        mBinding.toolbar.setTitle(file.getName());

        mBinding.seekBar.setOnSeekBarChangeListener(this);

        mBinding.videoView.setOnCompletionListener(mediaPlayer -> {
            mBinding.ivPlay.setImageResource(R.drawable.ic_play);
            mCurrentTime = 0;
            isPlaying = false;
        });

        mBinding.videoView.setOnPreparedListener(mediaPlayer -> {
            mMediaPlayer = mediaPlayer;
        });

        mBinding.videoView.requestFocus();
        mBinding.videoView.start();
        updateSeekBar();
    }

    private void setVolume(boolean isVolumeOn) {
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
                    mPreferenceManager.setRateApp(true);
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