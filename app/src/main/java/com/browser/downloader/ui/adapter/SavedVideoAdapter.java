package com.browser.downloader.ui.adapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.core.R;
import com.browser.downloader.ui.videoplayer.VideoPlayerActivity;
import com.browser.downloader.data.model.Video;
import com.browser.core.databinding.ItemVideoSavedBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import com.browser.downloader.data.local.PreferencesManager;
import com.browser.core.util.FileUtil;
import com.browser.core.util.IntentUtil;
import com.browser.core.util.TimeUtil;

public class SavedVideoAdapter
        extends RecyclerView.Adapter<SavedVideoAdapter.VideoViewHolder> {

    private ArrayList<Video> mVideos;
    private View.OnClickListener mOnClickListener;

    public SavedVideoAdapter(ArrayList<Video> videos, View.OnClickListener onClickListener) {
        mVideos = videos;
        mOnClickListener = onClickListener;
    }

    @Override
    public SavedVideoAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_video_saved, parent, false));
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    @Override
    public void onBindViewHolder(final SavedVideoAdapter.VideoViewHolder holder, int position) {

        Video video = mVideos.get(position);
        holder.mBinding.tvName.setText(video.getFileName());

        if (video.getDuration() != 0) {
            holder.mBinding.tvTime.setVisibility(View.VISIBLE);
            holder.mBinding.tvTime.setText(TimeUtil.convertMilliSecondsToTimer(video.getDuration() * 1000));
        } else {
            holder.mBinding.tvTime.setVisibility(View.GONE);
        }

        holder.mBinding.ivThumbnail.setImageURI(video.getThumbnail());

        holder.mBinding.ivMore.setOnClickListener(view -> showPopupMenu(view, video, position));

        holder.mBinding.getRoot().setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
            intent.putExtra(VideoPlayerActivity.VIDEO_PATH, video.getUrl());
            intent.putExtra(VideoPlayerActivity.VIDEO_NAME, video.getFileName());
            view.getContext().startActivity(intent);

            view.setTag(VideoPlayerActivity.class.getSimpleName());
            mOnClickListener.onClick(view);
        });

    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        public ItemVideoSavedBinding mBinding;

        public VideoViewHolder(ItemVideoSavedBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }

    private void showPopupMenu(View view, Video video, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_video_online, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(arg0 -> {
            switch (arg0.getItemId()) {
                case R.id.item_download:
                    EventBus.getDefault().post(video);
                    // Callback
                    mOnClickListener.onClick(view);
                    return true;

                case R.id.item_rename:
                    FileUtil.renameFile(view.getContext(), video.getFileName(), fileName -> {
                        mVideos.get(position).setFileName(fileName);
                        notifyDataSetChanged();
                        // Save changes
                        PreferencesManager.getInstance(view.getContext()).setSavedVideos(mVideos);
                    });
                    // Callback
                    mOnClickListener.onClick(view);
                    return true;

                case R.id.item_delete:
                    mVideos.remove(position);
                    notifyDataSetChanged();
                    // Save changes
                    PreferencesManager.getInstance(view.getContext()).setSavedVideos(mVideos);
                    // Callback
                    mOnClickListener.onClick(view);
                    return true;

                case R.id.item_share:
                    IntentUtil.shareLink(view.getContext(), video.getUrl());
                    // Callback
                    mOnClickListener.onClick(view);
                    return true;
                default:
                    return false;
            }
        });
    }
}
