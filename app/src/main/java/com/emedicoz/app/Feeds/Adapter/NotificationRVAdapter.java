package com.emedicoz.app.Feeds.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.NotificationResponse;
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
public class NotificationRVAdapter extends RecyclerView.Adapter<NotificationRVAdapter.ViewHolder> {

    ArrayList<NotificationResponse> notificationArrayList;
    Activity activity;

    public NotificationRVAdapter(ArrayList<NotificationResponse> notificationArrayList, Activity activity) {
        this.activity = activity;
        this.notificationArrayList = notificationArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_notification, parent, false);
        return new ViewHolder(view, activity);
    }

    int i = 0;

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        NotificationResponse notification = notificationArrayList.get(position);

        notification.getAction_performed_by().setName(Helper.CapitalizeText(notification.getAction_performed_by().getName()));


        if (i > 4) i = 0;
        else i++;

        if (!(notification.getAction_performed_by().getProfile_picture().equals(null) ||
                notification.getAction_performed_by().getProfile_picture().isEmpty() ||
                notification.getAction_performed_by().getProfile_picture().equals(""))) {
            holder.ImageIV.setVisibility(View.VISIBLE);
            holder.ImageIVText.setVisibility(View.GONE);
            Ion.with(holder.ImageIV.getContext()).load(notification.getAction_performed_by().getProfile_picture())
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
            Drawable dr = Helper.GetDrawable(notification.getAction_performed_by().getName(), activity, notification.getAction_performed_by().getId());
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
        String notifyaction = "";
        switch (notification.getActivity_type()) {
            case Const.LIKE_POST:
                notifyaction = "<b>" + notification.getAction_performed_by().getName() + "</b>" + activity.getString(R.string.liked_your_post);
                break;

            case Const.COMMENT_POST:
                notifyaction = "<b>" + notification.getAction_performed_by().getName() + "</b>" + activity.getString(R.string.commented_your_post);
                break;

            case Const.SHARE_POST:
                notifyaction = "<b>" + notification.getAction_performed_by().getName() + "</b>" + activity.getString(R.string.shared_your_post);
                break;

            case Const.FOLLOWING_USER:
                notifyaction = "<b>" + notification.getAction_performed_by().getName() + "</b>" + activity.getString(R.string.following_you);
                break;

        }
        holder.notifyRL.setTag(holder);
        if (notification.getView_state() == 0)
            holder.notifyRL.setBackgroundColor(activity.getResources().getColor(R.color.light_blue));
        else {
            holder.notifyRL.setBackgroundColor(activity.getResources().getColor(R.color.white));
        }
        holder.DescriptionTV.setText(Html.fromHtml(notifyaction));

        holder.TimeTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(notification.getCreation_time())).equals("0 minutes ago") ? "Just Now" : DateUtils.getRelativeTimeSpanString(Long.parseLong(notification.getCreation_time())));
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements NetworkCall.MyNetworkCallBack {

        RelativeLayout notifyRL;
        TextView DescriptionTV, TimeTV;

        ImageView ImageIV, ImageIVText;
        Activity activity;
        NetworkCall networkCall;
        int pos;

        public ViewHolder(final View view, final Activity activity) {
            super(view);
            this.activity = activity;
            networkCall = new NetworkCall(this, activity);
            DescriptionTV = (TextView) view.findViewById(R.id.descriptionTV);
            notifyRL = (RelativeLayout) view.findViewById(R.id.notifyRL);
            TimeTV = (TextView) view.findViewById(R.id.timeTV);

            ImageIV = (ImageView) view.findViewById(R.id.imageIV);
            ImageIVText = (ImageView) view.findViewById(R.id.imageIVText);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pos = getAdapterPosition();
                    ViewHolder vholder = (ViewHolder) view.getTag();
                    if (notificationArrayList.get(pos).getView_state() == 0) {
                        SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) - 1);
                        notificationArrayList.get(pos).setView_state(1);
                        vholder.notifyRL.setBackgroundColor(activity.getResources().getColor(R.color.white));
                        networkCall.NetworkAPICall(API.API_CHANGE_NOTIFICATION_STATE, false);
                    }

                    if (!notificationArrayList.get(pos).getActivity_type().equals(Const.FOLLOWING_USER)) {
                        Intent intent = new Intent(NotificationRVAdapter.this.activity, PostActivity.class); // Comment Fragment
                        intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                        intent.putExtra(Const.POST_ID, notificationArrayList.get(pos).getPost_id());
                        NotificationRVAdapter.this.activity.startActivity(intent);
                    } else
                        Helper.GoToProfileActivity(NotificationRVAdapter.this.activity, notificationArrayList.get(pos).getAction_performed_by().getId());
                }
            });
        }

        @Override
        public Builders.Any.M getAPI(String apitype) {
            return Ion.with(activity)
                    .load(API.API_CHANGE_NOTIFICATION_STATE)
                    .setTimeout(5 * 1000)
                    .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                    .setMultipartParameter(Const.ID, notificationArrayList.get(pos).getId());
        }

        @Override
        public Builders.Any.B getAPIB(String apitype) {
            return null;
        }

        @Override
        public void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException {

        }

        @Override
        public void ErrorCallBack(String jsonstring, String apitype) {

        }
    }
}
