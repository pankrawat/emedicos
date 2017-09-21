package com.emedicoz.app.Courses.Activity;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.emedicoz.app.Common.BaseABNavActivity;
import com.emedicoz.app.Common.BaseABNoNavActivity;
import com.emedicoz.app.Courses.Fragment.AllCourses;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;

public class CourseActivity extends BaseABNoNavActivity {

    private String frag_type = "";

    @Override
    protected void initViews() {


    }

    @Override
    protected boolean addBackButton() {
        return true;
    }

    @Override
    protected Fragment getFragment() {
        switch (frag_type) {
            case Const.ALLCOURSES:
//                setTitle(Const.ALLCOURSES);
                return AllCourses.newInstance();

            case Const.MYCOURSES:

                return null;
        }
        return null;
    }
}
