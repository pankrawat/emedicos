package com.emedicoz.app.Courses.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emedicoz.app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllCourses extends Fragment {


    public AllCourses() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course, container, false);
    }

    public static AllCourses newInstance() {
        AllCourses allCourses = new AllCourses();
        return allCourses;
    }
}
