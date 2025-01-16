package com.example.recipereach;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.camera.core.ImageProxy;

import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;

import java.util.List;

public class HandTracker {
    private HandLandmarker handLandmarker;
    private HandDetectionListener detectionListener;

    public HandTracker(Context context) {
        try {
            // אתחול HandLandmarker
            HandLandmarker.HandLandmarkerOptions options = HandLandmarker.HandLandmarkerOptions.builder()
                    .setBaseOptions(BaseOptions.builder()
                            .setModelAssetPath("hand_landmarker.task")
                            .build())
                    .setResultListener(this::returnLivestreamResult)
                    .setErrorListener(this::returnLivestreamError)
                    .setRunningMode(RunningMode.LIVE_STREAM)
                    .build();

            handLandmarker = HandLandmarker.createFromOptions(context, options);
        } catch (Exception e) {
            Log.e("HandTracker", "Error initializing HandLandmarker", e);
        }
    }

    private void returnLivestreamError(RuntimeException e) {
        Log.d("detection check","in result listener");
    }

    private void returnLivestreamResult(HandLandmarkerResult handLandmarkerResult, MPImage mpImage) {
        Log.d("detection check","in error listener");
    }

    // פונקציה להגדרת המאזין
    public void setHandDetectionListener(HandDetectionListener listener) {
        this.detectionListener = listener;
    }

    public List<NormalizedLandmark> detectHandLandmarks(ImageProxy imageProxy) {
        try {
            Bitmap bitmapBuffer = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
            try {
                bitmapBuffer.copyPixelsFromBuffer(imageProxy.getPlanes()[0].getBuffer());
            } finally {
                imageProxy.close();
            }


            Matrix matrix = new Matrix();
            matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());

            matrix.postScale(-1f, 1f, imageProxy.getWidth(), imageProxy.getHeight());

            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapBuffer, 0, 0, bitmapBuffer.getWidth(), bitmapBuffer.getHeight(), matrix, true);

            MPImage mpImage = new BitmapImageBuilder(rotatedBitmap).build();
            // ניתוח התמונה ולקיחת התוצאות
            HandLandmarkerResult result = handLandmarker.detect(mpImage,
                    ImageProcessingOptions.builder().build());
            List<List<NormalizedLandmark>> landmarks=result.landmarks();
            // אם זוהו ידיים, נחזיר את נקודות הציון של היד הראשונה (או את כל הידיים)
            if (!landmarks.isEmpty()) {
                if (detectionListener != null) {
                    detectionListener.onHandDetected(landmarks.get(0)); // מפעיל את המאזין עם נקודות הציון של היד הראשונה
                }
                return landmarks.get(0); // מחזיר את נקודות הציון של היד הראשונה
            }
        } catch (Exception e) {
            Log.e("HandTracker", "Error analyzing image", e);
        }
        return null;
    }
//
//    public void detectLiveStream(ImageProxy imageProxy, boolean isFrontCamera) {
////        if (runningMode != RunningMode.LIVE_STREAM) {
////            throw new IllegalArgumentException("Attempting to call detectLiveStream while not using RunningMode.LIVE_STREAM");
////        }
//
//        long frameTime = SystemClock.uptimeMillis();
//
//        Bitmap bitmapBuffer = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);
//        try {
//            bitmapBuffer.copyPixelsFromBuffer(imageProxy.getPlanes()[0].getBuffer());
//        } finally {
//            imageProxy.close();
//        }
//
//
//        Matrix matrix = new Matrix();
//        matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());
//
//        if (isFrontCamera) {
//            matrix.postScale(-1f, 1f, imageProxy.getWidth(), imageProxy.getHeight());
//        }
//
//        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapBuffer, 0, 0, bitmapBuffer.getWidth(), bitmapBuffer.getHeight(), matrix, true);
//
//        MPImage mpImage = new BitmapImageBuilder(rotatedBitmap).build();
//        detectAsync(mpImage, frameTime);
//    }
//    @VisibleForTesting
//    public void detectAsync(MPImage mpImage, long frameTime) {
//        if (handLandmarker != null) {
//            handLandmarker.detectAsync(mpImage, frameTime);
//        }
//    }
}

