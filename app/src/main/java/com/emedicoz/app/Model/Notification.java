package com.emedicoz.app.Model;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 06/28/17.
 */

public class Notification implements Serializable {
    private String post_id;

    private String message;

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
