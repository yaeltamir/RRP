package com.example.recipereach;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//זהו ניסיון להפעלת פעולות על המסך בלחיצת כפתור, ישמש כבסיס בעתיד
public class ActionsTempActivity extends AppCompatActivity {

    private TextView longTextView;
    private ScrollView scrollView;
    private float textSize = 16f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions_temp);

        longTextView = findViewById(R.id.longTextView);
        scrollView = findViewById(R.id.scrollView);

        // טקסט ארוך לדוגמה
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("זהו טקסט מאוד ארוך לחוויית גלילה.\n");
        }
        longTextView.setText(longText.toString());

        Button btnScrollUp = findViewById(R.id.btnScrollUp);
        Button btnScrollDown = findViewById(R.id.btnScrollDown);
        Button btnIncreaseSize = findViewById(R.id.btnIncreaseSize);
        Button btnDecreaseSize = findViewById(R.id.btnDecreaseSize);

        btnScrollUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, 0);
            }
        });

        btnScrollDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, longTextView.getBottom());
            }
        });

        btnIncreaseSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSize += 2;
                longTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            }
        });

        btnDecreaseSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSize -= 2;
                if (textSize > 8) { // מגבלת גודל מינימלי
                    longTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                }
            }
        });
    }
}
