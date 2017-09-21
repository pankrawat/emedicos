package com.emedicoz.app.Feeds.Fragment;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.Model.Registration;
import com.emedicoz.app.Model.User;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.Registration.SpecializationResponse;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class specializationFragment extends MainFragment {

    TextView streamTV;
    RelativeLayout specializationRL;
    LinearLayout specializationoptionLL;
    Button nextBtn;
    private Activity activity;

    EditText otherET;

    SpecializationResponse specialization = new SpecializationResponse();

    ArrayList<View> specializationViewList;

    ArrayList<String> specializationList;

    ArrayList<SpecializationResponse> specializationResponseListfrag;

    Registration registration;
    User user;

    boolean isOthers = false;
    String others;
    String regType;

    public specializationFragment() {
        // Required empty public constructor
    }

    public static specializationFragment newInstance(String reg_type) {
        specializationFragment fragment = new specializationFragment();
        Bundle args = new Bundle();
        args.putString(Const.TYPE, reg_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            regType = getArguments().getString(Const.TYPE);
        }
        user = User.getInstance();
        activity = getActivity();
        registration = user.getUser_registration_info();
        specializationResponseListfrag = new ArrayList();
        specializationResponseListfrag = ((PostActivity) activity).specializationResponseList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_specialization, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity.setTitle("Specialization");

        streamTV = (TextView) view.findViewById(R.id.mainstreamTV);

        otherET = (EditText) view.findViewById(R.id.otherET);

        specializationRL = (RelativeLayout) view.findViewById(R.id.specializationRL);

        specializationoptionLL = (LinearLayout) view.findViewById(R.id.specializationoptionLL);

        nextBtn = (Button) view.findViewById(R.id.nextBtn);

        streamTV.setText(registration.getMaster_id_name() + " > " + registration.getMaster_id_level_one_name());
        if (specializationResponseListfrag.size() > 0)
            InitSpecializationOptions();
        else NetworkAPICall(API.API_GET_COURSE_LIST_SECOND_LEVEL, true);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOthers) {
                    CheckValidation();
                } else if ((!TextUtils.isEmpty(registration.getMaster_id_level_two()) || !TextUtils.isEmpty(registration.getMaster_id_level_two()))
                        ) {/*   } else if ((!TextUtils.isEmpty(registration.getMaster_id_level_two()) || !TextUtils.isEmpty(registration.getMaster_id_level_two()))
                        & TextUtils.isEmpty(registration.getOptional_text())) {*/
                    user.setUser_registration_info(registration);
                    getFragmentManager().popBackStack(Const.REGISTRATIONFRAGMENT, 0);
                } else {
                    Toast.makeText(activity, "Kindly select any Option first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        specializationResponseListfrag = new ArrayList<>();
    }

    public void CheckValidation() {
        others = Helper.GetText(otherET);
        boolean isDataValid = true;

        if (TextUtils.isEmpty(others))
            isDataValid = Helper.DataNotValid(otherET);

        if (isDataValid) {
            registration.setMaster_id_level_two("");
            registration.setMaster_id_level_two_name("");
            registration.setOptional_text(others);
            user.setUser_registration_info(registration);
            getFragmentManager().popBackStack(Const.REGISTRATIONFRAGMENT, 0);
        }
    }

    public void InitSpecializationOptions() {
        specializationList = new ArrayList<String>();
        int i = 0;
        while (i < specializationResponseListfrag.size()) {
            specializationList.add(specializationResponseListfrag.get(i).getText_name());
            i++;
        }
        if (specializationList.size() > 0) {
//            specializationList.add(Const.OTHERS);
            addViewtospecializationOpt();
        }
    }

    public void addViewtospecializationOpt() {
        specializationRL.setVisibility(View.VISIBLE);
        specializationViewList = new ArrayList<View>();
        int i = 0;
        while (i < specializationList.size()) {
            View v = View.inflate(activity, R.layout.single_row_reg_option, null);
            TextView tv = (TextView) v.findViewById(R.id.optionTextTV);
            v = changeBackgroundColor(v, 2);
            (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.GONE);
            tv.setText(specializationList.get(i));
            v.setTag(tv.getText());
            v.setOnClickListener(onOptionClick);

            if (specializationList.get(i).equals(registration.getMaster_id_level_two_name())) {
                v = changeBackgroundColor(v, 1);
                (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.VISIBLE);
            }
            /*else if (!TextUtils.isEmpty(registration.getOptional_text()) &&
                    regType.equals(Const.PROFILE) &&
                    (TextUtils.isEmpty(registration.getMaster_id_level_two()) || registration.getMaster_id_level_two().equals("0"))) {
                if (specializationList.get(specializationList.size() - 1).equals(specializationList.get(mcqansCounter))) {
                    v = changeBackgroundColor(v, 1);
                    (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.VISIBLE);
//
                    otherET.setText(registration.getOptional_text());
                    otherET.setVisibility(View.VISIBLE);
                    otherET.requestFocus();
                    isOthers = true;
                }
            }*/
            specializationoptionLL.addView(v);
            specializationViewList.add(v);
            i++;
        }
    }

    public View changeBackgroundColor(View v, int type) {

        v.setBackgroundResource(R.drawable.bg_refcode_et);
        GradientDrawable drawable = (GradientDrawable) v.getBackground();
        drawable.setColor(getResources().getColor(R.color.white));
        if (type == 1) drawable.setStroke(3, getResources().getColor(R.color.blue));
        else drawable.setStroke(3, getResources().getColor(R.color.transparent));
        return v;
    }

    protected View.OnClickListener onOptionClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int i = 0;
            int j = 0;
            String substr = "";
            while (i < specializationViewList.size()) {
                View v = specializationViewList.get(i);
                if (view.getTag() == v.getTag()) {
                    (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.VISIBLE);
                    v = changeBackgroundColor(v, 1);
                    substr = String.valueOf(v.getTag());
                } else {
                    (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.GONE);
                    v = changeBackgroundColor(v, 2);
                }
                i++;
            }
            while (j < specializationResponseListfrag.size()) {
                SpecializationResponse sub = specializationResponseListfrag.get(j);
                if (sub.getText_name().equals(substr)) {
                    specialization = sub;
                    registration.setMaster_id_level_two(sub.getId());
                    registration.setMaster_id_level_two_name(sub.getText_name());
                    registration.setOptional_text("");
                    otherET.setVisibility(View.GONE);
                    otherET.setText("");
                    isOthers = false;
                }
                /*else if (substr.equals(Const.OTHERS)) {
                    registration.setMaster_id_level_two("");
                    registration.setMaster_id_level_two_name(sub.getText_name());
                    otherET.setVisibility(View.VISIBLE);
                    otherET.requestFocus();
                    isOthers = true;
                }*/
                j++;
            }
        }
    };

    @Override
    public Builders.Any.M getAPI(String apitype) {
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        switch (apitype) {
            case API.API_GET_COURSE_LIST_SECOND_LEVEL:
                return Ion.with(this)
                        .load(API.API_GET_COURSE_LIST_SECOND_LEVEL + "/" + registration.getMaster_id_level_one())
                        .setTimeout(15 * 1000);
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException {
        Gson gson = new Gson();
        Log.e("post ", " " + jsonstring.toString());
        switch (apitype) {
            case API.API_GET_COURSE_LIST_SECOND_LEVEL:
                if (jsonstring.optString(Const.STATUS).equals(Const.TRUE)) {
                    JSONArray dataarray = jsonstring.getJSONArray(Const.DATA);
                    if (dataarray.length() > 0) {
                        int i = 0;
                        while (i < dataarray.length()) {
                            JSONObject singledatarow = dataarray.getJSONObject(i);
                            SpecializationResponse response = gson.fromJson(singledatarow.toString(), SpecializationResponse.class);
                            specializationResponseListfrag.add(response);
                            i++;
                        }
                        ((PostActivity) activity).specializationResponseList = specializationResponseListfrag;
                        InitSpecializationOptions();
                        Log.e("SECOND post count", " " + specializationResponseListfrag.size());
                    } else {
                        this.ErrorCallBack(jsonstring.getString(Const.MESSAGE), apitype);
                    }
                } else {
                    this.ErrorCallBack(jsonstring.getString(Const.MESSAGE), apitype);
                }
                break;
        }
    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
    }
}
