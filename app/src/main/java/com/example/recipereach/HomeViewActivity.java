package com.example.recipereach;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>(); // List of recipes
    private List<Recipe> originalRecipeList; // Backup list for sorting
    private TextView welcomeText, noResultsTextView;
    private EditText searchEditText;
    private ImageButton sortButton, addRecipeButton;
    private boolean isSortedAscending = false;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view_activity);

        // Retrieve username from intent
        String username = getIntent().getStringExtra("USERNAME");
        userid = username;
        setRecipeList(username); // Load recipes for the user

        // Initialize UI components
        welcomeText = findViewById(R.id.welcomeTextView);
        recyclerView = findViewById(R.id.recipesRecyclerView);
        addRecipeButton = findViewById(R.id.addRecipeButton);
        searchEditText = findViewById(R.id.searchEditText);
        noResultsTextView = findViewById(R.id.noResultsTextView);
        sortButton = findViewById(R.id.sortButton);

        // Set welcome message
        String welcome = welcomeText.getText().toString();
        welcomeText.setText(welcome);

        // Configure RecyclerView with a grid layout
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 items per row

        // Set up RecyclerView adapter with a click listener
        recipeAdapter = new RecipeAdapter(recipeList, recipe -> {
            Intent intent = new Intent(HomeViewActivity.this, CameraTempActivity.class);
            intent.putExtra("RECIPE_NAME", recipe.getName());
            intent.putExtra("RECIPE_INGREDIENTS", recipe.getIngredients());
            intent.putExtra("RECIPE_INSTRUCTIONS", recipe.getInstructions());
            intent.putExtra("RECIPE_NOTES", recipe.getNotes());
            intent.putExtra("USERNAME", username);
            intent.putExtra("RECIPE_ID", recipe.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(recipeAdapter);

        // Add recipe button click listener
        addRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeViewActivity.this, AddRecipeActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        // Search field listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString()); // Filter recipes based on input
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Sort button listener
        sortButton.setOnClickListener(v -> sortList());
    }

    // Fetch user-specific recipes from Firestore
    private void setRecipeList(String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Recipes")
                .whereEqualTo("userId", username) // Filter recipes by user
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recipeList.clear(); // Clear the current list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String name = document.getString("recipeName");
                            String ingredients = document.getString("ingredients");
                            String instructions = document.getString("instructions");
                            String notes = document.getString("notes");
                            Recipe recipe = new Recipe(name, ingredients, instructions, notes, username, documentId);
                            recipeList.add(recipe);
                        }
                        originalRecipeList = new ArrayList<>(recipeList); // Save original list
                        recipeAdapter.updateList(recipeList); // Update UI
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    // Filter recipes by name
    private void filterList(String query) {
        List<Recipe> filteredList = new ArrayList<>();
        for (Recipe item : recipeList) {
            if (item.getName().startsWith(query)) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            noResultsTextView.setVisibility(View.VISIBLE);
            noResultsTextView.setText("לא נמצאו תוצאות"); // Show no results message
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noResultsTextView.setVisibility(View.GONE);
            recipeAdapter.updateList(filteredList);
        }
    }

    // Sort recipes alphabetically
    private void sortList() {
        if (isSortedAscending) {
            // Restore original order
            recipeList.clear();
            recipeList.addAll(originalRecipeList);
        } else {
            // Sort alphabetically
            Collections.sort(recipeList, Comparator.comparing(Recipe::getName));
        }
        isSortedAscending = !isSortedAscending;
        recipeAdapter.updateList(recipeList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRecipeList(userid); // Reload recipes when activity resumes
    }
}