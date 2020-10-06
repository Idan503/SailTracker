package com.idan_koren_israeli.sailtracker.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.idan_koren_israeli.sailtracker.activity.BaseActivity;

import java.io.ByteArrayOutputStream;

// For making glide usage more convenient
public class CommonUtils {
    Context context;

    @SuppressLint("StaticFieldLeak")
    private static CommonUtils single_instance = null;
    // This WILL NOT cause a memory leak - *using application context only*

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CHOOSE = 1;

    private CommonUtils(Context context){
        this.context = context;
    }

    public static CommonUtils getInstance(){
        return single_instance;
    }

    public static CommonUtils
    initHelper(Context context){
        if(single_instance == null)
            single_instance = new CommonUtils(context.getApplicationContext());
        return single_instance;
    }



    //region Glide Image Load

    public void setImageResource(@NonNull ImageView image, int resId){
        Glide.with(context)
                .load(resId)
                .into(image);
    }

    public void setImageResource(@NonNull ImageView image, Uri uri){
        Glide.with(context)
                .load(uri)
                .into(image);
    }

    public void setImageResource(@NonNull ImageView image, Uri uri, RequestListener<Drawable> listener){
        Glide.with(context)
                .load(uri).addListener(listener)
                .into(image);
    }


    //endregion


    public void showToast(String message){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }


    //region Photos & Camera

    // Opens the camera and returns result of image's bitmap
    public void dispatchTakePictureIntent(BaseActivity callerActivity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            callerActivity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    // Let user choose between having the photo from camera or from device's file system
    public void dispatchChoosePictureIntent(BaseActivity callerActivity){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent storagePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        storagePictureIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(takePictureIntent, "Upload or take a picture...");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{storagePictureIntent});
        callerActivity.startActivityForResult(chooserIntent, REQUEST_IMAGE_CHOOSE );
    }

    public byte[] convertBitmapToBytes(Bitmap photo, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, quality, stream);

        return stream.toByteArray();
    }

    //endregion

}
