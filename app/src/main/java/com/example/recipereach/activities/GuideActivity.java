package com.example.recipereach.activities; // ודאי שזה השם הנכון של ה-package שלך

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.recipereach.CameraTempActivity;
import com.example.recipereach.EditRecipeActivity;
import com.example.recipereach.HomeViewActivity;
import com.example.recipereach.R;
import com.example.recipereach.adapters.GuidePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GuideActivity extends AppCompatActivity {

    private String username,recipeId;
    ImageButton homeButton , goBackButton;
    private String newRecipeName ,newRecipeIngredients ,newRecipeInstructions,newRecipeNotes;


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

        username= getIntent().getStringExtra("USERNAME");
        recipeId = getIntent().getStringExtra("RECIPE_ID");
        newRecipeName = getIntent().getStringExtra("RECIPE_NAME");
        newRecipeIngredients = getIntent().getStringExtra("INGREDIENTS");
        newRecipeInstructions = getIntent().getStringExtra("INSTRUCTIONS");
        newRecipeNotes = getIntent().getStringExtra("NOTES");

        homeButton = findViewById(R.id.home_button);
        goBackButton = findViewById(R.id.goBack_button);



        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, HomeViewActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent); // או כל פעולה אחרת שצריך לבצע ביציאה
            }
        });


        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, CameraTempActivity.class);
                intent.putExtra("RECIPE_NAME", newRecipeName);
                intent.putExtra("RECIPE_INGREDIENTS",newRecipeIngredients);
                intent.putExtra("RECIPE_INSTRUCTIONS", newRecipeInstructions);
                intent.putExtra("RECIPE_NOTES", newRecipeNotes);
                intent.putExtra("USERNAME", username);
                intent.putExtra("RECIPE_ID", recipeId);
                startActivity(intent);
            }
        });

    }
}

