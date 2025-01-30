package com.example.recipereach;

import android.content.Context;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class GesturePredictor {

    // Enum to define possible gesture labels
    public enum labelNames {
        PALM,
        POINT,
        GRIP,
        LIKE,
        DISLIKE,
        NO_GESTURE;
    }

    // Interpreter instance to interact with the TensorFlow Lite model
    private final Interpreter tflite;

    // Constructor for the GesturePredictor class
    public GesturePredictor(Context context, String modelPath) throws IOException {
        // Set options to disable XNNPACK for performance
        Interpreter.Options options = new Interpreter.Options();
        options.setUseXNNPACK(false);  // Disable XNNPACK optimizations
        // Initialize the TensorFlow Lite interpreter with the model file
        tflite = new Interpreter(loadModelFile(context, modelPath), options);
        int[] inputShape = tflite.getInputTensor(0).shape(); // Getting input shape of the model
    }

    // Method to load the TensorFlow Lite model file from the assets folder
    private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        // Open the model file from assets using FileInputStream
        FileInputStream fileInputStream = new FileInputStream(context.getAssets().openFd(modelPath).getFileDescriptor());
        FileChannel fileChannel = fileInputStream.getChannel();
        long startOffset = context.getAssets().openFd(modelPath).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelPath).getDeclaredLength();
        // Return the model file as a MappedByteBuffer for TensorFlow Lite
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Method to predict the gesture based on hand landmarks
    public labelNames predictGesture(List<float[]> handLandmarks) {
        // Flatten the list of hand landmarks into a fixed-size array
        float[][] inputArray = flattenCoordinates(handLandmarks, 42); // Flatten to match the model input
        float[][] outputArray = new float[1][labelNames.values().length]; // Output array to hold prediction results

        // Run the model to get the prediction
        tflite.run(inputArray, outputArray);

        // Find the index of the highest prediction value (the predicted gesture)
        int predictedIndex = getMaxIndex(outputArray[0]);
        float confidence = outputArray[0][predictedIndex]; // Confidence score of the prediction

        // If confidence is below threshold, consider it as "NO_GESTURE"
        if(confidence < 0.96) {
            return labelNames.NO_GESTURE;
        }

        // Log the predicted label and confidence level for debugging
        Log.i("predict", "prediction index: " + predictedIndex);
        Log.i("predict", String.format("predicted label: %s, Confidence: %.2f%%", labelNames.values()[predictedIndex], confidence * 100));

        // Return the predicted gesture label
        return labelNames.values()[predictedIndex];
    }

    // Method to flatten the list of hand landmarks into a 2D array with padding if necessary
    private float[][] flattenCoordinates(List<float[]> handLandmarks, int targetLength) {
        float[][] result = new float[targetLength][2]; // Initialize array with 2D coordinates

        // Populate the array with available landmarks, adding padding for missing landmarks
        for (int i = 0; i < targetLength; i++) {
            if (i < handLandmarks.size()) {
                result[i] = handLandmarks.get(i); // Use the landmark coordinates
            } else {
                result[i] = new float[]{-2.0f, -2.0f}; // Add padding for missing landmarks
            }
        }

        return result; // Return the flattened and padded array
    }

    // Method to find the index of the maximum value in the array (for determining the predicted gesture)
    private int getMaxIndex(float[] array) {
        int maxIndex = 0;
        float maxValue = array[0];
        // Iterate through the array to find the maximum value and its index
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }
        return maxIndex; // Return the index of the maximum value
    }
}
