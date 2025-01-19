package com.example.recipereach;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GesturePredictor {

    private final Interpreter tflite;
    private final String[] labelNames = {"palm", "point", "grip", "like", "dislike", "no_gesture"};

    public GesturePredictor(Context context, String modelPath) throws IOException {
       // tflite = new Interpreter(loadModelFile(context, modelPath));
        Interpreter.Options options = new Interpreter.Options();
        options.setUseXNNPACK(false);
        tflite = new Interpreter(loadModelFile(context, modelPath), options);
        int[] inputShape = tflite.getInputTensor(0).shape();
        //Log.i("predict","Model Input Shape: "+Arrays.toString(inputShape));

    }

    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(context.getAssets().openFd(modelPath).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = context.getAssets().openFd(modelPath).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelPath).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String predictGesture(List<float[]> handLandmarks) {
        // הפיכת רשימת נקודות הציון למערך
        float[][] inputArray = flattenCoordinates(handLandmarks, 42); // התאמה לאורך הרצוי
        float[][] outputArray = new float[1][labelNames.length]; // תוצאת המודל

        // הפעלת המודל
        tflite.run(inputArray, outputArray);

        // חישוב התוצאה
        int predictedIndex = getMaxIndex(outputArray[0]);
        Log.i("predict","prediction index: "+predictedIndex);
        float confidence = outputArray[0][predictedIndex];

        // הדפסת התוצאה
        return String.format("Label: %s, Confidence: %.2f%%", labelNames[predictedIndex], confidence * 100);
    }

    private float[][] flattenCoordinates(List<float[]> handLandmarks, int targetLength) {
        float[][] result = new float[targetLength][2];

        for (int i = 0; i < targetLength; i++) {
            if (i < handLandmarks.size()) {
                result[i] = handLandmarks.get(i);
            } else {
                result[i] = new float[]{-2.0f, -2.0f}; // Padding with default value
            }
        }

        return result;
    }

    private int getMaxIndex(float[] array) {
        int maxIndex = 0;
        float maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}

