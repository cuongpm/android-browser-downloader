package com.browser.downloader.videodownloader.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.data.ProgressInfo;
import com.browser.downloader.videodownloader.databinding.ItemProgressBinding;

import java.util.ArrayList;

public class ProgressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ProgressInfo> mProgressInfos;

    public ProgressAdapter(ArrayList<ProgressInfo> progressInfos) {
        this.mProgressInfos = progressInfos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemProgressBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_progress, parent, false);
        return new ProgressViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        ProgressInfo progressInfo = mProgressInfos.get(position);
        ItemProgressBinding binding = ((ProgressViewHolder) holder).binding;

//        binding.tvTitle.setText(progressInfo.getVideo().getFileName());
//        binding.progressBar.setProgress(progressInfo.getProgress());
//        binding.tvProgress.setText(progressInfo.getProgressSize());
//        binding.ivThumbnail.setImageURI(progressInfo.getVideo().getThumbnail());

        // show test data
        binding.tvTitle.setText(position == 0 ? "my favorite video.mp4" : position == 1 ? "viral video.mp4" : "breaking news.mp4");
        binding.progressBar.setProgress(position == 0 ? 25 : position == 1 ? 40 : 70);
        binding.tvProgress.setText(position == 0 ? "15M / 60M" : position == 1 ? "40M / 100M" : "140M / 200M");
        binding.ivThumbnail.setImageURI(position == 0 ? "https://i2.wp.com/handluggageonly.co.uk/wp-content/uploads/2015/08/IMG_2537.jpg?w=256&h=256&crop=1&ssl=1"
                : position == 1 ? "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQGL26Dg_00N107n7-gf7e99l78gB4a4v0nwL6RxIm6ygKqgXP6"
                : "https://www.lebanoninapicture.com/Prv/Images/Pages/Page_96260/the-usual-haig-adventures-shot-on-a-38m-natural-2-28-2017-10-44-47-am-t.jpg");
    }

    @Override
    public int getItemCount() {
//        return mProgressInfos.size();
        return 3;
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {

        ItemProgressBinding binding;

        public ProgressViewHolder(ItemProgressBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
