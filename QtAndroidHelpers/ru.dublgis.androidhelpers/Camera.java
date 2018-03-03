package ru.dublgis.androidhelpers;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.app.Activity;
import android.os.Bundle;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.net.Uri;
import android.content.pm.ActivityInfo;


public class Camera extends Activity{
        private  String mRequestedPhotoPath="";
        private  int REQUEST_IMAGE_CAPTURE = 1;
        private  String TAG = "Grym/DesktopServices";



        @Override
          protected void onCreate(Bundle savedInstanceState) {
              super.onCreate(savedInstanceState);
              //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
              mRequestedPhotoPath=getIntent().getExtras().getString("path");
              takePhoto();
          }



         public  void takePhoto(){
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File image = new File(mRequestedPhotoPath);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                    Log.e(image.getAbsolutePath(),"Photo0!!!!!!!");
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    return;

         }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                       DesktopUtils.setFlagPhoto(1);
                       this.finish();
                       return;

             }
             if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
                 DesktopUtils.setFlagPhoto(2);
                 this.finish();
                 return;
              }

        }


}

