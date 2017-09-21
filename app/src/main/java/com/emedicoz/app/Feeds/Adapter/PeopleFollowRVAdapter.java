package com.emedicoz.app.Feeds.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.FollowResponse;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.Network.NetworkCall;
import com.emedicoz.app.Utils.SharedPreference;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * TODO: Replace the implementation with code for your data type.
 */
public class PeopleFollowRVAdapter extends RecyclerView.Adapter<PeopleFollowRVAdapter.ViewHolder> implements NetworkCall.MyNetworkCallBack {

    ArrayList<FollowResponse> peopleArrayList;
    Activity activity;
    NetworkCall networkCall;

    int currentPosition;

    ViewHolder mainHolder;

    public PeopleFollowRVAdapter(ArrayList<FollowResponse> peopleArrayList, Activity activity) {
        this.peopleArrayList = peopleArrayList;
        this.activity = activity;
        networkCall = new NetworkCall(this, activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_people, parent, false);
        return new ViewHolder(view);
    }

    int i = 0;

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        FollowResponse people = peopleArrayList.get(position);

        String name = people.getViewable_user().getName();
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        people.getViewable_user().setName(name);

        String firstLetter = people.getViewable_user().getName().substring(0, 1);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, Const.color[i]);
        if (i > 4) i = 0;
        else i++;
        holder.NameTV.setText(people.getViewable_user().getName());

        if (!(people.getViewable_user().getProfile_picture().equals(null) ||
                people.getViewable_user().getProfile_picture().isEmpty() ||
                people.getViewable_user().getProfile_picture().equals(""))) {
            holder.ImageIV.setVisibility(View.VISIBLE);
            holder.ImageIVText.setVisibility(View.GONE);
            Ion.with(holder.ImageIV.getContext()).load(people.getViewable_user().getProfile_picture())
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (result != null)
                                holder.ImageIV.setImageBitmap(result);
                            else
                                holder.ImageIV.setImageResource(R.mipmap.default_pic);
                        }
                    });
        } else {
            holder.ImageIV.setVisibility(View.GONE);
            holder.ImageIVText.setVisibility(View.VISIBLE);
            holder.ImageIVText.setImageDrawable(drawable);
        }
        holder.followBtn.setTag(holder);

        if (people.getViewable_user().getId().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
            holder.followBtn.setVisibility(View.GONE);
        else {
            holder.followBtn.setVisibility(View.VISIBLE);
            if (people.isWatcher_following()) {
                holder.followBtn = changeBackgroundColor(holder.followBtn, 1);
            } else {
                holder.followBtn = changeBackgroundColor(holder.followBtn, 0);
            }
        }
        holder.followBtn.setOnClickListener(holder.onFollowClick);

    }

    public Button changeBackgroundColor(Button v, int type) {
        v.invalidate();
        v.setBackgroundResource(R.drawable.reg_round_blue_bg);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        if (type == 1) {
            v.setText(R.string.following);
            v.setTextColor(activity.getResources().getColor(R.color.white));
            drawable.setStroke(0, activity.getResources().getColor(R.color.white));
            drawable.setColor(activity.getResources().getColor(R.color.blue));
        } else {
            drawable.setStroke(2, activity.getResources().getColor(R.color.blue));
            drawable.setColor(activity.getResources().getColor(R.color.white));
            v.setText(R.string.follow);
            v.setTextColor(activity.getResources().getColor(R.color.blue));
        }
        return v;
    }

    @Override
    public int getItemCount() {
        return peopleArrayList.size();
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_FOLLOW:
                return Ion.with(activity)
                        .load(API.API_FOLLOW).setTimeout(10 * 1000)
                        .setMultipartParameter(Const.USER_ID, peopleArrayList.get(currentPosition).getViewable_user().getId())
                        .setMultipartParameter(Const.FOLLOWER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
            case API.API_UNFOLLOW:
                return Ion.with(activity)
                        .load(API.API_UNFOLLOW).setTimeout(10 * 1000)
                        .setMultipartParameter(Const.USER_ID, peopleArrayList.get(currentPosition).getViewable_user().getId())
                        .setMultipartParameter(Const.FOLLOWER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonobject, String apitype) throws JSONException {
        mainHolder.followBtn.setEnabled(true);
        if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
            switch (apitype) {
                case API.API_FOLLOW:
                    InitViewFollowUnfollow(1);
                    break;
                case API.API_UNFOLLOW:
                    InitViewFollowUnfollow(0);
                    break;
            }
        } else {
            this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
            Log.e("follow people adapter ", " " + jsonobject);
        }
    }

    public void InitViewFollowUnfollow(int type) {
        if (type == 1) {
            mainHolder.followBtn = changeBackgroundColor(mainHolder.followBtn, type);
            peopleArrayList.get(currentPosition).setWatcher_following(true);
        } else {
            mainHolder.followBtn = changeBackgroundColor(mainHolder.followBtn, type);
            peopleArrayList.get(currentPosition).setWatcher_following(false);
        }
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView NameTV;

        ImageView ImageIV;
        ImageView ImageIVText;

        Button followBtn;
        public View.OnClickListener onFollowClick;

        public ViewHolder(final View view) {
            super(view);

            followBtn = (Button) view.findViewById(R.id.followBtn);

            NameTV = (TextView) view.findViewById(R.id.nameTV);

            ImageIV = (ImageView) view.findViewById(R.id.imageIV);
            ImageIVText = (ImageView) view.findViewById(R.id.imageIVText);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Helper.GoToProfileActivity(activity, peopleArrayList.get(getAdapterPosition()).getViewable_user().getId());

                }
            });
            onFollowClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainHolder = (ViewHolder) view.getTag();
                    currentPosition = getAdapterPosition();
                    mainHolder.followBtn.setEnabled(false);
                    if (peopleArrayList.get(currentPosition).isWatcher_following()) {
                        networkCall.NetworkAPICall(API.API_UNFOLLOW, true);
                    } else {
                        networkCall.NetworkAPICall(API.API_FOLLOW, true);
                    }
                }
            };
        }
    }
}
