package com.emedicoz.app.Feeds.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Model.People;
import com.emedicoz.app.R;
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
public class PeopleRVAdapter extends RecyclerView.Adapter<PeopleRVAdapter.ViewHolder> implements NetworkCall.MyNetworkCallBack {

    ArrayList<People> peopleArrayList;
    Activity activity;
    NetworkCall networkCall;
    ViewHolder peopleUKnowHolder;
    int currentPosition;

    public PeopleRVAdapter(ArrayList<People> peopleArrayList, Activity activity) {
        this.peopleArrayList = peopleArrayList;
        this.activity = activity;
        networkCall = new NetworkCall(this, activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_people_you_know, parent, false);
        return new ViewHolder(view);
    }

    int i = 0;

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        People people = peopleArrayList.get(position);

        people.setName(Helper.CapitalizeText(people.getName()));

        if (i > 4) i = 0;
        else i++;

        holder.NameTV.setText(people.getName());
        if (!TextUtils.isEmpty(people.getProfile_picture())) {
            holder.ImageIV.setVisibility(View.VISIBLE);
            holder.ImageIVText.setVisibility(View.GONE);
            Ion.with(holder.ImageIV.getContext()).load(people.getProfile_picture())
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
            Drawable dr = Helper.GetDrawable(people.getName(), activity, people.getId());
            if (dr != null) {
                holder.ImageIV.setVisibility(View.GONE);
                holder.ImageIVText.setVisibility(View.VISIBLE);
                holder.ImageIVText.setImageDrawable(dr);
            } else {
                holder.ImageIV.setVisibility(View.VISIBLE);
                holder.ImageIVText.setVisibility(View.GONE);
                holder.ImageIV.setImageResource(R.mipmap.default_pic);
            }
        }

        if (people.isWatcher_following()) {
            holder.followbtn = changeBackgroundColor(holder.followbtn, 1);
        } else {
            holder.followbtn = changeBackgroundColor(holder.followbtn, 0);
        }

        holder.followbtn.setTag(holder);
        holder.followbtn.setOnClickListener(holder.onFollowClick);
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
                        .setMultipartParameter(Const.USER_ID, peopleArrayList.get(currentPosition).getId())
                        .setMultipartParameter(Const.FOLLOWER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
            case API.API_UNFOLLOW:
                return Ion.with(activity)
                        .load(API.API_UNFOLLOW).setTimeout(10 * 1000)
                        .setMultipartParameter(Const.USER_ID, peopleArrayList.get(currentPosition).getId())
                        .setMultipartParameter(Const.FOLLOWER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    public void InitViewFollowUnfollow(int type) {
        if (type == 1) {
            peopleUKnowHolder.followbtn = changeBackgroundColor(peopleUKnowHolder.followbtn, type);
            peopleArrayList.get(currentPosition).setWatcher_following(true);
        } else {
            peopleUKnowHolder.followbtn = changeBackgroundColor(peopleUKnowHolder.followbtn, type);
            peopleArrayList.get(currentPosition).setWatcher_following(false);
        }
    }

    @Override
    public void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException {
        peopleUKnowHolder.followbtn.setEnabled(true);
        if (jsonstring.optString(Const.STATUS).equals(Const.TRUE)) {
            switch (apitype) {
                case API.API_FOLLOW:
                    InitViewFollowUnfollow(1);
                    break;
                case API.API_UNFOLLOW:
                    InitViewFollowUnfollow(0);
                    break;
            }
        } else {
            this.ErrorCallBack(jsonstring.getString(Const.MESSAGE), apitype);
            Log.e("follow people adapter ", " " + jsonstring);
        }
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView NameTV,
                CategoryTV;
        ImageView ImageIV, ImageIVText;
        Button followbtn;
        public View.OnClickListener onFollowClick;

        public ViewHolder(final View view) {
            super(view);

            NameTV = (TextView) view.findViewById(R.id.nameTV);
            CategoryTV = (TextView) view.findViewById(R.id.categoryTV);
            ImageIV = (ImageView) view.findViewById(R.id.imageIV);
            ImageIVText = (ImageView) view.findViewById(R.id.imageIVText);
            followbtn = (Button) view.findViewById(R.id.followBtn);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Helper.GoToProfileActivity(activity, peopleArrayList.get(getAdapterPosition()).getId());
                }
            });

            onFollowClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    peopleUKnowHolder = (ViewHolder) v.getTag();
                    currentPosition = getAdapterPosition();
                    peopleUKnowHolder.followbtn.setEnabled(false);
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
