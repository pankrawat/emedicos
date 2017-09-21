package com.emedicoz.app.Login.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.Network.NetworkCall;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class DemoActivity extends AppCompatActivity implements NetworkCall.MyNetworkCallBack {

    ViewPager mViewPager;
    MyPagerAdapter mPagerAdapter;
    private int[] layouts;
    Timer timer;
    int page = 1;

    private ImageView[] dots;
    private LinearLayout dotsLayout;
    private Button btn_signup;
    private Button btn_login;

    private NetworkCall NC;

    final int REQUEST_READ_PHONE_STATE = 100;
    final int REQUEST_READ_PHONE_STATE1 = 101;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        NC = new NetworkCall(this, this);
        Helper.logUser(this);

        layouts = new int[]{R.layout.demo_screen1, R.layout.demo_screen2, R.layout.demo_screen3, R.layout.demo_screen4};

        addBottomDots(0);

        mPagerAdapter = new MyPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        pageSwitcher(3);
        onclick();
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

    public void pageSwitcher(int seconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(), seconds * 1000, seconds * 1000);
    }

    public void onclick() {
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoActivity.this, SignInActivity.class);
                intent.putExtra(Const.TYPE, "SIGNUP");
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DemoActivity.this, SignInActivity.class);
                intent.putExtra(Const.TYPE, "LOGIN");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        NC.NetworkAPICall(API.API_GET_APP_VERSION, false);
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        switch (apitype) {
            case API.API_GET_APP_VERSION:
                return Ion.with(this)
                        .load(API.API_GET_APP_VERSION)
                        .setTimeout(10 * 1000);
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonObject, String apitype) throws JSONException {
        switch (apitype) {
            case API.API_GET_APP_VERSION:
                if (jsonObject.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject data = null;

                    data = jsonObject.getJSONObject(Const.DATA);

                    String androidv = data.optString("android");
                    int aCode = Integer.parseInt(androidv);
                    if (Helper.getVersionCode(this) >= aCode) {

                    } else {
                        Helper.getVersionUpdateDialog(this);
                    }
                } else {
                    this.ErrorCallBack(jsonObject.optString(Const.MESSAGE), apitype);
                }

                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (NC.mprogress.isShowing()) NC.mprogress.dismiss();
    }
    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {

    }

    class RemindTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {

                    if (page > layouts.length) {
                        timer.cancel();
                    } else {
                        mViewPager.setCurrentItem(page++);
                    }
                }
            });
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new ImageView[layouts.length];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.nonselecteditem_dot);
            dots[i].setPadding(10, 10, 10, 0);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setImageResource(R.drawable.selecteditem_dot);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public class MyPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
