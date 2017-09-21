package com.emedicoz.app.Feeds.Adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Feeds.Activity.FeedsActivity;
import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.Feeds.Fragment.CommonFragment;
import com.emedicoz.app.Model.Comment;
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

import static com.emedicoz.app.R.id.fragment_container;


/**
 * TODO: Replace the implementation with code for your data type.
 */
public class CommentRVAdapter extends RecyclerView.Adapter<CommentRVAdapter.ViewHolder> implements NetworkCall.MyNetworkCallBack {

    ArrayList<Comment> commentArrayList;
    Activity activity;
    NetworkCall networkCall;

    Comment comment;
    ViewHolder holder;
    String commentText;
    Fragment fragment;


    public CommentRVAdapter(ArrayList<Comment> commentArrayList, Activity activity) {
        this.commentArrayList = commentArrayList;
        this.activity = activity;
        networkCall = new NetworkCall(this, activity);
        fragment = ((PostActivity) activity).getSupportFragmentManager().findFragmentById(fragment_container);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Comment comment = commentArrayList.get(position);

        holder.writecommentLL.setVisibility(View.GONE);
        holder.showcommentLL.setVisibility(View.VISIBLE);

        comment.setName(Helper.CapitalizeText(comment.getName()));

        holder.NameTV.setText(Html.fromHtml("<b>" + comment.getName() + "</b>"));

        holder.CommentTV.setText(comment.getComment());

        holder.DateTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getTime())).equals("0 minutes ago") ? "Just Now" : DateUtils.getRelativeTimeSpanString(Long.parseLong(comment.getTime())));

        if (!TextUtils.isEmpty(comment.getProfile_picture())) {
            holder.ImageIV.setVisibility(View.VISIBLE);
            holder.ImageIVText.setVisibility(View.GONE);
            Ion.with(holder.ImageIV.getContext()).load(comment.getProfile_picture())
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
            Drawable dr = Helper.GetDrawable(comment.getName(), activity, comment.getUser_id());
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

        if (comment.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId())) {
            holder.moreoptionIV.setVisibility(View.VISIBLE);
        } else holder.moreoptionIV.setVisibility(View.GONE);

        holder.moreoptionIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopMenu(view);
            }
        });
    }

    public void showPopMenu(final View v) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editIM:
                        EditComment(v);
                        return true;
                    case R.id.deleteIM:
                        DeleteComment(v);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.comment_menu);
        popup.show();
    }

    public void DeleteComment(View v) {
        holder = (ViewHolder) v.getTag();
        comment = commentArrayList.get(holder.getAdapterPosition());

        new AlertDialog.Builder(activity)
                .setTitle("Delete")
                .setMessage("Do you really want to Delete?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        networkCall.NetworkAPICall(API.API_DELETE_COMMENT, true);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    public void EditComment(View v) {
        ((CommonFragment) fragment).writecommentLL.setVisibility(View.GONE);
        holder = (ViewHolder) v.getTag();
        comment = commentArrayList.get(holder.getAdapterPosition());

        holder.moreoptionIV.setVisibility(View.GONE);
        holder.writecommentLL.setVisibility(View.VISIBLE);
        holder.showcommentLL.setVisibility(View.GONE);

        holder.writecommentET.setText(comment.getComment());

        holder.postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckValidation(holder);
            }
        });
    }

    public void CheckValidation(CommentRVAdapter.ViewHolder holder) {
        commentText = Helper.GetText(holder.writecommentET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(commentText))
            isDataValid = Helper.DataNotValid(holder.writecommentET);

        if (isDataValid) {
            comment.setComment(commentText);
            networkCall.NetworkAPICall(API.API_EDIT_COMMENT, true);
        }
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_EDIT_COMMENT:
                return Ion.with(activity)
                        .load(API.API_EDIT_COMMENT)
                        .setTimeout(45 * 1000)
                        .setMultipartParameter(Const.ID, comment.getId())
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, comment.getPost_id())
                        .setMultipartParameter(Const.POST_COMMENT, comment.getComment());
            case API.API_DELETE_COMMENT:
                return Ion.with(activity)
                        .load(API.API_DELETE_COMMENT)
                        .setTimeout(45 * 1000)
                        .setMultipartParameter(Const.ID, comment.getId())
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, comment.getPost_id());
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException {
        Log.e("CommentAdapter", "SuccessCallBack " + jsonstring.toString());
        switch (apitype) {
            case API.API_EDIT_COMMENT:
                if (jsonstring.optString(Const.STATUS).equals(Const.TRUE)) {
                    notifyDataSetChanged();
                    holder.CommentTV.setText(comment.getComment());
                    ((CommonFragment) fragment).writecommentLL.setVisibility(View.VISIBLE);
                } else {
                    this.ErrorCallBack(jsonstring.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_DELETE_COMMENT:
                if (jsonstring.optString(Const.STATUS).equals(Const.TRUE)) {
                    ((CommonFragment) fragment).setComments(0);
                    FeedsActivity.IS_COMMENT_REFRESHED = true;

                    SharedPreference.getInstance().setPost(((CommonFragment) fragment).post);
                    commentArrayList.remove(holder.getAdapterPosition());
                    if (commentArrayList.size() == 0) {
                        ((CommonFragment) fragment).last_comment_id = "";
                        ((CommonFragment) fragment).InitcommentAdapter();
                    } else {
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
                    this.ErrorCallBack(jsonstring.getString(Const.MESSAGE), apitype);
                } else {
                    this.ErrorCallBack(jsonstring.getString(Const.MESSAGE), apitype);
                }
                break;
        }
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Log.e("json", jsonstring);
        Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView NameTV,
                CommentTV, DateTV;

        ImageView ImageIV, moreoptionIV, ImageIVText;

        ImageView postbtn;

        EditText writecommentET;

        LinearLayout showcommentLL, writecommentLL;
        RelativeLayout imageRL;

        public ViewHolder(final View view) {
            super(view);

            NameTV = (TextView) view.findViewById(R.id.nameTV);
            DateTV = (TextView) view.findViewById(R.id.dateTV);
            CommentTV = (TextView) view.findViewById(R.id.commentTV);

            ImageIV = (ImageView) view.findViewById(R.id.imageIV);
            ImageIVText = (ImageView) view.findViewById(R.id.ImageIVText);
            moreoptionIV = (ImageView) view.findViewById(R.id.moreoptionIV);
            postbtn = (ImageView) view.findViewById(R.id.postcommentIBtn);

            showcommentLL = (LinearLayout) view.findViewById(R.id.showcommentLL);
            writecommentLL = (LinearLayout) view.findViewById(R.id.writecommentLL);

            imageRL = (RelativeLayout) view.findViewById(R.id.imageRL);

            writecommentET = (EditText) view.findViewById(R.id.writecommentET);

            moreoptionIV.setTag(this);
            NameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Helper.GoToProfileActivity(activity, commentArrayList.get(getAdapterPosition()).getUser_id());
                }
            });
            imageRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Helper.GoToProfileActivity(activity, commentArrayList.get(getAdapterPosition()).getUser_id());
                }
            });
        }
    }
}
