package com.browser.downloader.videodownloader.service;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.browser.downloader.videodownloader.data.model.Video;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import core.util.DialogUtil;

public class DownloadService extends AsyncTask<String, Integer, Video> {

    private Context mContext;

    private DownloadCallback mDownloadCallback;

    public DownloadService(Context context, DownloadCallback downloadCallback) {
        this.mContext = context;
        this.mDownloadCallback = downloadCallback;
    }

    @Override
    protected Video doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();

            if (urlConnection.getResponseCode() == 500) {
                return null;
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            JSONObject json = new JSONObject(convertStreamToString(in));

            String downloadURL = json.getJSONObject("info").get("url").toString();
            String fileName = json.getJSONObject("info").get("title").toString() + "." + json.getJSONObject("info").get("ext").toString();

            fileName = fileName.replaceAll("[^\\w\\s.-]", "");

            Video video = new Video(fileName, downloadURL);
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

    protected void onPostExecute(Video video) {
        DialogUtil.closeProgressDialog();
        if (video != null && mDownloadCallback != null) {
            mDownloadCallback.onDownloadCompleted(video);
        } else {
            Toast.makeText(mContext, "Can't get video link!", Toast.LENGTH_LONG).show();
        }

    }

    private static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public interface DownloadCallback {
        void onDownloadCompleted(Video video);
    }

}