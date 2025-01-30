package com.example.recipereach;

// Importing required classes to work with SQLite
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Defining the database name and version
    private static final String DATABASE_NAME = "recipes.db";
    private static final int DATABASE_VERSION = 1;

    // Defining table and column names
    private static final String TABLE_RECIPES = "recipes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_RECIPE = "recipe";

    // Constructor of the class, which accepts the application context
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This method is called when the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the table with the appropriate columns
        String createTable = "CREATE TABLE " + TABLE_RECIPES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_RECIPE + " TEXT)";
        db.execSQL(createTable);
    }

    // This method is called when the database is upgraded (for example, from one version to another)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Dropping the existing table and creating it again
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        onCreate(db);
    }

    // Method to add a new recipe to the database
    public void addRecipe(String username, String recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_RECIPE, recipe);
        db.insert(TABLE_RECIPES, null, values);
        db.close();
    }

    // Method to retrieve all recipes for a given username
    public Cursor getRecipes(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECIPES, null, COLUMN_USERNAME + "=?",
                new String[]{username}, null, null, null);
    }
}
