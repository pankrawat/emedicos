package com.emedicoz.app.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.emedicoz.app.Model.User;
import com.emedicoz.app.Response.PostResponse;
import com.google.gson.Gson;

/**
 * Created by Abhishek Singh Arya on 29/10/2015.
 */
public class SharedPreference {
    public static final String MY_PREFERENCES = "MY_PREFERENCES";
    public static final int MODE = Context.MODE_PRIVATE;
    private static SharedPreference pref;
    private SharedPreferences sharedPreference;
    private SharedPreferences.Editor editor;

    private SharedPreference() {
        sharedPreference = eMedicozApp.getAppContext().getSharedPreferences(MY_PREFERENCES, MODE);
        editor = sharedPreference.edit();
    }

    public static SharedPreference getInstance() {
        if (pref == null) {
            pref = new SharedPreference();
        }
        return pref;
    }

    public String getString(String key) {
        return sharedPreference.getString(key, "");
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }


    public int getInt(String key) {
        return sharedPreference.getInt(key, 0);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }


    public long getLong(String key) {
        return sharedPreference.getLong(key, 0l);
    }


    public void putLong(String key, long value) {
        editor.putLong(key, value).commit();
    }


    public float getFloat(String key) {
        return sharedPreference.getFloat(key, 0.0f);
    }


    public void putFloat(String key, float value) {
        editor.putFloat(key, value).commit();
    }

    public boolean getBoolean(String key) {
        //    editor.putBoolean(key, value).commit();
        return sharedPreference.getBoolean(key, false);
    }


    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public void setLoggedInUser(User user) {
        editor.putString(Const.USER_LOGGED_IN, new Gson().toJson(user));
        editor.commit();
    }

    public void ClearLoggedInUser() {
        editor.putString(Const.USER_LOGGED_IN, new Gson().toJson(new User()));
        editor.commit();
    }

    public void setPost(PostResponse post) {
        editor.putString(Const.POST, new Gson().toJson(post));
        editor.commit();
    }

    public User getLoggedInUser() {
        User user = null;
        String userJson = sharedPreference.getString(Const.USER_LOGGED_IN, null);
        if (userJson != null && userJson.trim().length() > 0)
            user = new Gson().fromJson(userJson, User.class);
        return user;
    }


    public PostResponse getPost() {
        PostResponse post = null;
        String postJson = sharedPreference.getString(Const.POST, null);
        if (postJson != null && postJson.trim().length() > 0)
            post = new Gson().fromJson(postJson, PostResponse.class);
        return post;
    }


    public boolean contains(String key) {
        return sharedPreference.contains(key);
    }

    public void remove(String key) {
        editor.remove(key).commit();
    }
}
