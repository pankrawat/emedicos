package com.emedicoz.app.Login.Fragment;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Model.User;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.DbAdapter;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.SharedPreference;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class otpverification extends MainFragment implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {


    //    EditText otp1ET, otp2ET, otp3ET, otp4ET;
    Button verifyBtn;

    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;

    String otp;
    String otptext = "";

    int type;

    BroadcastReceiver broadcastReceiver;
    Gson gson;

    public otpverification() {
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Initialize EditText fields.
     */

    public static otpverification newInstance(String otp, int type) {
        otpverification fragment = new otpverification();
        Bundle args = new Bundle();
        args.putString(Const.OTP, otp);
        args.putInt(Const.TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            otp = getArguments().getString(Const.OTP);
            type = getArguments().getInt(Const.TYPE);
        }
    }

    public void AutoReadMessage() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                String message = b.getString("message");
                Log.e("newmesage", "" + message);
                try {
                    if (message.contains("eMedicoz")) {
                        otptext = parseCode(message);
                        Log.e("OTP MESSAGE", otptext);
                        mPinHiddenEditText.setText(otptext);
                        mPinFirstDigitEditText.setText(String.valueOf(otptext.charAt(0)));
                        mPinSecondDigitEditText.setText(String.valueOf(otptext.charAt(1)));
                        mPinThirdDigitEditText.setText(String.valueOf(otptext.charAt(2)));
                        mPinForthDigitEditText.setText(String.valueOf(otptext.charAt(3)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("broadCastOtp"));
    }

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{4}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otpverifcation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPinFirstDigitEditText = (EditText) view.findViewById(R.id.OPT1ET);
        mPinSecondDigitEditText = (EditText) view.findViewById(R.id.OPT2ET);
        mPinThirdDigitEditText = (EditText) view.findViewById(R.id.OPT3ET);
        mPinForthDigitEditText = (EditText) view.findViewById(R.id.OPT4ET);
        mPinHiddenEditText = (EditText) view.findViewById(R.id.pin_hidden_edittext);

        setPINListeners();
        verifyBtn = (Button) view.findViewById(R.id.verifyBtn);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otptext = mPinHiddenEditText.getText().toString().trim();

                Log.e("OtpActivity", "opt:  " + otptext);
                if (otptext.equals(otp)) {
                    if (type == 2) {
                        Helper.GoToChangePasswordActivity(activity, otp, Const.CHANGEPASSWORD);
                    } else if (type == 1) {
                        NetworkAPICall(API.API_REGISTER_USER, true);
                        SharedPreference.getInstance().putBoolean(Const.IS_PHONE_VERIFIED, true);
                    }
                } else {
                    Toast.makeText(activity, "OTP does not match.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AutoReadMessage();
    }

    @Override
    public Builders.Any.M getAPI(String apitype) {
        switch (apitype) {
            case API.API_REGISTER_USER:
                User user = User.getInstance();
                return Ion.with(this)
                        .load(API.API_REGISTER_USER)
                        .setTimeout(15 * 1000)
                        .setMultipartParameter(Const.NAME, user.getName())
                        .setMultipartParameter(Const.EMAIL, user.getEmail())
                        .setMultipartParameter(Const.PASSWORD, user.getPassword())
                        .setMultipartParameter(Const.MOBILE, user.getMobile())
                        .setMultipartParameter(Const.IS_SOCIAL, user.getIs_social())
                        .setMultipartParameter(Const.SOCIAL_TYPE, user.getSocial_type())
                        .setMultipartParameter(Const.SOCIAL_TOKEN, user.getSocial_tokken())
                        .setMultipartParameter(Const.DEVICE_TYPE, user.getDevice_type())
                        .setMultipartParameter(Const.DEVICE_TOKEN, user.getDevice_tokken())
                        .setMultipartParameter(Const.DAMS_TOKEN, user.getDams_tokken())
                        .setMultipartParameter(Const.PROFILE_PICTURE, user.getProfile_picture());
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonString, String apitype) throws JSONException {
        Log.e("json", jsonString.toString());
        switch (apitype) {
            case API.API_REGISTER_USER:
                SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);
                gson = new Gson();
                if (jsonString.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONObject dataJsonObject = jsonString.getJSONObject(Const.DATA);
                    Log.e("register", " " + jsonString);
                    DbAdapter.getInstance(activity).deleteAll(DbAdapter.TABLE_NAME_COLORCODE);
                    SharedPreference.getInstance().putBoolean(Const.IS_USER_LOGGED_IN, true);

                    User userprofile = gson.fromJson(dataJsonObject.toString(), User.class);

                    SharedPreference.getInstance().ClearLoggedInUser();
                    SharedPreference.getInstance().setLoggedInUser(userprofile);

                    if (userprofile.getIs_course_register().equals("0")) {
                        SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, false);
                        Helper.GoToRegistrationActivity(activity, Const.REGISTRATION, Const.REGISTRATION);
                    } else if (userprofile.getIs_course_register().equals("1")) {
                        Helper.GoToFeedsActivity(activity);
                        SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                    }

                } else {
                    Log.e("register", " " + jsonString);
                    ErrorCallBack(jsonString.optString(Const.MESSAGE), apitype);
                }
                break;
        }

    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Toast.makeText(activity, "error: " + jsonstring, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        setDefaultPinBackground(mPinFirstDigitEditText);
        setDefaultPinBackground(mPinSecondDigitEditText);
        setDefaultPinBackground(mPinThirdDigitEditText);
        setDefaultPinBackground(mPinForthDigitEditText);

        if (s.length() == 0) {
            setFocusedPinBackground(mPinFirstDigitEditText);
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(mPinSecondDigitEditText);
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 2) {
            setFocusedPinBackground(mPinThirdDigitEditText);
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 3) {
            setFocusedPinBackground(mPinForthDigitEditText);
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 4) {
            setFocusedPinBackground(mPinForthDigitEditText);
            mPinForthDigitEditText.setText(s.charAt(3) + "");
            hideSoftKeyboard(mPinForthDigitEditText);
        }
    }

    private void setDefaultPinBackground(EditText editText) {
//        setViewBackground(editText, getResources().getDrawable(R.drawable.textfield_default_holo_light));
    }

    /**
     * Sets focus on a specific EditText field.
     *
     * @param editText EditText to set focus on
     */
    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    /**
     * Sets focused PIN field background.
     *
     * @param editText edit text to change
     */
    private void setFocusedPinBackground(EditText editText) {
//        setViewBackground(editText, getResources().getDrawable(R.drawable.textfield_focused_holo_light));
    }

    /**
     * Sets listeners for EditText fields.
     */
    private void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);
    }

    /**
     * Sets background of the view.
     * This method varies in implementation depending on Android SDK version.
     *
     * @param view       View to which set background
     * @param background Background to set to view
     */
    @SuppressWarnings("deprecation")
    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    /**
     * Shows soft keyboard.
     *
     * @param editText EditText which has focus
     */
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        final int id = view.getId();
        switch (id) {
            case R.id.OPT1ET:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.OPT2ET:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.OPT3ET:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.OPT4ET:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = view.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0)
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }
}
