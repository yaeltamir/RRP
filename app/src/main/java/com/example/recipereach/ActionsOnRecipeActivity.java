package com.example.recipereach;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class ActionsOnRecipeActivity extends AppCompatActivity {

    private boolean isRunning = false;
    private Thread pythonThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_on_receipe);

        Button startButton = findViewById(R.id.startButton);
        Button stopButton = findViewById(R.id.stopButton);
        TextView resultTextView = findViewById(R.id.resultTextView);

        // אתחול Chaquopy
        Python py = Python.getInstance();

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PyObject pyModule = py.getModule("tmpmain");

                PyObject result = pyModule.callAttr("run_gesture_detection");
                resultTextView.setText(result.toString());
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = false;
                if (pythonThread != null && pythonThread.isAlive()) {
                    pythonThread.interrupt();
                }
            }
        });
    }
}
