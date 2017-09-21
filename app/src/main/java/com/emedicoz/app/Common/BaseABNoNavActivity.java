package com.emedicoz.app.Common;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.emedicoz.app.Feeds.Fragment.CommonFragment;
import com.emedicoz.app.Feeds.Fragment.CourseInterestedInFragment;
import com.emedicoz.app.Feeds.Fragment.NewPostFragment;
import com.emedicoz.app.Feeds.Fragment.RegistrationFragment;
import com.emedicoz.app.Feeds.Fragment.specializationFragment;
import com.emedicoz.app.Feeds.Fragment.subStreamFragment;
import com.emedicoz.app.Login.Fragment.changepassword;
import com.emedicoz.app.Login.Fragment.mobileverification;
import com.emedicoz.app.Login.Fragment.otpverification;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Helper;


/**
 * Created by Cbc-03 on 06/06/17.
 */
public abstract class BaseABNoNavActivity extends AppCompatActivity {

    RelativeLayout mFragmentLayout;
    public Fragment mFragment;
    protected Toolbar toolbar;

    public SwipeRefreshLayout swipeRefreshLayout;

    FragmentManager fragmentManager;
    Fragment fragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);

        mFragmentLayout = (RelativeLayout) findViewById(R.id.fragment_container);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        Helper.logUser(this);
        initViews();

        //Add back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(addBackButton());
            getSupportActionBar().setDisplayShowHomeEnabled(addBackButton());
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCustomBackPress();
            }
        });
        mFragment = getFragment();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
        } else if (mFragment != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(mFragment.getClass().getSimpleName()).commit();
    }

    @Override
    public void onBackPressed() {
        onCustomBackPress();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onCustomBackPress();
                break;
        }
        return true;
    }

    public void onCustomBackPress() {
        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment instanceof subStreamFragment ||
                fragment instanceof specializationFragment ||
                fragment instanceof CourseInterestedInFragment)
            getSupportFragmentManager().popBackStack();

        else if (fragment instanceof RegistrationFragment ||
                fragment instanceof NewPostFragment ||
                fragment instanceof CommonFragment ||
                fragment instanceof mobileverification ||
                fragment instanceof changepassword ||
                fragment instanceof otpverification)
            this.finish();
        else
            this.finish();
    }

    protected abstract void initViews();

    protected abstract boolean addBackButton();

    protected abstract Fragment getFragment();
}


