package com.emedicoz.app.Feeds.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Feeds.Activity.FeedsActivity;
import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.Feeds.Activity.ProfileActivity;
import com.emedicoz.app.Feeds.Fragment.TagSelectionFragment;
import com.emedicoz.app.Model.Banner;
import com.emedicoz.app.Model.People;
import com.emedicoz.app.Model.PostFile;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.PostResponse;
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
public class FeedRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements NetworkCall.MyNetworkCallBack {

    ArrayList<PostResponse> feedArrayList;
    ArrayList<People> peopleArrayList;
    Banner banner;
    Context context;

    ViewHolder1 TagHolder1;
    Activity activity;
    NetworkCall networkCall;
    String video_url;
    int Feed_Description_Length = 300;

    public FeedRVAdapter(Context context, ArrayList<PostResponse> feedArrayList, Activity activity, ArrayList<People> peopleArrayList, Banner banner) {
        this.context = context;
        this.feedArrayList = feedArrayList;
        this.activity = activity;
        this.peopleArrayList = peopleArrayList;
        networkCall = new NetworkCall(this, context);
        this.banner = banner;
        if (this.peopleArrayList == null) this.peopleArrayList = new ArrayList<>();
    }

    public void updateData(ArrayList<People> peopleArrayList, Banner banner) {
        this.peopleArrayList = peopleArrayList;
        this.banner = banner;
    }

