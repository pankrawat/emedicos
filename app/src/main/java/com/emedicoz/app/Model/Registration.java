package com.emedicoz.app.Model;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 06/10/17.
 */

public class Registration implements Serializable {

    static Registration registration;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String user_id;
    private String name;
    private String profilepicture;
    private String master_id;
    private String master_id_level_one;
    private String master_id_level_two;
    private String optional_text;
    private String master_id_name;
    private String master_id_level_one_name;
    private String master_id_level_two_name;
    private String interested_course_text;

    public String getInterested_course_text() {
        return interested_course_text;
    }

    public void setInterested_course_text(String interested_course_text) {
        this.interested_course_text = interested_course_text;
    }

    public String getMaster_id_name() {
        return master_id_name;
    }

    public void setMaster_id_name(String master_id_name) {
        this.master_id_name = master_id_name;
    }

    public String getMaster_id_level_one_name() {
        return master_id_level_one_name;
    }

    public void setMaster_id_level_one_name(String master_id_level_one_name) {
        this.master_id_level_one_name = master_id_level_one_name;
    }

    public String getMaster_id_level_two_name() {
        return master_id_level_two_name;
    }

    public void setMaster_id_level_two_name(String master_id_level_two_name) {
        this.master_id_level_two_name = master_id_level_two_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public static Registration newInstance() {
        registration = new Registration();
        return registration;

    }

    public static Registration getInstance() {
        if (registration == null) {
            registration = new Registration();
        }
        return registration;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getMaster_id_level_one() {
        return master_id_level_one;
    }

    public void setMaster_id_level_one(String master_id_level_one) {
        this.master_id_level_one = master_id_level_one;
    }

    public String getMaster_id_level_two() {
        return master_id_level_two;
    }

    public void setMaster_id_level_two(String master_id_level_two) {
        this.master_id_level_two = master_id_level_two;
    }

    public String getOptional_text() {
        return optional_text;
    }

    public void setOptional_text(String optional_text) {
        this.optional_text = optional_text;
    }

    public String getInterested_course() {
        return interested_course;
    }

    public void setInterested_course(String interested_course) {
        this.interested_course = interested_course;
    }

    private String interested_course;

}
