package com.example.recipereach;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

/**
 * ActionsOnRecipeActivity is responsible for running and stopping a Python script using Chaquopy.
 * It provides a UI to execute gesture detection and display results.
 */
public class ActionsOnRecipeActivity extends AppCompatActivity {

    private boolean isRunning = false;
    private Thread pythonThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_on_receipe);

        // Initialize UI elements
        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        TextView resultTextView = findViewById(R.id.resultTextView);

        // Initialize Chaquopy Python instance
        Python py = Python.getInstance();

        // Start button triggers the Python function
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PyObject pyModule = py.getModule("tmpmain"); // Load the Python script
                PyObject result = pyModule.callAttr("run_gesture_detection"); // Call the function
                resultTextView.setText(result.toString()); // Display result in TextView
            }
        });

        // Stop button stops the running Python thread
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;
                if (pythonThread != null && pythonThread.isAlive()) {
                    pythonThread.interrupt(); // Interrupt the thread if it's running
                }
            }
        });
    }
}
