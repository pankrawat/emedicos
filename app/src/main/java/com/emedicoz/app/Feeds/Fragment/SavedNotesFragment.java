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
import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Feeds.Adapter.FeedRVAdapter;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.PostResponse;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.Network.CheckConnection;
import com.emedicoz.app.Utils.SharedPreference;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SavedNotesFragment extends MainFragment implements SwipeRefreshLayout.OnRefreshListener {


    RecyclerView feedRV;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<PostResponse> feedArrayList;

    FeedRVAdapter feedRVAdapter;
    public boolean isRefresh;
    Activity activity;
    String errorMessageforSavednotes;
    TextView errorTV;

    public String last_post_id = "";
    LinearLayoutManager LM;

    public int previousTotalItemCount;

    private boolean loading = true;
    private int visibleThreshold = 2;
    public int firstVisibleItem, visibleItemCount, totalItemCount;


    public SavedNotesFragment() {
        // Required empty public constructor
    }

    public static SavedNotesFragment newInstance() {
        SavedNotesFragment fragment = new SavedNotesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        swipeRefreshLayout = ((BaseABNavActivity) activity).swipeRefreshLayout;
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
        feedArrayList = new ArrayList<PostResponse>();
        last_post_id = "";
        errorTV = (TextView) view.findViewById(R.id.errorTV);

        feedRV = (RecyclerView) view.findViewById(R.id.feedRV);
        LM = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        feedRV.setLayoutManager(LM);

        feedRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    ((BaseABNavActivity) activity).PostFAB.setVisibility(View.GONE);
                    ((BaseABNavActivity) activity).bottomPanelRL.setVisibility(View.GONE);
                } else if (dy < 0) {
                    ((BaseABNavActivity) activity).PostFAB.setVisibility(View.VISIBLE);
                    ((BaseABNavActivity) activity).bottomPanelRL.setVisibility(View.VISIBLE);
                }
                visibleItemCount = LM.getChildCount();
                totalItemCount = LM.getItemCount();
                firstVisibleItem = LM.findFirstVisibleItemPosition();
                if (totalItemCount >= 10) {
                    if (loading) {
                        if (totalItemCount > previousTotalItemCount) {
                            loading = false;
                            previousTotalItemCount = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + visibleThreshold)) { // 0<=4 0<=2
                        int i = 0;
                        while (i < totalItemCount) {
                            if (feedArrayList.get(totalItemCount - 1 - i).getId().equals("00")) {
                                i++;
                            } else {
                                last_post_id = feedArrayList.get(totalItemCount - 1 - i).getId();
                                i = totalItemCount;
                            }
                        }
                        RefreshFeedList(false);

                        loading = true;
                    }
                }
            }
        });
        RefreshFeedList(true);
    }

    protected void InitfeedAdapter() {
        if (last_post_id.equals("")) {
            if (feedArrayList.size() > 0) {
                errorTV.setVisibility(View.GONE);
                feedRV.setVisibility(View.VISIBLE);
                Helper.getStorageInstance(activity).addRecordStore(Const.OFFLINE_SAVEDNOTES, feedArrayList);
                feedRVAdapter = new FeedRVAdapter(getContext(), feedArrayList, activity, null, null);
                feedRV.setAdapter(feedRVAdapter);
            } else if (Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_SAVEDNOTES) != null &&
                    !Helper.isConnected(activity)) {
                errorTV.setVisibility(View.GONE);
                feedRV.setVisibility(View.VISIBLE);
                feedArrayList = (ArrayList<PostResponse>) Helper.getStorageInstance(activity).getRecordObject(Const.OFFLINE_SAVEDNOTES);
                feedRVAdapter = new FeedRVAdapter(activity, feedArrayList, activity, null, null);
                feedRV.setAdapter(feedRVAdapter);
            } else {
                errorTV.setVisibility(View.VISIBLE);
                feedRV.setVisibility(View.GONE);
                errorTV.setText("You have not saved anything.");
            }
        } else {
            feedRVAdapter.notifyDataSetChanged();
        }
    }

    public void RefreshFeedList(boolean show) {
        if (swipeRefreshLayout == null) {
            swipeRefreshLayout = ((BaseABNavActivity) getActivity()).swipeRefreshLayout;
        }
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(show);
            isRefresh = show;
        }
        NetworkAPICall(API.API_GET_FEEDS_FOR_USER, false);
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_GET_FEEDS_FOR_USER:
                return Ion.with(this)
                        .load(API.API_GET_FEEDS_FOR_USER + Const.BOOKMARK_APPEND)
                        .setTimeout(10 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.LAST_POST_ID, last_post_id)
                        .setMultipartParameter(Const.SEARCH_TEXT, SharedPreference.getInstance().getString(Const.SEARCHED_QUERY));
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonobject, String apitype) throws JSONException {
        Gson gson = new Gson();
        if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
            JSONArray dataarray = jsonobject.getJSONArray(Const.DATA);
            switch (apitype) {
                case API.API_GET_FEEDS_FOR_USER:
                    if (last_post_id.equals("")) {
                        feedArrayList = new ArrayList<>();
                    }
                    Log.e("saved notes ", " " + jsonobject);
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
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                        isRefresh = false;
                    }
                    break;
            }
        } else {
            if (last_post_id.equals("")) {
                this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
            }
        }
    }

    @Override
    public void ErrorCallBack(String object, String apitype) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
            isRefresh = false;
        }
        switch (apitype) {
            case API.API_GET_FEEDS_FOR_USER:
                if (last_post_id.equals("")) {
                    feedArrayList = new ArrayList<>();
                    errorMessageforSavednotes = object;
                    InitfeedAdapter();
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        Log.e("swipe refresh", "on refresh dash board");
        if (CheckConnection.isConnection(activity)) {
            swipeRefreshLayout.setRefreshing(true);
            isRefresh = true;
            last_post_id = "";
            firstVisibleItem = 0;
            previousTotalItemCount = 0;
            visibleItemCount = 0;
            RefreshFeedList(isRefresh);
        } else {
            isRefresh = false;
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
