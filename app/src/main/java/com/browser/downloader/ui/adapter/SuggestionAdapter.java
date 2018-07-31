package com.browser.downloader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.browser.core.R;
import com.browser.downloader.data.model.Suggestion;
import com.browser.downloader.data.model.SuggestionType;

import java.util.List;

public class SuggestionAdapter extends ArrayAdapter<Suggestion> {

    private Context mContext;

    private List<Suggestion> mSuggestions;

    public SuggestionAdapter(Context context, int layout, List<Suggestion> suggestions) {
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

        holder.tvSuggestion.setText(mSuggestions.get(position).getSuggestion());
        if (mSuggestions.get(position).getSuggestionType() == SuggestionType.SUGGESTION.getValue()) {
            holder.tvSuggestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_gray_24dp, 0, 0, 0);
        } else {
            holder.tvSuggestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_border_gray_24dp, 0, 0, 0);
        }

        return convertView;
    }

    private class ViewHolder {
        private AppCompatTextView tvSuggestion;
    }

}
