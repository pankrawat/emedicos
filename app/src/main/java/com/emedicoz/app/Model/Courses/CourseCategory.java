package com.emedicoz.app.Model.Courses;

import java.io.Serializable;

/**
 * Created by Cbc-03 on 09/04/17.
 */

public class CourseCategory implements Serializable {
    private String position;

    private String id;

    private String name;

    private String parent_fk;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_fk() {
        return parent_fk;
    }

    public void setParent_fk(String parent_fk) {
        this.parent_fk = parent_fk;
    }
}
