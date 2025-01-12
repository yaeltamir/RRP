package com.example.recipereach;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private Button buttonSaveRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSaveRecipe = findViewById(R.id.buttonSaveRecipe);

        // Set save button click listener
        buttonSaveRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipeToDatabase();
            }
        });
    }

    private void saveRecipeToDatabase() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get writable database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.addRecipe(title,description);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        // Insert recipe into database
//        ContentValues values = new ContentValues();
//        values.put("title", title);
//        values.put("description", description);
//
//        long newRowId = db.insert("recipes", null, values);
//
//        if (newRowId == -1) {
          Toast.makeText(this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Recipe saved successfully", Toast.LENGTH_SHORT).show();
//            finish(); // Close the activity
//        }
//
//        db.close();
    }
}
