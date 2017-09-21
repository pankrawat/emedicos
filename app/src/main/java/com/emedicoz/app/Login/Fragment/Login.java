package com.emedicoz.app.Login.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Login.Activity.LoginCatActivity;
import com.emedicoz.app.Login.Activity.SignInActivity;
import com.emedicoz.app.Model.User;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.DbAdapter;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.SharedPreference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends MainFragment implements View.OnClickListener {

    Activity activity;

    EditText memailET, mpasswordET, mDAMSET, mDAMSpasswordET;
    Button mlogInBtn;
    TextView mforgetpasswordTV;

    public String email, password, damstoken, damspass,
            socialType, socialToken,
            deviceId;

    ImageView mfacebookIV, mdamsIV, mgooglePlusIV, mlinkedInTV;
    User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deviceId = ((SignInActivity) activity).deviceId;
        Log.e("register", "deviceId: " + deviceId);

        memailET = (EditText) view.findViewById(R.id.emailTV);
        mpasswordET = (EditText) view.findViewById(R.id.passwordET);

        mfacebookIV = (ImageView) view.findViewById(R.id.fbIV);
        mgooglePlusIV = (ImageView) view.findViewById(R.id.gpIV);
        mlinkedInTV = (ImageView) view.findViewById(R.id.inIV);
        mdamsIV = (ImageView) view.findViewById(R.id.damsIV);

        mforgetpasswordTV = (TextView) view.findViewById(R.id.forgetpasswordTV);

        mlogInBtn = (Button) view.findViewById(R.id.loginBtn);

        mfacebookIV.setOnClickListener(this);
        mgooglePlusIV.setOnClickListener(this);
        mlinkedInTV.setOnClickListener(this);
        mlogInBtn.setOnClickListener(this);
        mforgetpasswordTV.setOnClickListener(this);
        mdamsIV.setOnClickListener(this);
    }

    public void CheckValidation() {
        email = Helper.GetText(memailET);
        password = Helper.GetText(mpasswordET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(email))
            isDataValid = Helper.DataNotValid(memailET);
        else if ((Patterns.EMAIL_ADDRESS.matcher(email).matches() != true))
            isDataValid = Helper.DataNotValid(memailET, 1);
        else if (TextUtils.isEmpty(password))
            isDataValid = Helper.DataNotValid(mpasswordET);

        if (isDataValid) {
            if (deviceId == null) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (deviceId == null || deviceId == "") {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }
            user.setEmail(email);
            user.setPassword(password);
            user.setIs_social(Const.SOCIAL_TYPE_FALSE);
            user.setSocial_type(socialType);
            user.setSocial_tokken(socialToken);
            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            SharedPreference.getInstance().setLoggedInUser(user);
            NetworkAPICall(API.API_USER_LOGIN_AUTHENTICATION, true);
        }
    }


    public void LogInTask(JSONObject object, String type) {
        try {
            switch (type) {
                case Const.SOCIAL_TYPE_FACEBOOK: //facebook picture url
                    user.setProfile_picture(
                            object.optJSONObject(Const.PICTURE).
                                    optJSONObject(Const.DATA).
                                    optString(Const.URL));
                    break;
                case Const.SOCIAL_TYPE_GMAIL://gmail image url
                    user.setProfile_picture(Const.IMGURL);
                    break;
                case Const.SOCIAL_TYPE_LINKEDIN://linkedin picture url)
                    user.setProfile_picture(Const.PICTUREURL);
                    break;
            }
            if (deviceId == null) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (deviceId == null || deviceId == "") {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }
            user.setIs_social(Const.SOCIAL_TYPE_TRUE);
            user.setSocial_tokken(object.getString(Const.ID));
            user.setEmail(object.getString(Const.EMAIL));
            user.setSocial_type(socialType);
            user.setName(object.getString(Const.NAME));
            user.setGender(object.getString(Const.GENDER));
            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            SharedPreference.getInstance().setLoggedInUser(user);
            NetworkAPICall(API.API_USER_LOGIN_AUTHENTICATION, true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        user = User.newInstance();
        switch (view.getId()) {
            case R.id.loginBtn:
                CheckValidation();
                break;
            case R.id.forgetpasswordTV:
                Intent intent = new Intent(activity, LoginCatActivity.class);
                intent.putExtra(Const.FRAG_TYPE, Const.FORGETPASSWORD);
                activity.startActivity(intent);
                break;
            case R.id.fbIV:
                socialType = Const.SOCIAL_TYPE_FACEBOOK;
                ((SignInActivity) activity).LoginMaster(socialType);
                break;
            case R.id.gpIV:
                socialType = Const.SOCIAL_TYPE_GMAIL;
                ((SignInActivity) activity).LoginMaster(socialType);
                break;
            case R.id.inIV:
                socialType = Const.SOCIAL_TYPE_LINKEDIN;
                ((SignInActivity) activity).LoginMaster(socialType);
                break;
            case R.id.damsIV:
                getDAMSLoginDialog(activity);
                break;
        }

    }

    public void CheckDAMSLoginValidation() {
        damstoken = Helper.GetText(mDAMSET);
        damspass = Helper.GetText(mDAMSpasswordET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(damstoken))
            isDataValid = Helper.DataNotValid(mDAMSET);

        else if (TextUtils.isEmpty(damspass))
            isDataValid = Helper.DataNotValid(mDAMSpasswordET);

        if (isDataValid) {
            if (deviceId == null) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (deviceId == null || deviceId == "") {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }
            user.setDams_username(damstoken);
            user.setDams_password(damspass);
            user.setIs_social(Const.SOCIAL_TYPE_FALSE);
            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            SharedPreference.getInstance().setLoggedInUser(user);
            NetworkAPICall(API.API_USER_DAMS_LOGIN_AUTHENTICATION, true);
        }
    }

    public void getDAMSLoginDialog(final Activity ctx) {
// custom dialog
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.layout_dams_login);
        // set the custom dialog components - text, image and button
        Button mDAMSLoginbtn = (Button) dialog.findViewById(R.id.loginBtn);
        mDAMSET = (EditText) dialog.findViewById(R.id.damstokenET);
        mDAMSpasswordET = (EditText) dialog.findViewById(R.id.damspassET);
        // if button is clicked, close the custom dialog
        mDAMSLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckDAMSLoginValidation();
            }
        });

        dialog.show();
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        User user = User.getInstance();
        switch (apitype) {
            case API.API_USER_LOGIN_AUTHENTICATION:
                return Ion.with(activity)
                        .load(API.API_USER_LOGIN_AUTHENTICATION)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.EMAIL, user.getEmail())
                        .setMultipartParameter(Const.PASSWORD, user.getPassword())
                        .setMultipartParameter(Const.IS_SOCIAL, user.getIs_social())
                        .setMultipartParameter(Const.SOCIAL_TYPE, user.getSocial_type())
                        .setMultipartParameter(Const.SOCIAL_TOKEN, user.getSocial_tokken())
                        .setMultipartParameter(Const.DEVICE_TYPE, user.getDevice_type())
                        .setMultipartParameter(Const.DEVICE_TOKEN, user.getDevice_tokken());
            case API.API_USER_DAMS_LOGIN_AUTHENTICATION:
                return Ion.with(activity)
                        .load(API.API_USER_DAMS_LOGIN_AUTHENTICATION)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.DAMS_USERNAME, user.getDams_username())
                        .setMultipartParameter(Const.DAMS_PASSWORD, user.getDams_password())
                        .setMultipartParameter(Const.IS_SOCIAL, user.getIs_social())
                        .setMultipartParameter(Const.DEVICE_TYPE, user.getDevice_type())
                        .setMultipartParameter(Const.DEVICE_TOKEN, user.getDevice_tokken());
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {

        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonObject, String apitype) throws JSONException {
        Gson gson = new Gson();
        switch (apitype) {

            case API.API_USER_LOGIN_AUTHENTICATION:
            case API.API_USER_DAMS_LOGIN_AUTHENTICATION:
                try {
                    Log.e("String Login", jsonObject.toString());

                    if (jsonObject.optString(Const.STATUS).equals(Const.TRUE)) {
                        SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);

                        DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_COLORCODE);
                        JSONObject dataJsonObject = jsonObject.getJSONObject(Const.DATA);
                        SharedPreference.getInstance().putBoolean(Const.IS_USER_LOGGED_IN, true);

                        User userprofile = gson.fromJson(dataJsonObject.toString(), User.class);
                        SharedPreference.getInstance().setLoggedInUser(userprofile);

                        if (userprofile.getIs_course_register().equals("0")) {
                            SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, false);
                            Helper.GoToRegistrationActivity(activity, Const.REGISTRATION, Const.REGISTRATION);
                        } else if (userprofile.getIs_course_register().equals("1")) {
                            Helper.GoToFeedsActivity(activity);
                            SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                        }

                    } else {
                        SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);

                        this.ErrorCallBack(jsonObject.getString(Const.MESSAGE), apitype);
                    }
                } catch (Exception ex) {
                    this.ErrorCallBack(ex.getMessage() + " : " + ex.getLocalizedMessage(), apitype);
                    ex.printStackTrace();
                }
                break;
        }

    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        switch (apitype) {
            case API.API_USER_LOGIN_AUTHENTICATION:
            case API.API_USER_DAMS_LOGIN_AUTHENTICATION:
                Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
