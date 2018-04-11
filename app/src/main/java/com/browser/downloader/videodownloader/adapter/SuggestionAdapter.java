package com.browser.downloader.videodownloader.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.browser.downloader.videodownloader.R;

import java.util.List;

public class SuggestionAdapter extends ArrayAdapter<String> {

    private Context mContext;

    private List<String> mSuggestions;

    public SuggestionAdapter(Context context, int layout, List<String> suggestions) {
        super(context, layout, suggestions);
        mContext = context;
        this.mSuggestions = suggestions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_suggestion, null);
            holder.tvSuggestion = convertView.findViewById(R.id.tv_suggestion);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvSuggestion.setText(mSuggestions.get(position));
        return convertView;
    }

    private class ViewHolder {
        private AppCompatTextView tvSuggestion;
    }

}
