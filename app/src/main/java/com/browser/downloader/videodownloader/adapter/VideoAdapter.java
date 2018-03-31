package com.browser.downloader.videodownloader.adapter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.downloader.videodownloader.R;
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
        return mFiles.size();
    }

    @Override
    public void onBindViewHolder(final VideoAdapter.FileViewHolder holder, int position) {

        File file = mFiles.get(position);
        holder.mBinding.tvName.setText(file.getName());
        holder.mBinding.tvSize.setText(FileUtil.getFileSize(file));

        Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
        holder.mBinding.ivThumbnail.setImageBitmap(thumbnail);

        holder.mBinding.ivMore.setOnClickListener(view -> showPopupMenu(view, file));

        holder.mBinding.getRoot().setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = FileProvider.getUriForFile(view.getContext(),
                    view.getContext().getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(uri, "video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            view.getContext().startActivity(intent);

            mOnClickListener.onClick(view);
        });


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
                    IntentUtil.shareLink(view.getContext(), file.getPath());

                    mOnClickListener.onClick(view);
                    return true;
                default:
                    return false;
            }
        });
    }
}
