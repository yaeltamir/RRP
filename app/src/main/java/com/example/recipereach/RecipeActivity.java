package com.example.recipereach;

import static org.checkerframework.checker.units.UnitsTools.C;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.tensorflow.lite.Interpreter;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class RecipeActivity extends AppCompatActivity {

    private TextView recipeTextView;
    private ScrollView scrollView;
    private Button startButton, stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        recipeTextView = findViewById(R.id.recipeTextView);
        scrollView = findViewById(R.id.scrollView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

//        try {
//            GestureRecognition gestureRecognition = new GestureRecognition();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                gestureRecognition.loadModel(getApplicationContext());
//
//                // כאן תוכלי להוסיף את הקוד שתופס את תנועת הידיים בזמן אמת (באמצעות מצלמת הסמארטפון)
//                // נניח שיש לך פונקציה שתופס את המידע הזה
//                float[] handMovementData = getHandMovementData(); // כאן תקבלי את נתוני תנועת הידיים
//
//                // זיהוי התנועה על ידי המודל
//                float[] result = gestureRecognition.recognizeGesture(handMovementData);
//
//                // השתמשי בתוצאה לגלול למעלה או למטה, להגדיל או להקטין בהתאם לצורך
//                handleGestureResult(result);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecipeActivity.this, "חיפוש מתכון הופסק", Toast.LENGTH_SHORT).show();
            }
        });

        // מאפשר שינוי גודל הטקסט באמצעות pinch-to-zoom
        recipeTextView.setOnTouchListener(new OnPinchZoomListener(recipeTextView));
    }
}


