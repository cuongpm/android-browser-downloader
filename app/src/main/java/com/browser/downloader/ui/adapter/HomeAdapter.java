package com.browser.downloader.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.browser.downloader.ui.home.BrowserFragment;
import com.browser.downloader.ui.videoplayer.OnlineFragment;
import com.browser.downloader.ui.progress.ProgressFragment;
import com.browser.downloader.ui.settings.SettingsFragment;
import com.browser.downloader.ui.videoplayer.OfflineFragment;

public class HomeAdapter extends FragmentPagerAdapter {

    public HomeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return BrowserFragment.getInstance();
        } else if (position == 1) {
            return ProgressFragment.getInstance();
        } else if (position == 2) {
            return OfflineFragment.getInstance();
        } else if (position == 3) {
            return OnlineFragment.getInstance();
        } else {
            return SettingsFragment.getInstance();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

}

