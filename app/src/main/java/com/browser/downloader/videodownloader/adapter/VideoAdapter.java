package com.browser.downloader.videodownloader.adapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.activities.VideoPlayerActivity;
import com.browser.downloader.videodownloader.databinding.ItemVideoBinding;

import java.io.File;
import java.util.ArrayList;

import vd.core.util.FileUtil;
import vd.core.util.IntentUtil;

public class VideoAdapter
        extends RecyclerView.Adapter<VideoAdapter.FileViewHolder> {

    private ArrayList<File> mFiles;
    private View.OnClickListener mOnClickListener;

    public VideoAdapter(ArrayList<File> files, View.OnClickListener onClickListener) {
        mFiles = files;
        mOnClickListener = onClickListener;
    }

    @Override
    public VideoAdapter.FileViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        return new FileViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.item_video, parent, false));
    }

    @Override
    public int getItemCount() {
//        return mFiles.size();
        return 4;
    }

    @Override
    public void onBindViewHolder(final VideoAdapter.FileViewHolder holder, int position) {

//        File file = mFiles.get(position);
//        holder.mBinding.tvName.setText(file.getName());
//        holder.mBinding.tvSize.setText(FileUtil.getFileSize(file));
//
//        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
//        holder.mBinding.ivThumbnail.setImageBitmap(thumbnail);

        holder.mBinding.ivMore.setOnClickListener(view -> showPopupMenu(view, null));
//
//        holder.mBinding.getRoot().setOnClickListener(view -> {
//            Intent intent = new Intent(view.getContext(), VideoPlayerActivity.class);
//            intent.putExtra(VideoPlayerActivity.VIDEO_PATH, file.getPath());
//            view.getContext().startActivity(intent);
//
//            mOnClickListener.onClick(view);
//        });

        // show test data
        holder.mBinding.tvName.setText(position == 0 ? "my favorite video.mp4" : position == 1 ? "viral video.mp4" : position == 2 ? "breaking news.mp4" : "myvideo.mp4");
        holder.mBinding.tvSize.setText(position == 0 ? "11.5 MB" : position == 1 ? "22.6 MB" : position == 2 ? "100.4 MB" : "90.8 MB");
        holder.mBinding.ivThumbnail.setImageURI(position == 0 ? "https://i2.wp.com/handluggageonly.co.uk/wp-content/uploads/2015/08/IMG_2537.jpg?w=256&h=256&crop=1&ssl=1"
                : position == 1 ? "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQGL26Dg_00N107n7-gf7e99l78gB4a4v0nwL6RxIm6ygKqgXP6"
                : position == 2 ? "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRC4IygFLuilywBNQ72U7fsNMT-O94UJoe41cpRa2CugPqMli00"
                : "https://www.lebanoninapicture.com/Prv/Images/Pages/Page_96260/the-usual-haig-adventures-shot-on-a-38m-natural-2-28-2017-10-44-47-am-t.jpg");
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {

        public ItemVideoBinding mBinding;

        public FileViewHolder(ItemVideoBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
        }
    }

    private void showPopupMenu(View view, final File file) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_video, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(arg0 -> {
            switch (arg0.getItemId()) {
                case R.id.item_rename:
                    FileUtil.renameFile(view.getContext(), file, fileName -> {
                        mFiles.clear();
                        mFiles.addAll(FileUtil.getListFiles());
                        notifyDataSetChanged();
                    });

                    mOnClickListener.onClick(view);
                    return true;

                case R.id.item_delete:
                    if (file.exists()) {
                        mFiles.remove(file);
                        file.delete();
                        notifyDataSetChanged();
                    }

                    mOnClickListener.onClick(view);
                    return true;

                case R.id.item_share:
                    IntentUtil.shareVideo(view.getContext(), file.getPath());

                    mOnClickListener.onClick(view);
                    return true;
                default:
                    return false;
            }
        });
    }
}
