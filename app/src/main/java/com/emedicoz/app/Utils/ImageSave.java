package com.emedicoz.app.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

/**
 * Created by sUrYa on 6/24/2016.
 */
public class ImageSave {

    public static String createImageFile(Activity activity, String name) {
        File path = new File(Environment.getExternalStorageDirectory().getPath() + "/" + activity.getPackageName() + "/" + "Images");
        if (!path.exists()) {
            path.mkdirs();
        }
        Calendar cal = Calendar.getInstance();
        String timeStamp = String.valueOf(cal.getTimeInMillis());
        String imageFileName = name + timeStamp + "_";
/*
File mFileTemp = null;
if (Environment.MEDIA_MOUNTED.equals(state)) {
try {
mFileTemp = File.createTempFile(imageFileName, ".png", Environment.getExternalStorageDirectory());
} catch (IOException e) {
e.printStackTrace();
}
} else {
try {
mFileTemp = File.createTempFile(imageFileName, ".png", activity.getFilesDir());
} catch (IOException e) {
e.printStackTrace();
}
}
return mFileTemp;
*/
        return imageFileName;
    }


    public static String saveImageToSdcard(Bitmap currentImage, Activity activity, String name) {

        String path = "";
//  File file = createImageFile(activity,name);
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + activity.getPackageName() + "/" + "Images" + "/" + createImageFile(activity, name) + ".png");
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(file);
            currentImage.compress(Bitmap.CompressFormat.PNG, 70, fout);
            fout.flush();
            path = file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            path = null;
        }
        return path;
    }
}
