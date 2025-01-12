package com.example.recipereach;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeViewActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecipeAdapter recipeAdapter;
    private List<String> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view_activity);

        TextView userNameTextView = findViewById(R.id.userNameTextView);
        RecyclerView recipeRecyclerView = findViewById(R.id.recipeRecyclerView);
        FloatingActionButton addRecipeButton = findViewById(R.id.addRecipeButton);

        // קבלת שם המשתמש מהמסך הקודם
        //String userName = getIntent().getStringExtra("userName");
        String userName="abc";
        userNameTextView.setText("Hello, " + userName);

        // חיבור למסד הנתונים
        dbHelper = new DatabaseHelper(this);

        // טוען מתכונים למשתמש
        recipeList = new ArrayList<>();
        Cursor cursor = dbHelper.getRecipes(userName);
        if (cursor.moveToFirst()) {
            do {
                int temp=cursor.getColumnIndex("recipe")>=0?cursor.getColumnIndex("recipe"):0;
                recipeList.add(cursor.getString(temp));
            } while (cursor.moveToNext());
        }

        // הגדרת RecyclerView
        recipeAdapter = new RecipeAdapter(recipeList);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeRecyclerView.setAdapter(recipeAdapter);

        // מעבר לדף הוספת מתכון
        addRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeViewActivity.this, AddRecipeActivity.class);
            intent.putExtra("userName", userName);
            startActivity(intent);
        });
    }
}
