package com.browser.core.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.List;

import timber.log.Timber;

public class FragmentHolderActivity extends BaseActivity {

    public static Intent newActivityIntent(Context context,
                                           Class<? extends Fragment> fragmentClass) {
        return newActivityIntent(context, fragmentClass, null);
    }

    public static Intent newActivityIntent(Context context,
                                           Class<? extends Fragment> fragmentClass,
                                           Bundle bundle) {
        Intent intent = new Intent(context, FragmentHolderActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(Fragment.class.getName(), fragmentClass.getName());
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.containsKey(Fragment.class.getName())) {
            String className = bundle.getString(Fragment.class.getName());
            try {
                Class<? extends Fragment> fragmentClass =
                        (Class<? extends Fragment>) Class.forName(className);
                Fragment fragment = fragmentClass.newInstance();
                fragment.setArguments(bundle);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(android.R.id.content,
                                fragment)
                        .commitAllowingStateLoss();
                return;
            } catch (ClassNotFoundException e) {
                Timber.e("Could not found fragment class " + className, e);
            } catch (InstantiationException e) {
                Timber.e("Could not create instance of fragment class " + className, e);
            } catch (IllegalAccessException e) {
                Timber.e("Illegal access of fragment class " + className, e);
            }
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null && fragmentList.size() > 0) {
            for (Fragment fragment : fragmentList) {
                if (fragment != null)
                    fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
