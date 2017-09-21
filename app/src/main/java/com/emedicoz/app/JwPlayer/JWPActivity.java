package com.emedicoz.app.JwPlayer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.events.listeners.VideoPlayerEvents;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;

public class JWPActivity extends AppCompatActivity implements VideoPlayerEvents.OnFullscreenListener{
    JWPlayerView playerView ;
    KeepScreenOnHandler keepScreenOnHandler;
    String Url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jwp);
        Url=getIntent().getExtras().getString(Const.VIDEO_LINK);
        playerView = (JWPlayerView) findViewById(R.id.playerView);
        // Create a PlaylistItem that points to your content
        //Log.d("URL",Url);

        PlaylistItem item = new PlaylistItem(Url);//"https://s3.ap-south-1.amazonaws.com/dams-apps-production/12BD5F37-6AFF-4C45-9F98-C6925CEC8DB3-1458-000003B4C7C6745B.mp4");//("http://techslides.com/demos/sample-videos/small.mp4");
        keepScreenOnHandler=new KeepScreenOnHandler(playerView,getWindow());
// Load the PlaylistItem into the player
        playerView.load(item);
        playerView.play();
    }
    @Override
    protected void onResume() {
        // Let JW Player know that the app has returned from the background
        super.onResume();
        playerView.onResume();

    }

    @Override
    protected void onPause() {
        // Let JW Player know that the app is going to the background
        playerView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Let JW Player know that the app is being destroyed
        playerView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // Set fullscreen when the device is rotated to landscape
        playerView.setFullscreen(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE, true);
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Exit fullscreen when the user pressed the Back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (playerView.getFullscreen()) {
                playerView.setFullscreen(false, false);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFullscreen(boolean state) {
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            if (state) {
                actionBar.hide();
            } else {
                actionBar.show();
            }
        }
    /*
        If the root layout of this activity is the coordinator layout (default for new projects created in Android Studio 2.0+)
        Then we also want to unset fitsSystemWindows
    */
    }
}


