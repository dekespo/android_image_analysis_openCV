package com.sample.dekespo.imageanalysisopencv;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import java.util.ArrayList;

class CameraUIController implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener
{
    private static final String TAG = "CameraUIController";

    private CameraBridgeViewBase _cameraBridgeViewBase;
    private ModeStatus _currentModeStatus = ModeStatus.COLOUR;
    private ColourTypeModes _currentColourType = ColourTypeModes.COLOR_RGB2RGBA;
    private ThresholdTypeModes _currentThresholdType = ThresholdTypeModes.THRESH_BINARY;
    private SegmentationTypes _currentSegmentationType = SegmentationTypes.CIRCLES;
    private int _currentThresholdValue = 128;

    private Button _modeButton;
    private Button _colourTypeButton;
    private Button _thresholdTypeButton;
    private SeekBar _thresholdValueSeekBar;

    private boolean _colourTypeOrSegmentationType = false; // false colourtype, true segementationtype // TODO name, enum ...
    private View _colourTypeButtonView;
    private View _segmentationTypeButtonView;

    private void UpdateButtonName(ButtonTypes buttonType)
    {
        switch (buttonType)
        {
            case MODE:
                _modeButton.setText(_currentModeStatus.toString());
                break;
            case COLOURTYPE:
                if(!_colourTypeOrSegmentationType)
                {
                    _colourTypeButton.setText(_currentColourType.toString());
                }
                else
                {
                    _colourTypeButton.setText(_currentSegmentationType.toString());
                }
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
        _thresholdTypeButton.setEnabled(EnableThresholdTypeButton());
        _colourTypeButton.setEnabled(EnableColourTypeButton());
        _thresholdValueSeekBar.setEnabled(EnableThresholdValueSeekBar());
    }

    private boolean EnableColourTypeButton()
    {
        boolean modeCondition = _currentModeStatus != ModeStatus.HISTOEQ && _currentModeStatus != ModeStatus.SEGMENTATION;
        boolean colourTypeCondition = true;
        boolean thresholdTypeCondition = true;
        boolean customCondition = !(_currentModeStatus == ModeStatus.BINARY && (_currentThresholdType == ThresholdTypeModes.THRESH_OTSU || _currentThresholdType == ThresholdTypeModes.THRESH_TRIANGLE));
        return _colourTypeOrSegmentationType || (modeCondition && colourTypeCondition && thresholdTypeCondition && customCondition);
    }

    private boolean EnableThresholdValueSeekBar()
    {
        boolean modeCondition = _currentModeStatus == ModeStatus.BINARY || _currentModeStatus == ModeStatus.SEGMENTATION;
        boolean colourTypeCondition = true;
        boolean thresholdTypeCondition = _currentThresholdType != ThresholdTypeModes.THRESH_OTSU && _currentThresholdType != ThresholdTypeModes.THRESH_TRIANGLE;
        return modeCondition && colourTypeCondition && thresholdTypeCondition;
    }

    private boolean EnableThresholdTypeButton()
    {
        boolean modeCondition = _currentModeStatus == ModeStatus.BINARY || _currentModeStatus == ModeStatus.SEGMENTATION;
        boolean colourTypeCondition = true;
        boolean thresholdTypeCondition = true;
        return modeCondition && colourTypeCondition && thresholdTypeCondition;
    }

    private Button InitializeButton(View buttonView, String text, CameraUIController listener, boolean enableButton)
    {
        Button buttonToInitialize = (Button) buttonView;
        buttonToInitialize.setText(text);
        buttonToInitialize.setOnClickListener(listener);
        buttonToInitialize.setEnabled(enableButton);
        return buttonToInitialize;
    }

    // Initialiaze the buttons
    CameraUIController(View modeButton, View colourTypeButton, View thresholdTypeButton, View thresholdValueSeekBar) {

        _modeButton = InitializeButton(modeButton, _currentModeStatus.toString(), this, true);
        _colourTypeButton = InitializeButton(colourTypeButton, _currentColourType.toString(), this, EnableColourTypeButton());
        _thresholdTypeButton = InitializeButton(thresholdTypeButton, _currentThresholdType.toString(), this, EnableThresholdTypeButton());

        _colourTypeButtonView = colourTypeButton;
        _segmentationTypeButtonView = colourTypeButton;

        _thresholdValueSeekBar = (SeekBar) thresholdValueSeekBar;
        _thresholdValueSeekBar.setProgress(_currentThresholdValue);
        _thresholdValueSeekBar.setEnabled(EnableThresholdValueSeekBar());
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
        ArrayList<ThresholdTypeModes> thresholdTypeGroup = new ArrayList<ThresholdTypeModes>();
        ArrayList<ModeStatus> modeStatusGroup = new ArrayList<ModeStatus>();
        switch ((v.getId()))
        {
            case R.id.modeButton:
                size = ModeStatus.values().length;
                currentNum = _currentModeStatus.ordinal();
                 _currentModeStatus = ModeStatus.values()[(currentNum + 1) % size];

                modeStatusGroup.add(ModeStatus.HISTOEQ);
                modeStatusGroup.add(ModeStatus.SEGMENTATION);
                if(modeStatusGroup.contains(_currentModeStatus))
                {
                    _currentColourType = ColourTypeModes.COLOR_RGB2RGBA;
                    UpdateButtonName(ButtonTypes.COLOURTYPE);
                }

                thresholdTypeGroup.add(ThresholdTypeModes.THRESH_OTSU);
                thresholdTypeGroup.add(ThresholdTypeModes.THRESH_TRIANGLE);
                if(_currentModeStatus == ModeStatus.BINARY && thresholdTypeGroup.contains(_currentThresholdType))
                {
                    _currentThresholdType = ThresholdTypeModes.THRESH_BINARY;
                    UpdateButtonName(ButtonTypes.THRESHOLDTYPE);
                }

                if(_currentModeStatus == ModeStatus.SEGMENTATION)
                {
                    _colourTypeOrSegmentationType = true;
                    _colourTypeButton = null;
                    _colourTypeButton = InitializeButton(_segmentationTypeButtonView, _currentSegmentationType.toString(), this, true);
                    UpdateButtonName(ButtonTypes.COLOURTYPE);
                }
                else
                {
                    _colourTypeOrSegmentationType = false;
                    _colourTypeButton = null;
                    _colourTypeButton = InitializeButton(_colourTypeButtonView, _currentThresholdType.toString(), this, EnableThresholdTypeButton());
                    UpdateButtonName(ButtonTypes.COLOURTYPE);
                }

                UpdateButtonName(ButtonTypes.MODE);
                break;
            case R.id.colourTypeButton:
                if(!_colourTypeOrSegmentationType)
                {
                    size = ColourTypeModes.values().length;
                    currentNum = _currentColourType.ordinal();
                    _currentColourType = ColourTypeModes.values()[(currentNum + 1) % size];
                }
                else
                {
                    size = SegmentationTypes.values().length;
                    currentNum = _currentSegmentationType.ordinal();
                    _currentSegmentationType = SegmentationTypes.values()[(currentNum + 1) % size];
                }
                UpdateButtonName(ButtonTypes.COLOURTYPE);
                break;
            case R.id.thresholdTypeButton:
                size = ThresholdTypeModes.values().length;
                currentNum = _currentThresholdType.ordinal();
                _currentThresholdType = ThresholdTypeModes.values()[(currentNum + 1) % size];

                thresholdTypeGroup.add(ThresholdTypeModes.THRESH_OTSU);
                thresholdTypeGroup.add(ThresholdTypeModes.THRESH_TRIANGLE);
                if(_currentModeStatus == ModeStatus.BINARY && thresholdTypeGroup.contains(_currentThresholdType))
                {
                    _currentColourType = ColourTypeModes.COLOR_RGB2GRAY;
                    UpdateButtonName(ButtonTypes.COLOURTYPE);
                }

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

        applyCameraAnalysisControl(cameraAddress, _currentModeStatus.getNumVal(), _currentColourType.getNumVal(), _currentThresholdType.getNumVal(), _currentThresholdValue, 255, _currentSegmentationType.getNumVal());
        return cameraMat;
    }

    private native void applyCameraAnalysisControl(long matColourAddress, int modeState, int colourType, int thresholdtpye, int thresholdValue, int thresholdMaxValue, int segmentationType); // TODO: JNI Objects wrapping (SWIG) ...

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
