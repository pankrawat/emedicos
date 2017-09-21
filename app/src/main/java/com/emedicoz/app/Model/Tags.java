package com.emedicoz.app.Model;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 07/06/17.
 */

public class Tags implements Serializable {

    private String id;

    private String text;

    private String status;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getText ()
    {
        return text;
    }

    public void setText (String text)
    {
        this.text = text;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

}
