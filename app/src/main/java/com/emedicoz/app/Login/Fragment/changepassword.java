package com.emedicoz.app.Login.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Login.Activity.SignInActivity;
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

public class changepassword extends MainFragment {

    Button submitBtn;
    EditText newPassowordET, retryPasswordET;

    String retrypassword, newpassword;

    String otp;

    public changepassword() {
    }

    public static changepassword newInstance(String otp) {
        changepassword fragment = new changepassword();
        Bundle args = new Bundle();
        args.putString(Const.OTP, otp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            otp = getArguments().getString(Const.OTP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_changepassword, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        submitBtn = (Button) view.findViewById(R.id.submitBtn);
        newPassowordET = (EditText) view.findViewById(R.id.newpasswordET);
        retryPasswordET = (EditText) view.findViewById(R.id.retrypasswordET);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckValidations();
            }
        });

    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_UPDATE_PASSWORD_WITH_OTP:
                User user = SharedPreference.getInstance().getLoggedInUser();
                return Ion.with(this)
                        .load(API.API_UPDATE_PASSWORD_WITH_OTP)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.MOBILE, user.getMobile())
                        .setMultipartParameter(Const.OTP, otp)
                        .setMultipartParameter(Const.PASSWORD, user.getPassword());
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonObject, String apitype) throws JSONException {
        Log.e("json", jsonObject.toString());
        switch (apitype) {
            case API.API_UPDATE_PASSWORD_WITH_OTP:
                if (jsonObject.optString(Const.STATUS).equals("true")) {
                    this.ErrorCallBack(jsonObject.getString(Const.MESSAGE), apitype);
                    Intent intent = new Intent(activity, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Const.TYPE, "LOGIN");
                    startActivity(intent);
                } else {
                    this.ErrorCallBack(jsonObject.getString(Const.MESSAGE), apitype);
                }
                break;
        }
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Log.e("json", jsonstring.toString());
        switch (apitype) {
            case API.API_UPDATE_PASSWORD_WITH_OTP:
                Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void CheckValidations() {
        newpassword = Helper.GetText(newPassowordET);
        retrypassword = Helper.GetText(retryPasswordET);

        boolean isDataValid = true;

        if (newpassword.length() < 8 && TextUtils.isEmpty(newpassword))
            isDataValid = Helper.DataNotValid(newPassowordET);
        else if (retrypassword.length() < 8 && TextUtils.isEmpty(retrypassword))
            isDataValid = Helper.DataNotValid(retryPasswordET);
        else if (!newpassword.equals(retrypassword)) {
            isDataValid = false;
            Toast.makeText(activity, "Password does not match", Toast.LENGTH_SHORT).show();
        }
        if (isDataValid) {
            User user = User.getInstance();
            user.setPassword(newpassword);
            SharedPreference.getInstance().setLoggedInUser(user);
            NetworkAPICall(API.API_UPDATE_PASSWORD_WITH_OTP, true);
        }
    }
}
