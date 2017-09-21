package com.emedicoz.app.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.emedicoz.app.R;
import com.emedicoz.app.Utils.Const;
import com.emedicoz.app.Utils.Helper;

import java.io.File;
import java.io.FileOutputStream;


public class CropActivity extends Activity {

    private String currentPhotoPath;
    private CropImageView imageToCrop;
    private int targetSize;
    private Bitmap currentImage;
    private RelativeLayout imageProgress;
    private String resultAs = Const.PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.crop_layout);
        imageToCrop = (CropImageView) findViewById(R.id.cropImageView);
        imageProgress = (RelativeLayout) findViewById(R.id.imageProgress);
        Helper.logUser(this);
        CropImageView.CropMode mode = CropImageView.CropMode.RATIO_1_1;
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.containsKey(Const.RESULT_AS)) {
                resultAs = bundle.getString(Const.RESULT_AS);
            }
            if (getIntent().getExtras().containsKey(Const.MODE)) {
                mode = (CropImageView.CropMode) getIntent().getSerializableExtra(Const.MODE);
            }
            currentPhotoPath = bundle.getString(Const.IMAGE_PATH);
        }
        imageToCrop.setCropMode(mode);
        targetSize = getResources().getDisplayMetrics().widthPixels;
        if (currentPhotoPath != null && !currentPhotoPath.equals("")) {
            new DecodeTask().execute();
        }
        findViewById(R.id.buttonCancleCrop).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        findViewById(R.id.buttonSaveImage).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onSavedClick();
            }
        });
        findViewById(R.id.rotate).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                imageToCrop.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });
    }


    class DecodeTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                currentImage = decodeSampledBitmap(targetSize);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                Log.e("Setting bitmap", "OutOfMemoryError");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Setting bitmap", " Error");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (currentImage != null) {
                imageToCrop.setImageBitmap(currentImage);
            }
        }
    }

    private Bitmap decodeSampledBitmap(int size) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, options);
        options.inSampleSize = calculateInSampleSize(options, size);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(currentPhotoPath, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqWidth || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqWidth && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void onSavedClick() {
        try {
            imageProgress.setVisibility(View.VISIBLE);
            currentImage = imageToCrop.getCroppedBitmap();
            if (resultAs.equals("bitmap")) {
                if (currentImage != null) {
                    //ReferenceWrapper.getInstance(this).recentBitmap = currentImage;
                }
                setResult(RESULT_OK);
                finish();
            } else {
                ((Button) findViewById(R.id.buttonSaveImage)).setEnabled(false);
                new DoBackgroungJob().execute();
            }
        } catch (Throwable e) {
            Log.e("Exception", "during image crop");
            ((Button) findViewById(R.id.buttonSaveImage)).setEnabled(true);
            e.printStackTrace();
        }
    }


    class DoBackgroungJob extends AsyncTask<Void, Bitmap, String> {

        @Override
        protected String doInBackground(Void... params) {
            String path = null;
            if (currentImage != null) {
                File file = Helper.createImageFile(CropActivity.this);
                if (file != null) {
                    FileOutputStream fout;
                    try {
                        fout = new FileOutputStream(file);
                        currentImage.compress(Bitmap.CompressFormat.PNG, 70, fout);
                        fout.flush();
                        //path=Uri.fromFile(file);
                        path = file.getAbsolutePath();
                    } catch (Exception e) {
                        e.printStackTrace();
                        path = null;
                    }
				/*if (currentImage != null) {
					currentImage.recycle();
				}*/
                }
            } else {
                return null;
            }
            return path;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            imageProgress.setVisibility(View.GONE);
            ((Button) findViewById(R.id.buttonSaveImage)).setEnabled(true);
            if (result != null) {
                Intent data = new Intent();
                data.putExtra(Const.IMAGE_PATH, result);
                setResult(RESULT_OK, data);
            } else {
                Toast.makeText(CropActivity.this, "Insufficient Storage", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    }
}
