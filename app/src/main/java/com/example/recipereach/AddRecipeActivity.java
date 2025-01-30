package com.example.recipereach;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipereach.activities.GuideActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * AddRecipeActivity allows users to add new recipes by providing a name, ingredients,
 * instructions, and optional notes. The data is stored in Firebase Firestore.
 */
public class AddRecipeActivity extends AppCompatActivity {
    private EditText recipeNameField, ingredientsField, instructionsField, notesField;
    private String username;

    // Temporary dataset (needs to be replaced with actual database storage)
    private ArrayList<Recipe> recipeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Retrieve the username from the intent
        username = getIntent().getStringExtra("USERNAME");
        Log.i("username", username == null ? "no name" : username);

        // Initialize UI elements
        recipeNameField = findViewById(R.id.recipe_name_field);
        ingredientsField = findViewById(R.id.ingredients_field);
        instructionsField = findViewById(R.id.instructions_field);
        notesField = findViewById(R.id.notes_field);

        Button saveButton = findViewById(R.id.save_button);
        ImageButton homeButton = findViewById(R.id.home_button);

        // Save button functionality
        saveButton.setOnClickListener(view -> saveRecipe());

        // Home button functionality - redirects to home screen
        homeButton.setOnClickListener(view -> {
            Log.i("addRecipe", "Home button clicked");
            Intent intent = new Intent(AddRecipeActivity.this, HomeViewActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });
    }

    /**
     * Validates user input and saves the recipe if all required fields are filled.
     */
    private void saveRecipe() {
        String recipeName = recipeNameField.getText().toString().trim();
        String ingredients = ingredientsField.getText().toString().trim();
        String instructions = instructionsField.getText().toString().trim();
        String notes = notesField.getText().toString().trim();

        // Validate input fields
        if (recipeName.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(this, "יש למלא את כל השדות (מלבד הערות)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save the recipe to Firestore
        if (!addRecipeToDatabase(recipeName, ingredients, instructions, notes)) {
            return;
        }

        // Clear input fields after successful save
        clearFields();
    }

    /**
     * Saves the recipe data to Firestore and ensures no duplicate names in the local dataset.
     *
     * @param recipeName   The name of the recipe
     * @param ingredients  The ingredients list
     * @param instructions The instructions for the recipe
     * @param notes        Additional notes (optional)
     * @return true if the recipe is successfully saved, false otherwise
     */
    private boolean addRecipeToDatabase(String recipeName, String ingredients, String instructions, String notes) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a data map for the recipe
        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("userId", username);
        recipeData.put("recipeName", recipeName);
        recipeData.put("ingredients", ingredients);
        recipeData.put("instructions", instructions);
        recipeData.put("notes", notes); // Notes can be null

        // Save the recipe in Firestore under the "Recipes" collection
        db.collection("Recipes")
                .add(recipeData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "מתכון נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "שמירת מתכון נכשלה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("addRecipe", e.getMessage());
                });

        // Check for duplicate names in the local dataset (redundant if using Firestore as source of truth)
        for (Recipe recipe : recipeList) {
            if (recipe.getName().equals(recipeName)) {
                Toast.makeText(this, "שם המתכון כבר קיים ברשימה", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Add new recipe to the local dataset (temporary storage)
        Recipe newRecipe = new Recipe(recipeName, ingredients, instructions, notes, username);
        recipeList.add(newRecipe);

        // Log the saved recipes
        for (Recipe recipe : recipeList) {
            Log.i("addRecipe", "Recipe in list: " + recipe);
        }

        return true;
    }

    /**
     * Clears all input fields after a recipe is saved.
     */
    private void clearFields() {
        recipeNameField.setText("");
        ingredientsField.setText("");
        instructionsField.setText("");
        notesField.setText("");
    }
}
