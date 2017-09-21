package com.emedicoz.app.Utils.Network;

import android.content.Context;
import android.util.Log;

import com.emedicoz.app.R;
import com.emedicoz.app.views.Progress;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Cbc-03 on 05/26/17.
 */

public class NetworkCall {

    MyNetworkCallBack myCBI;
    public Progress mprogress;
    Context context;

    public NetworkCall(MyNetworkCallBack callBackInterface, Context context) {
        mprogress = new Progress(context);
        mprogress.setCancelable(false);
        myCBI = callBackInterface;
        this.context = context;
    }

    public void NetworkAPICall(final String apiType, final boolean showprogress) {
        Log.e("NetworkAPI Interface", "================"+apiType);
        if (showprogress) mprogress.show();
        if (API.API_GET_COURSE_LIST_ZERO_LEVEL == apiType ||
                API.API_GET_COURSE_LIST_FIRST_LEVEL == apiType ||
                API.API_GET_COURSE_LIST_SECOND_LEVEL == apiType ||
                API.API_GET_COURSE_INTERESTED_IN == apiType ||
                API.API_GET_USER == apiType ||
                API.API_GET_TAG_LISTS == apiType ||
                API.API_GET_APP_VERSION == apiType) {
            myCBI.getAPIB(apiType).asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {

                            if (showprogress) mprogress.hide();
                            try {
                                if (e == null) {
                                    if (jsonString != null && !jsonString.isEmpty()) {
                                        JSONObject jsonObject = new JSONObject(jsonString);

                                        myCBI.SuccessCallBack(jsonObject, apiType);
                                    } else {
                                        myCBI.ErrorCallBack(context.getString(R.string.jsonparsing_error_message), apiType);
                                    }
                                } else {
                                    myCBI.ErrorCallBack(context.getString(R.string.exception_api_error_message), apiType);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        } else {
            myCBI.getAPI(apiType).asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            if (showprogress) mprogress.hide();
                            try {
                                if (e == null) {
                                    if (jsonString != null && !jsonString.isEmpty()) {
                                        JSONObject jsonObject = new JSONObject(jsonString);

                                        myCBI.SuccessCallBack(jsonObject, apiType);
                                    } else {
                                        myCBI.ErrorCallBack(context.getString(R.string.jsonparsing_error_message), apiType);
                                    }
                                } else {
                                    myCBI.ErrorCallBack(context.getString(R.string.exception_api_error_message), apiType);
                                }
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
        }
    }

    public interface MyNetworkCallBack {
        Builders.Any.M getAPI(String apitype);

        Builders.Any.B getAPIB(String apitype);

        void SuccessCallBack(JSONObject jsonstring, String apitype) throws JSONException;

        void ErrorCallBack(String jsonstring, String apitype);
    }


}
