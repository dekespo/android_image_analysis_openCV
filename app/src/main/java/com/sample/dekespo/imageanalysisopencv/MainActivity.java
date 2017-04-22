package com.sample.dekespo.imageanalysisopencv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.security.Policy;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase _cameraBridgeViewBase;
    private ModeStatus _currentModeStatus = ModeStatus.COLOR;

    private BaseLoaderCallback _baseLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load ndk built module, as specified in moduleName in build.gradle
                    // after opencv initialization
                    System.loadLibrary("native-lib");
                    _cameraBridgeViewBase.enableView();
                    _cameraBridgeViewBase.enableFpsMeter();
                }
                break;
                default:
                {
                    super.onManagerConnected(status);
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        // Initialiaze the button
        Button modeButton = (Button) findViewById(R.id.modeButton);
        modeButton.setText(_currentModeStatus.toString());
        modeButton.setOnClickListener(this);

        // Permissions for Android 6+
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);

        _cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.main_surface);
        _cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        _cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int size = ModeStatus.values().length;
        int currentNum = _currentModeStatus.ordinal();
        _currentModeStatus = ModeStatus.values()[(currentNum + 1) % size];
        Button modeButton = (Button) findViewById(R.id.modeButton);
        modeButton.setText(_currentModeStatus.toString());
    }

    @Override
    public void onPause()
    {
        super.onPause();
        disableCamera();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug())
        {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, _baseLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            _baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        disableCamera();
    }

    private void disableCamera()
    {
        if (_cameraBridgeViewBase != null)
        {
            _cameraBridgeViewBase.disableView();
            _cameraBridgeViewBase.disableFpsMeter();
        }
    }

    public void onCameraViewStarted(int width, int height) { }

    public void onCameraViewStopped() { }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        Mat matGray = inputFrame.gray();
        Mat matColour = inputFrame.rgba();

//        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//        {
//            rotate90(matColour, matColour, 90);
//        }

        switch (_currentModeStatus)
        {
            case HISTOEQ:
                histEq(matGray.getNativeObjAddr());
                break;
            case SALT:
                salt(matGray.getNativeObjAddr(), 2000);
                break;
            case COLOR:
                matGray = matColour;
                break;
            case INVERT:
                invert(matGray.getNativeObjAddr());
                break;
            default:
                Log.d(TAG, "Error in camera");
        }
        return matGray;
    }

    private native void histEq(long matAddrGray);
    private native void salt(long matAddrGray, int nbrElem);
    private native void invert(long matAddrGray);
    private native void rotate90(Mat input, Mat output, int degree);

}


