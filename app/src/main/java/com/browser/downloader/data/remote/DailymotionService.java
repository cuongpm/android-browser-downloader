package com.browser.downloader.data.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.text.TextUtils;

import com.browser.core.R;
import com.browser.core.ui.BaseActivity;
import com.browser.core.util.DialogUtil;
import com.browser.core.util.StringUtil;
import com.browser.downloader.data.local.Constant;
import com.browser.downloader.data.model.Format;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DailymotionService extends AsyncTask<String, String, String> {

    private Context mContext;

    private DailymotionCallback mDailymotionCallback;

    private String mUrl = "";

    public DailymotionService(Context context, DailymotionCallback dailymotionCallback) {
        this.mContext = context;
        this.mDailymotionCallback = dailymotionCallback;
    }

    @Override
    protected String doInBackground(String... params) {

        mUrl = params[0];
        HttpURLConnection urlConnection = null;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        try {
            urlConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", Constant.USER_AGENT);
            urlConnection.setUseCaches(false);
            urlConnection.setInstanceFollowRedirects(true);
            HttpURLConnection.setFollowRedirects(true);

            int responseCode = urlConnection.getResponseCode();
            if (responseCode != 200 && (responseCode == 301 || responseCode == 302 || responseCode == 303)) {
                urlConnection = (HttpURLConnection) new URL(urlConnection.getHeaderField("Location")).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(5500);
                urlConnection.setConnectTimeout(5500);
                urlConnection.setRequestProperty("User-Agent", Constant.USER_AGENT);
                urlConnection.setUseCaches(false);
                urlConnection.setInstanceFollowRedirects(true);
                HttpURLConnection.setFollowRedirects(true);
            }

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return StringUtil.convertStreamToString(in);
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
    protected void onPostExecute(String result) {
        DialogUtil.closeProgressDialog();

        ArrayList<Format> formats = new ArrayList<>();

        if (!TextUtils.isEmpty(result)) {
            String data = StringUtil.findContent(result, "\"qualities\":((.+?))\\}\\]\\}");
            if (!TextUtils.isEmpty(data)) data += "}]}";

            try {
                JSONObject jSONObject = new JSONObject(data);
                for (int j = 0; j < jSONObject.length(); j++) {
                    for (int i = 0; i < jSONObject.getJSONArray(jSONObject.names().get(j).toString()).length(); i++) {
                        String url = jSONObject.getJSONArray(jSONObject.names().get(j).toString()).getJSONObject(i).getString("url");
                        if (url.contains(".mp4")) {
                            // Add new format
                            Format format = new Format();
                            format.setUrl(url);
                            formats.add(format);
                        }
                    }
                }
            } catch (JSONException e) {
                e.getStackTrace();
            }

            if (formats.size() == 0) {
                ArrayList<String> urls = StringUtil.findListContent(removeSpecialCharaters(result), "\"(http([^\\s\"]+)?\\.mp4([^\\s\"]+)?)\"");
                for (int i = 0; i < urls.size(); i++) {
                    // Edit url
                    urls.set(i, urls.get(i).replace("\\", ""));
                    // Add new format
                    Format format = new Format();
                    format.setUrl(urls.get(i));
                    formats.add(format);
                }
            }
        }

        if (formats.size() > 0) {
            mDailymotionCallback.onDownloadCompleted(formats);
            try {
                // google analytics
                String website = mUrl;
                if (mUrl.contains("/")) website = mUrl.split("/")[2];
                ((BaseActivity) mContext).trackEvent(mContext.getString(R.string.event_get_link_success_dailymotion), website, mUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mDailymotionCallback.onDownloadFailed(mUrl);
            try {
                // google analytics
                String website = mUrl;
                if (mUrl.contains("/")) website = mUrl.split("/")[2];
                ((BaseActivity) mContext).trackEvent(mContext.getString(R.string.event_get_link_fail_dailymotion), website, mUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String removeSpecialCharaters(String data) {
        return data.replaceAll("&#123;", "{")
                .replaceAll("&#125;", "}")
                .replaceAll("&amp;", "&")
                .replaceAll("&gt;", ">")
                .replaceAll("&lt;", "<")
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "'");
    }

    public interface DailymotionCallback {
        void onDownloadCompleted(ArrayList<Format> formats);

        void onDownloadFailed(String url);
    }

}