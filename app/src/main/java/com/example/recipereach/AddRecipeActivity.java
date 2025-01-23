package com.example.recipereach;
//
//import android.content.ContentValues;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class AddRecipeActivity extends AppCompatActivity {
//
//    private EditText editTextTitle, editTextDescription;
//    private Button buttonSaveRecipe;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_recipe);
//
//        // Initialize views
//        editTextTitle = findViewById(R.id.editTextTitle);
//        editTextDescription = findViewById(R.id.editTextDescription);
//        buttonSaveRecipe = findViewById(R.id.buttonSaveRecipe);
//
//        // Set save button click listener
//        buttonSaveRecipe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveRecipeToDatabase();
//            }
//        });
//    }
//
//    private void saveRecipeToDatabase() {
//        String title = editTextTitle.getText().toString().trim();
//        String description = editTextDescription.getText().toString().trim();
//
//        if (title.isEmpty() || description.isEmpty()) {
//            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Get writable database
//        DatabaseHelper dbHelper = new DatabaseHelper(this);
//        dbHelper.addRecipe(title, description);
//
//        Toast.makeText(this, "saved recipe", Toast.LENGTH_SHORT).show();
//    }
//}

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddRecipeActivity extends AppCompatActivity {
    private EditText recipeNameField, ingredientsField, instructionsField, notesField;
    private String username;

    //-----------------------need to replace this with actual dataset -----------------------------
    private ArrayList<Recipe> recipeList = new ArrayList<>();
    //---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        username= getIntent().getStringExtra("USERNAME");
        Log.i("username",username==null?"no name":username);

        // Initialize UI elements
        recipeNameField = findViewById(R.id.recipe_name_field);
        ingredientsField = findViewById(R.id.ingredients_field);
        instructionsField = findViewById(R.id.instructions_field);
        notesField = findViewById(R.id.notes_field);

        Button saveButton = findViewById(R.id.save_button);
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton backButton = findViewById(R.id.back_button);

        // Save button functionality
        saveButton.setOnClickListener(view -> saveRecipe());

        // Currently, home and back buttons do nothing
        //maybe need to delete one of them----------------------------------------------------------
        homeButton.setOnClickListener(view -> {
            Log.i("addReceipe","home button was clicked");
            // Placeholder for home button functionality: need to redirect to home page
        });

        backButton.setOnClickListener(view -> {
            Log.i("addReceipe","back button was clicked");
            // Placeholder for back button functionality:  need to redirect to home page
        });
    }

    private void saveRecipe() {

        String recipeName = recipeNameField.getText().toString().trim();
        String ingredients = ingredientsField.getText().toString().trim();
        String instructions = instructionsField.getText().toString().trim();
        String notes = notesField.getText().toString().trim();

        if (recipeName.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            //fix: need to display like error------------------------------------------------------
            Toast.makeText(this, "יש למלא את כל השדות (מלבד הערות)", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if(!addReceipe(recipeName,ingredients,instructions,notes)){
            return;
        };

        // Clear fields and show success message
        clearFields();
      //  Toast.makeText(this, "המתכון נשמר בהצלחה!", Toast.LENGTH_SHORT).show();

    }

    private boolean addReceipe(String recipeName, String ingredients, String instructions, String notes){
        //need to save in data base ---------------------------------------------------------------
//
//
//// Add a new document with a generated ID
//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
      //  public void saveRecipe(String userId, String recipeName, String ingredients, String instructions, String notes) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

            // יצירת מפת נתונים למתכון
        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("userId", username);
        recipeData.put("recipeName", recipeName);
        recipeData.put("ingredients", ingredients);
        recipeData.put("instructions", instructions);
        recipeData.put("notes", notes); // יכול להיות null

            // שמירת המידע ב-Firestore תחת אוסף "Recipes"
            db.collection("Recipes")
                    .add(recipeData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getApplicationContext(), "מתכון נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "שמירת מתכון נכשלה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("addRecipe",e.getMessage());
                    });



        // maybe unnecessary because we have data set logic, but still need to handle errors ------

        for (Recipe recipe : recipeList) {
            if (recipe.getName().equals(recipeName)) {
                //fix: need to display like error--------------------------------------------------
                Toast.makeText(this, "שם המתכון כבר קיים ברשימה", Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        }

        //need database ---------------------------------------------------------------------------
        // need access the user identifier in order to save in receipe.userId
        // Add new recipe
        Recipe newRecipe = new Recipe(recipeName, ingredients, instructions, notes,username);
        recipeList.add(newRecipe);
        for (Recipe recipe:recipeList){
            Log.i("addReceipe","receipe in list: "+recipe);
        }

        return true;
    }

    private void clearFields() {
        recipeNameField.setText("");
        ingredientsField.setText("");
        instructionsField.setText("");
        notesField.setText("");
    }
}

