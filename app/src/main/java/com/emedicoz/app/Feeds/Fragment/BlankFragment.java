package com.emedicoz.app.Feeds.Fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.views.TouchImageView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class BlankFragment extends Fragment{

    String image;
    ViewPager viewPager;

    public BlankFragment() {
    }

    public static BlankFragment newInstance(String image) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(Const.IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            image = getArguments().getString(Const.IMAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewPager=(ViewPager)container;
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }

  TouchImageView IV;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        IV = (TouchImageView) view.findViewById(R.id.imageIV);

        Ion.with(getActivity()).load(image).asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                if (e == null) {
                    IV.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    IV.setImageBitmap(result);


                } else IV.setImageResource(R.mipmap.bg);
            }
        });
    }
}
