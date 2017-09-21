package com.emedicoz.app.Common;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ProgressBar;

import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.views.Progress;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class MainFragment extends Fragment {

    public MainFragment() {
    }

    public Progress mprogress;
    public ProgressBar progressBar;
    public Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mprogress = new Progress(getActivity());
        mprogress.setCancelable(false);
        activity = getActivity();
    }

    public void NetworkAPICall(final String apiType, final boolean showprogress) {
        Log.e(activity.getLocalClassName(), "++++++++++++++++" + apiType);

        if (showprogress) mprogress.show();
        if (API.API_GET_COURSE_LIST_ZERO_LEVEL == apiType ||
                API.API_GET_COURSE_LIST_FIRST_LEVEL == apiType ||
                API.API_GET_COURSE_LIST_SECOND_LEVEL == apiType ||
                API.API_GET_COURSE_INTERESTED_IN == apiType ||
                API.API_GET_USER == apiType ||
                API.API_FEEDS_BANNER == apiType ||
                API.API_GET_APP_VERSION == apiType) {
            getAPIB(apiType).asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            if (showprogress) mprogress.hide();
                            try {
                                if (e == null) {
//                                    Log.e("json", "API: " + apiType + ": " + jsonString.toString());

                                    if (jsonString != null && !jsonString.isEmpty()) {
                                        JSONObject jsonObject = new JSONObject(jsonString);

                                        SuccessCallBack(jsonObject, apiType);
                                    } else {
                                        ErrorCallBack(getString(R.string.jsonparsing_error_message), apiType);
                                    }
                                } else {
                                    ErrorCallBack(getString(R.string.exception_api_error_message), apiType);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        } else {
            getAPI(apiType).asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            if (showprogress) mprogress.hide();
                            try {
                                if (e == null) {
//                                    Log.e("json", "API: " + apiType + ": " + jsonString.toString());
                                    if (jsonString != null && !jsonString.isEmpty()) {
                                        JSONObject jsonObject = new JSONObject(jsonString);

                                        SuccessCallBack(jsonObject, apiType);
                                    } else {
                                        ErrorCallBack(getString(R.string.jsonparsing_error_message), apiType);
                                    }
                                } else {
                                    ErrorCallBack(getString(R.string.exception_api_error_message), apiType);
                                }
                            } catch (JSONException e1) {
                                Log.e("json", "API: " + apiType + ": " + jsonString.toString());
                                e1.printStackTrace();
                            }
                        }
                    });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mprogress.isShowing()) mprogress.dismiss();
    }

    public abstract Builders.Any.M getAPI(String apitype);

    public abstract Builders.Any.B getAPIB(String apitype);

    public abstract void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException;

    public abstract void ErrorCallBack(String jsonstring, String apitype);

}
