package com.emedicoz.app.Response.Registration;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 06/07/17.
 */

public class StreamResponse implements Serializable {

    private String id;

    private String visibilty;

    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVisibilty() {
        return visibilty;
    }

    public void setVisibilty(String visibilty) {
        this.visibilty = visibilty;
    }

    public String getText_name() {
        return text;
    }

    public void setText_name(String text) {
        this.text = text;
    }
}
