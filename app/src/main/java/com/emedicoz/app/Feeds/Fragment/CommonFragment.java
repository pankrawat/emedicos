package com.emedicoz.app.Feeds.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Feeds.Activity.FeedsActivity;
import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.Feeds.Adapter.CommentRVAdapter;
import com.emedicoz.app.Feeds.Adapter.GalleryAdapter;
import com.emedicoz.app.Feeds.Adapter.NotificationRVAdapter;
import com.emedicoz.app.Model.Comment;
import com.emedicoz.app.Model.PostFile;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.NotificationResponse;
import com.emedicoz.app.Response.PostResponse;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.SharedPreference;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommonFragment extends MainFragment {

    EditText writecommentET;
    ImageButton postcommentIBtn;

    public LinearLayout writecommentLL;
    RecyclerView commonRV;
    LinearLayout feeds_single_row_LL;

    TextView errorTV;

    ArrayList<Comment> commentArrayList;
    ArrayList<NotificationResponse> notificationArrayList;

    NotificationRVAdapter notificationRVAdapter;
    CommentRVAdapter commentRVAdapter;

    String commentText;
    Activity activity;
    String frag_type;
    String postid;

    public PostResponse post;

    TextView descriptionTV, timeTV, nameTV,
            categoryTV, tagTV, commentTV, likeTV, doc_nameTV;

    LinearLayout LikeClickRL, CommentClickRL, ShareClickRL, BookmarkClickRL;

    ImageView profilepicIV, profilepicIVText, optionTV, video_image, doc_imageIV;

    LinearLayout mcqoptionsLL;

    ViewPager mViewPager;
    View mView;

    RelativeLayout docRL;
    RelativeLayout view_pagerRL;
    RelativeLayout imageRL;

    private LinearLayout dotsLayout;

    private ImageView[] dots;
    String errorMessage;
    boolean ispostVisible = false;

    ArrayList<PostFile> imageArrayList;

    GalleryAdapter galleryAdapter;

    RelativeLayout mn_videoplayer;
    LinearLayoutManager LM;

    public String last_activity_id = "";
    public String last_comment_id = "";
    private boolean loading = true;
    private int visibleThreshold = 2;
    public int firstVisibleItem, visibleItemCount, totalItemCount;
    public int previousTotalItemCount;
    String video_url;


    SharedPreference sharedPreference = SharedPreference.getInstance();

    public CommonFragment() {
    }

    public static CommonFragment newInstance(PostResponse post, String frag_type, String postid) {
        CommonFragment fragment = new CommonFragment();
        Bundle args = new Bundle();
        args.putSerializable(Const.POST, post);
        args.putString(Const.FRAG_TYPE, frag_type);
        args.putString(Const.POST_ID, postid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            post = (PostResponse) getArguments().getSerializable(Const.POST);
            frag_type = getArguments().getString(Const.FRAG_TYPE);
            postid = getArguments().getString(Const.POST_ID);
        }
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_common, container, false);
    }

    int p = 0;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.readallIM:
                NetworkAPICall(API.API_ALL_NOTIFICATION_READ, true);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FeedsActivity.IS_POST_UPDATED) {
            if (SharedPreference.getInstance().getPost() != null) {
                post = SharedPreference.getInstance().getPost();
                initPost(mView);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        commentArrayList = new ArrayList<>();
        notificationArrayList = new ArrayList<>();
        last_activity_id = "";
        last_comment_id = "";
        mView = view;
        feeds_single_row_LL = (LinearLayout) view.findViewById(R.id.feeds_single_row_CV);
        feeds_single_row_LL.setVisibility(View.GONE);
        errorTV = (TextView) view.findViewById(R.id.errorTV);

        writecommentET = (EditText) view.findViewById(R.id.writecommentET);
        postcommentIBtn = (ImageButton) view.findViewById(R.id.postcommentIBtn);

        commonRV = (RecyclerView) view.findViewById(R.id.commonRV);

        writecommentLL = (LinearLayout) view.findViewById(R.id.writecommentLL);
        LM = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        commonRV.setLayoutManager(LM);
        commonRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

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

                    Log.i("Yaeye!", "end called");

                    int i = 0;
                    while (i < totalItemCount) {
                        if (frag_type.equals(Const.NOTIFICATION))
                            last_activity_id = notificationArrayList.get(totalItemCount - 1 - i).getId();
                        else
                            last_comment_id = commentArrayList.get(totalItemCount - 1 - i).getId();
                        i = totalItemCount;
                    }
                    RefreshCommon(false);
                    loading = true;
                }

            }
        });
        if (frag_type.equals(Const.NOTIFICATION)) {
            RefreshCommon(true);
            writecommentLL.setVisibility(View.GONE);
            feeds_single_row_LL.setVisibility(View.GONE);
        } else {
            writecommentLL.setVisibility(View.VISIBLE);
            if (post == null && postid != null) {
                NetworkAPICall(API.API_SINGLE_POST_FOR_USER, true);
            } else {
                ispostVisible = true;
                feeds_single_row_LL.setVisibility(View.VISIBLE);
                postid = post.getId();
                initPost(mView);
            }
        }
        postcommentIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckValidation();
            }
        });
    }

    public void CheckValidation() {
        commentText = Helper.GetText(writecommentET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(commentText))
            isDataValid = Helper.DataNotValid(writecommentET);

        if (isDataValid) {
            postcommentIBtn.setEnabled(false);
            NetworkAPICall(API.API_ADD_COMMENT, true);
        }
    }

    public void InitcommentAdapter() {
        if (last_comment_id.equals("")) {
            if (commentArrayList.size() > 0 && ispostVisible) {
                commentRVAdapter = new CommentRVAdapter(commentArrayList, activity);
                commonRV.setAdapter(commentRVAdapter);
                errorTV.setVisibility(View.GONE);
                commonRV.setVisibility(View.VISIBLE);
            } else {
                errorTV.setText(errorMessage);
                errorTV.setVisibility(View.VISIBLE);
                commonRV.setVisibility(View.GONE);
            }
        } else commentRVAdapter.notifyDataSetChanged();
    }

    protected void InitNotificationAdapter() {
        if (last_activity_id.equals("")) {
            if (notificationArrayList.size() > 0) {
                notificationRVAdapter = new NotificationRVAdapter(notificationArrayList, activity);
                commonRV.setAdapter(notificationRVAdapter);
                errorTV.setVisibility(View.GONE);
                commonRV.setVisibility(View.VISIBLE);
            } else {
                errorTV.setText(errorMessage);
                errorTV.setVisibility(View.VISIBLE);
                commonRV.setVisibility(View.GONE);
            }
        } else notificationRVAdapter.notifyDataSetChanged();
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_SINGLE_POST_FOR_USER:
                return Ion.with(this)
                        .load(API.API_SINGLE_POST_FOR_USER)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, postid);
            case API.API_ADD_COMMENT:
                return Ion.with(this)
                        .load(API.API_ADD_COMMENT)
                        .setTimeout(10 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, postid)
                        .setMultipartParameter(Const.POST_COMMENT, commentText);
            case API.API_GET_COMMENT_LIST:
                return Ion.with(this)
                        .load(API.API_GET_COMMENT_LIST)
                        .setTimeout(10 * 1000)
                        .setMultipartParameter(Const.POST_ID, postid)
                        .setMultipartParameter(Const.LAST_COMMENT_ID, last_comment_id);

            case API.API_GET_USER_NOTIFICATIONS:
                return Ion.with(this)
                        .load(API.API_GET_USER_NOTIFICATIONS)
                        .setTimeout(10 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.LAST_ACTIVITY_ID, last_activity_id);
            case API.API_LIKE_POST:
                return Ion.with(this)
                        .load(API.API_LIKE_POST)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, post.getId());
            case API.API_DISLIKE_POST:
                return Ion.with(this)
                        .load(API.API_DISLIKE_POST)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, post.getId());
            case API.API_ALL_NOTIFICATION_READ:
                return Ion.with(this)
                        .load(API.API_ALL_NOTIFICATION_READ)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId());
            case API.API_SHARE_POST:
                String postid;
                if (post.getIs_shared().equals("1"))
                    postid = post.getParent_id();
                else postid = post.getId();
                return Ion.with(this)
                        .load(API.API_SHARE_POST)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, postid);
            case API.API_ADD_BOOKMARK:
                return Ion.with(this)
                        .load(API.API_ADD_BOOKMARK)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, post.getId());
            case API.API_REMOVE_BOOKMARK:
                return Ion.with(this)
                        .load(API.API_REMOVE_BOOKMARK)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, post.getId());
            case API.API_REPORT_POST:
                return Ion.with(this)
                        .load(API.API_REPORT_POST)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.POST_ID, post.getId());
            case API.API_REQUEST_VIDEO_LINK:
                return Ion.with(this)
                        .load(API.API_REQUEST_VIDEO_LINK)
                        .setMultipartParameter(Const.NAME, video_url);
            case API.API_DELETE_POST:
                return Ion.with(this)
                        .load(API.API_DELETE_POST)
                        .setTimeout(5 * 1000)
                        .setMultipartParameter(Const.USER_ID, post.getUser_id())
                        .setMultipartParameter(Const.POST_ID, post.getId());
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
        switch (apitype) {
            case API.API_SINGLE_POST_FOR_USER:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {

                    JSONObject singledatarow = jsonobject.optJSONObject(Const.DATA);
                    post = gson.fromJson(singledatarow.toString(), PostResponse.class);
                    feeds_single_row_LL.setVisibility(View.VISIBLE);
                    initPost(mView);
//                    InitcommentAdapter(); // temporary removed
                    ispostVisible = true;
                } else {
                    errorTV.setVisibility(View.VISIBLE);
                    commonRV.setVisibility(View.GONE);
                    writecommentLL.setVisibility(View.GONE);
                    errorTV.setText(jsonobject.optString(Const.MESSAGE));
                }

                break;
            case API.API_ALL_NOTIFICATION_READ:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    last_activity_id = "";
                    if (notificationArrayList.size() > 0) {
                        for (NotificationResponse NR : notificationArrayList) {
                            NR.setView_state(1);
                        }
                    }
                } else {
                    ErrorCallBack(jsonobject.optString(Const.MESSAGE), apitype);
                }
                InitNotificationAdapter();

                break;
            case API.API_DELETE_POST:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                    activity.finish();

                } else {
                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_ADD_COMMENT:
                last_comment_id = "";
                firstVisibleItem = 0;
                previousTotalItemCount = 0;
                visibleItemCount = 0;
                writecommentET.setText("");
                postcommentIBtn.setEnabled(true);
                PostResponse postResponse = gson.fromJson(jsonobject.getJSONObject(Const.DATA).toString(), PostResponse.class);
                post = postResponse;
                commonRV.smoothScrollToPosition(0);

                if (!TextUtils.isEmpty(post.getComments()) && post.getComments().equals("1"))
                    commentTV.setText(post.getComments() + " Comment");
                else
                    commentTV.setText((TextUtils.isEmpty(post.getComments()) ? "0" : post.getComments()) + " Comments");

                if (!TextUtils.isEmpty(post.getComments()) && !post.getComments().equals("0"))
                    RefreshCommon(true);
                else {
                    errorTV.setVisibility(View.VISIBLE);
                    commonRV.setVisibility(View.GONE);
                    errorTV.setText(activity.getString(R.string.no_comments_found));
                }
                break;
            case API.API_GET_COMMENT_LIST:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONArray dataarray = jsonobject.getJSONArray(Const.DATA);
                    if (last_comment_id.equals("")) {
                        commentArrayList = new ArrayList();
                    }
                    if (dataarray.length() > 0) {
                        int i = 0;
                        while (i < dataarray.length()) {
                            JSONObject singledatarow = dataarray.getJSONObject(i);
                            Comment response = gson.fromJson(singledatarow.toString(), Comment.class);
                            commentArrayList.add(response);
                            i++;
                        }
                    }
                } else {
                    ErrorCallBack(jsonobject.optString(Const.MESSAGE), apitype);
                }
                InitcommentAdapter();
                break;
            case API.API_GET_USER_NOTIFICATIONS:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONArray dataarray = jsonobject.getJSONArray(Const.DATA);
                    if (last_activity_id.equals("")) {
                        notificationArrayList = new ArrayList();
                    }
                    if (dataarray.length() > 0) {
                        int i = 0;
                        while (i < dataarray.length()) {
                            JSONObject singledatarow = dataarray.getJSONObject(i);
                            NotificationResponse response = gson.fromJson(singledatarow.toString(), NotificationResponse.class);
                            notificationArrayList.add(response);
                            i++;
                        }
                    }
                } else {
                    this.ErrorCallBack(jsonobject.optString(Const.MESSAGE), apitype);
                }
                InitNotificationAdapter();
                break;
            case API.API_LIKE_POST:
                LikeClickRL.setEnabled(true);
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {

                } else {
                    setLikes(0);

                    this.ErrorCallBack(jsonobject.optString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_DISLIKE_POST:
                LikeClickRL.setEnabled(true);
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {


                } else {
                    setLikes(1);
                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_SHARE_POST:
                ShareClickRL.setEnabled(true);
                this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                break;
            case API.API_ADD_BOOKMARK:
                BookmarkClickRL.setEnabled(true);

                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                } else {

                    post.setIs_bookmarked(false);
                    ((ImageView) BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon);
                    ((TextView) BookmarkClickRL.getChildAt(1)).setTextColor(activity.getResources().getColor(R.color.transparent_black));

                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_REMOVE_BOOKMARK:
                BookmarkClickRL.setEnabled(true);
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                } else {
                    post.setIs_bookmarked(true);
                    ((ImageView) BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon_blue);
                    ((TextView) BookmarkClickRL.getChildAt(1)).setTextColor(activity.getResources().getColor(R.color.blue));

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
            case API.API_REPORT_POST:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    post.setReport_abuse("1");
                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                } else {
                    this.ErrorCallBack(jsonobject.getString(Const.MESSAGE), apitype);
                }
                break;
        }
        if (!apitype.equals(API.API_GET_USER_NOTIFICATIONS) && !apitype.equals(API.API_DELETE_POST) && !apitype.equals(API.API_GET_COMMENT_LIST) && !apitype.equals(API.API_SINGLE_POST_FOR_USER)) {
            FeedsActivity.IS_COMMENT_REFRESHED = true;
            sharedPreference.setPost(post);
        }
        if (apitype.equals(API.API_DELETE_POST)) {
            FeedsActivity.IS_POST_DELETED = true;
            sharedPreference.setPost(post);
        }

    }

    public void deletePost() {
        NetworkAPICall(API.API_DELETE_POST, true);
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        switch (apitype) {
            case API.API_GET_COMMENT_LIST:
            case API.API_GET_USER_NOTIFICATIONS:
                if (TextUtils.isEmpty(last_activity_id) || TextUtils.isEmpty(last_comment_id)) {
                    errorMessage = jsonstring;
                }
                break;
            default:
                Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void initPost(View view) {
        if (!post.getComments().equals("0")) RefreshCommon(true);
        else {
            errorTV.setVisibility(View.VISIBLE);
            commonRV.setVisibility(View.GONE);
            errorTV.setText(activity.getString(R.string.no_comments_found));
        }
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
                Helper.DownloadfilefromURL(activity, post.getPost_data().getPost_file().get(0));
//                ((BaseABNavActivity) activity).postFile = post.getPost_data().getPost_file().get(0);
//                Helper.GoToWebViewActivity(activity, Const.GOOGLE_PREVIEW_DOC_URL + post.getPost_data().getPost_file().get(0).getLink());
            }
        });
        mcqoptionsLL = (LinearLayout) view.findViewById(R.id.mcqoptions);

        nameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.GoToProfileActivity(activity, post.getUser_id());
            }
        });
        imageRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.GoToProfileActivity(activity, post.getUser_id());
            }
        });
        mn_videoplayer = (RelativeLayout) view.findViewById(R.id.mn_videoplayer);
        mn_videoplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                video_url = post.getPost_data().getPost_file().get(0).getLink();
                NetworkAPICall(API.API_REQUEST_VIDEO_LINK, true);
            }
        });

        if (post.getPost_data().getPost_file().size() > 0) {
            if (post.getPost_data().getPost_file().get(0).getFile_type().equals(Const.IMAGE)) {
                view_pagerRL.setVisibility(View.VISIBLE);
                mn_videoplayer.setVisibility(View.GONE);
                docRL.setVisibility(View.GONE);

                imageArrayList = post.getPost_data().getPost_file();
                InitImageView();

            } else if (post.getPost_data().getPost_file().get(0).getFile_type().equals(Const.VIDEO)) {
                mn_videoplayer.setVisibility(View.VISIBLE);
                view_pagerRL.setVisibility(View.GONE);
                docRL.setVisibility(View.GONE);

                if (post.getPost_data().getPost_file().get(0).getLink().contains(Const.AMAZON_S3_IMAGE_PREFIX))
                    Ion.with(this).load(post.getPost_data().getPost_file().get(0).getLink()).intoImageView(video_image);
                else
                    Ion.with(this).load(Const.AMAZON_S3_IMAGE_PREFIX + Const.AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES +
                            "/" + post.getPost_data().getPost_file().get(0).getFile_info().split(".mp4")[0] + ".png")
                            .intoImageView(video_image);
            } else if (post.getPost_data().getPost_file().get(0).getFile_type().equals(Const.PDF) ||
                    post.getPost_data().getPost_file().get(0).getFile_type().equals(Const.DOC) ||
                    post.getPost_data().getPost_file().get(0).getFile_type().equals(Const.XLS)) {

                mn_videoplayer.setVisibility(View.GONE);
                view_pagerRL.setVisibility(View.GONE);
                docRL.setVisibility(View.VISIBLE);

                if (post.getPost_data().getPost_file().get(0).getFile_type().equals(Const.PDF)) {
                    doc_nameTV.setText(post.getPost_data().getPost_file().get(0).getFile_info());
                    doc_imageIV.setImageResource(R.mipmap.pdf_download);

                } else if (post.getPost_data().getPost_file().get(0).getFile_type().equals(Const.DOC)) {
                    doc_nameTV.setText(post.getPost_data().getPost_file().get(0).getFile_info());
                    doc_imageIV.setImageResource(R.mipmap.doc_download);

                } else if (post.getPost_data().getPost_file().get(0).getFile_type().equals(Const.XLS)) {
                    doc_nameTV.setText(post.getPost_data().getPost_file().get(0).getFile_info());
                    doc_imageIV.setImageResource(R.mipmap.xls_download);
                }
            }
        } else {
            mn_videoplayer.setVisibility(View.GONE);
            view_pagerRL.setVisibility(View.GONE);
            docRL.setVisibility(View.GONE);
        }

        categoryTV.setText(post.getPost_owner_info().getSpeciality());

        if (post.getPost_tag() != null) {
            tagTV.setVisibility(View.VISIBLE);
            tagTV.setText(post.getPost_tag());
        } else
            tagTV.setVisibility(View.GONE);

        timeTV.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(post.getCreation_time())).equals("0 minutes ago") ? "Just Now" : DateUtils.getRelativeTimeSpanString(Long.parseLong(post.getCreation_time())));
