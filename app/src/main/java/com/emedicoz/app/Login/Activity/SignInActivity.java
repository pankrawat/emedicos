package com.emedicoz.app.Login.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Login.Fragment.Login;
import com.emedicoz.app.Login.Fragment.SignUp;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.GooglePlus_login;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.LinkDialog;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.Network.NetworkCall;
import com.emedicoz.app.views.Progress;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.Person;
import com.google.firebase.iid.FirebaseInstanceId;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public class SignInActivity extends AppCompatActivity implements GooglePlus_login.OnClientConnectedListener, NetworkCall.MyNetworkCallBack {
    private static final String TAG = "SignInActivity";
    public ViewPager viewPager;
    ViewPagerAdapter adapter;
    LinearLayout TabLayout;
    SignUp signUpFrag;
    Login loginFrag;
    CharSequence Titles[] = {"Sign Up", "Log In"};
    private TabLayout tabLayout;
    private TextView previousPosition = null;
    private CallbackManager callbackManager;
    private GooglePlus_login googlePlus_login;
    public Progress mprogress;
    LinkDialog d;
    NetworkCall NC;

    public String deviceId;

    @Override
    protected void onResume() {
        super.onResume();
        NC.NetworkAPICall(API.API_GET_APP_VERSION, false);
    }

    static com.linkedin.platform.utils.Scope buildScope() {
        return com.linkedin.platform.utils.Scope.build(com.linkedin.platform.utils.Scope.R_BASICPROFILE, com.linkedin.platform.utils.Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GooglePlus_login.RC_SIGN_IN) {
            googlePlus_login.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mprogress = new Progress(this);
        mprogress.setCancelable(false);
        deviceId = FirebaseInstanceId.getInstance().getToken();
        // TODO INITIALIZE THE FACEBOOK SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        Helper.logUser(this);
        NC = new NetworkCall(this, this);
        googlePlus_login = new GooglePlus_login(this);
        d = new LinkDialog(SignInActivity.this);

        //TODO FACEBOOK CALLBACK MANAGER
        callbackManager = new CallbackManager.Factory().create();
        facebookLogin();

        TabLayout = (LinearLayout) findViewById(R.id.fourFragmentLayout);
        viewPager = (ViewPager) findViewById(R.id.tabanim_viewpager);
        setupViewPager(viewPager);

        tabLayout = (android.support.design.widget.TabLayout) findViewById(R.id.tabanim_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(android.support.design.widget.TabLayout.GRAVITY_CENTER);
        setupTabs();

        viewPager.setOffscreenPageLimit(Titles.length);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (previousPosition != null) {
                    previousPosition.setTextColor(getResources().getColor(R.color.greayrefcode_dark));
                }

                TextView view = (TextView) tab.getCustomView();
                view.setTextColor(getResources().getColor(R.color.black));
                previousPosition = (TextView) tab.getCustomView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (getIntent().getExtras() != null) {
            String selectedfrag = getIntent().getStringExtra(Const.TYPE);
            switch (selectedfrag) {
                case "SIGNUP":
                    ((TextView) tabLayout.getTabAt(0).getCustomView()).setTextColor(getResources().getColor(R.color.black));
                    viewPager.setCurrentItem(0, true);
                    previousPosition = (TextView) tabLayout.getTabAt(0).getCustomView();
                    break;
                case "LOGIN":
                    ((TextView) tabLayout.getTabAt(1).getCustomView()).setTextColor(getResources().getColor(R.color.black));
                    viewPager.setCurrentItem(1, true);
                    previousPosition = (TextView) tabLayout.getTabAt(1).getCustomView();
                    break;
            }
        }
//        getLocation();
    }

    public void getLocation() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                String lat = Double.toString(location.getLatitude());
                String lon = Double.toString(location.getLongitude());
                Log.e("Location", "Your Location is:" + lat + "--" + lon);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e("Location", "onStatusChanged");
            }

            public void onProviderEnabled(String provider) {
                Log.e("Location", "onProviderEnabled");

            }

            public void onProviderDisabled(String provider) {
                Log.e("Location", "onProviderDisabled");

            }
        };
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // Check Permissions Now
            final int REQUEST_LOCATION = 2;

            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Display UI and wait for user interaction
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.LOCATION_FINE},
                        ACCESS_FINE_LOCATION);
            }*/
        } else {
            // permission has been granted, continue as usual
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    public String GetAddress(String lat, String lon) {
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        String ret = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                ret = strReturnedAddress.toString();
            } else {
                ret = "No Address returned!";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = "Can't get Address!";
        }
        return ret;
    }

    public void facebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mprogress.show();
                Log.e("AccessToken", (loginResult.getAccessToken()).toString());
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("SIGNUP_FACEBOOK", object.toString());

                        if (viewPager.getCurrentItem() == 0) {
                            ((SignUp) signUpFrag).SignUpTask(object, Const.SOCIAL_TYPE_FACEBOOK);
                        } else if (viewPager.getCurrentItem() == 1) {
                            ((Login) loginFrag).LogInTask(object, Const.SOCIAL_TYPE_FACEBOOK);
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,picture.width(500).height(500),birthday,friends{name,id}");
                request.setParameters(parameters);
                request.executeAsync();
                mprogress.hide();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    public void LoginMaster(String socialType) {
        if (Helper.isConnected(this)) {
            switch (socialType) {
                case Const.SOCIAL_TYPE_FACEBOOK:
                    LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_friends"));
                    break;
                case Const.SOCIAL_TYPE_GMAIL:
                    mprogress.show();
                    googlePlus_login.signIn();
                    break;
                case Const.SOCIAL_TYPE_LINKEDIN:
                    if (Helper.isLinkedInInstalled(this)) {
                        mprogress.show();
                        linkedInLoginFromApp();
                    } else {
                        linkedInLoginFromWeb();
                    }
                    break;
            }
        } else {
            mprogress.hide();
            Toast.makeText(this, Const.NO_INTERNET, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTabs() {
        Log.e(TAG, "the count of adapter is " + adapter.getCount());
        for (int i = 0; i < adapter.getCount(); i++) {
            TextView textView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            textView.setText(Titles[i]);

            tabLayout.getTabAt(i).setCustomView(textView);
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), viewPager);

        signUpFrag = new SignUp();
        loginFrag = new Login();

        adapter.addFrag(signUpFrag, Const.SIGNUP);
        adapter.addFrag(loginFrag, Const.LOGIN);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onGoogleProfileFetchComplete(JSONObject object) {
        mprogress.hide();
        Log.e("SIGNUP_Google", object.toString());
        if (viewPager.getCurrentItem() == 0) {
            ((SignUp) signUpFrag).SignUpTask(object, Const.SOCIAL_TYPE_GMAIL);
        } else if (viewPager.getCurrentItem() == 1) {
            ((Login) loginFrag).LogInTask(object, Const.SOCIAL_TYPE_GMAIL);
        }
    }

    @Override
    public void onClientFailed() {
        mprogress.hide();
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
        Log.e("SignUp", "SuccessCallBack " + jsonObject.toString());
        switch (apitype) {
            case API.API_GET_APP_VERSION:
                if (jsonObject.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject data = jsonObject.getJSONObject(Const.DATA);
                    String androidv = data.optString("android");
                    int aCode = Integer.parseInt(androidv);
                    if (Helper.getVersionCode(SignInActivity.this) >= aCode) {

                    } else {
                        Helper.getVersionUpdateDialog(SignInActivity.this);
                    }
                } else {
                    this.ErrorCallBack(jsonObject.optString(Const.MESSAGE), apitype);
                }
                break;
        }
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {

    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        ViewPager pager;

        public ViewPagerAdapter(FragmentManager manager, ViewPager pager) {
            super(manager);
            this.pager = pager;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void linkedInLoginFromApp() {
        LISessionManager.getInstance(getApplicationContext()).init(SignInActivity.this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                getLinkedInProfile();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Log.e("onAuthError", error.toString());
            }
        }, true);
    }

    public void linkedInLoginFromWeb() {
        d.show();
        d.setVerifierListener(new LinkDialog.OnVerifyListener() {
            @Override
            public void onVerify(String verifier) {
                d.dismiss();
                new GetLinkedInProTask().execute(verifier);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (NC.mprogress.isShowing()) NC.mprogress.dismiss();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void getLinkedInProfile() {

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());

        String linkedinRestApiUrl = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address,headline,industry,picture-url,num-connections,positions)";
        apiHelper.getRequest(SignInActivity.this, linkedinRestApiUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse result) {
                try {
                    Log.e("new login", "LinkedIn " + result.toString());
                    JSONObject responseData = result.getResponseDataAsJson();
                    String name = responseData.optString("firstName") + " " + responseData.optString("lastName");
                    String email = responseData.optString("emailAddress");
                    String post = responseData.optString("headline");
                    String id = responseData.optString(Const.ID);
                    String image = responseData.optString(Const.PICTUREURL);
                    String connection = responseData.optString("numConnections");
                    JSONObject positions = responseData.optJSONObject("positions");
                    JSONArray values = positions.optJSONArray("values");
                    JSONObject object = values.optJSONObject(0);
                    JSONObject userCompany = object.optJSONObject("company");
                    String company = userCompany.optString(Const.NAME);

                    JSONObject newObject = new JSONObject();
                    newObject.put(Const.ID, id).put(Const.NAME, name).put(Const.EMAIL, email).put(Const.GENDER, 0).put(Const.PICTUREURL, image);

                    if (viewPager.getCurrentItem() == 0) {
                        ((SignUp) signUpFrag).SignUpTask(newObject, Const.SOCIAL_TYPE_LINKEDIN);
                    } else if (viewPager.getCurrentItem() == 1) {
                        ((Login) loginFrag).LogInTask(newObject, Const.SOCIAL_TYPE_LINKEDIN);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mprogress.hide();
            }

            @Override
            public void onApiError(LIApiError LIApiError) {
                Log.e("onApiError", LIApiError.toString());
                mprogress.hide();
            }

        });
    }

    class GetLinkedInProTask extends AsyncTask<String, Void, Person> {


        final LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(Const.LINKEDIN_CONSUMER_KEY, Const.LINKEDIN_CONSUMER_SECRET);

        LinkedInApiClient client;
        LinkedInAccessToken accessToken = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mprogress.show();

        }

        @Override
        protected Person doInBackground(String... params) {
            try {
                accessToken = LinkDialog.oAuthService.getOAuthAccessToken(LinkDialog.liToken, params[0]);
                factory.createLinkedInApiClient(accessToken);
                client = factory.createLinkedInApiClient(accessToken);
                client = factory.createLinkedInApiClient(accessToken);
                com.google.code.linkedinapi.schema.Person profile = null;
                profile = client.getProfileForCurrentUser(EnumSet.of(ProfileField.ID, ProfileField.FIRST_NAME, ProfileField.LAST_NAME, ProfileField.EMAIL_ADDRESS, ProfileField.HEADLINE, ProfileField.INDUSTRY, ProfileField.PICTURE_URL, ProfileField.DATE_OF_BIRTH, ProfileField.LOCATION_NAME, ProfileField.MAIN_ADDRESS, ProfileField.LOCATION_COUNTRY, ProfileField.NUM_CONNECTIONS, ProfileField.POSITIONS_COMPANY, ProfileField.POSITIONS));
                if (profile != null) {
                    return profile;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Person profile) {
            super.onPostExecute(profile);
            mprogress.hide();
            try {
                if (profile == null) {
                    Toast.makeText(SignInActivity.this, "Unable to get LinkedInLogin ProfileActivity, Try Again Later", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("LinkedinSample", "error to get verifier");

                    Log.i("Linkedin", profile.getNumConnections() + "" + (profile.getHeadline().length() > 0 ? profile.getHeadline() : "No Position") + (profile.getPositions().getTotal() > 0 ? profile.getPositions().getPositionList().get(0).getCompany().getName() : "No Position"));

                    JSONObject newObject = new JSONObject();
                    newObject.put(Const.ID, profile.getId()).put(Const.NAME, profile.getFirstName() + " " + profile.getLastName()).put(Const.EMAIL, profile.getEmailAddress()).put(Const.GENDER, 0).put(Const.PICTUREURL, profile.getPictureUrl());

                    if (viewPager.getCurrentItem() == 0) {
                        ((SignUp) signUpFrag).SignUpTask(newObject, "3");
                    } else if (viewPager.getCurrentItem() == 1) {
                        ((Login) loginFrag).LogInTask(newObject, "3");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
