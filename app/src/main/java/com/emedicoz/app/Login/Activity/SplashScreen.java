package com.emedicoz.app.Login.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;

import com.emedicoz.app.Feeds.Activity.FeedsActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.Network.NetworkCall;
import com.emedicoz.app.Utils.SharedPreference;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreen extends AppCompatActivity {

    Class nextActivity;
    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        if (SharedPreference.getInstance().getBoolean(Const.IS_USER_LOGGED_IN) &&
                SharedPreference.getInstance().getBoolean(Const.IS_USER_REGISTRATION_DONE)) {
            nextActivity = FeedsActivity.class;

        } else nextActivity = DemoActivity.class;
        Helper.logUser(this);
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          Intent intent = new Intent(SplashScreen.this, nextActivity);
                                          if (nextActivity.equals(FeedsActivity.class)) {
                                              intent.putExtra(Const.FRAG_TYPE, Const.FEEDS);
                                          }
                                          startActivity(intent);
                                          finish();
                                      }
                                  }

                , SPLASH_TIME_OUT);

    }

}
