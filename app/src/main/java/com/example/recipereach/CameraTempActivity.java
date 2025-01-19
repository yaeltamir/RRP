package com.example.recipereach;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker.HandLandmarkerOptions;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions;

public class CameraTempActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private HandLandmarker handLandmarker;
    private OverlayView overlayView;
    private Button startButton,endButton,editButton;
    private TextView receipe;

    private boolean start=false;
    private ProcessCameraProvider cameraProvider;

    private static final int CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_temp);

        //initialize components of the activity view
        previewView = findViewById(R.id.view_finder);
        cameraExecutor = Executors.newSingleThreadExecutor();
        overlayView =findViewById(R.id.overlayView);
        startButton=findViewById(R.id.startButton);
        endButton=findViewById(R.id.endButton);
        editButton=findViewById(R.id.editButton);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checks camera permissions
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CameraTempActivity.this,
                            new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                } else {
                    startCamera();
                }
            }
        });
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCamera();
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receipe=findViewById(R.id.receipeText);
                receipe.setText("edit...");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setHandLandmarker();
            startButton.performClick();
        } else {
            Log.e("Permission", "Camera permission denied.");
        }
    }
    public void setHandLandmarker(){
        try {
            HandLandmarkerOptions options = HandLandmarkerOptions.builder()
                    .setBaseOptions(BaseOptions.builder().setModelAssetPath("hand_landmarker.task")
                            .build())
                    .setNumHands(1)
                    .setRunningMode(RunningMode.IMAGE)
                    .build();

            handLandmarker = HandLandmarker.createFromOptions(this, options);
            Log.i("HandLandmarker", "finish setting handLandmarker");
        } catch (Exception e) {
            Log.e("MediaPipe", "Error initializing HandLandmarker", e);
        }
    }

    private void startCamera() {
        Log.i("Camera", "start camera");
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                Camera camera = cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );
                previewView.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);

            } catch (Exception e) {
                Log.e("Camera", "Error starting camera", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void stopCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            cameraProvider = null;
            previewView.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.VISIBLE);
            Log.i("Camera", "Camera stopped");
        }
    }


    private void analyzeImage(@NonNull ImageProxy image) {
        try {
            Log.i("Analyze", "start analyzing");

            MPImage mpImage = createMPImage(image);
            if (handLandmarker == null) {
                Log.e("Analyze", "handLandmarker is not initialized");
                return;
            }

            //landmarks detection
            ImageProcessingOptions imgProcessingOpts=ImageProcessingOptions.builder().build();
            HandLandmarkerResult result = handLandmarker.detect(mpImage, imgProcessingOpts);

            //here we can whatever we like with the founded landmarks
            printLandmarks(result.landmarks());
            visualizeOnScreen(result.landmarks());

        } catch (Exception e) {
            Log.e("Analyze", "Error analyzing image", e);
        } finally {
            // release resources
            image.close();
        }
    }

    //handLandmarks [[[x0,y0],...[x20,y20]]]
    private void printLandmarks(List<List<NormalizedLandmark>> handLandmarks) {
        if(handLandmarks.isEmpty())
            return;
        int i=0;
        for (NormalizedLandmark landmark : handLandmarks.get(0)) {
            Log.d("MediaPipe","Landmark "+i+":(" + landmark.x()+ ", "+ landmark.y()+ ")");
            i++;
        }
    }

    //handLandmarks [[[x0,y0],...[x20,y20]]]
    private void visualizeOnScreen(List<List<NormalizedLandmark>> handLandmarks){
        if(handLandmarks.isEmpty()) {
            runOnUiThread(() -> overlayView.clear());
            return;
        }
        List<PointF> points = new ArrayList<>();
        for (NormalizedLandmark landmark : handLandmarks.get(0)) {

            // converting to points on the screen

            float xBn=landmark.x();
            float yBn=landmark.y();
//            float normalizedX = (float) (xBn/Math.sqrt(xBn*xBn+yBn*yBn));
//            float normalizedY = (float) (yBn/Math.sqrt(xBn*xBn+yBn*yBn)) ;

            int[] loc=new int[2];
            previewView.getLocationOnScreen(loc);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) previewView.getLayoutParams();

            float x = xBn * params.width;
            float y = yBn * params.height+loc[1];
            Log.i("LOC","calculated= ("+x+","+y+")");
            Log.i("LOC","pv loc= ("+loc[0]+","+loc[1]+")");
           // Log.i("LOC","original= ("+normalizedX+","+normalizedY+")");
            Log.i("LOC","pv lay= (w:"+params.width+",h:"+params.height+")");
            Log.i("LOC","pv size= (w:"+previewView.getWidth()+",h:"+previewView.getHeight()+")");

//            float x = landmark.x() * previewView.getWidth();
//            float y = landmark.y() * previewView.getHeight();

            points.add(new PointF(x, y));
        }

        runOnUiThread(() -> overlayView.setPoints(points));
    }


    private MPImage createMPImage(ImageProxy imageProxy) {
        Bitmap bitmap = convertImageProxyToBitmap(imageProxy);
        Matrix matrix = new Matrix();
        matrix.postRotate(imageProxy.getImageInfo().getRotationDegrees());
        matrix.postScale(-1f, 1f, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}


