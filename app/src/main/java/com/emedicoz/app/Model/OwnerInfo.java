package com.emedicoz.app.Model;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 08/17/17.
 */


public class OwnerInfo implements Serializable {
    private String id;

    private String profile_picture;

    private String name;
    private String speciality;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

