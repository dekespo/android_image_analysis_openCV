package com.sample.dekespo.imageanalysisopencv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase _cameraBridgeViewBase;
    private ModeStatus _currentModeStatus = ModeStatus.COLOUR;
    private ColourTypeModes _currentColourType = ColourTypeModes.COLOR_RGB2GRAY;
    private ThresholdTypeModes _currentThresholdType = ThresholdTypeModes.THRESH_BINARY;
    private int _currentThresholdValue = 128;

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

        // Initialiaze the buttons
        Button modeButton = (Button) findViewById(R.id.modeButton);
        modeButton.setText(_currentModeStatus.toString());
        modeButton.setOnClickListener(this);
        Button colourTypeButton = (Button) findViewById(R.id.colourTypeButton);
        colourTypeButton.setText(_currentColourType.toString());
        colourTypeButton.setOnClickListener(this);
        colourTypeButton.setEnabled(_currentModeStatus != ModeStatus.COLOUR);
        Button thresholdTypeButton = (Button) findViewById(R.id.thresholdTypeButton);
        thresholdTypeButton.setText(_currentThresholdType.toString());
        thresholdTypeButton.setEnabled(_currentModeStatus == ModeStatus.BINARY);
        thresholdTypeButton.setOnClickListener(this);

        SeekBar thresholdValueSeekBar = (SeekBar) findViewById(R.id.thresholdValueSeekBar);
        thresholdValueSeekBar.setProgress(_currentThresholdValue);
        thresholdValueSeekBar.setEnabled(_currentModeStatus == ModeStatus.BINARY);
        thresholdValueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _currentThresholdValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Permissions for Android 6+
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);

        _cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.main_surface);
        _cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        _cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int size;
        int currentNum;
        Button modeButton = (Button) findViewById(R.id.modeButton);
        Button colourTypeButton = (Button) findViewById(R.id.colourTypeButton);
        Button thresholdTypeButton = (Button) findViewById(R.id.thresholdTypeButton);
        SeekBar thresholdValueSeekBar = (SeekBar) findViewById(R.id.thresholdValueSeekBar);
        switch ((v.getId()))
        {
            case R.id.modeButton:
                size = ModeStatus.values().length;
                currentNum = _currentModeStatus.ordinal();
                _currentModeStatus = ModeStatus.values()[(currentNum + 1) % size];
                modeButton.setText(_currentModeStatus.toString());
                break;
            case R.id.colourTypeButton:
                size = ColourTypeModes.values().length;
                currentNum = _currentColourType.ordinal();
                _currentColourType = ColourTypeModes.values()[(currentNum + 1) % size];
                colourTypeButton.setText(_currentColourType.toString());
                break;
            case R.id.thresholdTypeButton:
                size = ThresholdTypeModes.values().length;
                currentNum = _currentThresholdType.ordinal();
                _currentThresholdType = ThresholdTypeModes.values()[(currentNum + 1) % size];
                thresholdTypeButton.setText(_currentThresholdType.toString());
                break;
            case R.id.thresholdValueSeekBar:
                break;
            default:
                break;
        }

        thresholdTypeButton.setEnabled(_currentModeStatus == ModeStatus.BINARY);
        colourTypeButton.setEnabled(_currentModeStatus != ModeStatus.COLOUR);
        thresholdValueSeekBar.setEnabled(_currentModeStatus == ModeStatus.BINARY);
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
        Mat cameraMat = inputFrame.rgba();
        long cameraAddress = cameraMat.getNativeObjAddr();

        Log.d(TAG, "Camera address = " + cameraAddress);
        Log.d(TAG, "Camera Mat address = " + cameraMat.getNativeObjAddr());
        Log.d(TAG, "Current Threshold Value = " + _currentThresholdValue);
        switch (_currentModeStatus)
        {
            case COLOUR:
            case GRAY:
            case BINARY:
            case HISTOEQ:
            case INVERT:
            case SALT:
            case GET_SHAPES:
                applyCameraAnalysisControl(cameraAddress, _currentModeStatus.getNumVal(), _currentColourType.getNumVal(), _currentThresholdType.getNumVal(), _currentThresholdValue, 255);
                break;
            default:
                Log.d(TAG, "Error in camera");
        }
        Log.d(TAG, "Final result");
        return cameraMat;
    }

    private native void applyCameraAnalysisControl(long matColourAddress, int modeState, int colourType, int thresholdtpye, int thresholdValue, int thresholdMaxValue);
}
