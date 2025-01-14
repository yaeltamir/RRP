package com.example.recipereach;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class GestureRecognition {
    private Interpreter interpreter;

    public GestureRecognition(Context context) throws IOException {
        interpreter = new Interpreter(loadModelFile(context));
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        FileInputStream inputStream = new FileInputStream(context.getAssets().openFd("hand_gesture_model.tflite").getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = context.getAssets().openFd("hand_gesture_model.tflite").getStartOffset();
        long declaredLength = context.getAssets().openFd("hand_gesture_model.tflite").getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String predict(Bitmap bitmap) {
        // עיבוד התמונה לגודל הנדרש על ידי המודל
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true); // לשנות בהתאם למודל
        TensorImage inputImage = TensorImage.fromBitmap(resizedBitmap);

        // יצירת מערך פלט
        TensorBuffer outputBuffer = TensorBuffer.createFixedSize(new int[]{1, 6}, org.tensorflow.lite.DataType.FLOAT32);

        // הרצת המודל
        interpreter.run(inputImage.getBuffer(), outputBuffer.getBuffer());

        // מציאת המחלקה עם הביטחון הגבוה ביותר
        float[] output = outputBuffer.getFloatArray();
        int predictedIndex = 0;
        float maxConfidence = output[0];
        for (int i = 1; i < output.length; i++) {
            if (output[i] > maxConfidence) {
                maxConfidence = output[i];
                predictedIndex = i;
            }
        }

        String[] labelNames = {"Scroll Up", "Scroll Down", "Zoom In", "Zoom Out", "Left Swipe", "Right Swipe"};
        return labelNames[predictedIndex];
    }
}
