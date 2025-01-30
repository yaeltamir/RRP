package com.example.recipereach;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditRecipeActivity extends AppCompatActivity {
    private EditText recipeNameField, ingredientsField, instructionsField, notesField;
    private String username, recipeId;
    private String newRecipeName, newRecipeIngredients, newRecipeInstructions, newRecipeNotes;
    private final boolean[] nameHasChange = {false};
    private final boolean[] ingredientsHasChange = {false};
    private final boolean[] instructionsHasChange = {false};
    private final boolean[] notesHasChange = {false};

    private interface OnLeaveConfirmationListener {
        void onConfirmLeave(boolean shouldLeave);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        username = getIntent().getStringExtra("USERNAME");
        recipeId = getIntent().getStringExtra("RECIPE_ID");

        newRecipeName = getIntent().getStringExtra("RECIPE_NAME");
        newRecipeIngredients = getIntent().getStringExtra("INGREDIENTS");
        newRecipeInstructions = getIntent().getStringExtra("INSTRUCTIONS");
        newRecipeNotes = getIntent().getStringExtra("NOTES");

        // Initialize UI elements
        recipeNameField = findViewById(R.id.recipe_name_field);
        ingredientsField = findViewById(R.id.ingredients_field);
        instructionsField = findViewById(R.id.instructions_field);
        notesField = findViewById(R.id.notes_field);
        Button saveButton = findViewById(R.id.save_button);
        ImageButton homeButton = findViewById(R.id.home_button);
        ImageButton goBackButton = findViewById(R.id.goBack_button);

        recipeNameField.setText(newRecipeName);
        ingredientsField.setText(newRecipeIngredients);
        instructionsField.setText(newRecipeInstructions);
        notesField.setText(newRecipeNotes);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfToLeave("האם אתה בטוח שברצונך לחזור לדף המתכון?\n השינויים שעשית לא יישמרו", new OnLeaveConfirmationListener() {
                    @Override
                    public void onConfirmLeave(boolean shouldLeave) {
                        if (shouldLeave) {
                            Intent intent = new Intent(EditRecipeActivity.this, CameraTempActivity.class);
                            intent.putExtra("RECIPE_NAME", newRecipeName);
                            intent.putExtra("RECIPE_INGREDIENTS", newRecipeIngredients);
                            intent.putExtra("RECIPE_INSTRUCTIONS", newRecipeInstructions);
                            intent.putExtra("RECIPE_NOTES", newRecipeNotes);
                            intent.putExtra("USERNAME", username);
                            intent.putExtra("RECIPE_ID", recipeId);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfToLeave("האם אתה בטוח שברצונך לחזור לדף הבית?\n השינויים שעשית לא יישמרו", new OnLeaveConfirmationListener() {
                    @Override
                    public void onConfirmLeave(boolean shouldLeave) {
                        if (shouldLeave) {
                            Intent intent = new Intent(EditRecipeActivity.this, HomeViewActivity.class);
                            intent.putExtra("USERNAME", username);
                            startActivity(intent); // or any other action needed when leaving
                        }
                    }
                });
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRecipe();
            }
        });

        // Adding TextWatcher for fields with boolean variables
        recipeNameField.addTextChangedListener(createFieldWatcher(nameHasChange));
        ingredientsField.addTextChangedListener(createFieldWatcher(ingredientsHasChange));
        instructionsField.addTextChangedListener(createFieldWatcher(instructionsHasChange));
        notesField.addTextChangedListener(createFieldWatcher(notesHasChange));
    }


    private void checkIfToLeave(String msg, OnLeaveConfirmationListener listener) {
        if (!(nameHasChange[0] || ingredientsHasChange[0] || instructionsHasChange[0] || notesHasChange[0])) {
            listener.onConfirmLeave(true);
            return;
        }

        new AlertDialog.Builder(EditRecipeActivity.this)
                .setTitle("אישור עזיבה")
                .setMessage(msg)
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onConfirmLeave(true);
                    }
                })
                .setNegativeButton("לא, זה היה בטעות", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onConfirmLeave(false);
                    }
                })
                .show();
    }

    private void saveRecipe() {
        if(!nameHasChange[0]&&!ingredientsHasChange[0]&&!instructionsHasChange[0]&&!notesHasChange[0]){
            Toast.makeText(this, "אין שינויים אז אין מה לשמור:)", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        newRecipeName = recipeNameField.getText().toString().trim();
        newRecipeIngredients = ingredientsField.getText().toString().trim();
        newRecipeInstructions = instructionsField.getText().toString().trim();
        newRecipeNotes = notesField.getText().toString().trim();

        if (newRecipeName.isEmpty() || newRecipeIngredients.isEmpty() || newRecipeInstructions.isEmpty()) {
            //fix: need to display like error------------------------------------------------------
            Toast.makeText(this, "כל השדות (מלבד הערות) צריכים להיות מלאים", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if(!editReceipe(newRecipeName,newRecipeIngredients,newRecipeInstructions,newRecipeNotes)){
            return;
        };

        nameHasChange[0]=false;
        instructionsHasChange[0]=false;
        ingredientsHasChange[0]=false;
        notesHasChange[0]=false;
        Toast.makeText(this, "המתכון נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
    }

    private boolean editReceipe(String recipeName, String ingredients, String instructions, String notes) {
        // Updating the document by its ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String documentId = getIntent().getStringExtra("RECIPE_ID");

        // Values map for updates
        Map<String, Object> updates = new HashMap<>();
        if (nameHasChange[0])
            updates.put("recipeName", recipeName);
        if (ingredientsHasChange[0])
            updates.put("ingredients", ingredients);
        if (instructionsHasChange[0])
            updates.put("instructions", instructions);          // Update other field
        if (notesHasChange[0])
            updates.put("notes", notes);

        db.collection("Recipes")
                .document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Document updated successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error updating document", e));
        return true;

    }

    // TextWatcher for change detection
    private TextWatcher createFieldWatcher(final boolean[] isFieldModified) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isFieldModified[0] = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used here
            }
        };
    }
}
