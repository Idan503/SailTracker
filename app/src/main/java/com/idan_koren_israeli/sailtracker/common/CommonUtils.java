package com.idan_koren_israeli.sailtracker.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.idan_koren_israeli.sailtracker.activity.BaseActivity;

import org.joda.time.DateTime;

import java.io.ByteArrayOutputStream;
import java.util.InputMismatchException;

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
                .apply(new RequestOptions()
                        .fitCenter()
                        .format(DecodeFormat.PREFER_ARGB_8888)
                        .override(Target.SIZE_ORIGINAL))
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
        Spannable centeredText = new SpannableString(message);
        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0, message.length() - 1,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        Toast.makeText(context, centeredText, Toast.LENGTH_LONG).show();
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
    public void dispatchChoosePictureIntent(BaseActivity callerActivity, String message){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent storagePictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
        storagePictureIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(takePictureIntent, message);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{storagePictureIntent});
        callerActivity.startActivityForResult(chooserIntent, REQUEST_IMAGE_CHOOSE );
    }

    public byte[] convertBitmapToBytes(Bitmap photo, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(photo!=null)
            photo.compress(Bitmap.CompressFormat.JPEG, quality, stream);

        return stream.toByteArray();
    }

    //endregion


    // region Screen Dimensions
    public int getScreenWidthPixels(@NonNull Activity activity) {
        return activity.getResources().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeightPixels(@NonNull Activity activity) {
        return activity.getResources().getDisplayMetrics().heightPixels;
    }

    public float getScreenWidthDp(@NonNull Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density = activity.getResources().getDisplayMetrics().density;
        return outMetrics.widthPixels / density;
    }

    public float getScreenHeightDp(@NonNull Activity activity){
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density = activity.getResources().getDisplayMetrics().density;
        return outMetrics.heightPixels / density;
    }
    //endregion

    // region Concurrency

    public void delayedTask(Runnable task, int delay){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(task, delay);
    }
    //endregion

    //region String Manipulations

    public String toPhoneString(String input) throws InputMismatchException {
        final int MIN_PHONE_LENGTH = 8;
        final String MESSAGE = "Invalid phone number";
        if(input.length()<8)
            throw new InputMismatchException(MESSAGE);

        // 0551234567 - > +97255123467
        char first = input.charAt(0);
        switch (first){
            case '0':
                return "+972" + input.substring(1);
            case '5':
                return "+972" + input;
            case '+':
                return input;
            default:
                throw new InputMismatchException();
        }

    }

    // Returns a string that contains only numbers from the input one
    public String toNumericString(@NonNull String input){
        StringBuilder builder = new StringBuilder();
        for(char c : input.toCharArray()){
            if(c >= '0' && c <='9')
                builder.append(c);
        }
        return builder.toString();
    }

    //endregion

    //region Permission Request

    private static final int REQUEST_LOCATION = 123;
    public void requestLocationPermission(Activity activity){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
    }

    //endregion

    //region

}
