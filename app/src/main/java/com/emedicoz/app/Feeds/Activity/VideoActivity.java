package com.emedicoz.app.Feeds.Activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;
import com.emedicoz.app.Utils.Network.API;
import com.emedicoz.app.Utils.Network.NetworkCall;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;
import com.maning.mnvideoplayerlibrary.player.MNViderPlayer;

import org.json.JSONException;
import org.json.JSONObject;

public class VideoActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {


    String URL, type;
    public MNViderPlayer mn_videoplayer;
    private static final String TAG = "MNViderPlayer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        if (getIntent() != null) {
            URL = getIntent().getExtras().getString(Const.VIDEO_LINK);
            type = getIntent().getExtras().getString(Const.TYPE);
        }
        mn_videoplayer = (MNViderPlayer) findViewById(R.id.mn_videoplayer);
        initPlayer(URL);
        Helper.logUser(this);
    }

    private void initPlayer(String url) {
        mn_videoplayer.setIsNeedBatteryListen(true);
        mn_videoplayer.setIsNeedNetChangeListen(true);
        Log.i(TAG, "Video URL_2 " + url);

        mn_videoplayer.setDataSource(url, "URL_2");


        mn_videoplayer.setOnCompletionListener(new MNViderPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.i(TAG, "Video Ended");
            }
        });

        mn_videoplayer.setOnNetChangeListener(new MNViderPlayer.OnNetChangeListener() {
            @Override
            public void onWifi(MediaPlayer mediaPlayer) {
            }

            @Override
            public void onMobile(MediaPlayer mediaPlayer) {
            }

            @Override
            public void onNoAvailable(MediaPlayer mediaPlayer) {
                Toast.makeText(VideoActivity.this, "No connection available", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mn_videoplayer.isFullScreen()) {
            mn_videoplayer.setOrientationPortrait();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mn_videoplayer.pauseVideo();
    }

    @Override
    protected void onDestroy() {
        //Be sure to destroy View
        if (mn_videoplayer != null) {
            mn_videoplayer.destroyVideo();
            mn_videoplayer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

}
