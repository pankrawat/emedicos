package com.emedicoz.app.Feeds.Fragment;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.Model.Registration;
import com.emedicoz.app.Model.User;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.Registration.SubStreamResponse;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Network.API;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class subStreamFragment extends MainFragment {

    TextView streamTV;

    RelativeLayout substreamRL;
    LinearLayout substreamoptionLL;
    Button nextBtn;

    private Activity activity;

    ArrayList<View> substreamViewList;

    ArrayList<SubStreamResponse> substreamResponseListfrag;

    ArrayList<String> substreamList;

    Registration registration;
    User user;
    String regType;

    public subStreamFragment() {
    }


    public static subStreamFragment newInstance(String regtype) {
        subStreamFragment fragment = new subStreamFragment();
        Bundle args = new Bundle();
        args.putString(Const.TYPE, regtype);
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
        registration = user.getUser_registration_info();
        activity = getActivity();
        substreamResponseListfrag = new ArrayList();
        substreamResponseListfrag = ((PostActivity) activity).substreamResponseList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        substreamResponseListfrag=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sub_stream, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity.setTitle("Sub Stream");
        streamTV = (TextView) view.findViewById(R.id.mainstreamTV);

        substreamRL = (RelativeLayout) view.findViewById(R.id.substreamRL);

        substreamoptionLL = (LinearLayout) view.findViewById(R.id.substreamoptionLL);

        nextBtn = (Button) view.findViewById(R.id.nextBtn);

        streamTV.setText(registration.getMaster_id_name() + " > ");

        if (substreamResponseListfrag.size() > 0) {
            InitSubstreamOptions();
        } else {
            NetworkAPICall(API.API_GET_COURSE_LIST_FIRST_LEVEL, true);
        }
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (registration.getMaster_id_level_one_name().equals("") || registration.getMaster_id_level_one_name().equals(null))
                    Toast.makeText(activity, "Kindly select any Option first", Toast.LENGTH_SHORT).show();
                else {
                    user.setUser_registration_info(registration);
                    getFragmentManager().popBackStack(Const.REGISTRATIONFRAGMENT, 0);
                }
            }
        });
    }

    public void InitSubstreamOptions() {
        substreamList = new ArrayList<String>();
        for (SubStreamResponse subStreamResponse : substreamResponseListfrag) {
            substreamList.add(subStreamResponse.getText_name());
        }
        addViewtoSubStreamOpt();
    }

    public void addViewtoSubStreamOpt() {
        substreamRL.setVisibility(View.VISIBLE);
        substreamViewList = new ArrayList<View>();
        int i = 0;
        while (i < substreamList.size()) {
            View v = View.inflate(activity, R.layout.single_row_reg_option, null);
            TextView tv = (TextView) v.findViewById(R.id.optionTextTV);
            v = changeBackgroundColor(v, 2);
            (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.GONE);
            tv.setText(substreamList.get(i));
            v.setTag(tv.getText());
            v.setOnClickListener(onOptionClick);

            if (substreamList.get(i).equals(registration.getMaster_id_level_one_name())) {
                v = changeBackgroundColor(v, 1);
                (v.findViewById(R.id.optioniconIBtn)).setVisibility(View.VISIBLE);
                registration.setMaster_id_level_one(substreamResponseListfrag.get(i).getId());
                registration.setMaster_id_level_one_name(substreamResponseListfrag.get(i).getText_name());
            }
            substreamoptionLL.addView(v);
            substreamViewList.add(v);
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
            registration.setMaster_id_level_two("");
            registration.setMaster_id_level_two_name("");
            ((PostActivity)activity).specializationResponseList=new ArrayList<>();
            int i = 0;
            int j = 0;
            String substr = "";
            while (i < substreamViewList.size()) {
                View v = substreamViewList.get(i);
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

            while (j < substreamResponseListfrag.size()) {
                SubStreamResponse sub = substreamResponseListfrag.get(j);
                if (sub.getText_name().equals(substr)) {
                    registration.setMaster_id_level_one(sub.getId());
                    registration.setMaster_id_level_one_name(sub.getText_name());
                }
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
            case API.API_GET_COURSE_LIST_FIRST_LEVEL:
                return Ion.with(this)
                        .load(API.API_GET_COURSE_LIST_FIRST_LEVEL + "/" + registration.getMaster_id())
                        .setTimeout(15 * 1000);
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException {
        Gson gson = new Gson();
        Log.e("fanwall post count", " " + jsonstring.toString());
        switch (apitype) {
            case API.API_GET_COURSE_LIST_FIRST_LEVEL:
                if (jsonstring.optString(Const.STATUS).equals("true")) {
                    JSONArray dataarray = jsonstring.getJSONArray(Const.DATA);
                    if (dataarray.length() > 0) {
                        int i = 0;
                        while (i < dataarray.length()) {
                            JSONObject singledatarow = dataarray.getJSONObject(i);
                            SubStreamResponse response = gson.fromJson(singledatarow.toString(), SubStreamResponse.class);
                            substreamResponseListfrag.add(response);
                            i++;
                        }
                        ((PostActivity) activity).substreamResponseList = substreamResponseListfrag;
                        InitSubstreamOptions();
                        Log.e("substream", "post count " + substreamResponseListfrag.size());
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