/*
        if (post.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId())) {
            optionTV.setVisibility(View.GONE);
        } else optionTV.setVisibility(View.VISIBLE);*/
        optionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = 0;
                if (post.getUser_id().equals(SharedPreference.getInstance().getLoggedInUser().getId()))
                    temp++;
                if (!TextUtils.isEmpty(SharedPreference.getInstance().getLoggedInUser().getIs_moderate()) && SharedPreference.getInstance().getLoggedInUser().getIs_moderate().equals("1"))
                    temp += 2;
                showPopMenu(view, temp);
            }
        });

        post.getPost_owner_info().setName(Helper.CapitalizeText(post.getPost_owner_info().getName()));

        if (!TextUtils.isEmpty(post.getPost_owner_info().getProfile_picture())) {
            profilepicIV.setVisibility(View.VISIBLE);
            profilepicIVText.setVisibility(View.GONE);
            Ion.with(profilepicIV.getContext()).load(post.getPost_owner_info().getProfile_picture())
                    .asBitmap()
                    .setCallback(new FutureCallback<Bitmap>() {
                        @Override
                        public void onCompleted(Exception e, Bitmap result) {
                            if (result != null)
                                profilepicIV.setImageBitmap(result);
                            else
                                profilepicIV.setImageResource(R.mipmap.default_pic);
                        }
                    });
        } else {
            profilepicIV.setVisibility(View.GONE);
            profilepicIVText.setVisibility(View.VISIBLE);
            profilepicIVText.setImageDrawable(Helper.GetDrawable(post.getPost_owner_info().getName(), activity, post.getPost_owner_info().getId()));
        }

        setLikes(2);

        LikeClickRL.setOnClickListener(onLikeClickListener);
        CommentClickRL.setOnClickListener(onCommentClickListener);
        ShareClickRL.setOnClickListener(onShareClickListener);
        BookmarkClickRL.setOnClickListener(onBookmarkClickListener);
        commentTV.setOnClickListener(onCommentClickListener);

        /*if (post.getComments().equals("1"))
            commentTV.setText(post.getComments() + " Comment");
        else
            commentTV.setText(post.getComments() + " Comments");*/
        setComments(2);

        //Bookmark/Save button action
        if (post.is_bookmarked()) {
            ((ImageView) BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon_blue);
            ((TextView) BookmarkClickRL.getChildAt(1)).setTextColor(getResources().getColor(R.color.blue));
            ((TextView) BookmarkClickRL.getChildAt(1)).setText("Saved");

        } else {
            ((ImageView) BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon);
            ((TextView) BookmarkClickRL.getChildAt(1)).setTextColor(getResources().getColor(R.color.black));
            ((TextView) BookmarkClickRL.getChildAt(1)).setText("Save");
        }

        post.getPost_owner_info().setName(Helper.CapitalizeText(post.getPost_owner_info().getName()));

        nameTV.setText(post.getPost_owner_info().getName());

        if (post.getPost_type().equals(Const.POST_TYPE_MCQ)) {
            mcqoptionsLL.removeAllViews();
            descriptionTV.setText("" + post.getPost_data().getQuestion());

            if (!(post.getPost_data().getAnswer_one().equals("") || post.getPost_data().getAnswer_one().equals(null))) {
                mcqoptionsLL.addView(initMCQOptionView("A", post.getPost_data().getAnswer_one()));
            }
            if (!(post.getPost_data().getAnswer_two().equals("") || post.getPost_data().getAnswer_two().equals(null))) {
                mcqoptionsLL.addView(initMCQOptionView("B", post.getPost_data().getAnswer_two()));
            }
            if (!(post.getPost_data().getAnswer_three().equals("") || post.getPost_data().getAnswer_three().equals(null))) {
                mcqoptionsLL.addView(initMCQOptionView("C", post.getPost_data().getAnswer_three()));
            }
            if (!(post.getPost_data().getAnswer_four().equals("") || post.getPost_data().getAnswer_four().equals(null))) {
                mcqoptionsLL.addView(initMCQOptionView("D", post.getPost_data().getAnswer_four()));
            }
            if (!(post.getPost_data().getAnswer_five().equals("") || post.getPost_data().getAnswer_five().equals(null))) {
                mcqoptionsLL.addView(initMCQOptionView("E", post.getPost_data().getAnswer_five()));
            }
        } else if (post.getPost_type().equals(Const.POST_TYPE_NORMAL)) {
            mcqoptionsLL.setVisibility(View.GONE);
            descriptionTV.setText(post.getPost_data().getText());
        }
    }

    public void InitImageView() {
        addBottomDots(0);
        galleryAdapter = new GalleryAdapter(activity, imageArrayList);
        mViewPager.setAdapter(galleryAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
//        mViewPager.setOnClickListener(onPagerClick);
    }


    private void addBottomDots(int currentPage) {
        dotsLayout.removeAllViews();
        if (imageArrayList.size() > 1) {
            dots = new ImageView[imageArrayList.size()];
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new ImageView(activity);
                dots[i].setImageResource(R.drawable.nonselecteditem_dot);
                dots[i].setPadding(5, 5, 5, 0);
                dotsLayout.addView(dots[i]);
            }
            if (dots.length > 0)
                dots[currentPage].setImageResource(R.drawable.selecteditem_dot);
        }
    }

    public void showPopMenu(final View v, int temp) {
        PopupMenu popup = new PopupMenu(activity, v);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.reportIM:
                        reportPost();
                        return true;

                    case R.id.editIM:
                        editPost();
                        return true;

                    case R.id.deleteIM:
                        AlertDialog.Builder alertBuild = new AlertDialog.Builder(activity);

                        alertBuild
                                .setTitle(activity.getString(R.string.app_name))
                                .setMessage(activity.getString(R.string.deleteMessage))
                                .setCancelable(true)
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        deletePost();
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

        menu.findItem(R.id.reportIM).setVisible(((temp == 0 || temp == 2) ? true : false));
        menu.findItem(R.id.editIM).setVisible((temp != 0 ? true : false));
        menu.findItem(R.id.deleteIM).setVisible((temp != 0 ? true : false));

        popup.show();
    }

    public void editPost() {
        Intent intent = new Intent(activity, PostActivity.class);// Edit Post
        intent.putExtra(Const.FRAG_TYPE, Const.POST_FRAG);
        intent.putExtra(Const.POST, post);
        activity.startActivity(intent);
    }


    public void reportPost() {
        NetworkAPICall(API.API_REPORT_POST, true);
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

    public LinearLayout initMCQOptionView(String text1, String text2) {
        LinearLayout v = (LinearLayout) View.inflate(activity, R.layout.layout_option_mcq_view, null);
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

    public void setLikes(int type) {
        String finallikes = "";
        String likes = post.getLikes();

        //this is to increment or decrement the likes on the post.
        if (type == 1) {
            post.setLiked(true);
            finallikes = String.valueOf(Integer.valueOf(likes) + 1);
        } else if (type == 0) {
            post.setLiked(false);
            finallikes = String.valueOf(Integer.valueOf(likes) - 1);
        } else
            finallikes = likes;

        // this is to change the icon and color of likes on the post.
        if (post.isLiked()) {
            ((TextView) LikeClickRL.getChildAt(1)).setTextColor(activity.getResources().getColor(R.color.blue));
            ((ImageView) LikeClickRL.getChildAt(0)).setImageResource(R.mipmap.like_blue);
        } else {
            ((TextView) LikeClickRL.getChildAt(1)).setTextColor(activity.getResources().getColor(R.color.transparent_black));
            ((ImageView) LikeClickRL.getChildAt(0)).setImageResource(R.mipmap.like);
        }

        post.setLikes(finallikes);

        //this is to show the count of likes on the post.
        if (post.getLikes().equals("1"))
            likeTV.setText(post.getLikes() + " like");
        else
            likeTV.setText(post.getLikes() + " likes");

    }

    public void setComments(int type) {
        String finalcomments = "";
        String comments = post.getComments();
        if (type == 1)
            finalcomments = String.valueOf(Integer.valueOf(comments) + 1);
        else if (type == 0)
            finalcomments = String.valueOf(Integer.valueOf(comments) - 1);
        else finalcomments = comments;

        post.setComments(finalcomments);

        if (post.getComments().equals("1"))
            commentTV.setText(post.getComments() + " Comment");
        else
            commentTV.setText(post.getComments() + " Comments");
    }

    View.OnClickListener onLikeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LikeClickRL.setEnabled(false);

            if (post.isLiked()) {
                setLikes(0);
                NetworkAPICall(API.API_DISLIKE_POST, false);
            } else {
                setLikes(1);
                NetworkAPICall(API.API_LIKE_POST, false);
            }
        }
    };

    View.OnClickListener onShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            NetworkAPICall(API.API_SHARE_POST, true);
            ShareClickRL.setEnabled(false);

        }
    };

    View.OnClickListener onCommentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Helper.ShowKeyboard(activity);
            writecommentET.requestFocus();
        }
    };


    View.OnClickListener onBookmarkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            BookmarkClickRL.setEnabled(false);

            if (post.is_bookmarked()) {
                post.setIs_bookmarked(false);
                ((ImageView) BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon);
                ((TextView) BookmarkClickRL.getChildAt(1)).setTextColor(activity.getResources().getColor(R.color.transparent_black));
                ((TextView) BookmarkClickRL.getChildAt(1)).setText("Save");
                NetworkAPICall(API.API_REMOVE_BOOKMARK, false);
            } else {
                post.setIs_bookmarked(true);
                ((ImageView) BookmarkClickRL.getChildAt(0)).setImageResource(R.mipmap.ribbon_blue);
                ((TextView) BookmarkClickRL.getChildAt(1)).setTextColor(activity.getResources().getColor(R.color.blue));
                ((TextView) BookmarkClickRL.getChildAt(1)).setText("Saved");
                NetworkAPICall(API.API_ADD_BOOKMARK, false);
            }
        }
    };

    public void RefreshCommon(boolean show) {
        if (!frag_type.equals(Const.NOTIFICATION))
            NetworkAPICall(API.API_GET_COMMENT_LIST, show);
        else NetworkAPICall(API.API_GET_USER_NOTIFICATIONS, show);
    }
}
