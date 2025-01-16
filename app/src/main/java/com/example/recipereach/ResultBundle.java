package com.example.recipereach;
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult;
public class ResultBundle {
    private HandLandmarkerResult result;
    private long inferenceTime;
    private int imageHeight;
    private int imageWidth;

    public ResultBundle(HandLandmarkerResult result, long inferenceTime, int imageHeight, int imageWidth) {
        this.result = result;
        this.inferenceTime = inferenceTime;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
    }

    public HandLandmarkerResult getResult() {
        return result;
    }

    public long getInferenceTime() {
        return inferenceTime;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }
}
