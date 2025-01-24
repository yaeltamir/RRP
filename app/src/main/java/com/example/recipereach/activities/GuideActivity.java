package com.example.recipereach.activities; // ודאי שזה השם הנכון של ה-package שלך

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.recipereach.R;
import com.example.recipereach.adapters.GuidePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide); // קובץ ה-XML של ה-Activity

        // חיבור ל-ViewPager2 מתוך ה-XML
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // יצירת ה-Adapter
        GuidePagerAdapter adapter = new GuidePagerAdapter(this);

        // חיבור ה-Adapter ל-ViewPager2
        viewPager.setAdapter(adapter);


        // חיבור TabLayout לנקודות
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    // את יכולה להגדיר כותרות מותאמות אישית לנקודות (אם יש צורך)
                }).attach();
    }
}

