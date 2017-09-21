package com.emedicoz.app.Feeds.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Feeds.Activity.FeedsActivity;
import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.Feeds.Adapter.ImageRVAdapter;
import com.emedicoz.app.Model.MediaFile;
import com.emedicoz.app.Model.PostFile;
import com.emedicoz.app.Model.PostMCQ;
import com.emedicoz.app.Model.User;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.PostResponse;
import com.emedicoz.app.Utils.AmazonUpload.AmazonCallBack;
import com.emedicoz.app.Utils.AmazonUpload.GetFilePath;
import com.emedicoz.app.Utils.AmazonUpload.s3ImageUploading;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.SharedPreference;
import com.emedicoz.app.Utils.TakeImageClass;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewPostFragment extends MainFragment implements View.OnClickListener, AmazonCallBack, TakeImageClass.imagefromcropper {

    ImageView addIV;
    ImageView removeIV;

    LinearLayout answerLayoutLL;
    LinearLayout mcqlayoutLL;

    LinearLayout addattachmentLL;
    LinearLayout addquestionLL;
    LinearLayout addmediaLL;
    LinearLayout addtagLL;

    EditText writepostET;

    TextView questioniconText;
    TextView nameTV;
    public TextView tagTV;

    ImageView profileImageIV;
    ImageView profileImageIVText;

    Button postBtn;

    RecyclerView multiImageRV;

    ImageRVAdapter imageRVAdapter;

    ArrayList<MediaFile> imageArrayList;
    ArrayList<MediaFile> newimageArrayList; // for new images to be uploaded to S3 at time of editing the post
    s3ImageUploading s3ImageUploading;
    ArrayList<PostFile> fileArrayList;
    Gson gson;

    String question, answer_one, answer_two, answer_three, answer_four, answer_five, right_answer = "answer_one";
    public String postTag;
    public String post_tag_id;
    Activity activity;

    boolean isQuestion = false;
    User user;
    private TakeImageClass takeImageClass;
    int RC_TAKE_PHOTO = 12;
    File file;
    Uri fileUri;
    PostResponse post;
    List<View> LinearLayoutList, LinearLayoutIconList;
    int mcqansCounter = 0;
    int isPostEdit = 0; // 0 -not edit, 1-mcq edit, 2-normal post edit
    String name, profile_picture, userid;

    public NewPostFragment() {
    }

    public static NewPostFragment newInstance(PostResponse post) {
        NewPostFragment fragment = new NewPostFragment();
        Bundle args = new Bundle();
        args.putSerializable(Const.POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        user = SharedPreference.getInstance().getLoggedInUser();
        if (getArguments() != null) {
            post = (PostResponse) getArguments().getSerializable(Const.POST);
        }
        takeImageClass = new TakeImageClass(activity, this) {
            @Override
            public void openGallery() {
                if (imageArrayList.size() < 5) {
                    selectMultipleImages();
                } else {
                    Toast.makeText(activity, "You cannot add other Media", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void takePicture() {
                if (imageArrayList.size() < 5) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file = new File(getActivity().getExternalCacheDir(),
                            String.valueOf(System.currentTimeMillis()) + ".jpg");
                    fileUri = Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    getActivity().startActivityForResult(intent, RC_TAKE_PHOTO);
                } else {
                    Toast.makeText(activity, "You cannot add other Media", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_post, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        fileArrayList = new ArrayList<>();
        gson = new Gson();

        progressBar = (ProgressBar) view.findViewById(R.id.newpostprogress);
        postBtn = (Button) view.findViewById(R.id.postBtn);
        addIV = (ImageView) view.findViewById(R.id.addIV);
        removeIV = (ImageView) view.findViewById(R.id.removeIV);
        profileImageIV = (ImageView) view.findViewById(R.id.profileImageIV);
        profileImageIVText = (ImageView) view.findViewById(R.id.profileImageIVText);

        writepostET = (EditText) view.findViewById(R.id.writepostET);
        //Sushant
        writepostET.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable mEdit) {
                if (!((((PostActivity) activity).isimageadded) && (((PostActivity) activity).isvideoadded) && (((PostActivity) activity).isattachmentadded && isQuestion))) {
                    if (!(((PostActivity) activity).isyoutubevideoattached)) {
                        String str = mEdit.toString();


                        String x = Helper.youtubevalidation(str);
                        if (x != null) {
                            Log.e("VideoId is->", "" + x);
                            String img_url = "http://img.youtube.com/vi/" + x + "/0.jpg"; // this is link which will give u thumnail image of that video
                            setupYoutubeVideo(img_url);
                        }
                    } else if (((PostActivity) activity).isyoutubevideoattached) {
                        imageArrayList = new ArrayList<>();
                        InitImageAdapter();
                        String str = mEdit.toString();

                        String x = Helper.youtubevalidation(str);
                        if (x != null) {
                            Log.e("VideoId is->", "" + x);
                            String img_url = "http://img.youtube.com/vi/" + x + "/0.jpg"; // this is link which will give u thumnail image of that video
                            setupYoutubeVideo(img_url);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Media already attached", Toast.LENGTH_SHORT).show();
                }
            }


            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        multiImageRV = (RecyclerView) view.findViewById(R.id.multiImageRV);
        LinearLayoutManager LM = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        multiImageRV.setLayoutManager(LM);

        answerLayoutLL = (LinearLayout) view.findViewById(R.id.answerlayoutLL);
        addattachmentLL = (LinearLayout) view.findViewById(R.id.attachLL);
        addquestionLL = (LinearLayout) view.findViewById(R.id.addquestionLL);
        mcqlayoutLL = (LinearLayout) view.findViewById(R.id.mcqlayout_LL);
        addmediaLL = (LinearLayout) view.findViewById(R.id.addimageLL);
        addtagLL = (LinearLayout) view.findViewById(R.id.addtagLL);

        questioniconText = (TextView) addquestionLL.findViewById(R.id.questiontextTV);
        nameTV = (TextView) view.findViewById(R.id.nameTV);
        tagTV = (TextView) view.findViewById(R.id.tagTV);

        LinearLayoutList = new ArrayList();
        LinearLayoutIconList = new ArrayList();

        postBtn.setOnClickListener(this);
        addattachmentLL.setOnClickListener(this);
        addquestionLL.setOnClickListener(this);
        addIV.setOnClickListener(this);
        removeIV.setOnClickListener(this);
        addmediaLL.setOnClickListener(this);
        addtagLL.setOnClickListener(this);

        progressBar.setScaleY(1f);
        progressBar.setVisibility(View.GONE);

        if (post != null) {
            userid = post.getPost_owner_info().getId();
            name = post.getPost_owner_info().getName();
            profile_picture = post.getPost_owner_info().getProfile_picture();
            EditProfileInitViews();
        } else {
            userid = user.getId();
            name = user.getName();
            profile_picture = user.getProfile_picture();
        }

        if (!TextUtils.isEmpty(profile_picture)) {
            profileImageIV.setVisibility(View.VISIBLE);
            profileImageIVText.setVisibility(View.GONE);
            Ion.with(activity).load(profile_picture)
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
            Drawable dr = Helper.GetDrawable(name, activity, userid);
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
        nameTV.setText(name);
    }

    public void EditProfileInitViews() { // intialising the post which is there to edit

        if (TextUtils.isEmpty(post.getPost_tag())) {
            tagTV.setVisibility(View.GONE);
        } else {
            post_tag_id = post.getPost_tag_id();
            tagTV.setVisibility(View.VISIBLE);
            tagTV.setText(post.getPost_tag());
        }

        if (post.getPost_type().equals(Const.POST_TYPE_MCQ)) {
            isQuestion = true;
            isPostEdit = 1;
            mcqansCounter = 0;
            addattachmentLL.setVisibility(View.GONE);
            addmediaLL.setVisibility(View.GONE);
            addquestionLL.setVisibility(View.GONE);
            answerLayoutLL.removeAllViews();
            mcqlayoutLL.setVisibility(View.VISIBLE);
            writepostET.setText("" + post.getPost_data().getQuestion());

            if (!TextUtils.isEmpty(post.getPost_data().getAnswer_one())) {

                initMCQOptionView(++mcqansCounter);
                ((EditText) LinearLayoutList.get(0).findViewById(R.id.answerET)).setText(post.getPost_data().getAnswer_one());
            }
            if (!TextUtils.isEmpty(post.getPost_data().getAnswer_two())) {

                initMCQOptionView(++mcqansCounter);
                ((EditText) LinearLayoutList.get(1).findViewById(R.id.answerET)).setText(post.getPost_data().getAnswer_two());
            }
            if (!TextUtils.isEmpty(post.getPost_data().getAnswer_three())) {

                initMCQOptionView(++mcqansCounter);
                ((EditText) LinearLayoutList.get(2).findViewById(R.id.answerET)).setText(post.getPost_data().getAnswer_three());
            }
            if (!TextUtils.isEmpty(post.getPost_data().getAnswer_four())) {

                initMCQOptionView(++mcqansCounter);
                ((EditText) LinearLayoutList.get(3).findViewById(R.id.answerET)).setText(post.getPost_data().getAnswer_four());
            }
            if (!TextUtils.isEmpty(post.getPost_data().getAnswer_five())) {

                initMCQOptionView(++mcqansCounter);
                ((EditText) LinearLayoutList.get(4).findViewById(R.id.answerET)).setText(post.getPost_data().getAnswer_five());
            }

            AddRemoveQuestionLayout(!isQuestion);

        } else if (post.getPost_type().equals(Const.POST_TYPE_NORMAL)) {
            addquestionLL.setVisibility(View.GONE);
            isPostEdit = 2;
            isQuestion = false;
            mcqlayoutLL.setVisibility(View.GONE);
            writepostET.setText(post.getPost_data().getText());
        }

        fileArrayList = new ArrayList<>();
        imageArrayList = new ArrayList<>();
        if (post.getPost_data().getPost_file().size() > 0) {
            fileArrayList = post.getPost_data().getPost_file();
            if (fileArrayList.get(0).getFile_type().equals(Const.IMAGE)) {
                for (PostFile file : fileArrayList) {
                    MediaFile media = new MediaFile();
                    media.setId(file.getId());
                    media.setFile_name(file.getFile_info());
                    media.setFile_type(file.getFile_type());
                    media.setFile(file.getLink());
                    imageArrayList.add(media);
                }
            } else if (fileArrayList.get(0).getFile_type().equals(Const.VIDEO)) {
                MediaFile media = new MediaFile();
                media.setId(fileArrayList.get(0).getId());
                media.setFile_name(fileArrayList.get(0).getFile_info());
                media.setFile(fileArrayList.get(0).getLink());
                media.setFile_type(Const.VIDEO);
                imageArrayList.add(media);

            } else if (fileArrayList.get(0).getFile_type().equals(Const.PDF) ||
                    fileArrayList.get(0).getFile_type().equals(Const.DOC) ||
                    fileArrayList.get(0).getFile_type().equals(Const.XLS)) {
                MediaFile media = new MediaFile();
                media.setId(fileArrayList.get(0).getId());
                media.setFile_name(fileArrayList.get(0).getFile_info());
                media.setFile(fileArrayList.get(0).getLink());

                if (media.getFile().contains(getString(R.string.pdf_extension))) {
                    media.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.pdf));
                    media.setFile_type(Const.PDF);
                } else if (media.getFile().contains(getString(R.string.doc_extension))) {
                    media.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.doc));
                    media.setFile_type(Const.DOC);
                } else if (media.getFile().contains(getString(R.string.xls_extension))) {
                    media.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.xls));
                    media.setFile_type(Const.XLS);
                }
                imageArrayList.add(media);
            }
            InitImageAdapter();
        }

    }

    public void InitImageAdapter(){
        if(imageArrayList.size()>0){

        if(imageArrayList.get(0).getFile_type().equals(Const.IMAGE))
        ((PostActivity)activity).isimageadded=true;

        else if(imageArrayList.get(0).getFile_type().equals(Const.VIDEO))
        ((PostActivity)activity).isvideoadded=true;

        else if(imageArrayList.get(0).getFile_type().equals(Const.PDF)||
        imageArrayList.get(0).getFile_type().equals(Const.DOC)||
        imageArrayList.get(0).getFile_type().equals(Const.XLS))

        ((PostActivity)activity).isattachmentadded=true;
        else if(imageArrayList.get(0).getFile_type().equals(Const.YOUTUBE_VIDEO))
        ((PostActivity)activity).isyoutubevideoattached=true;

        imageRVAdapter=new ImageRVAdapter(activity,imageArrayList);
        multiImageRV.setAdapter(imageRVAdapter);
        multiImageRV.setVisibility(View.VISIBLE);
        }else{
        ((PostActivity)activity).isimageadded=false;
        ((PostActivity)activity).isvideoadded=false;
        ((PostActivity)activity).isattachmentadded=false;
        ((PostActivity)activity).isyoutubevideoattached=false;
        imageRVAdapter.notifyDataSetChanged();
        multiImageRV.setVisibility(View.GONE);
        }
        }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.attachLL:
                if (!((((PostActivity) activity).isvideoadded || ((PostActivity) activity).isimageadded || ((PostActivity) activity).isyoutubevideoattached)
                        && imageArrayList.size() > 0)) {
                    imageArrayList = new ArrayList<>();
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), Const.REQUEST_TAKE_GALLERY_DOC);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "Please install a File Manager.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(activity, R.string.you_cannot_add_more_media, Toast.LENGTH_SHORT).show();
                break;
            case R.id.addquestionLL:
                if (!(((PostActivity) activity).isimageadded || ((PostActivity) activity).isvideoadded || ((PostActivity) activity).isattachmentadded || ((PostActivity) activity).isyoutubevideoattached))
                    AddRemoveQuestionLayout(isQuestion);
                else
                    Toast.makeText(activity, R.string.no_question_with_media, Toast.LENGTH_SHORT).show();
                break;
            case R.id.addIV:
                if (mcqansCounter < 5) {
                    mcqansCounter++;
                    initMCQOptionView(mcqansCounter);
                } else {
                    Toast.makeText(activity, R.string.you_cannot_add_more_opt, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.removeIV:
                if (mcqansCounter > 0) {
                    mcqansCounter--;
                    answerLayoutLL.removeViewAt(mcqansCounter);
                    LinearLayoutList.remove(mcqansCounter);
//                    if (selected_answer.equals(LinearLayoutIconList.get(mcqansCounter).getTag()))
//                        selected_answer = "";
//                    LlansweroptioniconLL.removeViewAt(mcqansCounter);
//                    LinearLayoutIconList.remove(mcqansCounter);
                    if (mcqansCounter == 0) removeIV.setVisibility(View.GONE);
                    else removeIV.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(activity, R.string.no_more_opt_to_remov, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.postBtn:
                checkValidation();
                break;
            case R.id.addtagLL:
                TagSelectionFragment newFragment = (TagSelectionFragment) Fragment.instantiate(activity, TagSelectionFragment.class.getName());
                newFragment.show(((PostActivity) activity).getSupportFragmentManager(), "dialog");
                break;
            case R.id.addimageLL:
                if (imageArrayList == null)
                    imageArrayList = new ArrayList<>();

                if (!((((PostActivity) activity).isvideoadded || ((PostActivity) activity).isattachmentadded || ((PostActivity) activity).isyoutubevideoattached) && imageArrayList.size() > 0))
                    getImagePickerDialog(activity, getString(R.string.app_name), getString(R.string.choose_media));
                else
                    Toast.makeText(activity, R.string.you_cannot_add_more_media, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void AddRemoveQuestionLayout(boolean b) {
        if (b) {
            isQuestion = false;
            writepostET.setHint(getString(R.string.write_your_post_here));
            questioniconText.setText(getString(R.string.add_question));
            addmediaLL.setVisibility(View.VISIBLE);
            addattachmentLL.setVisibility(View.VISIBLE);
            mcqlayoutLL.setVisibility(View.GONE);
        } else {
            isQuestion = true;
            writepostET.setHint(getString(R.string.write_your_question_here));
            questioniconText.setText(getString(R.string.remove_question));
            addmediaLL.setVisibility(View.GONE);
            addattachmentLL.setVisibility(View.GONE);
            mcqlayoutLL.setVisibility(View.VISIBLE);
        }

        addmediaLL.setEnabled(b);
        addattachmentLL.setEnabled(b);
        mcqlayoutLL.setVisibility((b ? View.GONE : View.VISIBLE));
    }

    public void initMCQOptionView(int i) {
        View v = View.inflate(activity, R.layout.single_row_option_layout, null);
        TextView tv = (TextView) v.findViewById(R.id.optionTV);
        tv = changeBackgroundColor(tv, 2);
        if (i == 1) tv.setText("A");
        else if (i == 2) tv.setText("B");
        else if (i == 3) tv.setText("C");
        else if (i == 4) tv.setText("D");
        else if (i == 5) tv.setText("E");
        v.setTag(tv.getText());
                    /*View v1 = View.inflate(activity, R.layout.single_row_answer_option_icon, null);
                    TextView tv1 = (TextView) v1.findViewById(R.id.optioniconTV);
                    tv1 = changeBackgroundColor(tv1, 2);
                    tv1.setText(tv.getText());
                    v1.setTag(tv1.getText());
                    v1.setOnClickListener(onClickListener);
*/
        answerLayoutLL.addView(v);
        LinearLayoutList.add(v);

//                    LinearLayoutIconList.add(v1);
//                    LlansweroptioniconLL.addView(v1);
        if (i == 1) removeIV.setVisibility(View.VISIBLE);
    }

    public TextView changeBackgroundColor(TextView v, int type) {

        v.setBackgroundResource(R.drawable.bg_imagebtn_transparent);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        if (type == 1) drawable.setColor(getResources().getColor(R.color.blue));
        else drawable.setColor(getResources().getColor(R.color.transparent));
        return v;
    }

    //// this has been removed. it was about selecting the correct answer.
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {/*
            if (LinearLayoutIconList.size() > 0) {
                for (View v : LinearLayoutIconList) {
                    if (v.getTag().equals(view.getTag())) {
                        TextView tv = (TextView) v.findViewById(R.id.optioniconTV);
                        tv = changeBackgroundColor(tv, 1);
                        selected_answer = (String) tv.getText();
                    } else {
                        TextView tv = (TextView) v.findViewById(R.id.optioniconTV);
                        tv = changeBackgroundColor(tv, 2);
                    }
                }
                view.getTag();
            }*/
        }
    };

    public void checkValidation() {
        int j = 0;
        j = LinearLayoutList.size();
        question = Helper.GetText(writepostET);

        if (isQuestion) {
            if (j >= 1)
                answer_one = Helper.GetText((EditText) LinearLayoutList.get(0).findViewById(R.id.answerET));
            if (j >= 2)
                answer_two = Helper.GetText((EditText) LinearLayoutList.get(1).findViewById(R.id.answerET));
            if (j >= 3)
                answer_three = Helper.GetText((EditText) LinearLayoutList.get(2).findViewById(R.id.answerET));
            if (j >= 4)
                answer_four = Helper.GetText((EditText) LinearLayoutList.get(3).findViewById(R.id.answerET));
            if (j >= 5)
                answer_five = Helper.GetText((EditText) LinearLayoutList.get(4).findViewById(R.id.answerET));
        }
        boolean isDataValid = true;

        if (isQuestion) {
            if (TextUtils.isEmpty(question))
                isDataValid = Helper.DataNotValid(writepostET);
            else if ((j >= 1) && TextUtils.isEmpty(answer_one))
                isDataValid = Helper.DataNotValid((EditText) LinearLayoutList.get(0).findViewById(R.id.answerET));
            else if ((j >= 2) && TextUtils.isEmpty(answer_two))
                isDataValid = Helper.DataNotValid((EditText) LinearLayoutList.get(1).findViewById(R.id.answerET));
            else if ((j >= 3) && TextUtils.isEmpty(answer_three))
                isDataValid = Helper.DataNotValid((EditText) LinearLayoutList.get(2).findViewById(R.id.answerET));
            else if ((j >= 4) && TextUtils.isEmpty(answer_four))
                isDataValid = Helper.DataNotValid((EditText) LinearLayoutList.get(3).findViewById(R.id.answerET));
            else if ((j >= 5) && TextUtils.isEmpty(answer_five))
                isDataValid = Helper.DataNotValid((EditText) LinearLayoutList.get(4).findViewById(R.id.answerET));
            else if (j < 2) {
                Toast.makeText(activity, "Kindly provide atleast 2 options", Toast.LENGTH_SHORT).show();
                isDataValid = false;
            }
        } else {
            if (!((((PostActivity) activity).isimageadded || ((PostActivity) activity).isvideoadded || ((PostActivity) activity).isattachmentadded) && imageArrayList.size() > 0)) {
                if (TextUtils.isEmpty(question))
                    isDataValid = Helper.DataNotValid(writepostET);
            }
        }
        if (isDataValid && TextUtils.isEmpty(post_tag_id)) {
            Toast.makeText(activity, "Kindly select the Tag", Toast.LENGTH_SHORT).show();
            isDataValid = false;
        }
        if (isDataValid) {
            if (isPostEdit != 0) {

                newimageArrayList = new ArrayList<>();
                for (MediaFile file : imageArrayList) {
                    if (TextUtils.isEmpty(file.getId())) {
                        newimageArrayList.add(file);
                    }
                }
                Log.e("new Post frag", "imageArrayList size total: " + imageArrayList.size());
                imageArrayList.clear();
                imageArrayList.addAll(newimageArrayList);
                Log.e("new Post frag", "newimageArrayList size new files: " + imageArrayList.size());
                if (imageArrayList.size() == 0) {
                    imageArrayList = null;
                    fileArrayList.clear();
                }
            }

            if (((PostActivity) activity).isyoutubevideoattached) {
                imageArrayList = null;
                fileArrayList.clear();
            }

            if (imageArrayList != null) {
                if (imageArrayList.size() > 0) {
                    if (imageArrayList.get(0).getFile_type().equals(Const.IMAGE)) {
                        s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_FANWALL_IMAGES, activity, this, progressBar);
                        s3ImageUploading.execute(imageArrayList);
                    }
                    if (imageArrayList.get(0).getFile_type().equals(Const.PDF) ||
                            imageArrayList.get(0).getFile_type().equals(Const.DOC)
                            || imageArrayList.get(0).getFile_type().equals(Const.XLS)) {
                        s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_DOCUMENT, activity, this, progressBar);
                        s3ImageUploading.execute(imageArrayList);

                    } else if (imageArrayList.get(0).getFile_type().equals(Const.VIDEO)) {
                        s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_VIDEO, activity, this, progressBar);
                        s3ImageUploading.execute(imageArrayList);
                    }
                }
            } else if (isQuestion) {
                PostMCQ postMCQ = PostMCQ.newInstance();
                postMCQ.setUser_id((isPostEdit == 0 ? SharedPreference.getInstance().getLoggedInUser().getId() : post.getUser_id()));
                postMCQ.setQuestion(question);
                postMCQ.setAnswer_one(answer_one);
                postMCQ.setAnswer_two(answer_two);
                postMCQ.setAnswer_three(answer_three);
                postMCQ.setAnswer_four(answer_four);
                postMCQ.setAnswer_five(answer_five);
                postMCQ.setRight_answer(right_answer);
                if (isPostEdit == 0)
                    NetworkAPICall(API.API_POST_MCQ, true);
                else if (isPostEdit == 1)
                    NetworkAPICall(API.API_EDIT_MCQ_POST, true);

            } else {
                if (isPostEdit == 0)
                    NetworkAPICall(API.API_POST_NORMAL_VIDEO, true);
                else if (isPostEdit == 2)
                    NetworkAPICall(API.API_EDIT_NORMAL_POST, true);

            }
        }

    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        PostMCQ postMCQ = PostMCQ.getInstance();
        switch (apitype) {
            case API.API_POST_MCQ:
                return Ion.with(activity)
                        .load(API.API_POST_MCQ)
                        .setMultipartParameter(Const.USER_ID, postMCQ.getUser_id())
                        .setMultipartParameter(Const.QUESTION, postMCQ.getQuestion())
                        .setMultipartParameter(Const.ANSWER_ONE, postMCQ.getAnswer_one())
                        .setMultipartParameter(Const.ANSWER_TWO, postMCQ.getAnswer_two())
                        .setMultipartParameter(Const.ANSWER_THREE, postMCQ.getAnswer_three())
                        .setMultipartParameter(Const.ANSWER_FOUR, postMCQ.getAnswer_four())
                        .setMultipartParameter(Const.ANSWER_FIVE, postMCQ.getAnswer_five())
                        .setMultipartParameter(Const.RIGHT_ANSWER, postMCQ.getRight_answer())
                        .setMultipartParameter(Const.POST_TAG, post_tag_id)
                        .setMultipartParameter(Const.FILE, gson.toJson(fileArrayList));
            case API.API_EDIT_MCQ_POST:
                return Ion.with(activity)
                        .load(API.API_EDIT_MCQ_POST)
                        .setMultipartParameter(Const.USER_ID, postMCQ.getUser_id())
                        .setMultipartParameter(Const.POST_ID, (post == null ? "" : post.getId()))
                        .setMultipartParameter(Const.QUESTION, postMCQ.getQuestion())
                        .setMultipartParameter(Const.ANSWER_ONE, postMCQ.getAnswer_one())
                        .setMultipartParameter(Const.ANSWER_TWO, postMCQ.getAnswer_two())
                        .setMultipartParameter(Const.ANSWER_THREE, postMCQ.getAnswer_three())
                        .setMultipartParameter(Const.ANSWER_FOUR, postMCQ.getAnswer_four())
                        .setMultipartParameter(Const.ANSWER_FIVE, postMCQ.getAnswer_five())
                        .setMultipartParameter(Const.RIGHT_ANSWER, postMCQ.getRight_answer())
                        .setMultipartParameter(Const.POST_TAG, post_tag_id)
                        .setMultipartParameter(Const.FILE, gson.toJson(fileArrayList))
                        .setMultipartParameter(Const.DELETE_META, ((PostActivity) activity).deleted_meta_ids);
            case API.API_POST_NORMAL_VIDEO:
                return Ion.with(activity)
                        .load(API.API_POST_NORMAL_VIDEO)
                        .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                        .setMultipartParameter(Const.TEXT, question)
                        .setMultipartParameter(Const.POST_TYPE, (((PostActivity) activity).isyoutubevideoattached) ? Const.POST_TEXT_TYPE_YOUTUBE_TEXT : Const.POST_TEXT_TYPE_TEXT)
                        .setMultipartParameter(Const.POST_TAG, post_tag_id)
                        .setMultipartParameter(Const.FILE, gson.toJson(fileArrayList));
            case API.API_EDIT_NORMAL_POST:
                return Ion.with(activity)
                        .load(API.API_EDIT_NORMAL_POST)
                        .setMultipartParameter(Const.USER_ID, post.getUser_id())
                        .setMultipartParameter(Const.POST_ID, (post == null ? "" : post.getId()))
                        .setMultipartParameter(Const.TEXT, question)
                        .setMultipartParameter(Const.POST_TYPE, Const.POST_TEXT_TYPE_TEXT)
                        .setMultipartParameter(Const.POST_TAG, post_tag_id)
                        .setMultipartParameter(Const.FILE, gson.toJson(fileArrayList))
                        .setMultipartParameter(Const.DELETE_META, ((PostActivity) activity).deleted_meta_ids);
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonObject, String apitype) throws JSONException {
        fileArrayList = new ArrayList<>();
        switch (apitype) {
            case API.API_POST_MCQ:
            case API.API_POST_NORMAL_VIDEO:
                Log.e("New Post ", jsonObject.toString());
                if (jsonObject.optString(Const.STATUS).equals(Const.TRUE)) {
                    activity.finish();
                    FeedsActivity.IS_NEW_POST_ADDED = true;
                    imageArrayList = new ArrayList<>();
                } else {
                    this.ErrorCallBack(jsonObject.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_EDIT_NORMAL_POST:
            case API.API_EDIT_MCQ_POST:
                Log.e("Edit ", jsonObject.toString());
                if (jsonObject.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject singledatarow = jsonObject.getJSONObject(Const.DATA);
                    PostResponse response = gson.fromJson(singledatarow.toString(), PostResponse.class);
                    post.setPost_data(response.getPost_data());
                    SharedPreference.getInstance().setPost(post);
                    FeedsActivity.IS_POST_UPDATED = true;
                    imageArrayList = new ArrayList<>();
                    activity.finish();

                } else {
                    this.ErrorCallBack(jsonObject.getString(Const.MESSAGE), apitype);
                }
                break;
        }
    }

    @Override
    public void ErrorCallBack(String str, String apitype) {
        fileArrayList = new ArrayList<>();
        Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
    }

    public void selectVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Video"), Const.REQUEST_TAKE_GALLERY_VIDEO);
    }

    public void selectMultipleImages() {
        Intent intent1 = new Intent(activity, AlbumSelectActivity.class);
        intent1.putExtra(Constants.INTENT_EXTRA_LIMIT, 5 - imageArrayList.size());
        activity.startActivityForResult(intent1, Const.REQUEST_TAKE_GALLERY_IMAGE);
    }

    public void getImagePickerDialog(final Activity ctx, final String title, final String message) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(ctx);

        alertBuild
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Images", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        takeImageClass.getImagePickerDialog(activity, getString(R.string.app_name), getString(R.string.choose_image));
                    }
                })
                .setNegativeButton("Videos", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        if (!(((PostActivity) activity).isimageadded && imageArrayList.size() > 0))
                            selectVideo();
                        else
                            Toast.makeText(activity, "You cannot add other Media", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog dialog = alertBuild.create();
        dialog.show();
        int alertTitle = ctx.getResources().getIdentifier("alertTitle", "id", "android");
        ((TextView) dialog.findViewById(alertTitle)).setGravity(Gravity.CENTER);
        ((TextView) dialog.findViewById(alertTitle)).setGravity(Gravity.CENTER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Const.REQUEST_TAKE_GALLERY_IMAGE && resultCode == activity.RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
            Log.d("", "images : " + images.size());
            setupImages(images);

        } else if (requestCode == Const.REQUEST_TAKE_GALLERY_VIDEO && resultCode == activity.RESULT_OK && data != null) {
            Uri selectedFileURI = data.getData();
            setupVideo(GetFilePath.getPath(activity, selectedFileURI));

        } else if (requestCode == Const.REQUEST_TAKE_GALLERY_DOC && resultCode == activity.RESULT_OK && data != null) {
            Uri selectedFileURI = data.getData();
            setupDoc(selectedFileURI);
        } else if (requestCode == RC_TAKE_PHOTO && resultCode == activity.RESULT_OK) {
            Image img = new Image(101, "camera", file.getPath(), false);
            ArrayList<Image> imageArrayList = new ArrayList<>();
            imageArrayList.add(img);
            setupImages(imageArrayList);
        }
    }

    public void setupImages(ArrayList<Image> images) {
        int i = 0;
        for (Image img : images) {
            MediaFile mediaFile = new MediaFile();
            mediaFile.setImage(Helper.decodeSampledBitmap(img.path, 200, 200));
            mediaFile.setFile_type(Const.IMAGE);
            imageArrayList.add(mediaFile);
            i++;
        }
        InitImageAdapter();
    }

    public void setupVideo(String videopath) {
        MediaFile mediaFile = new MediaFile();
        mediaFile.setImage(ThumbnailUtils.createVideoThumbnail(videopath, MediaStore.Video.Thumbnails.MICRO_KIND));
        mediaFile.setFile_type(Const.VIDEO);
        mediaFile.setFile(videopath);
        imageArrayList.add(mediaFile);
        InitImageAdapter();
    }

    //Sushant
    public void setupYoutubeVideo(String videopath) {
        final MediaFile mediaFile = new MediaFile();
        mediaFile.setFile_type(Const.YOUTUBE_VIDEO);
        mediaFile.setFile(videopath);
        /*Ion.with(getActivity()).load(videopath).asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                if (e == null) {
                    mediaFile.setImage(result);

                }
                else
                {
                    Log.e("Youtube error","Error: ");
                    Toast.makeText(getContext(),"Url can't be recogonized",Toast.LENGTH_SHORT).show();
            }

            }
        });*/
        imageArrayList = new ArrayList<>();
        imageArrayList.add(mediaFile);
        InitImageAdapter();
    }

    public void setupDoc(Uri selectedURI) {
        MediaFile mediaFile = new MediaFile();
        String docpath = GetFilePath.getPath(activity, selectedURI);
        String arr[] = docpath.split("/");

        if (!(docpath.contains(getString(R.string.pdf_extension)) || docpath.contains(getString(R.string.doc_extension)) || docpath.contains(getString(R.string.xls_extension))))
            Toast.makeText(activity, R.string.file_format_error, Toast.LENGTH_SHORT).show();

        else {
            if (docpath.contains(getString(R.string.pdf_extension))) {
                mediaFile.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.pdf));
                mediaFile.setFile_type(Const.PDF);
            } else if (docpath.contains(getString(R.string.doc_extension))) {
                mediaFile.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.doc));
                mediaFile.setFile_type(Const.DOC);
            } else if (docpath.contains(getString(R.string.xls_extension))) {
                mediaFile.setImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.xls));
                mediaFile.setFile_type(Const.XLS);
            }

            mediaFile.setFile_name(arr[arr.length - 1]);
            mediaFile.setFile(docpath);
            imageArrayList.add(mediaFile);
            InitImageAdapter();
        }
    }

    @Override
    public void onS3UploadData(ArrayList<MediaFile> images) {
        if (images.size() > 0) {
            fileArrayList = new ArrayList<>();

            if (images.get(0).getFile_type().equals(Const.IMAGE)) {
                for (MediaFile str : images) {
                    PostFile file = new PostFile();
                    file.setFile_info(str.getFile_name());
                    file.setFile_type(str.getFile_type());
                    file.setLink(str.getFile());
                    fileArrayList.add(file);
                }
            } else if (images.get(0).getFile_type().equals(Const.VIDEO)) {
                PostFile pf = new PostFile();
                pf.setFile_info(images.get(0).getFile_name() + ".mp4");
                pf.setLink(pf.getFile_info());
                pf.setFile_type(Const.VIDEO);
                fileArrayList.add(pf);

            } else if (images.get(0).getFile_type().equals(Const.PDF) ||
                    images.get(0).getFile_type().equals(Const.DOC) ||
                    images.get(0).getFile_type().equals(Const.XLS)) {
                PostFile pf = new PostFile();
                pf.setFile_info(images.get(0).getFile_name());
                pf.setLink(images.get(0).getFile());
                pf.setFile_type(images.get(0).getFile_type());
                fileArrayList.add(pf);
            }

            if (isQuestion) {
                PostMCQ postMCQ = PostMCQ.newInstance();
                postMCQ.setUser_id((isPostEdit == 0 ? SharedPreference.getInstance().getLoggedInUser().getId() : post.getUser_id()));
                postMCQ.setQuestion(question);
                postMCQ.setAnswer_one(answer_one);
                postMCQ.setAnswer_two(answer_two);
                postMCQ.setAnswer_three(answer_three);
                postMCQ.setAnswer_four(answer_four);
                postMCQ.setAnswer_five(answer_five);
                postMCQ.setRight_answer(right_answer);

                if (isPostEdit == 0)
                    NetworkAPICall(API.API_POST_MCQ, true);
                else if (isPostEdit == 1)
                    NetworkAPICall(API.API_EDIT_MCQ_POST, true);

            } else {
                if (isPostEdit == 0)
                    NetworkAPICall(API.API_POST_NORMAL_VIDEO, true);
                else if (isPostEdit == 2)
                    NetworkAPICall(API.API_EDIT_NORMAL_POST, true);

            }

        }

    }

    @Override
    public void imagePath(String str) {
        Image img = new Image(101, "camera", str, false);
        ArrayList<Image> imageArrayList = new ArrayList<>();
        imageArrayList.add(img);
        setupImages(imageArrayList);
    }

        }
