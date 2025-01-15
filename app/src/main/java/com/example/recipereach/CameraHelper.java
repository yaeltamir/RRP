//package com.example.recipereach;
//
//import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;
//import static androidx.core.content.ContentProviderCompat.requireContext;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.util.Log;
//import android.view.TextureView;
//import androidx.camera.core.Camera;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ImageAnalysis;
//import androidx.camera.core.ImageProxy;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.core.content.ContextCompat;
//
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//public class CameraHelper {
//    private final ExecutorService cameraExecutor;
//    private final Context context;
//    private final TextureView textureView;
//    private Camera camera;
//
//    public interface FrameListener {
//        void onFrameAvailable(ImageProxy image);
//    }
//
//    private FrameListener frameListener;
//
//    public CameraHelper(Context context, TextureView textureView) {
//        this.context = context;
//        this.textureView = textureView;
//        cameraExecutor = Executors.newSingleThreadExecutor();
//    }
//
//    public void startCamera(FrameListener listener) throws ExecutionException, InterruptedException {
//        this.frameListener = listener;
//       // ProcessCameraProvider cameraProviderFuture = ProcessCameraProvider.getInstance(context).get();
//       // @SuppressLint("RestrictedApi") ProcessCameraProvider cameraProviderFuture = ProcessCameraProvider.getInstance(getApplicationContext());
//        // ProcessCameraProvider cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
//
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//                CameraSelector cameraSelector = new CameraSelector.Builder()
//                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
//                        .build();
//
//                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                        .build();
//
//                imageAnalysis.setAnalyzer(cameraExecutor, image -> {
//                    if (frameListener != null) {
//                        frameListener.onFrameAvailable(image);
//                    }
//                    image.close();
//                });
//
//                cameraProvider.unbindAll();
//                camera = cameraProvider.bindToLifecycle((androidx.lifecycle.LifecycleOwner) context,
//                        cameraSelector, imageAnalysis);
//
//            } catch (Exception e) {
//                Log.e("CameraHelper", "Use case binding failed", e);
//            }
//        }, ContextCompat.getMainExecutor(context));
//    }
//
//    public void stopCamera() {
//        if (cameraExecutor != null) {
//            cameraExecutor.shutdown();
//        }
//    }
//}
//
