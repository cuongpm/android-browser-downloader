package com.browser.downloader.data.remote;

import android.os.AsyncTask;
import android.text.Html;

import com.browser.core.util.StringUtil;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchService extends AsyncTask<String, Integer, List<String>> {

    private SuggestionCallback mSuggestionCallback;

    public SearchService(SuggestionCallback suggestionCallback) {
        this.mSuggestionCallback = suggestionCallback;
    }

    @Override
    protected List<String> doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection = null;
        List<String> suggestions = new ArrayList<>();

        try {
            url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == 500) {
                return null;
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            JSONArray jsonArray = new JSONArray(StringUtil.convertStreamToString(in));
            JSONArray jsonArray2 = jsonArray.getJSONArray(1);
            for (int i = 0; i < jsonArray2.length(); i++) {
                JSONArray jsonArray3 = jsonArray2.getJSONArray(i);
                suggestions.add(Html.fromHtml(jsonArray3.getString(0))
                        .toString());
            }
            return suggestions;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<String> suggestions) {
        if (suggestions != null && mSuggestionCallback != null) {
            mSuggestionCallback.onCompleted(suggestions);
        }
    }

    public interface SuggestionCallback {
        void onCompleted(List<String> suggestions);
    }

}