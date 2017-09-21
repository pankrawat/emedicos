package com.emedicoz.app.Login.Activity;

import android.support.v4.app.Fragment;

import com.emedicoz.app.Common.BaseABNoNavActivity;
import com.emedicoz.app.Login.Fragment.changepassword;
import com.emedicoz.app.Login.Fragment.forgetpassword;
import com.emedicoz.app.Login.Fragment.mobileverification;
import com.emedicoz.app.Login.Fragment.otpverification;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;

/**
 * Created by Cbc-03 on 06/08/17.
 */

public class LoginCatActivity extends BaseABNoNavActivity {

    String otp;
    String Frag_type;
    int type;

    @Override
    protected void initViews() {
        if (getIntent().getExtras() != null) {
            Frag_type = getIntent().getExtras().getString(Const.FRAG_TYPE);
            switch (Frag_type) {
                case Const.CHANGEPASSWORD:
                    otp = getIntent().getExtras().getString(Const.OTP);
                    break;
                case Const.OTPVERIFICATION:
                    otp = getIntent().getExtras().getString(Const.OTP);
                    type = getIntent().getExtras().getInt(Const.TYPE);
                    break;
            }
        }
    }

    @Override
    protected boolean addBackButton() {
        return true;
    }

    @Override
    protected Fragment getFragment() {

        switch (Frag_type) {
            case Const.CHANGEPASSWORD:
                setTitle(getString(R.string.change_password));
                return changepassword.newInstance(otp);
            case Const.FORGETPASSWORD:
                setTitle(getString(R.string.forget_password));
                return forgetpassword.newInstance();
            case Const.MOBILEVERIFICATION:
                setTitle(getString(R.string.mobile_verification));
                return mobileverification.newInstance();
            case Const.OTPVERIFICATION:
                setTitle(getString(R.string.otp_verification));
                return otpverification.newInstance(otp, type);
        }
        return null;
    }

}
