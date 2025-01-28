package com.example.recipereach;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipereach.activities.GuideActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private List<Recipe> originalRecipeList;
    private TextView welcomeText;
    //private FloatingActionButton addRecipeButton;
    private EditText searchEditText;
    private TextView noResultsTextView;
    private ImageButton sortButton,addRecipeButton; //, btnOpenGuide ;
    private boolean isSortedAscending = false;
    private  String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view_activity);

        recipeList= new ArrayList<>();
        String username= getIntent().getStringExtra("USERNAME");
        setRecipeList(username);


        //initialize page components
        welcomeText=findViewById(R.id.welcomeTextView);
        recyclerView = findViewById(R.id.recipesRecyclerView);
        addRecipeButton=findViewById(R.id.addRecipeButton);
        searchEditText = findViewById(R.id.searchEditText);
        noResultsTextView = findViewById(R.id.noResultsTextView);
        sortButton = findViewById(R.id.sortButton);
        //btnOpenGuide = findViewById(R.id.guideButton);

        userid=username;

        //String welcome=welcomeText.getText()+username+"!";
        String welcome=welcomeText.getText().toString();
        welcomeText.setText(welcome);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // שני פריטים בכל שורה

        Log.i("home","list: "+recipeList.toString());
        //  originalRecipeList=new ArrayList<>(recipeList);
        // Log.i("home","original list: "+originalRecipeList.toString());

        recipeAdapter = new RecipeAdapter(recipeList, recipe-> {
            Intent intent = new Intent(HomeViewActivity.this, CameraTempActivity.class);
            //SpannableString temp=new SpannableString("this is wprking!");
            intent.putExtra("RECIPE_NAME", recipe.getName());
            intent.putExtra("RECIPE_INGREDIENTS", recipe.getIngredients());
            intent.putExtra("RECIPE_INSTRUCTIONS", recipe.getInstructions());
            intent.putExtra("RECIPE_NOTES", recipe.getNotes());
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });
        recyclerView.setAdapter(recipeAdapter);

        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeViewActivity.this, AddRecipeActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });

        // האזנה לשינויים בטקסט
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // מאזין לכפתור המיון
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortList();
            }
        });
//
//        recyclerView = findViewById(R.id.recyclerView);
//        FloatingActionButton btnAddRecipe = findViewById(R.id.btnAddRecipe);
//        FloatingActionButton btnDelete = findViewById(R.id.btnDelete);
//
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        recipeAdapter = new RecipeAdapter(recipeList);
//        recyclerView.setAdapter(recipeAdapter);
//
//        btnAddRecipe.setOnClickListener(v -> {
//            // TODO: Add logic to navigate to the Add Recipe screen
//        });


//        btnOpenGuide.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeViewActivity.this, GuideActivity.class);
//                intent.putExtra("USERNAME", username);
//                startActivity(intent);
//            }
//        });

    }


    private void setRecipeList(String username) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Recipes")
                .whereEqualTo("userId", username)  // התנאי לשליפה
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recipeList.clear(); // מנקים את הרשימה הקיימת
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("recipeName");
                            String ingredients = document.getString("ingredients");
                            String instructions = document.getString("instructions");
                            String notes = document.getString("notes");
                            Recipe recipe = new Recipe(name, ingredients, instructions, notes, username); // המרת המסמך לאובייקט
                            recipeList.add(recipe);
                        }

                        // מעדכנים את הרשימה המקורית אחרי טעינת הנתונים
                        originalRecipeList = new ArrayList<>(recipeList);

                        // מעדכנים את ה-RecyclerView
                        recipeAdapter.updateList(recipeList);
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

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
            noResultsTextView.setText("לא נמצאו תוצאות");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noResultsTextView.setVisibility(View.GONE);
            recipeAdapter.updateList(filteredList);
        }
    }
    private void sortList() {
        if (isSortedAscending) {
            // החזרת הרשימה לסדר המקורי
            recipeList.clear();
            recipeList.addAll(originalRecipeList);
//            sortButton.setContentDescription("מיין בסדר עולה");
        } else {
            // מיון לפי סדר אלפביתי עולה
            Collections.sort(recipeList, new Comparator<Recipe>() {
                @Override
                public int compare(Recipe o1, Recipe o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            sortButton.setContentDescription("החזר לסדר המקורי");
        }

        isSortedAscending = !isSortedAscending;
        recipeAdapter.updateList(recipeList);
    }
    @Override
    protected void onResume() {
        super.onResume();
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            setRecipeList(userid); // קריאה לטעינת הנתונים
        }
    }

}
