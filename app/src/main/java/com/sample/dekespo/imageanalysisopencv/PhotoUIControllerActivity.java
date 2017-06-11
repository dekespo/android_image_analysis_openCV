package com.sample.dekespo.imageanalysisopencv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoUIControllerActivity extends Activity
{
    private ImageView _imageView;
    private Button _gobackButton;

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
        Bitmap photo = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
//        Toast.makeText(PhotoUIControllerActivity.this, "Saving the picture...", Toast.LENGTH_SHORT).show();
        _imageView.setImageBitmap(photo);
        Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "Width " + _imageView.getWidth() + " Height " + _imageView.getHeight());

        _gobackButton = (Button) this.findViewById(R.id.button_goBack);
        _gobackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent photoUIControllerIntent = new Intent(getApplicationContext(), MainActivity.class);
                Log.d(Constants.LOG_PHOTO_UI_CONTROLLER, "Starting " + Constants.LOG_MAIN_ACTIVIY);
                startActivity(photoUIControllerIntent);
            }
        });

        // TODO Add Save button to save the photo to the external storage

    }
}
