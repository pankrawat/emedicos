package com.emedicoz.app.Feeds.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emedicoz.app.Common.BaseABNavActivity;
import com.emedicoz.app.Common.BaseABNoNavActivity;
import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Feeds.Adapter.FeedRVAdapter;
import com.emedicoz.app.Model.Banner;
import com.emedicoz.app.Model.OwnerInfo;
import com.emedicoz.app.Model.People;
import com.emedicoz.app.Model.Tags;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.PostResponse;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.SharedPreference;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedsFragment extends MainFragment {

    RecyclerView feedRV;
    TextView errorTV;
    SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<People> peopleArrayList;
    ArrayList<PostResponse> feedArrayList;
    Banner banner;

    public FeedRVAdapter feedRVAdapter;
    public boolean isRefresh;
    Activity activity;

    public String last_post_id = "";
    public String tag_id = "";
    LinearLayoutManager LM;
    public int people_L_count = 0;

    public int previousTotalItemCount;

    private boolean loading = true;
    private int visibleThreshold = 5;
    public int firstVisibleItem, visibleItemCount, totalItemCount;

    int people_you_m_position = 8;
    int banner_position = 15;
    int isalreadyconnected = 0;

    String errorMessageforFeeds;

    public FeedsFragment() {
        // Required empty public constructor
    }

    public static FeedsFragment newInstance() {
        FeedsFragment fragment = new FeedsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feeds, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        peopleArrayList = new ArrayList<People>();
        feedArrayList = new ArrayList<PostResponse>();
        banner = new Banner();
        last_post_id = "";
        if (activity instanceof BaseABNavActivity)
            swipeRefreshLayout = ((BaseABNavActivity) activity).swipeRefreshLayout;
        else if (activity instanceof BaseABNoNavActivity)
            swipeRefreshLayout = ((BaseABNoNavActivity) activity).swipeRefreshLayout;

        if (!SharedPreference.getInstance().getString(Const.SUBTITLE).equals("")) {
            try {
                String str = SharedPreference.getInstance().getString(Const.SUBTITLE);
                Tags tg = new Gson().fromJson(new JSONObject(str).toString(), Tags.class);
                tag_id = tg.getId();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        errorTV = (TextView) view.findViewById(R.id.errorTV);

        feedRV = (RecyclerView) view.findViewById(R.id.feedRV);
        LM = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        feedRV.setLayoutManager(LM);

        InitfeedAdapter();

        feedRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = LM.getChildCount();
                totalItemCount = LM.getItemCount();
                firstVisibleItem = LM.findFirstVisibleItemPosition();
                if (dy > 0) {
                    ((BaseABNavActivity) activity).PostFAB.setVisibility(View.GONE);
                    ((BaseABNavActivity) activity).bottomPanelRL.setVisibility(View.GONE);
                } else if (dy < 0) {
                    ((BaseABNavActivity) activity).PostFAB.setVisibility(View.VISIBLE);
                    ((BaseABNavActivity) activity).bottomPanelRL.setVisibility(View.VISIBLE);
                }
                if (totalItemCount > 10) { // if the list is more than 10 then only pagination will work
                    if (loading) {
                        if (totalItemCount > previousTotalItemCount) {
                            loading = false;
                            previousTotalItemCount = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) {

                        int i = 0;
                        while (i < totalItemCount) {

                            if (feedArrayList.size() > 0 && feedArrayList.size() > totalItemCount - 1 - i) {
                                if (feedArrayList.get(totalItemCount - 1 - i).getId().equals("00")) {
                                    i++;
                                } else {
                                    last_post_id = feedArrayList.get(totalItemCount - 1 - i).getId();
                                    i = totalItemCount;
                                }
                            }
                        }
                        if (isalreadyconnected == 0) {
                            Log.e("feeds ", "called 2 " + feedArrayList.size());
                            RefreshFeedList(false);
                            isalreadyconnected = 1;
                        }
                        people_L_count = 0;
                        loading = true;
                    }
                }
            }
        });
        RefreshFeedList(true);
    }

    protected void InitfeedAdapter() {
        if (last_post_id.equals("")) {
            if (feedArrayList.size() > 0 && peopleArrayList != null && banner != null) {
                errorTV.setVisibility(View.GONE);
                feedRV.setVisibility(View.VISIBLE);
                Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_FEEDS, feedArrayList);
                feedRVAdapter = new FeedRVAdapter(activity, feedArrayList, activity, peopleArrayList, banner);
                feedRV.setAdapter(feedRVAdapter);
            } else if (Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_FEEDS) != null &&
                    peopleArrayList != null &&
                    banner != null &&
                    !Helper.isConnected(activity)) {
                errorTV.setVisibility(View.GONE);
                feedRV.setVisibility(View.VISIBLE);
                feedArrayList = (ArrayList<PostResponse>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_FEEDS);
                feedRVAdapter = new FeedRVAdapter(activity, feedArrayList, activity, null, null);
                feedRV.setAdapter(feedRVAdapter);
            } else {
                errorTV.setVisibility(View.VISIBLE);
                feedRV.setVisibility(View.GONE);
                errorTV.setText(errorMessageforFeeds);
            }
        } else {
            feedRVAdapter.notifyDataSetChanged();
            feedRVAdapter.updateData(peopleArrayList, banner);
        }
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_GET_FEEDS_FOR_USER:
                return Ion.with(this)
                        .load(API.API_GET_FEEDS_FOR_USER)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.LAST_POST_ID, last_post_id)
                        .setMultipartParameter(Const.TAG_ID, tag_id)
                        .setMultipartParameter(Const.SEARCH_TEXT, SharedPreference.getInstance().getString(Const.SEARCHED_QUERY));
            case API.API_GET_LIVE_STREAM:
                return Ion.with(this)
                        .load(API.API_GET_LIVE_STREAM)
                        .setTimeout(12 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
            case API.API_PEOPLE_YOU_MAY_KNOW:
                return Ion.with(this)
                        .load(API.API_PEOPLE_YOU_MAY_KNOW)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
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
            case API.API_FEEDS_BANNER:
                return Ion.with(this)
                        .load(API.API_FEEDS_BANNER)
                        .setTimeout(15 * 1000);
            case API.API_GET_APP_VERSION:
                return Ion.with(this)
                        .load(API.API_GET_APP_VERSION)
                        .setTimeout(10 * 1000);
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonobject, String apitype) throws JSONException {
        Gson gson = new Gson();
        JSONArray dataarray;
        switch (apitype) {
            case API.API_GET_LIVE_STREAM:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    feedArrayList = new ArrayList<>();
                    JSONObject data = jsonobject.optJSONObject(Const.DATA);
                    PostResponse response = new PostResponse();
                    OwnerInfo owner = new OwnerInfo();
                    response.setPost_type(Const.POST_TYPE_LIVE_STREAM);
                    response.setHlslink(data.optString(Const.HLS));
                    response.setId("00");
                    owner.setId(data.optString(Const.ID));
                    owner.setName(data.optString(Const.NAME));
                    owner.setProfile_picture(data.optString(Const.PROFILE_PICTURE));
                    response.setPost_owner_info(owner);

                    feedArrayList.add(response);
                    NetworkAPICall(API.API_GET_FEEDS_FOR_USER, false);
                } else {
                    NetworkAPICall(API.API_GET_FEEDS_FOR_USER, false);
                    if (last_post_id.equals("")) {
                        feedArrayList = new ArrayList<>();
                    }
                }
                break;
            case API.API_GET_NOTIFICATION_COUNT:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject data = jsonobject.optJSONObject(Const.DATA);
                    SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, Integer.parseInt(data.optString(Const.COUNTER)));
                    if (Integer.parseInt(data.optString(Const.COUNTER)) > 0) {
                        ((BaseABNavActivity) activity).notifyTV.setVisibility(View.VISIBLE);
                        ((BaseABNavActivity) activity).notifyTV.setText(data.optString(Const.COUNTER));
                    }
                }
                break;
            case API.API_GET_APP_VERSION:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject data = jsonobject.getJSONObject(Const.DATA);
                    String androidv = data.optString("android");
                    int aCode = Integer.parseInt(androidv);
                    if (Helper.getVersionCode(activity) >= aCode) {
                    } else {
                        Helper.getVersionUpdateDialog(activity);
                    }
                } else {
                    this.ErrorCallBack(jsonobject.optString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_GET_FEEDS_FOR_USER:
                isalreadyconnected = 0;
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    dataarray = jsonobject.getJSONArray(Const.DATA);
                    Log.e("feeds ", " " + feedArrayList.size());
                    Log.e("feeds ", "array " + dataarray.toString());
                    int pos = 0;
                    if (dataarray.length() > 0) {
                        int i = 0;
                        while (i < dataarray.length() + dataarray.length() / people_you_m_position + dataarray.length() / banner_position) {
                            if (i > people_you_m_position || feedArrayList.size() > banner_position)
                                pos = i - people_L_count;
                            else pos = i;
                            if (i > 0 && i % people_you_m_position == 0) { //8
                                PostResponse response = new PostResponse();
                                response.setId("00");
                                response.setPost_type(Const.POST_TYPE_PEOPLEYMK);
                                feedArrayList.add(response);
                                i++;
                                people_L_count++;
                            } else if (feedArrayList.size() > 0 && feedArrayList.size() % banner_position == 0) { //15
                                PostResponse response = new PostResponse();
                                response.setId("00");
                                response.setPost_type(Const.POST_TYPE_BANNER);
                                feedArrayList.add(response);
                                i++;
                                people_L_count++;
                            } else {
                                JSONObject singledatarow = dataarray.getJSONObject(pos);
                                PostResponse response = gson.fromJson(singledatarow.toString(), PostResponse.class);
                                feedArrayList.add(response);
                                i++;
                            }
                        }
                    }
                    InitfeedAdapter();

                } else {

                    this.ErrorCallBack(jsonobject.optString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_PEOPLE_YOU_MAY_KNOW:
                isalreadyconnected = 0;
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    dataarray = jsonobject.getJSONArray(Const.DATA);
                    Log.e("people ", " " + jsonobject);
                    if (dataarray.length() > 0) {
                        int i = 0;
                        peopleArrayList = new ArrayList();
                        while (i < dataarray.length()) {
                            JSONObject singledatarow = dataarray.getJSONObject(i);
                            People response = gson.fromJson(singledatarow.toString(), People.class);
                            peopleArrayList.add(response);
                            i++;
                        }
                    }
                    InitfeedAdapter();
                } else {
                    this.ErrorCallBack(jsonobject.optString(Const.MESSAGE), apitype);
                }
                Log.e("get people", " " + jsonobject);
                break;
        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            isRefresh = false;
        }
        switch (apitype) {
            case API.API_FEEDS_BANNER:
                isalreadyconnected = 0;
                banner = new Banner();
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject data = jsonobject.optJSONObject(Const.DATA);
                    banner = gson.fromJson(data.toString(), Banner.class);
                    InitfeedAdapter();
                } else this.ErrorCallBack(jsonobject.optString(Const.MESSAGE), apitype);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) > 0) {
            ((BaseABNavActivity) activity).notifyTV.setVisibility(View.VISIBLE);
            ((BaseABNavActivity) activity).notifyTV.setText(String.valueOf(SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT)));
        } else {
            ((BaseABNavActivity) activity).notifyTV.setVisibility(View.GONE);
        }
    }

    @Override
    public void ErrorCallBack(String object, String apitype) {
        switch (apitype) {
            case API.API_GET_FEEDS_FOR_USER:
            case API.API_GET_LIVE_STREAM:
                if (last_post_id.equals("")) {
                    errorMessageforFeeds = object;
                    InitfeedAdapter();
                }
                break;
        }
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            isRefresh = false;
        }
    }

    public void RefreshFeedList(boolean show) {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(show);
            isRefresh = show;
        }
        NetworkAPICall(API.API_PEOPLE_YOU_MAY_KNOW, false);
        NetworkAPICall(API.API_FEEDS_BANNER, false);
        if (last_post_id == "") {
            NetworkAPICall(API.API_GET_LIVE_STREAM, false);

        } else NetworkAPICall(API.API_GET_FEEDS_FOR_USER, false);
    }
}
