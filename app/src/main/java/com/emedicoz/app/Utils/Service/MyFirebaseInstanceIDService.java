package com.emedicoz.app.Utils.Service;

import android.text.TextUtils;
import android.util.Log;

import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.SharedPreference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    String refreshedToken;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
        if (SharedPreference.getInstance().getBoolean(Const.IS_USER_LOGGED_IN)) {
            Ion.with(this)
                    .load(API.API_UPDATE_DEVICE_TOKEN)
                    .setTimeout(10 * 1000)
                    .setMultipartParameter(Const.USER_ID, SharedPreference.getInstance().getLoggedInUser().getId())
                    .setMultipartParameter(Const.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID)
                    .setMultipartParameter(Const.DEVICE_TOKEN, token).asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String jsonString) {
                            if (e == null) {
                                try {
                                    if (!TextUtils.isEmpty(jsonString)) {
                                        JSONObject jsonObject = new JSONObject(jsonString);
                                        if (jsonObject.optString(Const.STATUS).equals(Const.TRUE)) {
                                            Log.e(TAG, "Server Response : " + jsonObject.optString(Const.MESSAGE));
                                        }
                                    }
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }

    private void storeRegIdInPref(String token) {
        SharedPreference.getInstance().putString(Const.FIREBASE_TOKEN_ID, token);
    }
}