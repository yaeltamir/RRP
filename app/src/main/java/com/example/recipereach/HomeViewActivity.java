package com.example.recipereach;
//
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HomeViewActivity extends AppCompatActivity {
//
//    private DatabaseHelper dbHelper;
//    private RecipeAdapter recipeAdapter;
//    private List<String> recipeList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.home_view_activity);
//
//        TextView userNameTextView = findViewById(R.id.userNameTextView);
//        RecyclerView recipeRecyclerView = findViewById(R.id.recipeRecyclerView);
//        FloatingActionButton addRecipeButton = findViewById(R.id.addRecipeButton);
//
//        // קבלת שם המשתמש מהמסך הקודם
//        //String userName = getIntent().getStringExtra("userName");
//        String userName="abc";
//        userNameTextView.setText("Hello, " + userName);
//
//        // חיבור למסד הנתונים
//        dbHelper = new DatabaseHelper(this);
//
//        // טוען מתכונים למשתמש
//        recipeList = new ArrayList<>();
//        Cursor cursor = dbHelper.getRecipes(userName);
//        if (cursor.moveToFirst()) {
//            do {
//                int temp=cursor.getColumnIndex("recipe")>=0?cursor.getColumnIndex("recipe"):0;
//                recipeList.add(cursor.getString(temp));
//            } while (cursor.moveToNext());
//        }
//
//        // הגדרת RecyclerView
//        recipeAdapter = new RecipeAdapter(recipeList);
//        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recipeRecyclerView.setAdapter(recipeAdapter);
//
//        // מעבר לדף הוספת מתכון
//        addRecipeButton.setOnClickListener(v -> {
//            Intent intent = new Intent(HomeViewActivity.this, AddRecipeActivity.class);
//            intent.putExtra("userName", userName);
//            startActivity(intent);
//        });
//    }
//}
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
    private ImageButton sortButton,addRecipeButton, btnOpenGuide ;
    private boolean isSortedAscending = false;

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
        btnOpenGuide = findViewById(R.id.guideButton);


        String welcome=welcomeText.getText()+username+"!";
        welcomeText.setText(welcome);


        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // שני פריטים בכל שורה

 //       recipeList = new ArrayList<>();
//        recipeList.add(new Recipe("a","a1,a2,a3","put a1 and a2 and a3",null,username));
//        recipeList.add(new Recipe("חביתה","a1,a2,a3","put a1 and a2 and a3",null,username));
//        recipeList.add(new Recipe("פיצה","a1,a2,a3","put a1 and a2 and a3",null,username));
//        recipeList.add(new Recipe("סלט","a1,a2,a3","put a1 and a2 and a3",null,username));
//        recipeList.add(new Recipe("abcdefghi","a1,a2,a3","put a1 and a2 and a3",null,username));
//        recipeList.add(new Recipe("jjjjjjj","a1,a2,a3","put a1 and a2 and a3",null,username));
//        recipeList.add(new Recipe("חביתה עם הרבה מאוד תוספות  חביתה עם הרבה מאוד תוספות חביתה עם הרבה מאוד תוספות חביתה עם הרבה מאוד תוספות","a1,a2,a3","put a1 and a2 and a3",null,username));
//        recipeList.add(new Recipe("סלט קיסר","a1,a2,a3","put a1 and a2 and a3",null,username));


        Log.i("home","list: "+recipeList.toString());
        //  originalRecipeList=new ArrayList<>(recipeList);
        // Log.i("home","original list: "+originalRecipeList.toString());

        recipeAdapter = new RecipeAdapter(recipeList, recipeName -> {
            Intent intent = new Intent(HomeViewActivity.this, CameraTempActivity.class);
            intent.putExtra("RECIPE_NAME", recipeName);
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


        btnOpenGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeViewActivity.this, GuideActivity.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });

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
            setRecipeList(username); // קריאה לטעינת הנתונים
        }
    }

}
