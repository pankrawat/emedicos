package com.emedicoz.app.Utils.AmazonUpload;

import com.emedicoz.app.Model.MediaFile;

import java.util.ArrayList;

/**
 * Created by Cbc-03 on 06/19/17.
 */

public interface AmazonCallBack {
    abstract void onS3UploadData(ArrayList<MediaFile> images);
}
