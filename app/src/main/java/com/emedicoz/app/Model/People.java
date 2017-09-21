package com.emedicoz.app.Model;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 05/25/17.
 */

public class People implements Serializable {

    String id;
    String profile_picture;
    String name;
    private boolean watcher_following = false;

    public boolean isWatcher_following() {
        return watcher_following;
    }

    public void setWatcher_following(boolean watcher_following) {
        this.watcher_following = watcher_following;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

}
