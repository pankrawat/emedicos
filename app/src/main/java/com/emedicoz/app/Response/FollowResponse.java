package com.emedicoz.app.Response;

import com.emedicoz.app.Model.People;
import com.emedicoz.app.Response.ParentResponse.ParentResponse;

/**
 * Created by Cbc-03 on 06/25/17.
 */

public class FollowResponse extends ParentResponse {

    private String id;

    private boolean watcher_following;

    private String follower_id;

    private String action;

    private String creation_time;

    private People viewable_user;

    private String user_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isWatcher_following() {
        return watcher_following;
    }

    public void setWatcher_following(boolean watcher_following) {
        this.watcher_following = watcher_following;
    }

    public String getFollower_id() {
        return follower_id;
    }

    public void setFollower_id(String follower_id) {
        this.follower_id = follower_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCreation_time() {
        return creation_time;
    }

    public void setCreation_time(String creation_time) {
        this.creation_time = creation_time;
    }

    public People getViewable_user() {
        return viewable_user;
    }

    public void setViewable_user(People viewable_user) {
        this.viewable_user = viewable_user;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
