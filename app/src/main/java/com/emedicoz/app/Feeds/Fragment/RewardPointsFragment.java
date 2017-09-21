package com.emedicoz.app.Feeds.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emedicoz.app.Common.MainFragment;
import com.emedicoz.app.R;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

public class RewardPointsFragment extends MainFragment {

    public RewardPointsFragment() {
        // Required empty public constructor
    }

    public static RewardPointsFragment newInstance() {
        RewardPointsFragment fragment = new RewardPointsFragment();
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
        return inflater.inflate(R.layout.fragment_saved_notes, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    public void SuccessCallBack(JSONObject jsonobject, String apitype) throws JSONException {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void ErrorCallBack(String object, String apitype) {
    }
}
