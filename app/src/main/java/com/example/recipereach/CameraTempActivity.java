package com.example.recipereach;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mediapipe.framework.image.BitmapImageBuilder;
import com.google.mediapipe.framework.image.MPImage;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions;
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;

import java.util.List;



public class CameraTempActivity extends AppCompatActivity {

    private PreviewView previewView;

    private ExecutorService cameraExecutor;
    private HandTracker handTracker;
    private HandLandmarker handLandmarker;
    private static final int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_temp);

        previewView = findViewById(R.id.view_finder);
        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            try {
                HandLandmarker.HandLandmarkerOptions options = HandLandmarker.HandLandmarkerOptions.builder()
                        .setBaseOptions(BaseOptions.builder()
                                .setModelAssetPath("hand_landmarker.task")
                                .build())
//                        .setResultListener(
//                                (result, inputImage) -> {
//                                    analyzeImage(result);
//                                    // עבד את תוצאות נקודות הציון של היד כאן
//                                    // ...
//                                })
                        .setNumHands(1)
                        .setRunningMode(RunningMode.IMAGE)

                        .build();

                handLandmarker = HandLandmarker.createFromOptions(this, options);
                Log.d("CameraX", "finish on create ");
            } catch (Exception e) {
                Log.e("MediaPipe", "Error initializing HandLandmarker", e);
            }

            // handTracker = new HandTracker(this);
            startCamera();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Log.e("Permission", "Camera permission denied.");
        }
    }

    private void startCamera() {
        Log.d("CameraX", "start camera 1");
//        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
//                ProcessCameraProvider.getInstance(this);
//
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//
//                CameraSelector cameraSelector = new CameraSelector.Builder()
//                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
//                        .build();
//
//                Preview preview = new Preview.Builder().build();
//                preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//                Camera camera = cameraProvider.bindToLifecycle(
//                        this,
//                        cameraSelector,
//                        preview
//                );
//
//            } catch (Exception e) {
//                Log.e("CameraX", "Error starting camera", e);
//            }
//        }, ContextCompat.getMainExecutor(this));
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);


                Camera camera = cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );
                Log.d("CameraX", "in start camera");

            } catch (Exception e) {

                Log.e("CameraX", "Error starting camera", e);
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void analyzeImage(HandLandmarkerResult result) {
        Log.d("CameraX", "start camera analyzing");
        for (int i = 0; i < result.landmarks().size(); i++) {
            List<NormalizedLandmark> landmarks = result.landmarks().get(i);
            for (NormalizedLandmark landmark : landmarks) {
                Log.d("MediaPipe", "Landmark: (" + landmark.x() + ", " + landmark.y() + ")");
            }
        }
        Log.d("CameraX", "finish analyzing");
    }
    private void analyzeImage(@NonNull ImageProxy image) {
        try {
            Log.d("CameraX", "in analyze image");

            MPImage mpImage = detectLiveStream(image, true);
            if (handLandmarker == null) {
                Log.i("HandLandmarker", "HandLandmarker is not initialized");
                HandLandmarker.HandLandmarkerOptions options = HandLandmarker.HandLandmarkerOptions.builder()
                        .setBaseOptions(BaseOptions.builder()
                                .setModelAssetPath("hand_landmarker.task")
                                .build())
//                        .setResultListener(
//                                (result, inputImage) -> {
//                                    analyzeImage(result);
//                                    // עבד את תוצאות נקודות הציון של היד כאן
//                                    // ...
//                                })
                        .setNumHands(1)
                        .setRunningMode(RunningMode.IMAGE)

                        .build();

                handLandmarker = HandLandmarker.createFromOptions(this, options);
                Log.i("HandLandmarker", "HandLandmarker is initialized");
                return;
            }

            // עיבוד התמונה לזיהוי
            HandLandmarkerResult result = handLandmarker.detect(mpImage, ImageProcessingOptions.builder().build());

            // הדפסת נקודות הציון
            for (List<NormalizedLandmark> landmarks : result.landmarks()) {
                int i=0;
                for (NormalizedLandmark landmark : landmarks) {
                    Log.d("MediaPipe", "Landmark "+i+": (" + landmark.x() + ", " + landmark.y() + ")");
                    i++;
                }
            }

        } catch (Exception e) {
            Log.e("MediaPipe", "Error analyzing image", e);
        } finally {
            // סגירת התמונה כדי לשחרר משאבים
            image.close();
        }
    }

    public MPImage detectLiveStream(ImageProxy imageProxy, boolean isFrontCamera) {
        Bitmap bitmap = convertImageProxyToBitmap(imageProxy);

        // סיבוב התמונה לפי המידע מ-ImageProxy
        Matrix matrix = new Matrix();
        matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());

        if (isFrontCamera) {
            matrix.postScale(-1f, 1f, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return new BitmapImageBuilder(rotatedBitmap).build();
    }

    private Bitmap convertImageProxyToBitmap(ImageProxy imageProxy) {
        ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
        if (planes.length < 3) {
            throw new IllegalStateException("ImageProxy does not contain all required planes.");
        }

        ByteBuffer yBuffer = planes[0].getBuffer(); // Y
        ByteBuffer uBuffer = planes[1].getBuffer(); // U
        ByteBuffer vBuffer = planes[2].getBuffer(); // V

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        // איחוד הנתונים של YUV
        byte[] nv21 = new byte[ySize + uSize + vSize];
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        // המרת NV21 ל-Bitmap בפורמט ARGB_8888
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, imageProxy.getWidth(), imageProxy.getHeight(), null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, imageProxy.getWidth(), imageProxy.getHeight()), 100, outputStream);
        byte[] jpegBytes = outputStream.toByteArray();

        return BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);
    }


//        detectAsync(mpImage, frameTime);
//    }
//    @VisibleForTesting
//    public void detectAsync(MPImage mpImage, long frameTime) {
//        if (handLandmarker != null) {
//            handLandmarker.detectAsync(mpImage, frameTime);
//        }
//    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}


