package com.example.recipereach;

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import java.util.List;

public interface HandDetectionListener {
    void onHandDetected(List<NormalizedLandmark> landmarks);
}
