package com.browser.downloader.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.browser.core.R;
import com.browser.downloader.callback.CallbackListener;
import com.browser.downloader.data.model.WebViewData;
import com.browser.core.databinding.ItemHistoryBinding;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<WebViewData> mListHistory;

    private CallbackListener<WebViewData> mCallbackListener;

    public HistoryAdapter(ArrayList<WebViewData> listHistory, CallbackListener<WebViewData> callbackListener) {
        this.mListHistory = listHistory;
        this.mCallbackListener = callbackListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemHistoryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_history, parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WebViewData history = mListHistory.get(position);
        ItemHistoryBinding binding = ((HistoryViewHolder) holder).binding;

        binding.tvTitle.setText(history.getTitle());
        binding.tvUrl.setText(history.getUrl());

        binding.getRoot().setOnClickListener(view -> mCallbackListener.onCallback(history));
    }

    @Override
    public int getItemCount() {
        return mListHistory.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        ItemHistoryBinding binding;

        public HistoryViewHolder(ItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
