package com.emedicoz.app.Login.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Model.User;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.SharedPreference;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;


public class mobileverification extends MainFragment {

    Button VerifyBtn;
    EditText mobileET;

    String phone;

    User user;

    public mobileverification() {
    }

    public static mobileverification newInstance() {
        mobileverification fragment = new mobileverification();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mobileverification, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        VerifyBtn = (Button) view.findViewById(R.id.verifyBtn);
        mobileET = (EditText) view.findViewById(R.id.mobileET);


        VerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckValidation();
            }
        });
    }


    public void CheckValidation() {
        phone = Helper.GetText(mobileET);

        boolean isDataValid = true;

        if (TextUtils.isEmpty(phone))
            isDataValid = Helper.DataNotValid(mobileET);
        else if ((Patterns.PHONE.matcher(phone).matches() != true) || (phone.length() != 10))
            isDataValid = Helper.DataNotValid(mobileET, 2);

        if (isDataValid) {
            user = User.getInstance();
            user.setMobile(phone);
            SharedPreference.getInstance().setLoggedInUser(user);
            NetworkAPICall(API.API_OTP, true);
        }
    }


    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_OTP:
                return Ion.with(this)
                        .load(API.API_OTP)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.MOBILE, phone)
                        .setMultipartParameter(Const.EMAIL, user.getEmail());
        }
        return null;

    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonObject, String apitype) throws JSONException {
        switch (apitype) {
            case API.API_OTP:
                Log.e("json", jsonObject.toString());
                if (jsonObject.optString(Const.STATUS).equals("true")) {
                    JSONObject dataJsonObject = jsonObject.getJSONObject(Const.DATA);
                    String otp = dataJsonObject.getString(Const.OTP);
                    Helper.GoToOtpVerificationActivity(activity, otp, 1, Const.OTPVERIFICATION);
                } else {
                    this.ErrorCallBack(jsonObject.getString(Const.MESSAGE), apitype);
                }
                break;
        }
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        switch (apitype) {
            case API.API_OTP:
                Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
