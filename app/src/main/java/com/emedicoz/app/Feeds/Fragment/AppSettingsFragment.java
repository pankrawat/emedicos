package com.emedicoz.app.Feeds.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.SharedPreference;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

public class AppSettingsFragment extends MainFragment {

    Activity activity;
    Switch notificationswitch;

    public AppSettingsFragment() {
        // Required empty public constructor
    }

    public static AppSettingsFragment newInstance() {
        AppSettingsFragment fragment = new AppSettingsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationswitch = (Switch) view.findViewById(R.id.switch1);
        notificationswitch.setChecked(!SharedPreference.getInstance().getBoolean(Const.IS_NOTIFICATION_BLOCKED));
        notificationswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreference.getInstance().putBoolean(Const.IS_NOTIFICATION_BLOCKED, !b);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public Builders.Any.M getAPI(String apitype) {
        return null;
    }

    @Override
    public Builders.Any.B getAPIB(String apitype) {
        return null;
    }

    @Override
    public void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException {

    }

    @Override
    public void ErrorCallBack(String jsonstring, String apitype) {

    }


}
