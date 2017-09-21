package com.emedicoz.app.Feeds.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.emedicoz.app.Common.BaseABNavActivity;
import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Model.HelpQuery;
import com.emedicoz.app.Model.MediaFile;
import com.emedicoz.app.Model.PostFile;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.AmazonUpload.AmazonCallBack;
import com.emedicoz.app.Utils.AmazonUpload.s3ImageUploading;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.SharedPreference;
import com.emedicoz.app.Utils.TakeImageClass;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HelpSupportFragment extends MainFragment implements TakeImageClass.imagefromcropper, AmazonCallBack {

    Activity activity;

    EditText titleET, descriptionET;
    Button submitbtn;

    ImageView imageIV;
    ImageView deleteIV;
    RelativeLayout imageRL;

    LinearLayout selectionoptionLL;
    ArrayList<RadioButton> substreamViewList;
    RadioGroup optionRG;
    String[] selectionList = new String[]{"General", "Installation", "LMS issue", "Others", "None"};
    String title, description;
    String selectedoption = "";
    HelpQuery helpQuery;

    private LinearLayout addimageLL;
    MediaFile mediaFile;
    PostFile postFile;
    boolean isImageAdded = false;

    Bitmap bitmap;
    private TakeImageClass takeImageClass;
    s3ImageUploading s3ImageUploading;


    public HelpSupportFragment() {
    }

    public static HelpSupportFragment newInstance() {
        HelpSupportFragment fragment = new HelpSupportFragment();

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
        return inflater.inflate(R.layout.fragment_help_support, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleET = (EditText) view.findViewById(R.id.addtitleET);
        descriptionET = (EditText) view.findViewById(R.id.adddescriptionET);
        addimageLL = (LinearLayout) view.findViewById(R.id.addimageLL);
        submitbtn = (Button) view.findViewById(R.id.submitBtn);
        imageIV = (ImageView) view.findViewById(R.id.imageIV);
        deleteIV = (ImageView) view.findViewById(R.id.deleteIV);

        selectionoptionLL = (LinearLayout) view.findViewById(R.id.selectionoptionLL);
        imageRL = (RelativeLayout) view.findViewById(R.id.imageRL);

        optionRG = (RadioGroup) view.findViewById(R.id.optionRG);
        postFile = new PostFile();
        mediaFile = new MediaFile();

        addViewtoSelectionOpt();

        deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaFile = new MediaFile();
                imageRL.setVisibility(View.GONE);
            }
        });
        takeImageClass = new TakeImageClass(activity, this);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckValidation();
            }
        });
        addimageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImageClass.getImagePickerDialog(activity, "Upload Profile Picture", getString(R.string.choose_image));
            }
        });
    }

    public void CheckValidation() {
        int radioButtonID = optionRG.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) optionRG.findViewById(radioButtonID);
        selectedoption = (String) radioButton.getText();

        title = Helper.GetText(titleET);
        description = Helper.GetText(descriptionET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(title))
            isDataValid = Helper.DataNotValid(titleET);
        else if (TextUtils.isEmpty(description))
            isDataValid = Helper.DataNotValid(descriptionET);

        if (isDataValid) {
            if (!selectedoption.equals("")) {
                helpQuery = HelpQuery.newInstance();
                helpQuery.setUser_id(SharedPreference.getInstance().getLoggedInUser().getId());
                helpQuery.setCategory(selectedoption);
                helpQuery.setTitle(title);
                helpQuery.setDescription(description);

                if (isImageAdded) {
                    ArrayList<MediaFile> MFArray = new ArrayList<>();
                    MFArray.add(mediaFile);
                    s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_FEEDBACK, activity, this, null);
                    s3ImageUploading.execute(MFArray);
                } else
                    NetworkAPICall(API.API_SUBMIT_QUERY, true);
            } else
                Toast.makeText(activity, "Kindly select any on option above", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_SUBMIT_QUERY:
                helpQuery = HelpQuery.getInstance();
                return Ion.with(activity)
                        .load(API.API_SUBMIT_QUERY)
                        .setTimeout(45 * 1000)
                        .setMultipartParameter(Const.USER_ID, helpQuery.getUser_id())
                        .setMultipartParameter(Const.TITLE, helpQuery.getTitle())
                        .setMultipartParameter(Const.DESCRIPTION, helpQuery.getDescription())
                        .setMultipartParameter(Const.CATEGORY, helpQuery.getCategory())
                        .setMultipartParameter(Const.FILE, postFile.getLink());
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonobject, String apitype) throws JSONException {
        switch (apitype) {
            case API.API_SUBMIT_QUERY:
                if (jsonobject.optString(Const.STATUS).equals(Const.TRUE)) {
                    ErrorCallBack(jsonobject.optString(Const.MESSAGE), API.API_SUBMIT_QUERY);
                    ClearViews();
                } else
                    ErrorCallBack(jsonobject.optString(Const.MESSAGE), API.API_SUBMIT_QUERY);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void ClearViews() {
        titleET.setText("");
        descriptionET.setText("");
        title = description = "";
        selectedoption = "";
        imageRL.setVisibility(View.GONE);
        imageIV.setImageResource(R.drawable.splashbg);
        //going back to feeds page and changing the title and color of Tabs
        ((BaseABNavActivity) activity).toolbartitleTV.setText(getString(R.string.app_name));
        ((BaseABNavActivity) activity).ChangeTabColor(Const.FEEDS);
        ((BaseABNavActivity) activity).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FeedsFragment.newInstance()).addToBackStack(Const.FEEDS).commit();
    }

    @Override
    public void ErrorCallBack(String object, String apitype) {
        Toast.makeText(activity, object, Toast.LENGTH_SHORT).show();
    }

    public void addViewtoSelectionOpt() {
        substreamViewList = new ArrayList<RadioButton>();
        optionRG.removeAllViews();
        Log.e("My RG", String.valueOf(optionRG.getChildCount()));
        int i = 0;
        while (i < selectionList.length) {
            RadioButton rb = new RadioButton(activity);
            rb.setText(selectionList[i]);
            rb.setTag(selectionList[i]);
            optionRG.addView(rb);
            substreamViewList.add(rb);
            i++;
            if (i == selectionList.length) {
                rb.setChecked(true);
                selectedoption = (String) rb.getText();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        takeImageClass.onActivityResult(requestCode, resultCode, data);
    }

    public void InitImageView(MediaFile image) {
        imageIV.setImageBitmap(image.getImage());
        imageIV.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageRL.setVisibility(View.VISIBLE);
    }

    @Override
    public void imagePath(String str) {
        if (str != null) {
            bitmap = BitmapFactory.decodeFile(str);
            if (bitmap != null) {
                isImageAdded = true;
                mediaFile = new MediaFile();
                mediaFile.setFile_type(Const.IMAGE);
                mediaFile.setImage(bitmap);
                mediaFile.setFile(str);
                InitImageView(mediaFile);
            }
        }
    }

    @Override
    public void onS3UploadData(ArrayList<MediaFile> images) {
        if (images.size() > 0) {
            for (MediaFile media : images) {
                postFile = new PostFile();
                postFile.setLink(media.getFile());
                Log.e("Tag", new Gson().toJson(postFile));
                NetworkAPICall(API.API_SUBMIT_QUERY, true);
            }
        }
    }
}
