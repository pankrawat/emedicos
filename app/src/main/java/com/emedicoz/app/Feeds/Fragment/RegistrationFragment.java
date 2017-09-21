package com.emedicoz.app.Feeds.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.Feeds.Activity.ProfileActivity;
import com.emedicoz.app.Model.MediaFile;
import com.emedicoz.app.Model.Registration;
import com.emedicoz.app.Model.User;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.Registration.StreamResponse;
import com.emedicoz.app.Utils.AmazonUpload.AmazonCallBack;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RegistrationFragment extends MainFragment implements AmazonCallBack, TakeImageClass.imagefromcropper {
    private StreamResponse stream;

    private TakeImageClass takeImageClass;

    Spinner streamSpinner;
    Button nextBtn;
    Bitmap bitmap;

    s3ImageUploading s3ImageUploading;
    ArrayList<MediaFile> mediafile;

    ImageView profileImage, profileImageText, editimageIV;

    FrameLayout circleImageFL;

    TextView emailTV, phoneTV;
    TextView substreamTV;
    TextView specialisationTV;
    TextView IntcoursesTV;

    EditText damsidET;

    EditText mDAMSpasswordET;
    EditText mDAMSET;
    Dialog dialog;

    CheckBox damsidCB;
    Button damsidBtn;
    TextView damsidTV;

    String damstoken;
    String damspassword;

    EditText nameET;

    Activity activity;

    ArrayList<String> streamList;

    ArrayList<StreamResponse> streamResponseListfrag;

    ArrayAdapter<String> streamAdapter;
    User user;
    User userMain;
    Registration registration;

    String name, profilepicture, regType;

    boolean isDatachanged = false;
    boolean isStreamchanged = false;

    public RegistrationFragment() {
    }

    public static RegistrationFragment newInstance(String regType) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(Const.TYPE, regType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            regType = getArguments().getString(Const.TYPE);
        }
        activity = getActivity();
        userMain = SharedPreference.getInstance().getLoggedInUser();
        User user1 = SharedPreference.getInstance().getLoggedInUser();
        user = User.newInstance();
        user = User.copyInstance(user1);

        if (regType.equals(Const.REGISTRATION)) {
            registration = new Registration();
            user.setUser_registration_info(registration);
        }

        streamResponseListfrag = new ArrayList<>();
        streamResponseListfrag = ((PostActivity) activity).streamResponseList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        streamResponseListfrag.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (regType.equals(Const.PROFILE)) {
            activity.setTitle(getString(R.string.profile));
        } else {
            activity.setTitle(getString(R.string.registration));
        }

        profileImage = (ImageView) view.findViewById(R.id.cirle_image);
        profileImageText = (ImageView) view.findViewById(R.id.cirle_imageText);
        editimageIV = (ImageView) view.findViewById(R.id.editimage);

        circleImageFL = (FrameLayout) view.findViewById(R.id.circle_image_FL);

        streamSpinner = (Spinner) view.findViewById(R.id.streamSpinner);
        nextBtn = (Button) view.findViewById(R.id.nextBtn);
        damsidBtn = (Button) view.findViewById(R.id.verifyBtn);

        nameET = (EditText) view.findViewById(R.id.nameET);
        emailTV = (TextView) view.findViewById(R.id.emailTV);
        substreamTV = (TextView) view.findViewById(R.id.substreamTV);
        IntcoursesTV = (TextView) view.findViewById(R.id.IntcoursesTV);
        specialisationTV = (TextView) view.findViewById(R.id.specialisationTV);
        phoneTV = (TextView) view.findViewById(R.id.phonenumberTV);
        damsidTV = (TextView) view.findViewById(R.id.damsidTV);

        damsidET = (EditText) view.findViewById(R.id.damsidET);
        damsidCB = (CheckBox) view.findViewById(R.id.damsidCB);

        takeImageClass = new TakeImageClass(activity, this);
        damsidCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    getDAMSLoginDialog(activity);
                }

            }
        });

    }

    public void getDAMSLoginDialog(final Activity ctx) {
// custom dialog
        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_dams_login);
        // set the custom dialog components - text, image and button
        Button mDAMSLoginbtn = (Button) dialog.findViewById(R.id.loginBtn);
        mDAMSET = (EditText) dialog.findViewById(R.id.damstokenET);
        mDAMSpasswordET = (EditText) dialog.findViewById(R.id.damspassET);
        mDAMSLoginbtn.setText(activity.getString(R.string.verfiy));
        // if button is clicked, close the custom dialog
        mDAMSLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckDAMSLoginValidation();
            }
        });

        dialog.show();
    }

    public void CheckDAMSLoginValidation() {
        damstoken = Helper.GetText(mDAMSET);
        damspassword = Helper.GetText(mDAMSpasswordET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(damstoken))
            isDataValid = Helper.DataNotValid(mDAMSET);

        else if (TextUtils.isEmpty(damspassword))
            isDataValid = Helper.DataNotValid(mDAMSpasswordET);

        if (isDataValid) {
            user.setDams_username(damstoken);
            user.setDams_password(damspassword);
            NetworkAPICall(API.API_UPDATE_DAMS_TOKEN, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (regType.equals(Const.PROFILE)) {
            if (user != null) {
                registration = user.getUser_registration_info();
                InitViews();
                if (streamResponseListfrag != null && streamResponseListfrag.size() > 0)
                    InitStreamList();
                else
                    NetworkAPICall(API.API_GET_COURSE_LIST_ZERO_LEVEL, true);
            } else {
                NetworkAPICall(API.API_GET_USER, true);
            }
        } else if (regType.equals(Const.REGISTRATION)) {
            InitViews();
            isDatachanged = true;
            isStreamchanged = true;
            if (streamResponseListfrag != null && streamResponseListfrag.size() > 0)
                InitStreamList();
            else
                NetworkAPICall(API.API_GET_COURSE_LIST_ZERO_LEVEL, true);
        }
    }

    public void InitViews() {
        Log.e("String reg", new Gson().toJson(user).toString());

        specialisationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateUserPreference();
                if (TextUtils.isEmpty(registration.getMaster_id_level_one_name())) {
                    Toast.makeText(activity, R.string.please_select_the_substream, Toast.LENGTH_SHORT).show();
                } else {
                    if (getFragmentManager().findFragmentByTag(Const.SPCIALISATIONFRAGMENT) == null)
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, specializationFragment.newInstance(regType)).addToBackStack(Const.SPCIALISATIONFRAGMENT).commit();
                    else
                        getFragmentManager().popBackStack(Const.SPCIALISATIONFRAGMENT, 0);
                }
            }
        });
        substreamTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateUserPreference();
                if (TextUtils.isEmpty(registration.getMaster_id_name())) {
                    Toast.makeText(activity, R.string.please_select_the_stream, Toast.LENGTH_SHORT).show();
                } else {
                    if (getFragmentManager().findFragmentByTag(Const.SUBSTREAMFRAGMENT) == null)
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, subStreamFragment.newInstance(regType)).addToBackStack(Const.SUBSTREAMFRAGMENT).commit();
                    else
                        getFragmentManager().popBackStack(Const.SUBSTREAMFRAGMENT, 0);
                }
            }
        });
        IntcoursesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateUserPreference();
                if (TextUtils.isEmpty(registration.getMaster_id_name())) {
                    Toast.makeText(activity, R.string.please_select_the_stream, Toast.LENGTH_SHORT).show();
                } else {
                    if (getFragmentManager().findFragmentByTag(Const.INTERESTEDCOURSESFRAGMENT) == null)
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, CourseInterestedInFragment.newInstance(regType)).addToBackStack(Const.INTERESTEDCOURSESFRAGMENT).commit();
                    else
                        getFragmentManager().popBackStack(Const.INTERESTEDCOURSESFRAGMENT, 0);
                }
            }
        });
        streamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    stream = streamResponseListfrag.get(i - 1);
                    if (!registration.getMaster_id().equals(stream.getId())) {
                        registration.setMaster_id(stream.getId());
                        registration.setMaster_id_name(stream.getText_name());
                        registration.setMaster_id_level_one("");
                        registration.setMaster_id_level_one_name("");
                        registration.setMaster_id_level_two("");
                        registration.setMaster_id_level_two_name("");
                        specialisationTV.setText("");
                        substreamTV.setText("");
                        ((PostActivity) activity).substreamResponseList = new ArrayList<>();
                    }
                } else {
                    stream = null;
                    registration.setMaster_id("");
                    registration.setMaster_id_name("");
                    registration.setMaster_id_level_one("");
                    registration.setMaster_id_level_one_name("");
                    registration.setMaster_id_level_two("");
                    registration.setMaster_id_level_two_name("");
                    specialisationTV.setText("");
                    substreamTV.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckValidation();
            }
        });

        if (!ispictureChanged) {
            if (!TextUtils.isEmpty(user.getProfile_picture())) {
                profileImage.setVisibility(View.VISIBLE);
                profileImageText.setVisibility(View.GONE);
                Ion.with(this).load(user.getProfile_picture())
                        .asBitmap()
                        .setCallback(new FutureCallback<Bitmap>() {
                            @Override
                            public void onCompleted(Exception e, Bitmap result) {
                                if (result != null)
                                    profileImage.setImageBitmap(result);
                                else
                                    profileImage.setImageResource(R.mipmap.default_pic);
                            }
                        });
            } else {

                Drawable dr = Helper.GetDrawable(user.getName(), activity, user.getId());
                if (dr != null) {
                    profileImage.setVisibility(View.GONE);
                    profileImageText.setVisibility(View.VISIBLE);
                    profileImageText.setImageDrawable(dr);
                } else {
                    profileImage.setVisibility(View.VISIBLE);
                    profileImageText.setVisibility(View.GONE);
                    profileImage.setImageResource(R.mipmap.default_pic);
                }
            }
        }

        nameET.setText(user.getName());
        emailTV.setText(user.getEmail());
        phoneTV.setText(user.getMobile());

        if (TextUtils.isEmpty(user.getDams_tokken())) {
            damsidCB.setVisibility(View.VISIBLE);
            damsidTV.setVisibility(View.GONE);
        } else {
            damsidCB.setVisibility(View.GONE);
            damsidTV.setVisibility(View.VISIBLE);
            damsidTV.setText(user.getDams_tokken());
        }


        substreamTV.setText(registration.getMaster_id_level_one_name());

        if (!(TextUtils.isEmpty(registration.getOptional_text())) &&
                (TextUtils.isEmpty(registration.getMaster_id_level_two()) || registration.getMaster_id_level_two().equals("0")))
            specialisationTV.setText(registration.getOptional_text());
        else
            specialisationTV.setText(registration.getMaster_id_level_two_name());

        IntcoursesTV.setText(registration.getInterested_course_text());

        editimageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImageClass.getImagePickerDialog(activity, getString(R.string.upload_profile_picture), getString(R.string.choose_image));
            }
        });
        circleImageFL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImageClass.getImagePickerDialog(activity, getString(R.string.upload_profile_picture), getString(R.string.choose_image));
            }
        });
    }

    public void CheckValidation() {
        name = Helper.GetText(nameET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(name))
            isDataValid = Helper.DataNotValid(nameET);
        else if (streamSpinner.getCount() <= 0) {
            isDataValid = false;
            Toast.makeText(activity, "Something went wrong kindly close the app and open it again.", Toast.LENGTH_SHORT).show();
        } else if (streamSpinner.getSelectedItem().equals(getString(R.string.select_option))) {
            isDataValid = false;
            Toast.makeText(activity, R.string.please_select_the_stream, Toast.LENGTH_SHORT).show();
        } else if (registration.getMaster_id_level_one_name() == "" || registration.getMaster_id_level_one_name() == null) {
            isDataValid = false;
            Toast.makeText(activity, R.string.please_select_the_substream, Toast.LENGTH_SHORT).show();
        } else if ((registration.getMaster_id_level_two_name() == "" || registration.getMaster_id_level_two_name() == null) &&
                (registration.getOptional_text() == null || registration.getOptional_text() == "")) {
            Toast.makeText(activity, R.string.please_select_the_specialisation, Toast.LENGTH_SHORT).show();
            isDataValid = false;
        }

        if (isDataValid) {
            if (!name.equals(userMain.getName()))
                isDatachanged = true;
            if (regType.equals(Const.PROFILE)) {
                if (stream != null) {
                    if (!stream.getId().equals(userMain.getUser_registration_info().getMaster_id())) {
                        isDatachanged = true;
                        SharedPreference.getInstance().putBoolean(Const.IS_PROFILE_CHANGED, true);
                    }
                }
                if (!registration.getMaster_id_level_one().equals(userMain.getUser_registration_info().getMaster_id_level_one())) {
                    isDatachanged = true;
                }
                if (!registration.getMaster_id_level_two().equals(userMain.getUser_registration_info().getMaster_id_level_two()) ||
                        !registration.getOptional_text().equals(userMain.getUser_registration_info().getOptional_text())) {
                    isDatachanged = true;
                }
                if (!registration.getInterested_course().equals(userMain.getUser_registration_info().getInterested_course())) {
                    isDatachanged = true;
                }
            }
            UpdateUserPreference();
            if (isDatachanged) {
                NetworkAPICall(API.API_STREAM_REGISTRATION, true);
            } else {
                Toast.makeText(activity, R.string.no_data_changed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void UpdateUserPreference() {
        if (profilepicture != null)
            registration.setProfilepicture(profilepicture);
        else registration.setProfilepicture(user.getProfile_picture());
        registration.setName(name);
        registration.setUser_id(user.getId());
        user.setUser_registration_info(registration);
    }

    public void InitStreamList() {
        streamList = new ArrayList<String>();
        streamList.add(getString(R.string.select_option));
        int i = 0, pos = 0;
        while (i < streamResponseListfrag.size()) {

            streamList.add(streamResponseListfrag.get(i).getText_name());
            if (user.getUser_registration_info().getMaster_id() != null) {
                if (user.getUser_registration_info().getMaster_id().equals(streamResponseListfrag.get(i).getId())) {
                    stream = streamResponseListfrag.get(i);
                    pos = i + 1;
                }
            }
            i++;
        }
        streamAdapter = new ArrayAdapter<String>(getContext(), R.layout.single_row_spinner_item, streamList);
        streamSpinner.setAdapter(streamAdapter);
        if (stream != null) {
            streamSpinner.setSelection(pos);
        }
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_STREAM_REGISTRATION:
                return Ion.with(this)
                        .load(API.API_STREAM_REGISTRATION)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.USER_ID, registration.getUser_id())
                        .setMultipartParameter(Const.MASTER_ID, registration.getMaster_id())
                        .setMultipartParameter(Const.MASTER_ID_LEVEL_ONE, registration.getMaster_id_level_one())
                        .setMultipartParameter(Const.MASTER_ID_LEVEL_TWO, registration.getMaster_id_level_two())
                        .setMultipartParameter(Const.OPTIONAL_TEXT, registration.getOptional_text())
                        .setMultipartParameter(Const.INTERESTED_COURSE, registration.getInterested_course())
                        .setMultipartParameter(Const.NAME, registration.getName())
                        .setMultipartParameter(Const.PROFILE_PICTURE, registration.getProfilepicture());
            case API.API_UPDATE_DAMS_TOKEN:
                return Ion.with(this)
                        .load(API.API_UPDATE_DAMS_TOKEN)
                        .setTimeout(10 * 1000)
                        .setMultipartParameter(Const.USER_ID, user.getId())
                        .setMultipartParameter(Const.DAMS_USERNAME, user.getDams_username())
                        .setMultipartParameter(Const.DAMS_PASSWORD, user.getDams_password());
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        switch (apitype) {
            case API.API_GET_USER:
                return Ion.with(this)
                        .load(API.API_GET_USER + SharedPreference.getInstance().getLoggedInUser().getId())
                        .setTimeout(15 * 1000);
            case API.API_GET_COURSE_LIST_ZERO_LEVEL:
                return Ion.with(this)
                        .load(API.API_GET_COURSE_LIST_ZERO_LEVEL)
                        .setTimeout(15 * 1000);
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException {
        Gson gson = new Gson();
        JSONObject data;
        User user1;
        if (jsonstring.optString(Const.STATUS).equals(Const.TRUE)) {
            switch (apitype) {
                case API.API_GET_USER:
                    data = jsonstring.getJSONObject(Const.DATA);
                    user1 = gson.fromJson(data.toString(), User.class);
                    user = User.newInstance();
                    user = User.copyInstance(user1);
                    registration = user.getUser_registration_info();
                    InitViews();
                    if (streamResponseListfrag.size() > 0) InitStreamList();
                    else
                        NetworkAPICall(API.API_GET_COURSE_LIST_ZERO_LEVEL, true);
                    break;
                case API.API_UPDATE_DAMS_TOKEN:
                    if (dialog.isShowing()) dialog.dismiss();

                    data = jsonstring.getJSONObject(Const.DATA);
                    user1 = gson.fromJson(data.toString(), User.class);
                    user = User.newInstance();
                    user = User.copyInstance(user1);
                    registration = (user.getUser_registration_info() == null ? new Registration() : user.getUser_registration_info());

                    user.setUser_registration_info(registration);
                    SharedPreference.getInstance().setLoggedInUser(user);
                    InitViews();

                    if (streamResponseListfrag.size() > 0) InitStreamList();
                    else
                        NetworkAPICall(API.API_GET_COURSE_LIST_ZERO_LEVEL, true);
                    break;
                case API.API_GET_COURSE_LIST_ZERO_LEVEL:
                    JSONArray dataarray = jsonstring.getJSONArray(Const.DATA);
                    if (dataarray.length() > 0) {
                        streamResponseListfrag = new ArrayList<>();
                        int i = 0;
                        while (i < dataarray.length()) {
                            JSONObject singledatarow = dataarray.getJSONObject(i);
                            StreamResponse response = gson.fromJson(singledatarow.toString(), StreamResponse.class);
                            if (regType.equals(Const.REGISTRATION)) {
                                if (response.getVisibilty().equals("0"))
                                    streamResponseListfrag.add(response);
                            } else
                                streamResponseListfrag.add(response);

                            i++;
                        }
                        ((PostActivity) activity).streamResponseList = streamResponseListfrag;
                        InitStreamList();
                    } else {
                        this.ErrorCallBack(jsonstring.getString(Const.MESSAGE), apitype);
                    }
                    break;
                case API.API_STREAM_REGISTRATION:
                    if (registration.getProfilepicture() != null)
                        user.setProfile_picture(registration.getProfilepicture());
                    user.setName(registration.getName());
                    user.setUser_registration_info(registration);
                    SharedPreference.getInstance().setLoggedInUser(user);
                    userMain = SharedPreference.getInstance().getLoggedInUser();

                    ProfileActivity.IS_PROFILE_UPDATED = true;
                    this.ErrorCallBack(jsonstring.optString(Const.MESSAGE), apitype);
                    isDatachanged = false;
                    if (regType.equals(Const.REGISTRATION))
                        Helper.GoToFeedsActivity(activity);
                    else activity.finish();
                    break;
            }
        } else {
            this.ErrorCallBack(jsonstring.getString(Const.MESSAGE), apitype);
        }
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        takeImageClass.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onS3UploadData(ArrayList<MediaFile> images) {
        if (images.size() > 0) {
            profilepicture = images.get(0).getFile();
        }
    }

    boolean ispictureChanged = false;

    public void imagePath(String str) {
        if (str != null) {
            isDatachanged = true;
            bitmap = BitmapFactory.decodeFile(str);
//            user.setProfile_picture(str);
            if (bitmap != null) {
                profileImage.setImageBitmap(bitmap);
                ispictureChanged = true;
                profileImage.setVisibility(View.VISIBLE);
                profileImageText.setVisibility(View.GONE);
                mediafile = new ArrayList<>();
                MediaFile mf = new MediaFile();
                mf.setFile_type(Const.IMAGE);
                mf.setImage(bitmap);
                mediafile.add(mf);
                s3ImageUploading = new s3ImageUploading(Const.AMAZON_S3_BUCKET_NAME_PROFILE_IMAGES, activity, this, null);
                s3ImageUploading.execute(mediafile);
            }
        }
    }
}
