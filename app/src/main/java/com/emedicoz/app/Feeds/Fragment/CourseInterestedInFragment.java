package com.emedicoz.app.Feeds.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.Model.Registration;
import com.emedicoz.app.Model.User;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.Registration.CoursesInterestedResponse;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Network.API;
import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class CourseInterestedInFragment extends MainFragment {

    LinearLayout coursesLL;
    Button nextBtn;
    private Activity activity;
    EditText otherET;

    User user;

    ArrayList<View> coursesViewList;

    ArrayList<String> selectedcoursesList;

    ArrayList<CheckBox> coursesCheckBox;

    ArrayList<CoursesInterestedResponse> coursesResponseList;

    Registration registration;
    String regType;

    public CourseInterestedInFragment() {
    }

    public static CourseInterestedInFragment newInstance(String regType) {
        CourseInterestedInFragment fragment = new CourseInterestedInFragment();
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
        user = User.getInstance();
        registration = user.getUser_registration_info();
        selectedcoursesList = new ArrayList<>();
        coursesResponseList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_coursesinterested, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setTitle("Exams Interested");
        otherET = (EditText) view.findViewById(R.id.otherET);

        coursesLL = (LinearLayout) view.findViewById(R.id.coursesLL);

        nextBtn = (Button) view.findViewById(R.id.nextBtn);
        if (coursesResponseList.size() > 0) {
            InitCoursesOptions();
        } else {
            NetworkAPICall(API.API_GET_COURSE_INTERESTED_IN, true);
        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String substr = "";
                String substrtext = "";
                for (String str : selectedcoursesList) {
                    if (substr == "") substr = str;
                    else substr = substr + "," + str;
                }
                for (String str1 : selectedcoursesList) {
                    for (CoursesInterestedResponse ci : coursesResponseList) {
                        if (ci.getId().equals(str1)) {
                            if (substrtext == "") substrtext = ci.getText_name();
                            else substrtext = substrtext + ", " + ci.getText_name();
                        }
                    }
                }
                registration.setInterested_course(substr);
                registration.setInterested_course_text(substrtext);
                user.setUser_registration_info(registration);
                getFragmentManager().popBackStack(Const.REGISTRATIONFRAGMENT, 0);
            }

        });
    }

    public void InitCoursesOptions() {
        addViewtoCoursesOpt();
    }

    public void addViewtoCoursesOpt() {
        String[] courses = null;
        coursesCheckBox = new ArrayList<>();
        coursesLL.setVisibility(View.VISIBLE);
        coursesViewList = new ArrayList<View>();
        View v = View.inflate(activity, R.layout.single_row_catcourse, null);
        LinearLayout coursesoptionLL = (LinearLayout) v.findViewById(R.id.coursesoptionLL);
        int j = 0;
        while (j < coursesResponseList.size()) {
            CheckBox cb = new CheckBox(activity);
            cb.setText(coursesResponseList.get(j).getText_name());
            cb.setPadding(8, 8, 8, 8);
            cb.setTag(coursesResponseList.get(j));

            if (registration.getInterested_course() != null && registration.getInterested_course() != "") {
                courses = registration.getInterested_course().split(",");
            }
            if (courses != null) {
                for (String str : courses) {
                    if (coursesResponseList.get(j).getId().equals(str)) {
                        cb.setChecked(true);
                        selectedcoursesList.add(str);
                    }
                }
            }
            coursesCheckBox.add(cb);
            coursesoptionLL.addView(cb);
            cb.setOnCheckedChangeListener(onCheckboxClick);
            j++;
        }
        coursesLL.addView(v);


    }


    CompoundButton.OnCheckedChangeListener onCheckboxClick = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            CoursesInterestedResponse resp = (CoursesInterestedResponse) compoundButton.getTag();
            if (b && !selectedcoursesList.contains(resp.getId())) {
                selectedcoursesList.add(resp.getId());
            } else if (!b && selectedcoursesList.contains(resp.getId())) {
                selectedcoursesList.remove(resp.getId());
            }
        }
    };

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
        }
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        switch (apitype) {
            case API.API_GET_COURSE_INTERESTED_IN:
                return Ion.with(this)
                        .load(API.API_GET_COURSE_INTERESTED_IN + "/1")
                        .setTimeout(15 * 1000);
        }
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException {
        Gson gson = new Gson();
        switch (apitype) {
            case API.API_GET_COURSE_INTERESTED_IN:
                if (jsonstring.optString(Const.STATUS).equals("true")) {
                    JSONObject dataobject = jsonstring.getJSONObject(Const.DATA);
                    JSONArray datatitle = dataobject.getJSONArray(Const.TITLE);
                    JSONArray titleData = dataobject.getJSONArray(Const.TITLE_DATA);
                   /* if (datatitle.length() > 0) {
                        int mcqansCounter = 0;
                        titlecoursesList = new ArrayList<>();
                        while (mcqansCounter < datatitle.length()) {
                            coursesResponseList = new ArrayList<>();
                            JSONObject singledatarow = datatitle.getJSONObject(mcqansCounter);
                            TitleMasterResponse title = gson.fromJson(singledatarow.toString(), TitleMasterResponse.class);
                            titlecoursesList.add(title);
                            int j = 0;
                            while (j < titleData.length()) {
                                JSONObject jsonObject = titleData.getJSONObject(j);
                                CoursesInterestedResponse coursesResponse = gson.fromJson(jsonObject.toString(), CoursesInterestedResponse.class);
                                if (title.getId().equals(coursesResponse.getParent_id()))
                                    coursesResponseList.add(coursesResponse);
                                j++;
                            }
                            courseinterestedList.put(title.getText_name(), coursesResponseList);
                            mcqansCounter++;
                        }
                    } */

                    int j = 0;
                    while (j < titleData.length()) {
                        JSONObject jsonObject = titleData.getJSONObject(j);
                        CoursesInterestedResponse coursesResponse = gson.fromJson(jsonObject.toString(), CoursesInterestedResponse.class);
//                        if (title.getId().equals(coursesResponse.getParent_id()))
                        coursesResponseList.add(coursesResponse);
                        j++;
                    }
//                    courseinterestedList.put(title.getText_name(), coursesResponseList);

                } else {
                    this.ErrorCallBack(jsonstring.getString(Const.MESSAGE), apitype);
                }

                /*if (titleData.length() > 0) {
                    int j = 0;

                } else {
                    this.ErrorCallBack(jsonstring.getString(Const.MESSAGE), apitype);

                }*/
                InitCoursesOptions();

                break;

            case API.API_STREAM_REGISTRATION:/*
                if (jsonstring.optString(Const.STATUS).equals(Const.TRUE)) {
                    SharedPreference.getInstance().putBoolean(Const.IS_USER_REGISTRATION_DONE, true);
                    user.setProfile_picture(registration.getProfilepicture());
                    user.setName(registration.getName());
                    user.setUser_registration_info(registration);
                    SharedPreference.getInstance().setLoggedInUser(user);

                    this.ErrorCallBack(jsonstring.optString(Const.MESSAGE), apitype);
                    Helper.GoToFeedsActivity(activity, Const.FEEDS, null);
                } else {
                    this.ErrorCallBack(jsonstring.optString(Const.MESSAGE), apitype);
                }*/
                break;
        }

    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {
        Toast.makeText(activity, jsonstring, Toast.LENGTH_SHORT).show();
    }
}
