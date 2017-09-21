package com.emedicoz.app.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.crashlytics.android.Crashlytics;
import com.emedicoz.app.Feeds.Activity.FeedsActivity;
import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.Feeds.Activity.ProfileActivity;
import com.emedicoz.app.JwPlayer.JWPActivity;
import com.emedicoz.app.Login.Activity.LoginCatActivity;
import com.emedicoz.app.Login.Activity.SignInActivity;
import com.emedicoz.app.Model.PostFile;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.PostResponse;
import com.emedicoz.app.Utils.OfflineData.eMedicozStorage;
import com.emedicoz.app.views.WebViewActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cbc-03 on 05/22/17.
 */

public class Helper {

    public static boolean DataNotValid(EditText view) {
        view.setError("This field is required");
        view.requestFocus();
        return false;
    }


    public static eMedicozStorage storage;
    private static Tracker sTracker;

    public static eMedicozStorage getStorageInstance(Activity activity) {
        try {
            storage = new eMedicozStorage(activity, activity.getString(R.string.app_name), activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return storage;
    }

    public static String youtubevalidation(String des) {

        des = des.trim();
        String[] parts = des.split("\\s+");

        Log.d("Youtube Validation", "Enter");
        Log.d("String", parts[0]);
        final String regex1 = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|watch\\?v%3D|\u200C\u200B%2Fvideos%2F|embed%2\u200C\u200BF|youtu.be%2F|%2Fv%2\u200C\u200BF)[^#\\&\\?\\n]*";
        final Pattern pattern1 = Pattern.compile(regex1, Pattern.MULTILINE);
        for (int i = 0; i < parts.length; i++) {
            final Matcher matcher1 = pattern1.matcher(parts[i]);
            Log.d("Youtube Validation", "Matching");
            if (matcher1.find()) {
                Log.d("Youtube Validation", "Matched");
                return matcher1.group();
            }
        }
        return null;
    }

    public static void logUser(Activity activity) {
        eMedicozApp application = (eMedicozApp) activity.getApplication();
        sTracker = application.getDefaultTracker();

        // TODO: Use the current user's information
        // You can call any combination of these three methods
        if (SharedPreference.getInstance().getLoggedInUser() != null) {
            Crashlytics.setUserIdentifier(SharedPreference.getInstance().getLoggedInUser().getId());
            Crashlytics.setUserEmail(SharedPreference.getInstance().getLoggedInUser().getEmail());
            Crashlytics.setUserName(SharedPreference.getInstance().getLoggedInUser().getName());
            sTracker.setScreenName("Image~" + SharedPreference.getInstance().getLoggedInUser().getName());

        } else {
            Crashlytics.setUserIdentifier(String.valueOf(Calendar.getInstance().getTimeInMillis()));
            Crashlytics.setUserEmail("g@email.com");
            Crashlytics.setUserName("Abc");
            sTracker.setScreenName("Image~" + "Abc");
        }
        sTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void rateapp(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            activity.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    public static String getVersionName(Activity activity) {
        String version = "";
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static int getVersionCode(Activity activity) {
        int version = 0;
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static ArrayList<String> gettitleList(Activity activity) {
        ArrayList<String> expandableListTitle = new ArrayList<>();
        expandableListTitle.add(activity.getString(R.string.feeds));
//        expandableListTitle.add(activity.getString(R.string.course));
        expandableListTitle.add(activity.getString(R.string.savedNotes));
        expandableListTitle.add(activity.getString(R.string.feedback));
        expandableListTitle.add(activity.getString(R.string.appSettings));
        expandableListTitle.add(activity.getString(R.string.logout));

        return expandableListTitle;
    }

    public static ArrayList<String> getcourseSubList(Context activity) {
        ArrayList<String> coursesublist = new ArrayList<>();
        coursesublist.add(activity.getString(R.string.allcourses));
        coursesublist.add(activity.getString(R.string.mycourse));
        return coursesublist;
    }

    public static File createImageFile(Context ctx) {
        File extStorageAppBasePath = null;
        File extStorageAppCachePath;
        String state = Environment.getExternalStorageState();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mFileTemp = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // Retrieve the base path for the application in the external storage
            File externalStorageDir = Environment.getExternalStorageDirectory();

            if (externalStorageDir != null) {
                // {SD_PATH}/Android/data/com.app.urend
                extStorageAppBasePath = new File(externalStorageDir.getAbsolutePath() +
                        File.separator + "Android" + File.separator + "data" +
                        File.separator + ctx.getPackageName() + File.separator + "EmedicozImages");
            }

            if (extStorageAppBasePath != null) {
                // {SD_PATH}/Android/data/com.app.urend/cache
                extStorageAppCachePath = new File(extStorageAppBasePath.getAbsolutePath() +
                        File.separator + "cache");

                boolean isCachePathAvailable = true;

                if (!extStorageAppCachePath.exists()) {
                    // Create the cache path on the external storage
                    isCachePathAvailable = extStorageAppCachePath.mkdirs();
                }

                if (!isCachePathAvailable) {
                    // Unable to create the cache path
                    extStorageAppCachePath = null;
                }
            }
        }
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            try {
                if (extStorageAppBasePath != null) {
                    mFileTemp = File.createTempFile(imageFileName, ".jpg", extStorageAppBasePath);
                } else {
                    mFileTemp = File.createTempFile(imageFileName, ".jpg", Environment.getExternalStorageDirectory());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (extStorageAppBasePath != null) {
                    mFileTemp = File.createTempFile(imageFileName, ".jpg", extStorageAppBasePath);
                } else {
                    mFileTemp = File.createTempFile(imageFileName, ".jpg", ctx.getFilesDir());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mFileTemp;
    }

    //methods to compress image starts//
    public static Bitmap decodeSampledBitmap(String url, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeFile(url, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(url, options);
    }

    //methods to compress image starts//
    public static byte[] FileToByteArray(String file) {
        File fil = new File(file);

        byte[] b = new byte[(int) fil.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(fil);
            fileInputStream.read(b);
            for (int i = 0; i < b.length; i++) {
                System.out.print((char) b[i]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        } catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }
        return b;
    }

    public static void DownloadfilefromURL(Activity activity, PostFile postFile) {
        Log.e("Download", "This is state " + Environment.getExternalStorageState());
        Log.e("Download", "This is getAbsolutePath " + Environment.getExternalStorageDirectory().getAbsolutePath());

        File path = new File(Environment.getExternalStorageDirectory().getPath() + "/" + activity.getPackageName() + "/" + "Downloaded");
        if (!path.exists()) {
            Log.e("Download", "path created " + path.toString());
            path.mkdirs();
        }

        String FileName = postFile.getFile_info() + "." + postFile.getFile_type();
        File filepath = new File(path + "/" + FileName);
        if (!filepath.exists()) {
            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(postFile.getLink());
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE |
                    DownloadManager.Request.NETWORK_WIFI).setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED).
                    setTitle(activity.getResources().getString(R.string.app_name)).setAllowedOverRoaming(true);
            File internalPath = new File(Environment.getExternalStorageDirectory().getPath() + "/" + activity.getPackageName() + "/" + "Downloaded");
            if (!internalPath.exists()) {
                Log.e("Download", "internalPath created " + internalPath.toString());
                internalPath.mkdirs();
            }

            request.setDestinationInExternalPublicDir(activity.getPackageName() + "/" + "Downloaded", FileName);
            request.allowScanningByMediaScanner();
//            request.setMimeType("application/" + postFile.getFile_type());
            manager.enqueue(request);
            File NewFileCreated = new File(internalPath + "/" + FileName);
            Log.e("Download", "request created ");
            if (postFile.getFile_type().equals(Const.PDF))
                showPdf(NewFileCreated, activity, 1);
        }
    }

    public static void showPdf(File file, Activity activity, int type) {
        PackageManager packageManager = activity.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        if (type == 1)
            testIntent.setType("application/pdf");

        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        if (type == 1)
            intent.setDataAndType(uri, "application/pdf");
        activity.startActivity(intent);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static boolean DataNotValid(EditText view, int type) {
        if (type == 1) view.setError("This Email Id is invalid");
        else if (type == 2) view.setError("This Phone is invalid");
        view.requestFocus();
        return false;
    }

    public static String GetText(EditText text) {
        return text.getText().toString().trim();
    }

    public static boolean isConnected(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static String CapitalizeText(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.contains(" ")) {
                String[] strarr = text.split(" +");
                String fname = null;
                for (String name : strarr) {
                    name = Character.toUpperCase(name.charAt(0)) + name.substring(1); // d
                    fname = (fname == null ? name : fname + " " + name);
                }
                return fname;
            } else return Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
        return text;
    }

    public static void getVersionUpdateDialog(final Activity ctx) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(ctx);

        alertBuild
                .setTitle(ctx.getString(R.string.update_app_dialog_title))
                .setMessage(ctx.getString(R.string.update_app_dialog_message))
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Helper.rateapp(ctx);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        ctx.finishAffinity();
                    }
                });
        AlertDialog dialog = alertBuild.create();
        dialog.show();
        int alertTitle = ctx.getResources().getIdentifier("alertTitle", "id", "android");
        ((TextView) dialog.findViewById(alertTitle)).setGravity(Gravity.CENTER);
        ((TextView) dialog.findViewById(alertTitle)).setGravity(Gravity.CENTER);
    }

    public static TextDrawable GetDrawable(String text, Activity activity, String userid) {
        if (!TextUtils.isEmpty(text)) {
            String firstLetter = text.substring(0, 1);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, Const.color[DbAdapter.getInstance(activity).getColor(userid)]);
            return drawable;
        } else
            return null;
    }

    public static boolean isLinkedInInstalled(Activity activity) {
        PackageManager pm = activity.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo("com.linkedin.android", PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static void GoToOtpVerificationActivity(Activity activity, String otp, int type, String FragType) {
        Intent intent = new Intent(activity, LoginCatActivity.class);
        intent.putExtra(Const.OTP, otp);
        intent.putExtra(Const.TYPE, type);
        intent.putExtra(Const.FRAG_TYPE, FragType);

        activity.startActivity(intent);
    }

    public static void ShowKeyboard(Context ctx) {
        InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void GoToChangePasswordActivity(Activity activity, String otp, String FragType) {
        Intent intent = new Intent(activity, LoginCatActivity.class);
        intent.putExtra(Const.OTP, otp);
        intent.putExtra(Const.FRAG_TYPE, FragType);
        activity.startActivity(intent);
    }

    public static void GoToVideoActivity(Activity activity, String url, String type) {
        Intent intent = new Intent(activity, JWPActivity.class);
        intent.putExtra(Const.VIDEO_LINK, url);
        intent.putExtra(Const.TYPE, type);
        activity.startActivity(intent);
    }

    public static void GoToMobileVerificationActivity(Activity activity, String FragType) {
        Intent intent = new Intent(activity, LoginCatActivity.class);
        intent.putExtra(Const.FRAG_TYPE, FragType);
        activity.startActivity(intent);
    }

    public static void GoToFeedsActivity(Context activity) {
        Intent intent = new Intent(activity, FeedsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void GoToRegistrationActivity(Activity activity, String FragType, String RegType) {
        Intent intent = new Intent(activity, PostActivity.class);// registration fragment
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Const.FRAG_TYPE, FragType);
        intent.putExtra(Const.TYPE, RegType);
        activity.startActivity(intent);
    }

    public static void GoToEditProfileActivity(Activity activity, String FragType, String RegType) {
        Intent intent = new Intent(activity, PostActivity.class);// edit profile registration fragment
        intent.putExtra(Const.FRAG_TYPE, FragType);
        intent.putExtra(Const.TYPE, RegType);
        activity.startActivity(intent);
    }

    public static void GoToProfileActivity(Activity activity, String id) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(Const.ID, id);
        activity.startActivity(intent);
        if (activity instanceof ProfileActivity)
            activity.finish();
    }

    public static void GoToPostActivity(Activity activity, PostResponse post, String type) {
        Intent intent = new Intent(activity, PostActivity.class); // comment fragment
        intent.putExtra(Const.FRAG_TYPE, type);
        intent.putExtra(Const.POST, post);
        activity.startActivity(intent);
    }

    public static void GoToWebViewActivity(Activity activity, String url) {
        Intent newintent = new Intent(activity, WebViewActivity.class);
        newintent.putExtra(Const.URL, url);
        activity.startActivity(newintent);
    }

    public static void SignOutUser(Context context) {
        SharedPreference sharedPreference = SharedPreference.getInstance();
        sharedPreference.ClearLoggedInUser();
        sharedPreference.putBoolean(Const.IS_USER_LOGGED_IN, false);
        sharedPreference.putBoolean(Const.IS_NOTIFICATION_BLOCKED, false);
        DbAdapter.getInstance(context).deleteAll(DbAdapter.TABLE_NAME_COLORCODE);
        Intent intent = new Intent(context, SignInActivity.class);
        intent.putExtra(Const.TYPE, "LOGIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
