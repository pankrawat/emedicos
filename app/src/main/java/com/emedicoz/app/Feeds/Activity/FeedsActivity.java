package com.emedicoz.app.Feeds.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.emedicoz.app.Common.BaseABNavActivity;
import com.emedicoz.app.Feeds.Fragment.FeedsFragment;
import com.emedicoz.app.Feeds.Fragment.TagSelectionFragment;
import com.emedicoz.app.Model.Tags;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.SharedPreference;

public class FeedsActivity extends BaseABNavActivity implements TagSelectionFragment.ITagSelectionListener{

    public static boolean IS_NEW_POST_ADDED = false, IS_COMMENT_REFRESHED = false, IS_POST_DELETED = false,IS_POST_UPDATED=false;

    public BroadcastReceiver mReceiver;
    final int REQUEST_READ_PHONE_STATE = 100;
    final int REQUEST_READ_PHONE_STATE1 = 101;

    @Override
    protected void InitViews() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE");
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");//
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
        int permissionCheck3 = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        int permissionCheck4 = ContextCompat.checkSelfPermission(this, "android.permission.READ_SMS");//
        int permissionCheck5 = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS");
        int permissionCheck6 = ContextCompat.checkSelfPermission(this, "android.permission.GET_ACCOUNTS");
        int permissionCheck7 = ContextCompat.checkSelfPermission(this, "android.permission.READ_PROFILE");
        int permissionCheck8 = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION");
        int permissionCheck9 = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION");
        int permissionCheck10 = ContextCompat.checkSelfPermission(this, "android.permission.RECEIVE_SMS");//

        if (permissionCheck != PackageManager.PERMISSION_GRANTED || permissionCheck1 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck2 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck3 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck4 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck5 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck6 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck7 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck8 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck9 != PackageManager.PERMISSION_GRANTED ||
                permissionCheck10 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {"android.permission.READ_PHONE_STATE", "android.permission.WRITE_EXTERNAL_STORAGE",
                            "android.permission.READ_EXTERNAL_STORAGE",
                            "android.permission.CAMERA",
                            "android.permission.READ_SMS",
                            "android.permission.READ_CONTACTS",
                            "android.permission.GET_ACCOUNTS",
                            "android.permission.READ_PROFILE",
                            "android.permission.ACCESS_FINE_LOCATION",
                            "android.permission.ACCESS_COARSE_LOCATION", "android.permission.RECEIVE_SMS"}, REQUEST_READ_PHONE_STATE);
        } else {

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;
            case REQUEST_READ_PHONE_STATE1:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                }
                break;
            default:
                break;
        }
    }

    @Override
    protected Fragment getFragment() {
        PostFAB.setVisibility(View.VISIBLE);
        toolbartitleTV.setText(getString(R.string.app_name));
        SharedPreference.getInstance().putString(Const.SEARCHED_QUERY, "");
        return FeedsFragment.newInstance();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                //log our message value
                if (SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) != 0) {
                    notifyTV.setVisibility(View.VISIBLE);
                    notifyTV.setText(String.valueOf(SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT)));
                } else {
                    notifyTV.setVisibility(View.GONE);
                }
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment instanceof FeedsFragment) {
            if (IS_NEW_POST_ADDED) {
                ((FeedsFragment) fragment).RefreshFeedList(true);
                ((FeedsFragment) fragment).last_post_id = "";
                IS_NEW_POST_ADDED = false;
            }
            if (IS_COMMENT_REFRESHED) {
                if (((FeedsFragment) fragment).feedRVAdapter != null)
                    ((FeedsFragment) fragment).feedRVAdapter.ItemChangedatPostId(SharedPreference.getInstance().getPost(), 0);
                IS_COMMENT_REFRESHED = false;
            }
            if (IS_POST_DELETED) {
                if (((FeedsFragment) fragment).feedRVAdapter != null)
                    ((FeedsFragment) fragment).feedRVAdapter.ItemChangedatPostId(SharedPreference.getInstance().getPost(), 1);
                IS_POST_DELETED = false;
            }
            if (IS_POST_UPDATED) {
                if (((FeedsFragment) fragment).feedRVAdapter != null)
                    ((FeedsFragment) fragment).feedRVAdapter.ItemChangedatPostId(SharedPreference.getInstance().getPost(), 0);
                IS_POST_UPDATED = false;
            }
        }
    }

    @Override
    public void ontagSelected(Tags tag) {

    }
}
