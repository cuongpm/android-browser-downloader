package com.browser.downloader.ui.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.browser.core.R;
import com.browser.downloader.callback.CallbackListener;
import com.browser.downloader.data.model.WebViewData;
import com.browser.core.databinding.ItemBookmarkBinding;

import java.util.ArrayList;

public class BookmarkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<WebViewData> mListBookmark;

    private CallbackListener<WebViewData> mCallbackListener;

    public BookmarkAdapter(ArrayList<WebViewData> listBookmark, CallbackListener<WebViewData> callbackListener) {
        this.mListBookmark = listBookmark;
        this.mCallbackListener = callbackListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemBookmarkBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_bookmark, parent, false);
        return new BookmarkViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WebViewData bookmark = mListBookmark.get(position);
        ItemBookmarkBinding binding = ((BookmarkViewHolder) holder).binding;

        binding.tvTitle.setText(bookmark.getTitle());
        binding.tvUrl.setText(bookmark.getUrl());

        binding.getRoot().setOnClickListener(view -> mCallbackListener.onCallback(bookmark));
    }

    @Override
    public int getItemCount() {
        return mListBookmark.size();
    }

    class BookmarkViewHolder extends RecyclerView.ViewHolder {

        ItemBookmarkBinding binding;

        public BookmarkViewHolder(ItemBookmarkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
