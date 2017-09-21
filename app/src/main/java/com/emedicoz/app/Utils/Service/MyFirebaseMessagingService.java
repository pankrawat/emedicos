package com.emedicoz.app.Utils.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.emedicoz.app.Feeds.Activity.FeedsActivity;
import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Notification.NotificationUtils;
import com.emedicoz.app.Utils.SharedPreference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().get(Const.MESSAGE).toString());
                if (SharedPreference.getInstance().getBoolean(Const.IS_USER_LOGGED_IN) && !SharedPreference.getInstance().getBoolean(Const.IS_NOTIFICATION_BLOCKED)) {
                    handleDataMessage(json);
                    SharedPreference.getInstance().putInt(Const.NOTIFICATION_COUNT, SharedPreference.getInstance().getInt(Const.NOTIFICATION_COUNT) + 1);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        // app is in foreground, broadcast the push message
        Intent pushNotification = new Intent();
        pushNotification.setAction("android.intent.action.MAIN");
        pushNotification.putExtra(Const.MESSAGE, message);
        sendBroadcast(pushNotification);

        // play notification sound
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();

    }

    private void handleDataMessage(JSONObject json) {
        try {
            Intent intent = null;
            String message = json.optString(Const.MESSAGE);
            JSONObject data = null;
            String post_id = null;
            int notification_code = json.optInt(Const.NOTIFICATION_CODE);
            if (json.optJSONObject(Const.DATA) != null) {
                data = json.getJSONObject(Const.DATA);
                post_id = data.optString(Const.POST_ID);
            }

            handleNotification(message);
            if (notification_code == 401) {
                intent = new Intent(this, FeedsActivity.class); // for live notification will go to feeds fragment
            } else {
                intent = new Intent(this, PostActivity.class); // notification fragment
                intent.putExtra(Const.FRAG_TYPE, Const.COMMENT);
                intent.putExtra(Const.POST_ID, post_id);
            }
            showNotification(message, getString(R.string.app_name), intent);

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }


    public void showNotification(String pushMessage, String pushTitle, Intent intent) {

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.medicos_icon);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon_small)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 120, 120, false))
                .setContentTitle(pushTitle)
                .setContentText(pushMessage)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{500, 500, 500, 500, 500})
                .setContentIntent(pendingIntent);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Random random = new Random();
        int notificationId = random.nextInt(10000);
        notificationManager.notify(notificationId/* ID of notification */, notificationBuilder.build());
    }

}