package com.emedicoz.app.Feeds.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emedicoz.app.Feeds.Activity.PostActivity;
import com.emedicoz.app.Model.MediaFile;
import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


/**
 * TODO: Replace the implementation with code for your data type.
 */
public class ImageRVAdapter extends RecyclerView.Adapter<ImageRVAdapter.ViewHolder> {

    ArrayList<MediaFile> imageArrayBM;
    Activity activity;


    public ImageRVAdapter(Activity activity, ArrayList<MediaFile> imageArrayBM) {
        this.imageArrayBM = imageArrayBM;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_newpost_image, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MediaFile image = imageArrayBM.get(position);
        if (image.getImage() == null) {
            holder.ImageIV.setScaleType(ImageView.ScaleType.FIT_CENTER);

            if (image.getFile_type().equals(Const.VIDEO)) {
                if (image.getFile().contains(Const.AMAZON_S3_IMAGE_PREFIX))
                    Ion.with(activity).load(image.getFile()).intoImageView(holder.ImageIV);
                else
                    Ion.with(activity).load(Const.AMAZON_S3_IMAGE_PREFIX + Const.AMAZON_S3_BUCKET_NAME_VIDEO_IMAGES +
                            "/" + image.getFile().split(".mp4")[0] + ".png")
                            .intoImageView(holder.ImageIV);
            } else {
                Ion.with(activity).load(image.getFile()).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (e == null) {
                            holder.ImageIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            holder.ImageIV.setImageBitmap(result);

                        } else holder.ImageIV.setImageResource(R.mipmap.bg);
                    }
                });
            }
        } else
            holder.ImageIV.setImageBitmap(image.getImage());

        holder.deleteIV.setTag(holder);
        holder.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewHolder holder1 = (ViewHolder) view.getTag();

                // if post it getting edited each old mediafile will have ids. if we will remove that id we have to send it to server to remove them from db.

                if (!TextUtils.isEmpty(imageArrayBM.get(holder1.getAdapterPosition()).getId()))
                    ((PostActivity) activity).addDeletedIdToMeta(imageArrayBM.get(holder1.getAdapterPosition()).getId());

                imageArrayBM.remove(holder1.getAdapterPosition());
                notifyItemRemoved(holder1.getAdapterPosition());
                if (imageArrayBM.size() == 0) {
                    ((PostActivity) activity).isimageadded = false;
                    ((PostActivity) activity).isvideoadded = false;
                    ((PostActivity) activity).isyoutubevideoattached = false;
                    ((PostActivity) activity).isattachmentadded = false;
                }

            }
        });
        // setting the name of the file
        if (image.getFile_type().equals(Const.PDF) || image.getFile_type().equals(Const.DOC) || image.getFile_type().equals(Const.XLS)) {
            holder.nameTV.setVisibility(View.VISIBLE);
            holder.nameTV.setText(image.getFile_name());
        } else holder.nameTV.setVisibility(View.GONE);

        //setting the play button if it is video
        if (image.getFile_type().equals(Const.VIDEO)) {
            holder.playIV.setVisibility(View.VISIBLE);
        } else holder.playIV.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return imageArrayBM.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ImageIV, deleteIV, playIV;

        TextView nameTV;

        public ViewHolder(final View view) {
            super(view);

            nameTV = (TextView) view.findViewById(R.id.nameTV);

            playIV = (ImageView) view.findViewById(R.id.playIV);
            ImageIV = (ImageView) view.findViewById(R.id.imageIV);
            deleteIV = (ImageView) view.findViewById(R.id.deleteIV);

        }

    }
}
