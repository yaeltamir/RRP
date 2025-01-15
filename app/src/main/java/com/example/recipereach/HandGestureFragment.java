//package com.example.recipereach;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.TextureView;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.camera.core.ImageProxy;
//import androidx.fragment.app.Fragment;
//
//import java.util.concurrent.ExecutionException;
//
//public class HandGestureFragment extends Fragment {
//    private TextureView textureView;
//    private CameraHelper cameraHelper;
//
//    @SuppressLint("MissingInflatedId")
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.activity_recipe, container, false);
//        textureView = view.findViewById(R.id.textureView);
//        cameraHelper = new CameraHelper(requireContext(), textureView);
//        return view;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        try {
//            cameraHelper.startCamera(image -> {
//                // כאן נקרא למודל כדי לזהות תנועות ידיים בפריימים שמגיעים מהמצלמה
//                processFrame(image);
//            });
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        cameraHelper.stopCamera();
//    }
//
//    private void processFrame(ImageProxy image) {
//        // כאן תבוא לוגיקת זיהוי התנועות באמצעות TensorFlow Lite
//    }
//}
//
