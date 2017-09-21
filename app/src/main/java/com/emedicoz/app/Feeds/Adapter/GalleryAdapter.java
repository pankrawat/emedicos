package com.emedicoz.app.Feeds.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.emedicoz.app.Feeds.Activity.ImageViewActivity;
import com.emedicoz.app.Model.PostFile;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class GalleryAdapter extends PagerAdapter {

    ArrayList<PostFile> images;
    private static final String TAG = "GalleryAdapter";
    private final LayoutInflater mLayoutInflater;
    Context context;
    /**
     * The click event listener which will propagate click events to the parent or any other listener set
     */
    private View.OnClickListener mOnItemClickListener;

    /**
     * Constructor for gallery adapter which will create and screen slide of images.
     *
     * @param context The context which will be used to inflate the layout for each page.
     */
    public GalleryAdapter(@NonNull Context context, ArrayList<PostFile> images) {
        super();
        this.context = context;
        // Inflater which will be used for creating all the necessary pages
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // The items which will be displayed.
        this.images = images;
    }

    public void getDynamicHeight(int height) {

    }


    @Override
    public int getCount() {
        // Just to be safe, check also if we have an valid list of items - never return invalid size.
        return null == images ? 0 : images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // The object returned by instantiateItem() is a key/identifier. This method checks whether
        // the View passed to it (representing the page) is associated with that key or not.
        // It is required by a PagerAdapter to function properly.
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        // This method should create the page for the given position passed to it as an argument.
        // In our case, we inflate() our layout resource to create the hierarchy of view objects and then
        // set resource for the ImageView in it.
        // Finally, the inflated view is added to the container (which should be the ViewPager) and return it as well.

        // inflate our layout resource
        View itemView = mLayoutInflater.inflate(R.layout.single_row_image, container, false);
        ImageView IV = (ImageView) itemView.findViewById(R.id.imageIV);
        DisplayMetrics m = context.getResources().getDisplayMetrics();
        // Display the resource on the view
//        IV.setScaleX(m.densityDpi);
        displayGalleryItem(IV, images.get(position).getLink());

        // Add our inflated view to the container
        container.addView(itemView);
//
        // Detect the click events and pass them to any listeners
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.putExtra(Const.POSITION, position);
                intent.putExtra(Const.IMAGES, images);
                context.startActivity(intent);
            }
        });

        // Return our view
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Removes the page from the container for the given position. We simply removed object using removeView()
        // but could’ve also used removeViewAt() by passing it the position.
        try {
            // Remove the view from the container
            container.removeView((View) object);

            // Try to clear resources used for displaying this view
            Glide.clear(((View) object).findViewById(R.id.imageIV));
            // Remove any resources used by this view
            unbindDrawables((View) object);
            // Invalidate the object
            object = null;
        } catch (Exception e) {
            Log.w(TAG, "destroyItem: failed to destroy item and clear it's used resources", e);
        }
    }

    /**
     * Recursively unbind any resources from the provided view. This method will clear the resources of all the
     * children of the view before invalidating the provided view itself.
     *
     * @param view The view for which to unbind resource.
     */
    protected void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    /**
     * Set an listener which will notify of any click events that are detected on the pages of the view pager.
     *
     * @param onItemClickListener The listener. If {@code null} it will disable any events from being sent.
     */
//    public void setOnItemClickListener(ItemClickSupport.SimpleOnItemClickListener onItemClickListener) {
//        mOnItemClickListener = onItemClickListener;
//    }

    /**
     * Display the gallery image into the image view provided.
     *
     * @param galleryView The view which will display the image.
     * @param galleryItem The item from which to get the image.
     */
    private void displayGalleryItem(final ImageView galleryView, String galleryItem) {
        if (null != galleryItem) {
            DisplayMetrics dpm = context.getResources().getDisplayMetrics();
            final int pheight = 300 * (dpm.densityDpi / 160);
            Ion.with(galleryView.getContext()).load(galleryItem).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                @Override
                public void onCompleted(Exception e, Bitmap result) {
                    if (e == null) {
                        int h = result.getHeight();
                        int w = result.getWidth();
                        if (w > h) {
                            galleryView.setImageBitmap(result);
                        } else if (h < w && h < pheight) {
                            getDynamicHeight(h);
                            galleryView.setImageBitmap(result);
                        } else {
                            galleryView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            galleryView.setImageBitmap(result);
                        }
                    } else {
                        galleryView.setImageResource(R.mipmap.bg);
                        galleryView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
            });
        }
    }

}