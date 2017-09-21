package com.emedicoz.app.Model;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 06/23/17.
 */

public class HelpQuery implements Serializable {
    String user_id;
    String category;
    String title;
    String description;
    static HelpQuery helpquery;


    public static HelpQuery newInstance() {
        helpquery = new HelpQuery();
        return helpquery;
    }

    public static HelpQuery getInstance() {
        if (helpquery == null) {
            helpquery = new HelpQuery();
        }
        return helpquery;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
