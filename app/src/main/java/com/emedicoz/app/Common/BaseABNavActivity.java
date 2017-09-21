package com.emedicoz.app.Common;

/**
 * Created by Cbc-03 on 06/07/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.Feeds.Adapter.MyListAdapter;
import com.emedicoz.app.Feeds.Fragment.AppSettingsFragment;
import com.emedicoz.app.Feeds.Fragment.FeedsFragment;
import com.emedicoz.app.Feeds.Fragment.HelpSupportFragment;
import com.emedicoz.app.Feeds.Fragment.RewardPointsFragment;
import com.emedicoz.app.Feeds.Fragment.SavedNotesFragment;
import com.emedicoz.app.Model.Tags;
import com.emedicoz.app.Model.User;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.Network.CheckConnection;
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

public abstract class BaseABNavActivity extends AppCompatActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, NetworkCall.MyNetworkCallBack {

    LinearLayout navheaderLL;
    RelativeLayout mFragmentLayout;
    RelativeLayout titleRL;
    public Fragment mFragment;
    protected Toolbar toolbar;

    public SwipeRefreshLayout swipeRefreshLayout;
    public SearchView searchView;

    public ImageView profileImage, profileImageText;

    TextView profileName, specialityTV, damsidTV, versionnameTV;
    public TextView toolbartitleTV;
    public TextView toolbarsubtitleTV;
    public TextView notifyTV;

    DrawerLayout drawer;
    public FloatingActionButton PostFAB;
    public LinearLayout bottomPanelRL;

    ListView listView;
    MyListAdapter listAdapter;
    ArrayList<String> expandableListTitle;

    public ImageView notificationiconIV;

    LinearLayout feedsLL;
    LinearLayout savednotesLL;
    LinearLayout helpLL;
    LinearLayout appsettingLL;

    public User user;
    public ArrayList<Tags> tagsArrayList;
    NetworkCall networkCall;

    Tags tg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkCall = new NetworkCall(this, this);
        // TODO: Move this to where you establish a user session
        Helper.logUser(this);

        toolbar = (Toolbar) findViewById(R.id.feeds_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.menu);
        user = SharedPreference.getInstance().getLoggedInUser();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        PostFAB = (FloatingActionButton) findViewById(R.id.postFAB);
        bottomPanelRL = (LinearLayout) findViewById(R.id.RL1);
        PostFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseABNavActivity.this, PostActivity.class);// new Post
                intent.putExtra(Const.FRAG_TYPE, Const.POST_FRAG);
                startActivity(intent);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.colorAccent, R.color.colorPrimaryDark);
        notifyTV = (TextView) findViewById(R.id.notifyTV);

        profileName = (TextView) findViewById(R.id.profileName);
        specialityTV = (TextView) findViewById(R.id.specialityTV);
        damsidTV = (TextView) findViewById(R.id.damsidTV);
        versionnameTV = (TextView) findViewById(R.id.vNameTV);

        toolbartitleTV = (TextView) findViewById(R.id.toolbartitleTV);
        toolbarsubtitleTV = (TextView) findViewById(R.id.toolbarsubtitleTV);
        titleRL = (RelativeLayout) findViewById(R.id.titleRL);

        InitSearchView();
        notificationiconIV = (ImageView) findViewById(R.id.imageIV);

        feedsLL = (LinearLayout) findViewById(R.id.feedsLL);
        savednotesLL = (LinearLayout) findViewById(R.id.savenotesLL);
        helpLL = (LinearLayout) findViewById(R.id.helpLL);
        appsettingLL = (LinearLayout) findViewById(R.id.settingsLL);

        profileImage = (ImageView) findViewById(R.id.profileImage);
        profileImageText = (ImageView) findViewById(R.id.profileImageText);

        listView = (ListView) findViewById(R.id.navLV);
        navheaderLL = (LinearLayout) findViewById(R.id.nav_headerLL);

        notificationiconIV.setOnClickListener(this);
        feedsLL.setOnClickListener(this);
        savednotesLL.setOnClickListener(this);
        appsettingLL.setOnClickListener(this);
        helpLL.setOnClickListener(this);

        feedsLL.setTag(Const.FEEDS);
        savednotesLL.setTag(Const.SAVEDNOTES);
        helpLL.setTag(Const.FEEDBACK);
        appsettingLL.setTag(Const.APPSETTING);

        mFragmentLayout = (RelativeLayout) findViewById(R.id.fragment_container);
        navheaderLL.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               Helper.GoToProfileActivity(BaseABNavActivity.this, user.getId());
                                               if (drawer.isDrawerOpen(GravityCompat.START))
                                                   drawer.closeDrawer(GravityCompat.START);
                                           }
                                       }
        );
        InitViews();
        mFragment = getFragment();
        if (mFragment != null)
            if (mFragment instanceof FeedsFragment) {
                ((ImageView) feedsLL.getChildAt(0)).setImageResource(R.mipmap.feed_blue);
                ((TextView) feedsLL.getChildAt(1)).setTextColor(getResources().getColor(R.color.blue));

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack("feeds").commit();
            } else
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).commit();
        if (!SharedPreference.getInstance().getString(Const.SUBTITLE).equals("")) {
            try {
                String str = SharedPreference.getInstance().getString(Const.SUBTITLE);
                tg = new Gson().fromJson(new JSONObject(str).toString(), Tags.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (tg != null) {
                toolbarsubtitleTV.setText(tg.getText());
                toolbarsubtitleTV.setVisibility(View.VISIBLE);
            }
        }
        getNavData();
        networkCall.NetworkAPICall(API.API_GET_TAG_LISTS, false);
        versionnameTV.setText(Html.fromHtml("<b>Version- </b>" + Helper.getVersionName(BaseABNavActivity.this)));

        // this hit will only pe called once the app is launched or the activity has been called.
        networkCall.NetworkAPICall(API.API_GET_APP_VERSION, false);
        networkCall.NetworkAPICall(API.API_GET_NOTIFICATION_COUNT, false);
    }

    public void InitSearchView() {

        searchView = (SearchView) findViewById(R.id.searchSV);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                titleRL.setVisibility(View.VISIBLE);
                PostFAB.setVisibility(View.VISIBLE);
                bottomPanelRL.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostFAB.setVisibility(View.GONE);
                bottomPanelRL.setVisibility(View.GONE);
                titleRL.setVisibility(View.GONE);
            }
        });

        View searchPlateView = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        // use this method for search process
        searchView.setQueryHint("Search here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // use this method when query submitted
                SharedPreference.getInstance().putString(Const.SEARCHED_QUERY, query);
                RefreshFragmentList(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e("Search Text Change", newText);
                return false;
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    }

    public void RefreshFragmentList(Fragment fragment) {
        if (fragment instanceof FeedsFragment) {
            ((FeedsFragment) fragment).last_post_id = "";
            ((FeedsFragment) fragment).firstVisibleItem = 0;
            ((FeedsFragment) fragment).previousTotalItemCount = 0;
            ((FeedsFragment) fragment).visibleItemCount = 0;
            ((FeedsFragment) fragment).RefreshFeedList(true);
        } else if (fragment instanceof SavedNotesFragment) {
            ((SavedNotesFragment) fragment).last_post_id = "";
            ((SavedNotesFragment) fragment).firstVisibleItem = 0;
            ((SavedNotesFragment) fragment).previousTotalItemCount = 0;
            ((SavedNotesFragment) fragment).visibleItemCount = 0;
            ((SavedNotesFragment) fragment).RefreshFeedList(true);
        }
    }

    public void InitTagsAdapter(final ArrayList<Tags> tagsList) {

        listAdapter = new MyListAdapter(this, expandableListTitle, tagsList) {
            @Override
            public void OnTextClick(String title, int type) {
                CustomNavigationClick(title, type);
            }
        };
        listView.setAdapter(listAdapter);
    }

    public void CustomNavigationClick(String title, int type) {
        String fragtitle = "";

        searchView.setVisibility(View.GONE);
        PostFAB.setVisibility(View.GONE);
        bottomPanelRL.setVisibility(View.GONE);
        toolbarsubtitleTV.setVisibility(View.GONE);

        if (!searchView.isIconified()) {
            titleRL.setVisibility(View.VISIBLE);
            PostFAB.setVisibility(View.VISIBLE);
            bottomPanelRL.setVisibility(View.VISIBLE);
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();
            // use this method when query submitted
            SharedPreference.getInstance().putString(Const.SEARCHED_QUERY, "");
        }
        switch (title) {

            case Const.FEEDS:
                PostFAB.setVisibility(View.VISIBLE);
                bottomPanelRL.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                fragtitle = getString(R.string.app_name);
                mFragment = FeedsFragment.newInstance();
                if (type == 1) {
                    if (!SharedPreference.getInstance().getString(Const.SUBTITLE).equals(null)) {
                        try {
                            String str = SharedPreference.getInstance().getString(Const.SUBTITLE);
                            tg = new Gson().fromJson(new JSONObject(str).toString(), Tags.class);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        toolbarsubtitleTV.setText(tg.getText());
                        toolbarsubtitleTV.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case Const.SAVEDNOTES:
                bottomPanelRL.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                fragtitle = getString(R.string.savedNotes);
                mFragment = SavedNotesFragment.newInstance();
                break;
            case Const.ALLCOURSES:
                Toast.makeText(this, "All Courses", Toast.LENGTH_SHORT);
                bottomPanelRL.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                break;
            case Const.MYCOURSES:
                Toast.makeText(this, "My Courses", Toast.LENGTH_SHORT);
                bottomPanelRL.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                break;
            case Const.FEEDBACK:
                fragtitle = getString(R.string.feedback);
                bottomPanelRL.setVisibility(View.VISIBLE);
                mFragment = HelpSupportFragment.newInstance();
                break;
            case Const.RATEUS:
                PostFAB.setVisibility(View.VISIBLE);
                bottomPanelRL.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                Helper.rateapp(this);
                break;
            case Const.APPSETTING:
                bottomPanelRL.setVisibility(View.VISIBLE);
                fragtitle = getString(R.string.appSettings);
                mFragment = AppSettingsFragment.newInstance();
                break;
            case Const.LOGOUT:
                PostFAB.setVisibility(View.VISIBLE);
                bottomPanelRL.setVisibility(View.VISIBLE);
                searchView.setVisibility(View.VISIBLE);
                if (type == 1) {
                    if (!SharedPreference.getInstance().getString(Const.SUBTITLE).equals(null)) {
                        try {
                            String str = SharedPreference.getInstance().getString(Const.SUBTITLE);
                            tg = new Gson().fromJson(new JSONObject(str).toString(), Tags.class);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        toolbarsubtitleTV.setText(tg.getText());
                        toolbarsubtitleTV.setVisibility(View.VISIBLE);
                    }
                }
                getLogoutDialog(this, getString(R.string.logout_title), getString(R.string.logout_confirmation_message));
                break;
        }
        if (fragtitle != "") {
            ChangeTabColor(title);
            toolbartitleTV.setText(fragtitle);
        }

        if (mFragment != null)
            if (mFragment instanceof FeedsFragment)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(Const.FEEDS).commit();
            else
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).addToBackStack(null).commit();
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);

        if (mFragment instanceof FeedsFragment || mFragment instanceof SavedNotesFragment) {
            swipeRefreshLayout.setEnabled(true);
        } else {
            swipeRefreshLayout.setEnabled(false);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (networkCall.mprogress.isShowing()) networkCall.mprogress.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = SharedPreference.getInstance().getLoggedInUser();
        user.setName(Helper.CapitalizeText(user.getName()));

        if (!TextUtils.isEmpty(user.getProfile_picture())) {
            profileImage.setVisibility(View.VISIBLE);
            profileImageText.setVisibility(View.GONE);
            Ion.with(this).load(user.getProfile_picture())
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (result != null)
                                profileImage.setImageBitmap(result);
                            else
                                profileImage.setImageResource(R.mipmap.default_pic);
                        }
                    });
        } else {
            Drawable dr = Helper.GetDrawable(user.getName(), BaseABNavActivity.this, user.getId());
            if (dr != null) {
                profileImage.setVisibility(View.GONE);
                profileImageText.setVisibility(View.VISIBLE);
                profileImageText.setImageDrawable(dr);
            } else {
                profileImage.setVisibility(View.VISIBLE);
                profileImageText.setVisibility(View.GONE);
                profileImage.setImageResource(R.mipmap.default_pic);
            }
        }
        if (SharedPreference.getInstance().getBoolean(Const.IS_PROFILE_CHANGED)) {
            networkCall.NetworkAPICall(API.API_GET_TAG_LISTS, false);
        }

        profileName.setText(user.getName());

        if (!TextUtils.isEmpty(user.getDams_tokken())) damsidTV.setVisibility(View.VISIBLE);
        else damsidTV.setVisibility(View.GONE);

        if (user.getUser_registration_info() != null) {
            if (!TextUtils.isEmpty(user.getUser_registration_info().getMaster_id_level_two_name()))
                specialityTV.setText(user.getUser_registration_info().getMaster_id_level_two_name());
        }
        damsidTV.setText(Html.fromHtml("<b>Dams Id:</b> " + (TextUtils.isEmpty(user.getDams_tokken()) ? "" : user.getDams_tokken())));
    }

    @Override
    public void onBackPressed() {
        onCustomBackPress();
    }

    protected abstract Fragment getFragment();

    protected abstract void InitViews();

    public void onCustomBackPress() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (!searchView.isIconified()) {
            titleRL.setVisibility(View.VISIBLE);
            PostFAB.setVisibility(View.VISIBLE);
            bottomPanelRL.setVisibility(View.VISIBLE);
            searchView.setIconified(true);
            searchView.onActionViewCollapsed();
            // use this method when query submitted
            SharedPreference.getInstance().putString(Const.SEARCHED_QUERY, "");
            RefreshFragmentList(fragment);
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragment instanceof SavedNotesFragment ||
                fragment instanceof AppSettingsFragment ||
                fragment instanceof HelpSupportFragment ||
                fragment instanceof RewardPointsFragment) {
            toolbartitleTV.setText(getString(R.string.app_name));
            ChangeTabColor(Const.FEEDS);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FeedsFragment.newInstance()).addToBackStack(Const.FEEDS).commit();
        } else if (fragment instanceof FeedsFragment) {
            this.finish();
        } else
            this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageIV:
                Intent intent = new Intent(BaseABNavActivity.this, PostActivity.class);// notification fragment
                intent.putExtra(Const.FRAG_TYPE, Const.NOTIFICATION);
                startActivity(intent);
                break;

            case R.id.feedsLL:

            case R.id.savenotesLL:

            case R.id.helpLL:

            case R.id.settingsLL:
                CustomNavigationClick((String) v.getTag(), 0);
//                ((ImageView) appsettingLL.getChildAt(0)).setImageResource(R.mipmap.settings_blue);
//                ((TextView) appsettingLL.getChildAt(1)).setTextColor(getResources().getColor(R.color.blue));
                break;
        }
    }

    public void getLogoutDialog(final Activity ctx, final String title, final String message) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(ctx);
        alertBuild
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        Helper.SignOutUser(BaseABNavActivity.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = alertBuild.create();
        dialog.show();
        int alertTitle = ctx.getResources().getIdentifier("alertTitle", "id", "android");
        ((TextView) dialog.findViewById(alertTitle)).setGravity(Gravity.CENTER);
        ((TextView) dialog.findViewById(alertTitle)).setGravity(Gravity.CENTER);
    }

    public void ChangeTabColor(String title) {

        ((ImageView) feedsLL.getChildAt(0)).setImageResource(R.mipmap.feeds_menu);
        ((TextView) feedsLL.getChildAt(1)).setTextColor(getResources().getColor(R.color.black));

        ((ImageView) savednotesLL.getChildAt(0)).setImageResource(R.mipmap.saved_notes_menu);
        ((TextView) savednotesLL.getChildAt(1)).setTextColor(getResources().getColor(R.color.black));

        ((ImageView) helpLL.getChildAt(0)).setImageResource(R.mipmap.help_menu);
        ((TextView) helpLL.getChildAt(1)).setTextColor(getResources().getColor(R.color.black));

        ((ImageView) appsettingLL.getChildAt(0)).setImageResource(R.mipmap.settings_menu);
        ((TextView) appsettingLL.getChildAt(1)).setTextColor(getResources().getColor(R.color.black));

        switch (title) {
            case Const.FEEDS:
                ((ImageView) feedsLL.getChildAt(0)).setImageResource(R.mipmap.feed_blue);
                ((TextView) feedsLL.getChildAt(1)).setTextColor(getResources().getColor(R.color.blue));
                break;
            case Const.SAVEDNOTES:
                ((ImageView) savednotesLL.getChildAt(0)).setImageResource(R.mipmap.saved_notes_blue);
                ((TextView) savednotesLL.getChildAt(1)).setTextColor(getResources().getColor(R.color.blue));
                break;
            case Const.APPSETTING:
                ((ImageView) appsettingLL.getChildAt(0)).setImageResource(R.mipmap.settings_blue);
                ((TextView) appsettingLL.getChildAt(1)).setTextColor(getResources().getColor(R.color.blue));
                break;
            case Const.FEEDBACK:
                ((ImageView) helpLL.getChildAt(0)).setImageResource(R.mipmap.help_blue);
                ((TextView) helpLL.getChildAt(1)).setTextColor(getResources().getColor(R.color.blue));
                break;

        }
    }

    @Override
    public void onRefresh() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment instanceof FeedsFragment) {
            if (CheckConnection.isConnection(this)) {
                swipeRefreshLayout.setRefreshing(true);
                ((FeedsFragment) fragment).isRefresh = true;
                ((FeedsFragment) fragment).last_post_id = "";
                ((FeedsFragment) fragment).firstVisibleItem = 0;
                ((FeedsFragment) fragment).previousTotalItemCount = 0;
                ((FeedsFragment) fragment).visibleItemCount = 0;
                ((FeedsFragment) fragment).people_L_count = 0;
                ((FeedsFragment) fragment).RefreshFeedList(true);
            } else {
                ((FeedsFragment) fragment).isRefresh = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        } else if (fragment instanceof SavedNotesFragment) {
            if (CheckConnection.isConnection(this)) {
                swipeRefreshLayout.setRefreshing(true);
                ((SavedNotesFragment) fragment).isRefresh = true;
                ((SavedNotesFragment) fragment).last_post_id = "";
                ((SavedNotesFragment) fragment).firstVisibleItem = 0;
                ((SavedNotesFragment) fragment).previousTotalItemCount = 0;
                ((SavedNotesFragment) fragment).visibleItemCount = 0;
                ((SavedNotesFragment) fragment).RefreshFeedList(true);
            } else {
                ((SavedNotesFragment) fragment).isRefresh = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_GET_NOTIFICATION_COUNT:
                return Ion.with(this)
                        .load(API.API_GET_NOTIFICATION_COUNT)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        switch (apitype) {
            case API.API_GET_TAG_LISTS:
                return Ion.with(this)
                        .load(API.API_GET_TAG_LISTS + SharedPreference.getInstance().getLoggedInUser().getId())
                        .setTimeout(10 * 1000);
            case API.API_GET_APP_VERSION:
                return Ion.with(this)
                        .load(API.API_GET_APP_VERSION)
                        .setTimeout(10 * 1000);
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException {
        Gson gson = new Gson();
        switch (apitype) {
            case API.API_GET_APP_VERSION:
                if (jsonstring.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject data = jsonstring.getJSONObject(Const.DATA);
                    String androidv = data.optString("android");
                    int aCode = Integer.parseInt(androidv);
                    if (Helper.getVersionCode(BaseABNavActivity.this) >= aCode) {
                    } else {
                        Helper.getVersionUpdateDialog(BaseABNavActivity.this);
                    }
                } else {
                    this.ErrorCallBack(jsonstring.optString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_GET_TAG_LISTS:
                if (jsonstring.optString(Const.STATUS).equals(Const.TRUE)) {
                    SharedPreference.getInstance().putBoolean(Const.IS_PROFILE_CHANGED, false);
                    JSONArray dataarray = jsonstring.getJSONArray(Const.DATA);
                    if (dataarray.length() > 0) {
                        tagsArrayList = new ArrayList<>();
                        int i = 0;
                        while (i < dataarray.length()) {
                            Tags tag = gson.fromJson(dataarray.get(i).toString(), Tags.class);
                            tagsArrayList.add(tag);
                            i++;
                        }
                        SharedPreference.getInstance().putString(Const.TAGLIST_OFFLINE, new Gson().toJson(tagsArrayList));
                        Log.e("tagList", new Gson().toJson(tagsArrayList));
                        getNavData();
                    }
                } else {
                    this.ErrorCallBack(jsonstring.optString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_GET_NOTIFICATION_COUNT:
                if (jsonstring.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject data = jsonstring.optJSONObject(Const.DATA);
                    SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, Integer.parseInt(data.optString(Const.COUNTER)));
                    if (Integer.parseInt(data.optString(Const.COUNTER)) > 0) {
                        notifyTV.setVisibility(View.VISIBLE);
                        notifyTV.setText(data.optString(Const.COUNTER));
                    }
                }
                break;
        }
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Toast.makeText(this, jsonstring, Toast.LENGTH_SHORT).show();
    }

    public void getNavData() {

        expandableListTitle = Helper.gettitleList(this);
        InitTagsAdapter(tagsArrayList);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFragment instanceof HelpSupportFragment) {
            mFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
