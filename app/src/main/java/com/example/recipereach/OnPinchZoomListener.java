package com.example.recipereach;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

public class OnPinchZoomListener implements View.OnTouchListener {

    private final ScaleGestureDetector scaleGestureDetector;

    public OnPinchZoomListener(TextView textView) {
        scaleGestureDetector = new ScaleGestureDetector(textView.getContext(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        float scale = Math.max(0.5f, Math.min(2.0f, detector.getScaleFactor()));
                        textView.setTextSize(textView.getTextSize() * scale / textView.getContext().getResources().getDisplayMetrics().scaledDensity);
                        return true;
                    }
                });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }
}
