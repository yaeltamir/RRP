package com.example.recipereach;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * ActionsTempActivity demonstrates basic UI interactions, including scrolling and text resizing.
 * This serves as a foundation for future UI functionalities.
 */
public class ActionsTempActivity extends AppCompatActivity {

    private TextView longTextView;
    private ScrollView scrollView;
    private float textSize = 16f; // Default text size

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_temp);

        // Initialize UI components
        longTextView = findViewById(R.id.longTextView);
        scrollView = findViewById(R.id.scrollView);

        // Generate a long text for scrolling demonstration
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("זהו טקסט מאוד ארוך לחוויית גלילה.\n");
        }
        longTextView.setText(longText.toString());

        // Initialize buttons
        Button btnScrollUp = findViewById(R.id.btnScrollUp);
        Button btnScrollDown = findViewById(R.id.btnScrollDown);
        Button btnIncreaseSize = findViewById(R.id.btnIncreaseSize);
        Button btnDecreaseSize = findViewById(R.id.btnDecreaseSize);

        // Scroll to the top when clicking the "Scroll Up" button
        btnScrollUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, 0);
            }
        });

        // Scroll to the bottom when clicking the "Scroll Down" button
        btnScrollDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, longTextView.getBottom());
            }
        });

        // Increase text size when clicking the "Increase Size" button
        btnIncreaseSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSize += 2;
                longTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            }
        });

        // Decrease text size when clicking the "Decrease Size" button (minimum size: 8)
        btnDecreaseSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSize -= 2;
                if (textSize > 8) {
                    longTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                }
            }
        });
    }
}
