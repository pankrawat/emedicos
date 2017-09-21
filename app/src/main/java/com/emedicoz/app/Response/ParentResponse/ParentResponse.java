package com.emedicoz.app.Response.ParentResponse;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 05/22/17.
 */

public class ParentResponse implements Serializable {

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
