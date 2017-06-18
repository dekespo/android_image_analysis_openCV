package com.sample.dekespo.imageanalysisopencv;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PhotoUIControllerActivity extends Activity
{
    private ImageView _imageView;
    private Button _gobackButton;
    private Button _saveButton;
    private Bitmap _image;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "OnCreate Started");
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_photo_ui);

        _imageView = (ImageView) this.findViewById(R.id.photoImageView);

        Intent intent = getIntent();
        byte[] imageByteArray = intent.getByteArrayExtra(Constants.BITMAP_IMAGE_STR);
        _image = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        _imageView.setImageBitmap(_image);
        Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "Width " + _imageView.getWidth() + " Height " + _imageView.getHeight());

        _gobackButton = (Button) this.findViewById(R.id.button_goBack);
        _gobackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "Finishing the current activity " + Constants.LOG_PHOTO_UI_CONTROLLER);
                finish(); // Go back to the previous activity properly
            }
        });

        _saveButton = (Button) this.findViewById(R.id.button_save);
        _saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "Saving the photo");
                if (isStoragePermissionGranted())
                {
                    saveImage();
                }
            }
        });
    }

    private void saveImage()
    {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null)
        {
            Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "Error creating media file, check external storage permissions.");// ex.getMessage());
            return;
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            _image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        }
        catch (FileNotFoundException ex)
        {
            Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "File not found: " + ex.getMessage());
        }
        catch (IOException ex)
        {
            Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "Error accessing file: " + ex.getMessage());
        }

        Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "Media Store OK");
    }

    public boolean isStoragePermissionGranted()
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                Log.v(Constants.LOG_PHOTO_UI_CONTROLLER, "Permission is granted");
                return true;
            }
            else
            {
                Log.v(Constants.LOG_PHOTO_UI_CONTROLLER, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else
        {
            Log.v(Constants.LOG_PHOTO_UI_CONTROLLER, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) // TODO: put all permissions into one place and debug post-permission stuff
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Log.v(Constants.LOG_PHOTO_UI_CONTROLLER, "Permission: " + permissions[0] + " was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

    private File getOutputMediaFile()
    {

        Toast.makeText(PhotoUIControllerActivity.this, "Saving the picture...", Toast.LENGTH_SHORT).show();

        // SD Card or internal storage?
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
//        {
//            Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "Media is mounted");
//        }
//        else
//        {
//            Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "Media is NOT mounted. State = " + state);
//        }

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + Constants.OPENCV_IMAGE_NAME + "/");

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                Toast.makeText(PhotoUIControllerActivity.this, "The picture could not be saved!", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = Constants.OPENCV_IMAGE_NAME + "_" + timeStamp + ".jpg";
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + imageFileName);

        // To let Gallery know about the picture
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(mediaFile);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Toast.makeText(PhotoUIControllerActivity.this, "The picture has been saved.", Toast.LENGTH_SHORT).show();
        return mediaFile;
    }
}