    public void ItemChangedatPostId(PostResponse postResponse, int type) {
        int i = 0;
        if (postResponse != null) {
            if (type == 0) {
                while (i < feedArrayList.size()) {
                    if ((feedArrayList.get(i).getId()).equals(postResponse.getId())) {
                        feedArrayList.set(i, postResponse);
                        notifyItemChanged(i, postResponse);
                        i = feedArrayList.size();
                    }
                    i++;
                }
            } else if (type == 1) {
                while (i < feedArrayList.size()) {
                    if ((feedArrayList.get(i).getId()).equals(postResponse.getId())) {
                        feedArrayList.remove(i);
                        notifyItemRemoved(i);
                        i = feedArrayList.size();
                    }
                    i++;
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1 || viewType == 2) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_feeds, parent, false);
            return new ViewHolder1(itemView);
        } else if (viewType == 3 || viewType == 4) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_people_you_m_k, parent, false);
            return new ViewHolder3(itemView);
        } else if (viewType == 5) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_livestream, parent, false);
            return new ViewHolder2(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder sholder, int position) {
        int itemtype = getItemViewType(position);
        if (itemtype == 5) {
            final ViewHolder2 holder = (ViewHolder2) sholder;
            PostResponse feed = feedArrayList.get(position);
            holder.mn_videoplayerRL.setVisibility(View.VISIBLE);
            holder.video_image.setScaleType(ImageView.ScaleType.FIT_XY);

            feed.getPost_owner_info().setName(Helper.CapitalizeText(feed.getPost_owner_info().getName()));

            holder.nameTV.setText(Html.fromHtml("<b>" + feed.getPost_owner_info().getName().toUpperCase() + "</b> LIVE"));

            //Setting up user's profile picture

            if (!TextUtils.isEmpty(feed.getPost_owner_info().getProfile_picture())) {
                holder.profilepicIV.setVisibility(View.VISIBLE);
                holder.profilepicIVText.setVisibility(View.GONE);
                Ion.with(holder.profilepicIV.getContext()).load(feed.getPost_owner_info().getProfile_picture())
                        .asBitmap()
                        .setCallback(new FutureCallback<Bitmap>() {
                            @Override
                            public void onCompleted(Exception e, Bitmap result) {
                                if (result != null)
                                    holder.profilepicIV.setImageBitmap(result);
                                else
                                    holder.profilepicIV.setImageResource(R.mipmap.default_pic);
                            }
                        });
            } else {
                Drawable dr = Helper.GetDrawable(feed.getPost_owner_info().getName(), activity, feed.getPost_owner_info().getId());

                if (dr != null) {
                    holder.profilepicIV.setVisibility(View.GONE);
                    holder.profilepicIVText.setVisibility(View.VISIBLE);
                    holder.profilepicIVText.setImageDrawable(dr);
                } else {
                    holder.profilepicIV.setVisibility(View.VISIBLE);
                    holder.profilepicIVText.setVisibility(View.GONE);
                    holder.profilepicIV.setImageResource(R.mipmap.default_pic);
                }
            }

        } else if (itemtype == 3 || itemtype == 4) {
            final ViewHolder3 holder = (ViewHolder3) sholder;
            if (feedArrayList.get(position).getPost_type().equals(Const.POST_TYPE_BANNER)) {
                holder.peopleTV.setVisibility(View.GONE);
                holder.imageIV.setVisibility(View.VISIBLE);

                Ion.with(context).load(banner.getImage_link())
                        .asBitmap()
                        .setCallback(new FutureCallback<Bitmap>() {
                            @Override
                            public void onCompleted(Exception e, Bitmap result) {
                                if (result != null)
                                    holder.imageIV.setImageBitmap(result);
                                else
                                    holder.imageIV.setImageResource(R.drawable.splashbg);
                            }
                        });

                holder.peopleTV.setVisibility(View.VISIBLE);
                holder.peopleTV.setText(activity.getString(R.string.ad_banner));
                holder.advertiseText.setVisibility(View.VISIBLE);
                holder.advertiseText.setText(banner.getText());
            } else {
                holder.peopleTV.setText(activity.getString(R.string.people_you_may_know));
                holder.advertiseText.setVisibility(View.GONE);
                holder.InitpeopleAdapter(peopleArrayList);
                holder.imageIV.setVisibility(View.GONE);
            }
        } else {
            final PostResponse feed = feedArrayList.get(position);
            final ViewHolder1 holder = (ViewHolder1) sholder;

            if (feed.getPost_data().getPost_file().size() > 0) {

                if (feed.getPost_data().getPost_file().get(0).getFile_type().equals(Const.IMAGE)) {
                    holder.view_pagerRL.setVisibility(View.VISIBLE);
                    holder.mn_videoplayer.setVisibility(View.GONE);
                    holder.docRL.setVisibility(View.GONE);

                    holder.imageArrayList = feed.getPost_data().getPost_file();
                    holder.InitImageView(holder);

                } else if (feed.getPost_data().getPost_file().get(0).getFile_type().equals(Const.VIDEO)) {
                    holder.mn_videoplayer.setVisibility(View.VISIBLE);
                    holder.view_pagerRL.setVisibility(View.GONE);
                    holder.docRL.setVisibility(View.GONE);

                    if (feed.getPost_data().getPost_file().get(0).getLink().contains(Const.AMAZON_S3_IMAGE_PREFIX))
                        Ion.with(context).load(feed.getPost_data().getPost_file().get(0).getLink()).intoImageView(holder.video_image);
                    else
                        Ion.with(context).load(Const.AMAZON_S3_IMAGE_PREFIX + Const.AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES +
                                "/" + feed.getPost_data().getPost_file().get(0).getFile_info().split(".mp4")[0] + ".png")
                                .intoImageView(holder.video_image);

                    holder.video_image.setScaleType(ImageView.ScaleType.FIT_XY);

                } else if (feed.getPost_data().getPost_file().get(0).getFile_type().equals(Const.PDF) ||
                        feed.getPost_data().getPost_file().get(0).getFile_type().equals(Const.DOC) ||
                        feed.getPost_data().getPost_file().get(0).getFile_type().equals(Const.XLS)) {

                    holder.mn_videoplayer.setVisibility(View.GONE);
                    holder.view_pagerRL.setVisibility(View.GONE);
                    holder.docRL.setVisibility(View.VISIBLE);

                    if (feed.getPost_data().getPost_file().get(0).getFile_type().equals(Const.PDF)) {
                        holder.doc_nameTV.setText(feed.getPost_data().getPost_file().get(0).getFile_info());
                        holder.doc_imageIV.setImageResource(R.mipmap.pdf_download);
                        holder.doc_imageIV.setTag(holder);
                    } else if (feed.getPost_data().getPost_file().get(0).getFile_type().equals(Const.DOC)) {
                        holder.doc_nameTV.setText(feed.getPost_data().getPost_file().get(0).getFile_info());
                        holder.doc_imageIV.setImageResource(R.mipmap.doc_download);
                        holder.doc_imageIV.setTag(holder);
                    } else if (feed.getPost_data().getPost_file().get(0).getFile_type().equals(Const.XLS)) {
                        holder.doc_nameTV.setText(feed.getPost_data().getPost_file().get(0).getFile_info());
                        holder.doc_imageIV.setImageResource(R.mipmap.xls_download);
                        holder.doc_imageIV.setTag(holder);
                    }
                }
            } else {
                holder.mn_videoplayer.setVisibility(View.GONE);
                holder.view_pagerRL.setVisibility(View.GONE);
                holder.docRL.setVisibility(View.GONE);
            }

            holder.categoryTV.setText(feed.getPost_owner_info().getSpeciality());

            if (TextUtils.isEmpty(feed.getPost_tag())) {
                holder.tagTV.setVisibility(View.GONE);
            } else {
                holder.tagTV.setVisibility(View.VISIBLE);
                holder.tagTV.setText(feed.getPost_tag());
            }

            // setting the time in the feeds.
            holder.timeTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(feed.getCreation_time())).equals("0 minutes ago") ? "Just Now" : DateUtils.getRelativeTimeSpanString(Long.parseLong(feed.getCreation_time())));

            //setting the option if can report abuse or not.
            holder.optionTV.setTag(holder);
            holder.optionTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int temp = 0;
                    if (feed.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
                        temp++;
                    if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getIs_moderate()) && SharedPreference.getInstance().getLoggedInUser().getIs_moderate().equals("1"))
                        temp += 2;
                    holder.showPopMenu(view, temp);
                }
            });

            //Captialising the first letter of User's Name
            feed.getPost_owner_info().setName(Helper.CapitalizeText(feed.getPost_owner_info().getName()));

            if (!TextUtils.isEmpty(feed.getPost_owner_info().getProfile_picture())) {
                holder.profilepicIV.setVisibility(View.VISIBLE);
                holder.profilepicIVText.setVisibility(View.GONE);
                Ion.with(holder.profilepicIV.getContext()).load(feed.getPost_owner_info().getProfile_picture())
                        .asBitmap()
                        .setCallback(new FutureCallback<Bitmap>() {
                            @Override
                            public void onCompleted(Exception e, Bitmap result) {
                                if (result != null)
                                    holder.profilepicIV.setImageBitmap(result);
                                else
                                    holder.profilepicIV.setImageResource(R.mipmap.default_pic);
                            }
                        });
            } else {
                Drawable dr = Helper.GetDrawable(feed.getPost_owner_info().getName(), activity, feed.getPost_owner_info().getId());
                if (dr != null) {
                    holder.profilepicIV.setVisibility(View.GONE);
                    holder.profilepicIVText.setVisibility(View.VISIBLE);
                    holder.profilepicIVText.setImageDrawable(dr);
                } else {
                    holder.profilepicIV.setVisibility(View.VISIBLE);
                    holder.profilepicIVText.setVisibility(View.GONE);
                    holder.profilepicIV.setImageResource(R.mipmap.default_pic);
                }
            }

            //like button action
            setLikes(position, 2, holder);

            if (feed.getComments().equals("1"))
                holder.commentTV.setText(feed.getComments() + " Comment");
            else
                holder.commentTV.setText(feed.getComments() + " Comments");

            //Bookmark/Save button action
            if (feed.is_bookmarked()) {
                ((ImageView) holder.BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon_blue);
                ((TextView) holder.BookmarkClickRL.getChildAt(1)).setTextColor(context.getResources().getColor(R.color.blue));
                ((TextView) holder.BookmarkClickRL.getChildAt(1)).setText("Saved");

            } else {
                ((ImageView) holder.BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon);
                ((TextView) holder.BookmarkClickRL.getChildAt(1)).setTextColor(context.getResources().getColor(R.color.black));
                ((TextView) holder.BookmarkClickRL.getChildAt(1)).setText("Save");
            }

            //setting us the listeners for the action to perform
            holder.LikeClickRL.setOnClickListener(onLikeClickListener);
            holder.LikeClickRL.setTag(holder);
            holder.CommentClickRL.setOnClickListener(onCommentClickListener);
            holder.CommentClickRL.setTag(holder);
            holder.ShareClickRL.setOnClickListener(onShareClickListener);
            holder.ShareClickRL.setTag(holder);
            holder.BookmarkClickRL.setOnClickListener(onBookmarkClickListener);
            holder.BookmarkClickRL.setTag(holder);
            holder.commentTV.setOnClickListener(onCommentClickListener);
            holder.commentTV.setTag(holder);


            //setting up user's name or the shared post
            holder.nameTV.setText(Html.fromHtml("<b>" + feed.getPost_owner_info().getName() + "</b>"));

            //setting discription or mcq answers
            if (itemtype == 1) {
                holder.mcqoptionsLL.removeAllViews();
                holder.descriptionTV.setText("" + feed.getPost_data().getQuestion());

                if (!(feed.getPost_data().getAnswer_one().equals("") || feed.getPost_data().getAnswer_one().equals(null))) {
                    holder.mcqoptionsLL.addView(initMCQOptionView("A", feed.getPost_data().getAnswer_one()));
                }
                if (!(feed.getPost_data().getAnswer_two().equals("") || feed.getPost_data().getAnswer_two().equals(null))) {
                    holder.mcqoptionsLL.addView(initMCQOptionView("B", feed.getPost_data().getAnswer_two()));
                }
                if (!(feed.getPost_data().getAnswer_three().equals("") || feed.getPost_data().getAnswer_three().equals(null))) {
                    holder.mcqoptionsLL.addView(initMCQOptionView("C", feed.getPost_data().getAnswer_three()));
                }
                if (!(feed.getPost_data().getAnswer_four().equals("") || feed.getPost_data().getAnswer_four().equals(null))) {
                    holder.mcqoptionsLL.addView(initMCQOptionView("D", feed.getPost_data().getAnswer_four()));
                }
                if (!(feed.getPost_data().getAnswer_five().equals("") || feed.getPost_data().getAnswer_five().equals(null))) {
                    holder.mcqoptionsLL.addView(initMCQOptionView("E", feed.getPost_data().getAnswer_five()));
                }
            } else if (itemtype == 2) {
                holder.mcqoptionsLL.setVisibility(View.GONE);
                String des = feed.getPost_data().getText();
                holder.descriptionTV.setTag(holder);

                if (feed.getPost_data().getPost_text_type().equals(Const.POST_TEXT_TYPE_YOUTUBE_TEXT)) {

                    holder.mn_videoplayer.setVisibility(View.VISIBLE);
                    holder.view_pagerRL.setVisibility(View.GONE);
                    holder.docRL.setVisibility(View.GONE);

                    String x = Helper.youtubevalidation(des);
                    if (x != null) {
                        String img_url = "http://img.youtube.com/vi/" + x + "/0.jpg"; // this is link which will give u thumnail image of that video
                        Ion.with(context).load(img_url).intoImageView(holder.video_image);
                    }
                    holder.video_image.setScaleType(ImageView.ScaleType.FIT_XY);
                }

                if (des.length() > Feed_Description_Length) {
                    des = des.substring(0, Feed_Description_Length) + "...";
                    holder.descriptionTV.setText(des + Html.fromHtml("<font color='#33A2D9'> <u>Read More</u></font>"));
                    ClickableSpan readmoreclick = new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            TagHolder1 = (ViewHolder1) holder.descriptionTV.getTag();
                            Intent intent = new Intent(activity, PostActivity.class); // comment fragment
                            intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                            intent.putExtra(Const.POST, feedArrayList.get(TagHolder1.getAdapterPosition()));
                            activity.startActivity(intent);
                        }
                    };
                    makeLinks(holder.descriptionTV, "Read More", readmoreclick);
                } else {
                    holder.descriptionTV.setText(des);
                }

            }
        }
    }

    public void makeLinks(TextView textView, String link, ClickableSpan clickableSpan) {
        SpannableString spannableString = new SpannableString(textView.getText());

        int startIndexOfLink = textView.getText().toString().indexOf(link);
        spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }

    public LinearLayout initMCQOptionView(String text1, String text2) {
        LinearLayout v = (LinearLayout) View.inflate(context, R.layout.layout_option_mcq_view, null);
        TextView tv = (TextView) v.findViewById(R.id.optioniconTV);
        TextView text = (TextView) v.findViewById(R.id.optionTextTV);
        tv = changeBackgroundColor(tv, 1);
        v = changeBackgroundColor(v, 1);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 20, 0, 0);
        v.setLayoutParams(lp);
        tv.setText(text1);
        text.setText(text2);
        v.setTag(tv.getText());
        return v;
    }

    @Override
    public int getItemCount() {
        return feedArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (feedArrayList.get(position).getPost_type().equals(Const.POST_TYPE_MCQ))
            return 1;
        else if (feedArrayList.get(position).getPost_type().equals(Const.POST_TYPE_NORMAL))
            return 2;
        else if (feedArrayList.get(position).getPost_type().equals(Const.POST_TYPE_PEOPLEYMK))
            return 3;
        else if (feedArrayList.get(position).getPost_type().equals(Const.POST_TYPE_BANNER))
            return 4;
        else if (feedArrayList.get(position).getPost_type().equals(Const.POST_TYPE_LIVE_STREAM))
            return 5;
        else
            return 1;
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_LIKE_POST:
                return Ion.with(context)
                        .load(API.API_LIKE_POST)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, feedArrayList.get(TagHolder1.getAdapterPosition()).getId());
            case API.API_DISLIKE_POST:
                return Ion.with(context)
                        .load(API.API_DISLIKE_POST)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, feedArrayList.get(TagHolder1.getAdapterPosition()).getId());
            case API.API_SHARE_POST:
                String postid;
                if (feedArrayList.get(TagHolder1.getAdapterPosition()).getIs_shared().equals("1"))
                    postid = feedArrayList.get(TagHolder1.getAdapterPosition()).getParent_id();
                else postid = feedArrayList.get(TagHolder1.getAdapterPosition()).getId();
                return Ion.with(context)
                        .load(API.API_SHARE_POST)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, postid);
            case API.API_ADD_BOOKMARK:
                return Ion.with(context)
                        .load(API.API_ADD_BOOKMARK)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, feedArrayList.get(TagHolder1.getAdapterPosition()).getId());
            case API.API_REMOVE_BOOKMARK:
                return Ion.with(context)
                        .load(API.API_REMOVE_BOOKMARK)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, feedArrayList.get(TagHolder1.getAdapterPosition()).getId());
            case API.API_REPORT_POST:
                return Ion.with(context)
                        .load(API.API_REPORT_POST)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, feedArrayList.get(TagHolder1.getAdapterPosition()).getId());

            case API.API_DELETE_POST:
                return Ion.with(context)
                        .load(API.API_DELETE_POST)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, feedArrayList.get(TagHolder1.getAdapterPosition()).getUser_id())
                        .setMultipartParameter(Const.POST_ID, feedArrayList.get(TagHolder1.getAdapterPosition()).getId());
            case API.API_REQUEST_VIDEO_LINK:
                return Ion.with(context)
                        .load(API.API_REQUEST_VIDEO_LINK)
                        .setMultipartParameter(Const.NAME, video_url);

        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonobject, String apitype) throws JSONException {
        Log.e("json", jsonobject.toString());
        int pos = 0;
        if (!apitype.equals(API.API_REQUEST_VIDEO_LINK))
            pos = TagHolder1.getAdapterPosition();
        switch (apitype) {
            case API.API_LIKE_POST:
                TagHolder1.LikeClickRL.setEnabled(true);
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    Log.e("API_LIKE_POST ", "API_LIKE_POST true");
                } else {
                    Log.e("API_LIKE_POST ", "API_LIKE_POST false");
                    setLikes(pos, 0, TagHolder1);
                }
                break;
            case API.API_DISLIKE_POST:
                TagHolder1.LikeClickRL.setEnabled(true);
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    Log.e("API_DISLIKE_POST ", "API_DISLIKE_POST true");

                } else {
                    Log.e("API_DISLIKE_POST ", "API_DISLIKE_POST false");

                    setLikes(pos, 1, TagHolder1);
                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_SHARE_POST:
                TagHolder1.ShareClickRL.setEnabled(true);
                this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);

                break;
            case API.API_ADD_BOOKMARK:
                TagHolder1.BookmarkClickRL.setEnabled(true);

                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    Log.e("API_ADD_BOOKMARK ", "API_ADD_BOOKMARK true");

                } else {
                    Log.e("API_ADD_BOOKMARK ", "API_ADD_BOOKMARK false");

                    feedArrayList.get(pos).setIs_bookmarked(false);
                    ((ImageView) TagHolder1.BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon);
                    ((TextView) TagHolder1.BookmarkClickRL.getChildAt(1)).setTextColor(context.getResources().getColor(R.color.transparent_black));
                    ((TextView) TagHolder1.BookmarkClickRL.getChildAt(1)).setText("Save");

                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_REMOVE_BOOKMARK:
                TagHolder1.BookmarkClickRL.setEnabled(true);

                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    Log.e("API_REMOVE_BOOKMARK ", "API_REMOVE_BOOKMARK true");

                } else {
                    Log.e("API_REMOVE_BOOKMARK ", "API_REMOVE_BOOKMARK false");

                    feedArrayList.get(pos).setIs_bookmarked(true);
                    ((ImageView) TagHolder1.BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon_blue);
                    ((TextView) TagHolder1.BookmarkClickRL.getChildAt(1)).setTextColor(context.getResources().getColor(R.color.blue));
                    ((TextView) TagHolder1.BookmarkClickRL.getChildAt(1)).setText("Saved");

                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_REPORT_POST:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    feedArrayList.get(pos).setReport_abuse("1");
                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                } else {
                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                }
                break;

            case API.API_DELETE_POST:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                    feedArrayList.remove(pos);
                    notifyItemRemoved(pos);
                } else {
                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_REQUEST_VIDEO_LINK:
                try {
                    Log.e(activity.getLocalClassName(), "URL ACTIVE LINK : " + jsonobject.toString());
                    if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                        String Url = jsonobject.optJSONObject(Const.DATA).optString(Const.LINK);
                        Helper.GoToVideoActivity(activity, Url, Const.VIDEO_STREAM);
                    } else {
                        this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
        }
    }

    public void setLikes(int position, int type, ViewHolder1 holder) {
        String finallikes = "";
        String likes = feedArrayList.get(position).getLikes();

        //this is to increment or decrement the likes on the post.
        if (type == 1) {
            feedArrayList.get(position).setLiked(true);
            finallikes = String.valueOf(Integer.valueOf(likes) + 1);
        } else if (type == 0) {
            feedArrayList.get(position).setLiked(false);
            finallikes = String.valueOf(Integer.valueOf(likes) - 1);
        } else
            finallikes = likes;

        // this is to change the icon and color of likes on the post.
        if (feedArrayList.get(position).isLiked()) {
            ((TextView) holder.LikeClickRL.getChildAt(1)).setTextColor(context.getResources().getColor(R.color.blue));
            ((ImageView) holder.LikeClickRL.getChildAt(0)).setImageResource(R.mipmap.like_blue);
        } else {
            ((TextView) holder.LikeClickRL.getChildAt(1)).setTextColor(context.getResources().getColor(R.color.transparent_black));
            ((ImageView) holder.LikeClickRL.getChildAt(0)).setImageResource(R.mipmap.like);
        }

        feedArrayList.get(position).setLikes(finallikes);

        //this is to show the count of likes on the post.
        if (feedArrayList.get(position).getLikes().equals("1"))
            holder.likeTV.setText(feedArrayList.get(position).getLikes() + " like");
        else
            holder.likeTV.setText(feedArrayList.get(position).getLikes() + " likes");

    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Toast.makeText(context, jsonstring, Toast.LENGTH_SHORT).show();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        TextView descriptionTV, timeTV, nameTV,
                categoryTV, tagTV, commentTV, likeTV, doc_nameTV;

        LinearLayout LikeClickRL, CommentClickRL, ShareClickRL, BookmarkClickRL;

        ImageView profilepicIV, profilepicIVText, optionTV, video_image, doc_imageIV;

        LinearLayout mcqoptionsLL;

        ViewPager mViewPager;

        RelativeLayout docRL;
        RelativeLayout view_pagerRL;
        RelativeLayout imageRL;

        private LinearLayout dotsLayout;

        private ImageView[] dots;

        ArrayList<PostFile> imageArrayList;

        GalleryAdapter galleryAdapter;

        RelativeLayout mn_videoplayer;

        public ViewHolder1(final View view) {
            super(view);

            mViewPager = (ViewPager) view.findViewById(R.id.view_pager);

            docRL = (RelativeLayout) view.findViewById(R.id.docRL);
            view_pagerRL = (RelativeLayout) view.findViewById(R.id.view_pagerRL);
            imageRL = (RelativeLayout) view.findViewById(R.id.imageRL);

            dotsLayout = (LinearLayout) view.findViewById(R.id.layoutDots);

            doc_imageIV = (ImageView) view.findViewById(R.id.doc_imageIV);
            video_image = (ImageView) view.findViewById(R.id.video_image);
            optionTV = (ImageView) view.findViewById(R.id.optionTV);
            profilepicIV = (ImageView) view.findViewById(R.id.profilepicIV);
            profilepicIVText = (ImageView) view.findViewById(R.id.profilepicIVText);

            doc_nameTV = (TextView) view.findViewById(R.id.doc_nameTV);
            descriptionTV = (TextView) view.findViewById(R.id.descriptionTV);
            nameTV = (TextView) view.findViewById(R.id.nameTV);
            timeTV = (TextView) view.findViewById(R.id.timeTV);
            categoryTV = (TextView) view.findViewById(R.id.categoryTV);
            tagTV = (TextView) view.findViewById(R.id.tagTV);

            commentTV = (TextView) view.findViewById(R.id.commentTV);
            likeTV = (TextView) view.findViewById(R.id.likeTV);

            LikeClickRL = (LinearLayout) view.findViewById(R.id.likeClickRL);
            CommentClickRL = (LinearLayout) view.findViewById(R.id.commentClickRL);
            ShareClickRL = (LinearLayout) view.findViewById(R.id.shareClickRL);
            BookmarkClickRL = (LinearLayout) view.findViewById(R.id.bookmarkClickRL);

            doc_imageIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TagHolder1 = (ViewHolder1) view.getTag();
                    int pos = getAdapterPosition();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(feedArrayList.get(pos).getPost_data().getPost_file().get(0).getLink()));

                    Helper.DownloadfilefromURL(activity, feedArrayList.get(pos).getPost_data().getPost_file().get(0));
//                    ((BaseABNavActivity) activity).postFile = feedArrayList.get(pos).getPost_data().getPost_file().get(0);
//                    Helper.GoToWebViewActivity(activity, Const.GOOGLE_PREVIEW_DOC_URL + feedArrayList.get(pos).getPost_data().getPost_file().get(0).getLink());
                }
            });
            mcqoptionsLL = (LinearLayout) view.findViewById(R.id.mcqoptions);

            nameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(activity instanceof ProfileActivity))
                        Helper.GoToProfileActivity(activity, feedArrayList.get(getAdapterPosition()).getUser_id());
                }
            });
            imageRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(activity instanceof ProfileActivity))
                        Helper.GoToProfileActivity(activity, feedArrayList.get(getAdapterPosition()).getUser_id());
                }
            });
            mn_videoplayer = (RelativeLayout) view.findViewById(R.id.mn_videoplayer);
            mn_videoplayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (!feedArrayList.get(pos).getPost_data().getPost_text_type().equals(Const.POST_TEXT_TYPE_YOUTUBE_TEXT)) {
                        video_url = feedArrayList.get(pos).getPost_data().getPost_file().get(0).getLink();
                        networkCall.NetworkAPICall(API.API_REQUEST_VIDEO_LINK, true);
                    } else {
                        Intent intent = new Intent(activity, PostActivity.class);// youtube video player
                        intent.putExtra(Const.FRAG_TYPE, Const.YOUTUBE);
                        intent.putExtra(Const.YOUTUBE_ID, Helper.youtubevalidation(feedArrayList.get(pos).getPost_data().getText()));
                        activity.startActivity(intent);
                    }
                }
            });
        }


        public void InitImageView(final ViewHolder1 vh) {
            vh.addBottomDots(0);
            vh.galleryAdapter = new GalleryAdapter(context, imageArrayList) {
                @Override
                public void getDynamicHeight(int height) {
//                    vh.mViewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
                }
            };
            vh.mViewPager.setAdapter(vh.galleryAdapter);
            vh.mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
            vh.mViewPager.setTag(vh);
        }


        private void addBottomDots(int currentPage) {
            dotsLayout.removeAllViews();
            if (imageArrayList.size() > 1) {
                dots = new ImageView[imageArrayList.size()];
                for (int i = 0; i < dots.length; i++) {
                    dots[i] = new ImageView(context);
                    dots[i].setImageResource(R.drawable.nonselecteditem_dot);
                    dots[i].setPadding(5, 5, 5, 0);
                    dotsLayout.addView(dots[i]);
                }
                if (dots.length > 0)
                    dots[currentPage].setImageResource(R.drawable.selecteditem_dot);
            }
        }

        public void showPopMenu(final View v, int userDecider) {
            PopupMenu popup = new PopupMenu(activity, v);

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.reportIM:
                            reportPost(v);
                            return true;

                        case R.id.editIM:
                            editPost(v);
                            return true;

                        case R.id.deleteIM:
                            AlertDialog.Builder alertBuild = new AlertDialog.Builder(context);

                            alertBuild
                                    .setTitle(context.getString(R.string.app_name))
                                    .setMessage(context.getString(R.string.deleteMessage))
                                    .setCancelable(true)
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                            deletePost(v);
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog = alertBuild.create();
                            dialog.show();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popup.inflate(R.menu.feed_menu);
            Menu menu = popup.getMenu();

            menu.findItem(R.id.reportIM).setVisible(((userDecider == 0 || userDecider == 2) ? true : false));
            menu.findItem(R.id.editIM).setVisible((userDecider != 0 ? true : false));
            menu.findItem(R.id.deleteIM).setVisible((userDecider != 0 ? true : false));

            popup.show();
        }

        public void deletePost(View v) {
            TagHolder1 = (ViewHolder1) v.getTag();
            networkCall.NetworkAPICall(API.API_DELETE_POST, true);
        }

        public void reportPost(View v) {
            TagHolder1 = (ViewHolder1) v.getTag();
            networkCall.NetworkAPICall(API.API_REPORT_POST, true);
        }

        public void editPost(View v) {
            TagHolder1 = (ViewHolder1) v.getTag();
            Intent intent = new Intent(activity, PostActivity.class);// Edit Post
            intent.putExtra(Const.FRAG_TYPE, Const.POST_FRAG);
            intent.putExtra(Const.POST, feedArrayList.get(TagHolder1.getAdapterPosition()));
            activity.startActivity(intent);
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
    }

    // Live Video Layout
    public class ViewHolder2 extends RecyclerView.ViewHolder {

        TextView nameTV;

        ImageView profilepicIV, profilepicIVText, video_image, liveIV;

        RelativeLayout imageRL;

        RelativeLayout mn_videoplayerRL;

        public ViewHolder2(final View view) {
            super(view);

            imageRL = (RelativeLayout) view.findViewById(R.id.imageRL);

            liveIV = (ImageView) view.findViewById(R.id.liveIV);
            video_image = (ImageView) view.findViewById(R.id.video_image);
            profilepicIV = (ImageView) view.findViewById(R.id.profilepicIV);
            profilepicIVText = (ImageView) view.findViewById(R.id.profilepicIVText);

            nameTV = (TextView) view.findViewById(R.id.nameTV);

            nameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Helper.GoToProfileActivity(activity, feedArrayList.get(getAdapterPosition()).getPost_owner_info().getId());
                }
            });
            imageRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Helper.GoToProfileActivity(activity, feedArrayList.get(getAdapterPosition()).getPost_owner_info().getId());
                }
            });
            mn_videoplayerRL = (RelativeLayout) view.findViewById(R.id.mn_videoplayer);
            mn_videoplayerRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Helper.GoToVideoActivity(activity, feedArrayList.get(pos).getHlslink(), Const.VIDEO_LIVE);
                }
            });
            liveIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    Helper.GoToVideoActivity(activity, feedArrayList.get(pos).getHlslink(), Const.VIDEO_LIVE);
                }
            });
        }
    }

    // People you may Know Layout
    public class ViewHolder3 extends RecyclerView.ViewHolder {

        ImageView imageIV;

        TextView peopleTV, advertiseText, peopleViewAll;

        RecyclerView peopleRV;

        PeopleRVAdapter peopleRVAdapter;

        public ViewHolder3(final View view) {
            super(view);

            peopleTV = (TextView) view.findViewById(R.id.peopleknownTV1);
            imageIV = (ImageView) view.findViewById(R.id.imageIV);
            advertiseText = (TextView) view.findViewById(R.id.advertismentText);
            peopleRV = (RecyclerView) view.findViewById(R.id.peopleRV);
            peopleViewAll = (TextView) view.findViewById(R.id.peopleknownViewAll);
            peopleRV.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

            imageIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Helper.GoToWebViewActivity(activity, banner.getWeb_link());
                }
            });

            peopleViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    TagSelectionFragment newFragment = (TagSelectionFragment) Fragment.instantiate(activity, TagSelectionFragment.class.getName());
