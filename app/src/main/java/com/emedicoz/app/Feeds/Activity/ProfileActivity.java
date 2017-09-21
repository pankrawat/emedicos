package com.emedicoz.app.Feeds.Activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Feeds.Adapter.FeedRVAdapter;
import com.emedicoz.app.Feeds.Adapter.PeopleFollowRVAdapter;
import com.emedicoz.app.Model.User;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.FollowResponse;
import com.emedicoz.app.Response.PostResponse;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.Network.NetworkCall;
import com.emedicoz.app.Utils.SharedPreference;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener, NetworkCall.MyNetworkCallBack {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    public static boolean IS_PROFILE_UPDATED = false;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private RelativeLayout mTitleContainer;
    private TextView mTitle;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    public NetworkCall networkCall;

    String id;
    String errorMessage;
    public String last_post_id = "";
    ArrayList<PostResponse> feedArrayList;
    ArrayList<FollowResponse> followingArrayList;
    ArrayList<FollowResponse> followersArrayList;

    TextView profileName;
    TextView postTV;
    TextView followersTV;
    TextView followingTV;
    TextView errorTV;
    TextView interestedcoursesTV;

    Button followBtn;

    ImageView profileImageIV;
    ImageView profileImageIVText;

    RecyclerView profileRV;

    LinearLayoutManager LM;

    boolean isRefresh = false;

    FeedRVAdapter feedRVAdapter;
    PeopleFollowRVAdapter peopleFollowRVAdapter;

    public int previousTotalItemCount;

    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    CollapsingToolbarLayout collapsingToolbar;
    User user;

    int AdapterType = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        networkCall = new NetworkCall(this, this);
        feedArrayList = new ArrayList<PostResponse>();
        followingArrayList = new ArrayList<FollowResponse>();
        followersArrayList = new ArrayList<FollowResponse>();

        if (getIntent() != null) {
            id = getIntent().getStringExtra(Const.ID);
            networkCall.NetworkAPICall(API.API_GET_USER, true);
        }
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.blue));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent));
        last_post_id = "";

        followBtn = (Button) findViewById(R.id.followBtn);
        profileRV = (RecyclerView) findViewById(R.id.ProfileRV);

        LM = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        profileRV.setLayoutManager(LM);

        profileName = (TextView) findViewById(R.id.profileName);
        followingTV = (TextView) findViewById(R.id.followingTV);
        followersTV = (TextView) findViewById(R.id.followersTV);
        postTV = (TextView) findViewById(R.id.postTV);
        errorTV = (TextView) findViewById(R.id.errorTV);
        interestedcoursesTV = (TextView) findViewById(R.id.interestedcoursesTV);

        followingTV.setOnClickListener(this);
        followersTV.setOnClickListener(this);
        postTV.setOnClickListener(this);

        profileImageIV = (ImageView) findViewById(R.id.profileImage);
        profileImageIVText = (ImageView) findViewById(R.id.profileImageText);

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle = (TextView) findViewById(R.id.main_textview_title);

        mTitleContainer = (RelativeLayout) findViewById(R.id.collapse_toolbar_RL);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);

        mAppBarLayout.addOnOffsetChangedListener(this);

        setSupportActionBar(mToolbar);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        profileRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (AdapterType != 1 && AdapterType != 2) {
                    visibleItemCount = LM.getChildCount();
                    totalItemCount = LM.getItemCount();
                    firstVisibleItem = LM.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotalItemCount) {
                            loading = false;
                            previousTotalItemCount = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {
                        // End has been reached

                        Log.i("Yaeye!", "end called");

                        // Do something
                        int i = 0;
                        while (i < totalItemCount) {
                            last_post_id = feedArrayList.get(totalItemCount - 1 - i).getId();
                            i = totalItemCount;
                        }
                        RefreshFeedList(false);
                        loading = true;
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (networkCall.mprogress.isShowing()) networkCall.mprogress.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FeedsActivity.IS_COMMENT_REFRESHED) {
            if (feedRVAdapter != null)
                feedRVAdapter.ItemChangedatPostId(SharedPreference.getInstance().getPost(), 0);
            FeedsActivity.IS_COMMENT_REFRESHED = false;
        }
        if (FeedsActivity.IS_NEW_POST_ADDED) {
            RefreshFeedList(true);
            last_post_id = "";
            FeedsActivity.IS_NEW_POST_ADDED = false;
        }
        if (FeedsActivity.IS_POST_DELETED) {
            if (feedRVAdapter != null)
                feedRVAdapter.ItemChangedatPostId(SharedPreference.getInstance().getPost(), 1);
            last_post_id = "";
            FeedsActivity.IS_POST_DELETED = false;
        }
        if (FeedsActivity.IS_POST_UPDATED) {
            if (feedRVAdapter != null)
                feedRVAdapter.ItemChangedatPostId(SharedPreference.getInstance().getPost(), 0);
            last_post_id = "";
            FeedsActivity.IS_POST_UPDATED = false;
        }
        if (IS_PROFILE_UPDATED) {
            user = SharedPreference.getInstance().getLoggedInUser();
            InitView();
            IS_PROFILE_UPDATED = false;
        }
    }

    public void RefreshFeedList(boolean show1) {
        if (user != null && !user.getPost_count().equals("0"))
            networkCall.NetworkAPICall(API.API_GET_FEEDS_FOR_USER, show1);
        else {
            InitfeedAdapter();
        }
    }

    public void InitView() {
        if (AdapterType == 3)
            RefreshFeedList(false);
        user.setName(Helper.CapitalizeText(user.getName()));

        if (!TextUtils.isEmpty(user.getProfile_picture())) {
            profileImageIV.setVisibility(View.VISIBLE);
            profileImageIVText.setVisibility(View.GONE);
            Ion.with(ProfileActivity.this).load(user.getProfile_picture())
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (result != null)
                                profileImageIV.setImageBitmap(result);
                            else
                                profileImageIV.setImageResource(R.mipmap.default_pic);
                        }
                    });
        } else {
            Drawable dr = Helper.GetDrawable(user.getName(), this, user.getId());
            if (dr != null) {
                profileImageIV.setVisibility(View.GONE);
                profileImageIVText.setVisibility(View.VISIBLE);
                profileImageIVText.setImageDrawable(dr);
            } else {
                profileImageIV.setVisibility(View.VISIBLE);
                profileImageIVText.setVisibility(View.GONE);
                profileImageIV.setImageResource(R.mipmap.default_pic);
            }
        }
        collapsingToolbar.setTitle(user.getName());
        profileName.setText(user.getName());
        if (!TextUtils.isEmpty(user.getUser_registration_info().getInterested_course_text())) {
            interestedcoursesTV.setText(Html.fromHtml("<b><i>Courses Interested In: </b></i>" + user.getUser_registration_info().getInterested_course_text()));
            interestedcoursesTV.setVisibility(View.VISIBLE);
        } else interestedcoursesTV.setVisibility(View.GONE);

        if (user.getId().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
            followBtn.setVisibility(View.GONE);
        else {
            followBtn.setVisibility(View.VISIBLE);
            if (user.is_following()) {
                followBtn = changeBackgroundColor(followBtn, 1);
            } else {
                followBtn = changeBackgroundColor(followBtn, 0);
            }
        }
        followBtn.setOnClickListener(onFollowClick);
        if (Integer.valueOf(user.getFollowers_count()) == 1)
            followersTV.setText(user.getFollowers_count() + "\nFollower");
        else
            followersTV.setText(user.getFollowers_count() + "\nFollowers");

        followingTV.setText(user.getFollowing_count() + "\nFollowing");
        postTV.setText(user.getPost_count() + "\nPosts");
    }

    public Button changeBackgroundColor(Button v, int type) {
        v.setBackgroundResource(R.drawable.reg_round_blue_bg);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        if (type == 1) {
            v.setText(R.string.following);
            v.setTextColor(getResources().getColor(R.color.white));
            drawable.setStroke(2, getResources().getColor(R.color.white));
            drawable.setColor(getResources().getColor(R.color.blue));
        } else {
            drawable.setStroke(2, getResources().getColor(R.color.blue));
            drawable.setColor(getResources().getColor(R.color.white));
            v.setText(R.string.follow);
            v.setTextColor(getResources().getColor(R.color.blue));
        }

        return v;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (id.equals(SharedPreference.getInstance().getLoggedInUser().getId()))
            getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editIM:
                Helper.GoToEditProfileActivity(ProfileActivity.this, Const.REGISTRATION, Const.PROFILE);
                break;
        }
        return true;
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_FOLLOW:
                return Ion.with(this)
                        .load(API.API_FOLLOW).setTimeout(10 * 1000)
                        .setMultipartParameter(Const.USER_ID, user.getId())
                        .setMultipartParameter(Const.FOLLOWER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
            case API.API_UNFOLLOW:
                return Ion.with(this)
                        .load(API.API_UNFOLLOW).setTimeout(10 * 1000)
                        .setMultipartParameter(Const.USER_ID, user.getId())
                        .setMultipartParameter(Const.FOLLOWER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
            case API.API_GET_FEEDS_FOR_USER:
                return Ion.with(this)
                        .load(API.API_GET_FEEDS_FOR_USER + Const.IS_WATCHER + SharedPreference.getInstance().getLoggedInUser().getId())
                        .setTimeout(20 * 1000)
                        .setMultipartParameter(Const.USER_ID, id)
                        .setMultipartParameter(Const.LAST_POST_ID, last_post_id);
            case API.API_FOLLOWING_LIST:
                return Ion.with(this)
                        .load(API.API_FOLLOWING_LIST + Const.IS_WATCHER + SharedPreference.getInstance().getLoggedInUser().getId())
                        .setTimeout(20 * 1000)
                        .setMultipartParameter(Const.USER_ID, id);
            case API.API_FOLLOWERS_LIST:
                return Ion.with(this)
                        .load(API.API_FOLLOWERS_LIST + Const.IS_WATCHER + SharedPreference.getInstance().getLoggedInUser().getId())
                        .setTimeout(20 * 1000)
                        .setMultipartParameter(Const.USER_ID, id);
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        switch (apitype) {
            case API.API_GET_USER:
                return Ion.with(this)
                        .load(API.API_GET_USER + id + Const.IS_WATCHER + SharedPreference.getInstance().getLoggedInUser().getId())
                        .setTimeout(10 * 1000);
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonobject, String apitype) throws JSONException {
        Gson gson = new Gson();
        JSONArray dataarray;
        if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
            switch (apitype) {
                case API.API_GET_USER:
                    JSONObject data = jsonobject.getJSONObject(Const.DATA);
                    user = gson.fromJson(data.toString(), User.class);
                    InitView();
                    break;
                case API.API_FOLLOW:
                    InitViewFollowUnfollow(1);
                    break;
                case API.API_UNFOLLOW:
                    InitViewFollowUnfollow(0);
                    break;
                case API.API_GET_FEEDS_FOR_USER:
                    dataarray = jsonobject.getJSONArray(Const.DATA);
                    if (last_post_id.equals("")) {
                        feedArrayList = new ArrayList<>();
                    }
                    if (dataarray.length() > 0) {
                        int i = 0;
                        while (i < dataarray.length()) {
                            JSONObject singledatarow = dataarray.getJSONObject(i);
                            PostResponse response = gson.fromJson(singledatarow.toString(), PostResponse.class);
                            feedArrayList.add(response);
                            i++;
                        }
                    }
                    InitfeedAdapter();

                    break;
                case API.API_FOLLOWING_LIST:
                    followingArrayList = new ArrayList<>();
                    dataarray = jsonobject.getJSONArray(Const.DATA);
                    if (dataarray.length() > 0) {
                        int i = 0;
                        while (i < dataarray.length()) {
                            JSONObject singledatarow = dataarray.getJSONObject(i);
                            FollowResponse response = gson.fromJson(singledatarow.toString(), FollowResponse.class);
                            followingArrayList.add(response);
                            i++;
                        }
                    }
                    InitfollowingAdapter();

                    break;
                case API.API_FOLLOWERS_LIST:
                    followersArrayList = new ArrayList<>();
                    dataarray = jsonobject.getJSONArray(Const.DATA);
                    if (dataarray.length() > 0) {
                        int i = 0;
                        while (i < dataarray.length()) {
                            JSONObject singledatarow = dataarray.getJSONObject(i);
                            FollowResponse response = gson.fromJson(singledatarow.toString(), FollowResponse.class);
                            followersArrayList.add(response);
                            i++;
                        }
                    }
                    InitfollowersAdapter();

                    break;
            }
        } else {
            if (last_post_id.equals("")) {
                profileRV.setVisibility(View.GONE);
                this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
            }
            if (apitype.equals(API.API_FOLLOW) || apitype.equals(API.API_UNFOLLOW))
                followBtn.setEnabled(true);

        }
        Log.e("Profile ", apitype + " " + jsonobject);

    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Log.e("profile json", "Error " + jsonstring);
        errorMessage = jsonstring;
        switch (apitype) {
            case API.API_GET_FEEDS_FOR_USER:
                InitfeedAdapter();
                break;
            case API.API_FOLLOWERS_LIST:
                InitfollowersAdapter();
                break;
            case API.API_FOLLOWING_LIST:
                InitfollowingAdapter();
                break;
            default:
                Toast.makeText(this, jsonstring, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    protected void InitfeedAdapter() {
        if (last_post_id.equals("")) {
            profileRV.invalidate();
            if (feedArrayList.size() > 0) {
                errorTV.setVisibility(View.GONE);
                profileRV.setVisibility(View.VISIBLE);
                feedRVAdapter = new FeedRVAdapter(ProfileActivity.this, feedArrayList, ProfileActivity.this, null, null);
                profileRV.setAdapter(feedRVAdapter);
            } else {
                errorTV.setText(R.string.no_feeds_found);
                errorTV.setVisibility(View.VISIBLE);
                profileRV.setVisibility(View.GONE);
            }
        } else {
            profileRV.setVisibility(View.VISIBLE);
            feedRVAdapter.notifyDataSetChanged();
        }
    }

    protected void InitfollowingAdapter() {
        if (followingArrayList.size() > 0) {
            profileRV.invalidate();
            profileRV.setVisibility(View.VISIBLE);
            errorTV.setVisibility(View.GONE);

            peopleFollowRVAdapter = new PeopleFollowRVAdapter(followingArrayList, ProfileActivity.this);
            profileRV.setAdapter(peopleFollowRVAdapter);
        } else {
            errorTV.setText(R.string.no_following_found);
            errorTV.setVisibility(View.VISIBLE);
            profileRV.setVisibility(View.GONE);
        }
    }

    protected void InitfollowersAdapter() {
        if (followersArrayList.size() > 0) {
            profileRV.invalidate();
            errorTV.setVisibility(View.GONE);
            profileRV.setVisibility(View.VISIBLE);
            peopleFollowRVAdapter = new PeopleFollowRVAdapter(followersArrayList, ProfileActivity.this);
            profileRV.setAdapter(peopleFollowRVAdapter);
        } else {
            errorTV.setText(R.string.no_followers_found);
            errorTV.setVisibility(View.VISIBLE);
            profileRV.setVisibility(View.GONE);
        }
    }

    public void InitViewFollowUnfollow(int type) {
        followBtn.setEnabled(true);
        if (type == 1) {
            followBtn = changeBackgroundColor(followBtn, type);
            user.setIs_following(true);
            user.setFollowers_count(setfollowers(type));
            followersTV.setText(user.getFollowers_count() + "\nFollowers");
        } else {
            followBtn = changeBackgroundColor(followBtn, type);
            user.setIs_following(false);
            user.setFollowers_count(setfollowers(type));
            followersTV.setText(user.getFollowers_count() + "\nFollowers");
        }
    }

    public String setfollowers(int type) {
        String followers = user.getFollowers_count();
        if (type == 1)
            return String.valueOf(Integer.valueOf(followers) + 1);
        else
            return String.valueOf(Integer.valueOf(followers) - 1);
    }

    View.OnClickListener onFollowClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            followBtn.setEnabled(false);
            if (user.is_following()) {
                networkCall.NetworkAPICall(API.API_UNFOLLOW, true);
            } else {
                networkCall.NetworkAPICall(API.API_FOLLOW, true);
            }
        }
    };

  /*  @Override
    public void onRefresh() {
        Log.e("swipe refresh", "on refresh dash board");
        if (CheckConnection.isConnection(this)) {
//            swipeRefreshLayout.setRefreshing(true);
//            isRefresh = true;
            last_post_id = "";
            firstVisibleItem = 0;
            previousTotalItemCount = 0;
            visibleItemCount = 0;
            RefreshFeedList(!isRefresh);
        } else {
            isRefresh = false;
//            swipeRefreshLayout.setRefreshing(false);
        }
    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.followersTV:
                interestedcoursesTV.setVisibility(View.GONE);
                if (AdapterType != 1) {
                    postTV.setTextColor(getResources().getColor(R.color.black));
                    followersTV.setTextColor(getResources().getColor(R.color.blue));
                    followingTV.setTextColor(getResources().getColor(R.color.black));
                    if (!user.getFollowers_count().equals("0") && followersArrayList.size() == 0)
                        networkCall.NetworkAPICall(API.API_FOLLOWERS_LIST, true);
                    else InitfollowersAdapter();
                }
                AdapterType = 1;
                break;
            case R.id.followingTV:
                interestedcoursesTV.setVisibility(View.GONE);
                if (AdapterType != 2) {
                    postTV.setTextColor(getResources().getColor(R.color.black));
                    followersTV.setTextColor(getResources().getColor(R.color.black));
                    followingTV.setTextColor(getResources().getColor(R.color.blue));
                    if (!user.getFollowing_count().equals("0") && followingArrayList.size() == 0)
                        networkCall.NetworkAPICall(API.API_FOLLOWING_LIST, true);
                    else InitfollowingAdapter();
                }
                AdapterType = 2;
                break;
            case R.id.postTV:
                interestedcoursesTV.setVisibility(View.VISIBLE);
                last_post_id = "";
                if (AdapterType != 3) {
                    postTV.setTextColor(getResources().getColor(R.color.blue));
                    followersTV.setTextColor(getResources().getColor(R.color.black));
                    followingTV.setTextColor(getResources().getColor(R.color.black));
                    if (!user.getPost_count().equals("0") && feedArrayList.size() == 0)
                        networkCall.NetworkAPICall(API.API_GET_FEEDS_FOR_USER, true);
                    else InitfeedAdapter();
                }
                AdapterType = 3;
                break;
        }
    }
}
