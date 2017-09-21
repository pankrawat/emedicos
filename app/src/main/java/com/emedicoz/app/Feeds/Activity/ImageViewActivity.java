package com.emedicoz.app.Feeds.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.emedicoz.app.Feeds.Fragment.BlankFragment;
import com.emedicoz.app.Model.PostFile;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;

import java.util.ArrayList;

public class ImageViewActivity extends AppCompatActivity {
    private static final String TAG = "Touch";

    ViewPager mPager;
    PagerAdapter mPagerAdapter;

    ArrayList<PostFile> ImageList;
    private LinearLayout dotsLayout;
    private ImageView[] dots;

    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        if (getIntent() != null) {
            ImageList = (ArrayList<PostFile>) getIntent().getSerializableExtra(Const.IMAGES);
            position = getIntent().getIntExtra(Const.POSITION, 0);
        }
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        mPager = (ViewPager) findViewById(R.id.imageviewpager);

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(position, true);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        Helper.logUser(this);
        addBottomDots(position);
    }

    private void addBottomDots(int currentPage) {
        dotsLayout.removeAllViews();
        if (ImageList.size() > 1) {
            dots = new ImageView[ImageList.size()];
            for (int i = 0; i < dots.length; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageResource(R.drawable.nonselecteditem_dot);
                dots[i].setPadding(5, 5, 5, 0);
                dotsLayout.addView(dots[i]);
            }
            if (dots.length > 0)
                dots[currentPage].setImageResource(R.drawable.selecteditem_dot);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            BlankFragment fragment = BlankFragment.newInstance(ImageList.get(position).getLink());
            return fragment;
        }

        @Override
        public int getCount() {
            return ImageList.size();
        }
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();
            addBottomDots(mPager.getCurrentItem());
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
