package com.emedicoz.app.Login.Fragment;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Common.MainFragment;
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

public class SignUp extends MainFragment implements View.OnClickListener {

    Activity activity;
    EditText mnameET, memailET, mphonenumberET, mpasswordET, mdamsidET, mreferralET, mDAMSET, mDAMSpasswordET;
    Button msignUpBtn;
    ImageView mfacebookIV, mgooglePlusIV, mlinkedInTV,mdamsIV;
    TextView mloginTV;
    CheckBox mdamsuserCB;
    String name, email, phone, password,damstoken, damspass,
            damsid, referral,
            socialType, socialToken,
            deviceId = "";
    Boolean isDamsUser;

    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        deviceId = ((SignInActivity) activity).deviceId;
        Log.e("register", "deviceId: " + deviceId);
        mnameET = (EditText) view.findViewById(R.id.nameTV);
        memailET = (EditText) view.findViewById(R.id.emailTV);
        mphonenumberET = (EditText) view.findViewById(R.id.phonenumberTV);
        mpasswordET = (EditText) view.findViewById(R.id.passwordET);
        mdamsidET = (EditText) view.findViewById(R.id.damsidET);
        mreferralET = (EditText) view.findViewById(R.id.referralET);

        mdamsIV = (ImageView) view.findViewById(R.id.damsIV);

        msignUpBtn = (Button) view.findViewById(R.id.signupBtn);

        mdamsuserCB = (CheckBox) view.findViewById(R.id.damsidCB);

        mloginTV = (TextView) view.findViewById(R.id.loginTV);

        mfacebookIV = (ImageView) view.findViewById(R.id.fbIV);
        mgooglePlusIV = (ImageView) view.findViewById(R.id.gpIV);
        mlinkedInTV = (ImageView) view.findViewById(R.id.inIV);

        msignUpBtn.setOnClickListener(this);
        mloginTV.setOnClickListener(this);
        mfacebookIV.setOnClickListener(this);
        mgooglePlusIV.setOnClickListener(this);
        mlinkedInTV.setOnClickListener(this);
        mdamsIV.setOnClickListener(this);

        mdamsuserCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mdamsidET.setEnabled(b);
                if (!mdamsuserCB.isChecked()) {
                    if (mdamsidET.getError() != null) mdamsidET.setError(null);
                }
                if (mdamsuserCB.isChecked()) mdamsidET.setVisibility(View.VISIBLE);
                else mdamsidET.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        user = User.newInstance();

        switch (view.getId()) {
            case R.id.signupBtn:
                CheckValidation();
                break;
            case R.id.loginTV:
                ((SignInActivity) activity).viewPager.setCurrentItem(1, true);
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

    public void CheckValidation() {
        name = Helper.GetText(mnameET);
        email = Helper.GetText(memailET);
        phone = Helper.GetText(mphonenumberET);
        password = Helper.GetText(mpasswordET);
        damsid = Helper.GetText(mdamsidET);
        referral = Helper.GetText(mreferralET);
        isDamsUser = mdamsuserCB.isChecked();

        boolean isDataValid = true;

        if (TextUtils.isEmpty(name))
            isDataValid = Helper.DataNotValid(mnameET);
        else if (TextUtils.isEmpty(email))
            isDataValid = Helper.DataNotValid(memailET);
        else if ((Patterns.EMAIL_ADDRESS.matcher(email).matches() != true))
            isDataValid = Helper.DataNotValid(memailET, 1);
        else if (TextUtils.isEmpty(phone))
            isDataValid = Helper.DataNotValid(mphonenumberET);
        else if ((Patterns.PHONE.matcher(phone).matches() != true) || (phone.length() != 10))
            isDataValid = Helper.DataNotValid(mphonenumberET, 2);
        else if (password.trim().length() < 8 && TextUtils.isEmpty(password))
            isDataValid = Helper.DataNotValid(mpasswordET);
        else if (isDamsUser && TextUtils.isEmpty(damsid))
            isDataValid = Helper.DataNotValid(mdamsidET);

        if (isDataValid) {
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }
            user.setName(name);
            user.setEmail(email);
            user.setMobile(phone);
            user.setPassword(password);
            user.setIs_social(Const.SOCIAL_TYPE_FALSE);
            user.setSocial_type(socialType);
            user.setSocial_tokken(socialToken);
            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            user.setDams_tokken(damsid);
            SharedPreference.getInstance().setLoggedInUser(user);

            if (isDamsUser) {
                NetworkAPICall(API.API_USER_DAMS_VERIFICATION, true);
            } else {
                NetworkAPICall(API.API_OTP, true);
            }
        }
    }

