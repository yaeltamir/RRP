package com.example.recipereach.activities;

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

/**
 * GuideActivity displays a ViewPager2 with different guide fragments.
 * It allows navigation between the guide steps and provides buttons for returning home or going back.
 */
public class GuideActivity extends AppCompatActivity {

    private String username, recipeId;
    private String newRecipeName, newRecipeIngredients, newRecipeInstructions, newRecipeNotes;
    private ImageButton homeButton, goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide); // Guide XML layout

        // Initialize ViewPager2 for displaying guide steps
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        GuidePagerAdapter adapter = new GuidePagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach TabLayout for navigation dots
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    // Custom titles for dots (if needed)
                }).attach();

        // Retrieve passed data from previous activity
        username = getIntent().getStringExtra("USERNAME");
        recipeId = getIntent().getStringExtra("RECIPE_ID");
        newRecipeName = getIntent().getStringExtra("RECIPE_NAME");
        newRecipeIngredients = getIntent().getStringExtra("INGREDIENTS");
        newRecipeInstructions = getIntent().getStringExtra("INSTRUCTIONS");
        newRecipeNotes = getIntent().getStringExtra("NOTES");

        // Initialize buttons
        homeButton = findViewById(R.id.home_button);
        goBackButton = findViewById(R.id.goBack_button);

        // Navigate to HomeViewActivity when the home button is clicked
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, HomeViewActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });

        // Navigate back to CameraTempActivity with recipe details when the back button is clicked
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, CameraTempActivity.class);
                intent.putExtra("RECIPE_NAME", newRecipeName);
                intent.putExtra("RECIPE_INGREDIENTS", newRecipeIngredients);
                intent.putExtra("RECIPE_INSTRUCTIONS", newRecipeInstructions);
                intent.putExtra("RECIPE_NOTES", newRecipeNotes);
                intent.putExtra("USERNAME", username);
                intent.putExtra("RECIPE_ID", recipeId);
                startActivity(intent);
            }
        });
    }
}