//                    newFragment.show(((FeedsActivity) activity).getSupportFragmentManager(), "dialog");
                }
            });
        }

        protected void InitpeopleAdapter(ArrayList<People> peopleArrayList) {
            if (peopleArrayList.size() > 0) {
                peopleRVAdapter = new PeopleRVAdapter(peopleArrayList, activity);
                peopleRV.setAdapter(peopleRVAdapter);
                peopleTV.setVisibility(View.VISIBLE);
            } else
                peopleTV.setVisibility(View.GONE);
        }
    }

    public TextView changeBackgroundColor(TextView v, int type) {

        v.setBackgroundResource(R.drawable.bg_refcode_et);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        if (type == 1)
            drawable.setColor(v.getContext().getResources().getColor(R.color.greayrefcode_dark));
        else if (type == 2)
            drawable.setColor(v.getContext().getResources().getColor(R.color.green));
        else if (type == 3) drawable.setColor(v.getContext().getResources().getColor(R.color.red));
        return v;
    }

    public LinearLayout changeBackgroundColor(LinearLayout v, int type) {

        v.setBackgroundResource(R.drawable.bg_refcode_et);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(v.getContext().getResources().getColor(R.color.white));
        if (type == 1)
            drawable.setStroke(2, v.getContext().getResources().getColor(R.color.greayrefcode_dark));
        else if (type == 2)
            drawable.setStroke(2, v.getContext().getResources().getColor(R.color.green));
        else if (type == 3)
            drawable.setStroke(2, v.getContext().getResources().getColor(R.color.red));

        return v;
    }

    View.OnClickListener onLikeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TagHolder1 = (ViewHolder1) view.getTag();
            TagHolder1.LikeClickRL.setEnabled(false);
            int pos = TagHolder1.getAdapterPosition();

            if (feedArrayList.get(TagHolder1.getAdapterPosition()).isLiked()) {

                setLikes(pos, 0, TagHolder1);

                networkCall.NetworkAPICall(API.API_DISLIKE_POST, false);
            } else {
                setLikes(pos, 1, TagHolder1);

                networkCall.NetworkAPICall(API.API_LIKE_POST, false);
            }
        }
    };

    View.OnClickListener onShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            TagHolder1 = (ViewHolder1) view.getTag();

            networkCall.NetworkAPICall(API.API_SHARE_POST, true);
            TagHolder1.ShareClickRL.setEnabled(false);

        }
    };

    View.OnClickListener onCommentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TagHolder1 = (ViewHolder1) view.getTag();
            Helper.GoToPostActivity(activity, feedArrayList.get(TagHolder1.getAdapterPosition()), Const.COMMENT);
        }
    };


    View.OnClickListener onBookmarkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TagHolder1 = (ViewHolder1) view.getTag();
            TagHolder1.BookmarkClickRL.setEnabled(false);
            int pos = TagHolder1.getAdapterPosition();

            if (feedArrayList.get(TagHolder1.getAdapterPosition()).is_bookmarked()) {

                feedArrayList.get(pos).setIs_bookmarked(false);
                ((ImageView) TagHolder1.BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon);
                ((TextView) TagHolder1.BookmarkClickRL.getChildAt(1)).setTextColor(context.getResources().getColor(R.color.transparent_black));
                ((TextView) TagHolder1.BookmarkClickRL.getChildAt(1)).setText("Save");

                networkCall.NetworkAPICall(API.API_REMOVE_BOOKMARK, false);
            } else {

                feedArrayList.get(pos).setIs_bookmarked(true);
                ((ImageView) TagHolder1.BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon_blue);
                ((TextView) TagHolder1.BookmarkClickRL.getChildAt(1)).setTextColor(context.getResources().getColor(R.color.blue));
                ((TextView) TagHolder1.BookmarkClickRL.getChildAt(1)).setText("Saved");

                networkCall.NetworkAPICall(API.API_ADD_BOOKMARK, false);
            }
        }
    };
}