    public void SignUpTask(JSONObject object, String type) {
        try {
            user = User.newInstance();
            switch (type) {
                case Const.SOCIAL_TYPE_FACEBOOK: //facebook picture url
                    user.setProfile_picture(
                            object.optJSONObject(Const.PICTURE).
                                    optJSONObject(Const.DATA).
                                    optString(Const.URL));
                    break;
                case Const.SOCIAL_TYPE_GMAIL://gmail image url
                    user.setProfile_picture(object.optString(Const.IMGURL));
                    break;
                case Const.SOCIAL_TYPE_LINKEDIN://linkedin picture url)
                    user.setProfile_picture(object.optString(Const.PICTUREURL));
                    break;
            }
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = SharedPreference.getInstance().getString(Const.FIREBASE_TOKEN_ID);
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = FirebaseInstanceId.getInstance().getToken();
                }
            }
            user.setSocial_type(socialType);
            user.setDevice_type(Const.DEVICE_TYPE_ANDROID);
            user.setDevice_tokken(deviceId);
            user.setIs_social(Const.SOCIAL_TYPE_TRUE);
            user.setSocial_tokken(object.getString(Const.ID));
            user.setEmail(object.getString(Const.EMAIL));
            user.setName(object.getString(Const.NAME));
            user.setGender(object.getString(Const.GENDER));
            SharedPreference.getInstance().setLoggedInUser(user);

            if (user.getMobile() == null)
                Helper.GoToMobileVerificationActivity(activity, Const.MOBILEVERIFICATION);
            else
                NetworkAPICall(API.API_OTP, true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_USER_DAMS_VERIFICATION:
                return Ion.with(activity)
                        .load(API.API_USER_DAMS_VERIFICATION)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.USER_TOKEN, damsid);
            case API.API_OTP:
                return Ion.with(activity)
                        .load(API.API_OTP)
                        .setTimeout(10 * 1000)
                        .setMultipartParameter(Const.MOBILE, phone)
                        .setMultipartParameter(Const.EMAIL, email);
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
        Log.e("SignUp", "SuccessCallBack " + jsonObject.toString());
        switch (apitype) {
            case API.API_USER_DAMS_VERIFICATION:
                if (jsonObject.optString(Const.STATUS).equals(Const.TRUE)) {
                    NetworkAPICall(API.API_OTP, true);
                } else {
                    this.ErrorCallBack(jsonObject.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_OTP:
                if (jsonObject.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject dataJsonObject = jsonObject.getJSONObject(Const.DATA);
                    String otp = dataJsonObject.getString(Const.OTP);
                    Helper.GoToOtpVerificationActivity(activity, otp, 1, Const.OTPVERIFICATION);
                } else {
                    this.ErrorCallBack(jsonObject.getString(Const.MESSAGE), apitype);
                }
                break;
            case API.API_USER_DAMS_LOGIN_AUTHENTICATION:
                try {
                    Gson gson=new Gson();
                    if (jsonObject.optString(Const.STATUS).equals(Const.TRUE)) {
                        SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);

                        DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_COLORCODE);
                        JSONObject dataJsonObject = jsonObject.getJSONObject(Const.DATA);
                        SharedPreference.getInstance().putBoolean(Const.IS_USER_LOGGED_IN, true);

                        User userprofile = gson.fromJson(dataJsonObject.toString(), User.class);
                        Log.e("String Login", gson.toJson(user).toString());
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
            case API.API_USER_DAMS_VERIFICATION:
                Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
                break;
            case API.API_OTP:
                Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
