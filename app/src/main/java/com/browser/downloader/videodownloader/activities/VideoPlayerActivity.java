package com.browser.downloader.videodownloader.activities;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.data.VideoState;
import com.browser.downloader.videodownloader.databinding.ActivityVideoPlayerBinding;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import vd.core.common.PreferencesManager;
import vd.core.util.IntentUtil;
import vd.core.util.TimeUtil;

public class VideoPlayerActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    ActivityVideoPlayerBinding mBinding;

    private VideoState mVideoState = new VideoState();

    private Handler mHandler = new Handler();

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
        ButterKnife.bind(this);
        initUI();

        // google analytics
        trackEvent(getResources().getString(R.string.app_name), getString(R.string.screen_player), "");
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

        mBinding.videoView.setOnCompletionListener(new MediaComplete());
        mBinding.videoView.requestFocus();
        mBinding.videoView.start();
        updateSeekBar();
    }

    @Override
    public void onBackPressed() {
        boolean isShowRate = PreferencesManager.getInstance(this).isRateApp();
        if (!isShowRate) {
            new AlertDialog.Builder(this).setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.rate_app))
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        PreferencesManager.getInstance(this).setRateApp(true);
                        IntentUtil.openGooglePlay(VideoPlayerActivity.this, getPackageName());
                        // google analytics
                        trackEvent(getString(R.string.app_name), getString(R.string.action_rate_us_video_player), "");
                        finish();
                    })
                    .setNegativeButton("LATER", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        finish();
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

    private class MediaComplete implements OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            mBinding.ivPlay.setImageResource(R.drawable.ic_play);
            mCurrentTime = 0;
            isPlaying = false;
        }
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