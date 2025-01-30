package com.example.recipereach;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes;  // List of recipes
    private final OnRecipeClickListener listener;  // Listener for recipe clicks

    // Interface for handling recipe click events
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipeName);
    }

    // Constructor that takes the list of recipes and the listener for clicks
    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creates the ViewHolder for each item in the list
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Binds data to the ViewHolder for the current recipe
        Recipe recipe = recipes.get(position);
        holder.recipeName.setText(recipe.getName());  // Sets the recipe name
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));  // Sets click listener for the recipe
    }

    @Override
    public int getItemCount() {
        // Returns the number of recipes in the list
        return recipes.size();
    }

    // ViewHolder class to store views for each item
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName;  // TextView to display the recipe name

        ViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);  // Initializes the recipe name TextView
        }
    }

    // Method to update the list of recipes and notify the adapter of data changes
    public void updateList(List<Recipe> newList) {
        recipes = newList;
        notifyDataSetChanged();  // Notifies the adapter that the data has changed
    }
}
