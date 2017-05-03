package com.sample.dekespo.imageanalysisopencv;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

class CameraUIController implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener
{
    private static final String TAG = "CameraUIController";

    private CameraBridgeViewBase _cameraBridgeViewBase;
    private ModeStatus _currentModeStatus = ModeStatus.COLOUR;
    private ColourTypeModes _currentColourType = ColourTypeModes.COLOR_RGB2GRAY;
    private ThresholdTypeModes _currentThresholdType = ThresholdTypeModes.THRESH_BINARY;
    private int _currentThresholdValue = 128;

    private Button _modeButton;
    private Button _colourTypeButton;
    private Button _thresholdTypeButton;
    private SeekBar _thresholdValueSeekBar;

    private void UpdateButtonName(ButtonTypes buttonType)
    {
        switch (buttonType)
        {
            case MODE:
                _modeButton.setText(_currentModeStatus.toString());
                break;
            case COLOURTYPE:
                _colourTypeButton.setText(_currentColourType.toString());
                break;
            case THRESHOLDTYPE:
                _thresholdTypeButton.setText(_currentThresholdType.toString());
                break;
            default:
                break;
        }
    }

    private void ControlEnableness()
    {
        _thresholdTypeButton.setEnabled(_currentModeStatus == ModeStatus.BINARY);
        _colourTypeButton.setEnabled(_currentModeStatus != ModeStatus.COLOUR);
        _thresholdValueSeekBar.setEnabled(_currentModeStatus == ModeStatus.BINARY);
    }

    // Initialiaze the buttons
    CameraUIController(View modeButton, View colourTypeButton, View thresholdTypeButton, View thresholdValueSeekBar) {
        _modeButton = (Button) modeButton;
        _modeButton.setText(_currentModeStatus.toString());
        _modeButton.setOnClickListener(this);
        _colourTypeButton = (Button) colourTypeButton;
        _colourTypeButton.setText(_currentColourType.toString());
        _colourTypeButton.setOnClickListener(this);
        _colourTypeButton.setEnabled(_currentModeStatus != ModeStatus.COLOUR);
        _thresholdTypeButton = (Button) thresholdTypeButton;
        _thresholdTypeButton.setText(_currentThresholdType.toString());
        _thresholdTypeButton.setEnabled(_currentModeStatus == ModeStatus.BINARY);
        _thresholdTypeButton.setOnClickListener(this);

        _thresholdValueSeekBar = (SeekBar) thresholdValueSeekBar;
        _thresholdValueSeekBar.setProgress(_currentThresholdValue);
        _thresholdValueSeekBar.setEnabled(_currentModeStatus == ModeStatus.BINARY);
        _thresholdValueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _currentThresholdValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        int size;
        int currentNum;
        switch ((v.getId()))
        {
            case R.id.modeButton:
                size = ModeStatus.values().length;
                currentNum = _currentModeStatus.ordinal();
                 _currentModeStatus = ModeStatus.values()[(currentNum + 1) % size];
                UpdateButtonName(ButtonTypes.MODE);
                break;
            case R.id.colourTypeButton:
                size = ColourTypeModes.values().length;
                currentNum = _currentColourType.ordinal();
                _currentColourType = ColourTypeModes.values()[(currentNum + 1) % size];
                UpdateButtonName(ButtonTypes.COLOURTYPE);
                break;
            case R.id.thresholdTypeButton:
                size = ThresholdTypeModes.values().length;
                currentNum = _currentThresholdType.ordinal();
                _currentThresholdType = ThresholdTypeModes.values()[(currentNum + 1) % size];
                UpdateButtonName(ButtonTypes.THRESHOLDTYPE);
                break;
            case R.id.thresholdValueSeekBar:
                break;
            default:
                break;
        }

        ControlEnableness();

    }

    @Override
    public void onCameraViewStarted(int width, int height) { }

    @Override
    public void onCameraViewStopped() { }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
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

    void LoadCamera(CameraBridgeViewBase viewById, int visible) {
        _cameraBridgeViewBase = viewById;
        _cameraBridgeViewBase.setVisibility(visible);
        _cameraBridgeViewBase.setCvCameraViewListener(this);
    }

    void EnableCamera()
    {
        _cameraBridgeViewBase.enableView();
        _cameraBridgeViewBase.enableFpsMeter();
    }

    void DisableCamera()
    {
        if (_cameraBridgeViewBase != null)
        {
            _cameraBridgeViewBase.disableView();
            _cameraBridgeViewBase.disableFpsMeter();
        }
    }
}
