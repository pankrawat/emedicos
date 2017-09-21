package com.emedicoz.app.Model.Courses;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Cbc-03 on 09/04/17.
 */

public class CoursesData implements Serializable {
    private CourseCategory category_info;

    private ArrayList<Course> course_list;

    public CourseCategory getCategory_info() {
        return category_info;
    }

    public void setCategory_info(CourseCategory category_info) {
        this.category_info = category_info;
    }

    public ArrayList<Course> getCourse_list() {
        return course_list;
    }

    public void setCourse_list(ArrayList<Course> course_list) {
        this.course_list = course_list;
    }
}
