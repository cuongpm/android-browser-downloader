package com.browser.downloader.videodownloader.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import com.browser.downloader.videodownloader.R;
import com.browser.downloader.videodownloader.databinding.ItemTabHomeBinding;
import com.browser.downloader.videodownloader.fragment.BrowserFragment;
import com.browser.downloader.videodownloader.fragment.SettingsFragment;
import com.browser.downloader.videodownloader.fragment.VideoFragment;

public class HomeAdapter extends FragmentPagerAdapter {

    public HomeAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return BrowserFragment.getInstance();
        } else if (position == 1) {
            return VideoFragment.getInstance();
        } else {
            return SettingsFragment.getInstance();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public View getTabViewAt(Activity activity, int position) {
        ItemTabHomeBinding binding = DataBindingUtil.inflate(activity.getLayoutInflater(), R.layout.item_tab_home, null, false);
        binding.getRoot().setScaleY(-1);
        if (position == 0) {
//            tabView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_doctor_menu, 0, 0);
            binding.tvTitle.setText("Browser");
            binding.ivIcon.setImageResource(R.drawable.ic_browser);
        } else if (position == 1) {
            binding.tvTitle.setText("Player");
            binding.ivIcon.setImageResource(R.drawable.ic_video);
        } else {
//            tabView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_platform_menu, 0, 0);
            binding.tvTitle.setText("Settings");
            binding.ivIcon.setImageResource(R.drawable.ic_settings);
        }
        return binding.getRoot();
    }
}

