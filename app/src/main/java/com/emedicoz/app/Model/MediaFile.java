package com.emedicoz.app.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 06/19/17.
 */

public class MediaFile implements Serializable {

    private String id;
    private Bitmap image;
    private String file_type;
    private String file;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    private String file_name;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }
}
