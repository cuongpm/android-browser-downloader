package com.browser.downloader.videodownloader.service;

import android.content.Context;
import android.os.AsyncTask;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.activities.BaseActivity;
import com.browser.downloader.videodownloader.data.Video;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vd.core.util.AppUtil;
import vd.core.util.DialogUtil;

public class DownloadService extends AsyncTask<String, Integer, Video> {

    private Context mContext;

    private DownloadCallback mDownloadCallback;

    private String mUrl = "";

    public DownloadService(Context context, DownloadCallback downloadCallback) {
        this.mContext = context;
        this.mDownloadCallback = downloadCallback;
    }

    @Override
    protected Video doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            mUrl = params[0];
            url = new URL(AppUtil.buildUrl(mContext, params[0]));
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == 500) {
                return null;
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            JSONObject json = new JSONObject(convertStreamToString(in));

            String downloadURL = json.getJSONObject("info").getString("url");
            String fileName = json.getJSONObject("info").getString("title") + "." + json.getJSONObject("info").getString("ext");
            String thumbnail = "";
            long duration = 0;

            try {
                thumbnail = json.getJSONObject("info").getString("thumbnail");
                duration = json.getJSONObject("info").getLong("duration");
            } catch (Exception e) {
                e.printStackTrace();
            }

            fileName = fileName.replaceAll("[^\\w\\s.-]", "");

            Video video = new Video(fileName, downloadURL, thumbnail, duration);
            return video;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        DialogUtil.showSimpleProgressDialog(mContext);
    }

    @Override
    protected void onPostExecute(Video video) {
        DialogUtil.closeProgressDialog();
        if (video != null) {
            mDownloadCallback.onDownloadCompleted(video);
            try {
                // google analytics
                String website = mUrl;
                if (mUrl.contains("/")) website = mUrl.split("/")[2];
                ((BaseActivity) mContext).trackEvent(mContext.getString(R.string.event_get_link_success), website, mUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mDownloadCallback.onDownloadFailed(mUrl);
            try {
                // google analytics
                String website = mUrl;
                if (mUrl.contains("/")) website = mUrl.split("/")[2];
                ((BaseActivity) mContext).trackEvent(mContext.getString(R.string.event_get_link_fail), website, mUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public interface DownloadCallback {
        void onDownloadCompleted(Video video);

        void onDownloadFailed(String url);
    }

}