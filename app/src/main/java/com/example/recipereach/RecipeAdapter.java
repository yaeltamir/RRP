//package com.example.recipereach;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
//
//    private final List<String> recipeList;
//
//    public RecipeAdapter(List<String> recipeList) {
//        this.recipeList = recipeList;
//    }
//
//    @NonNull
//    @Override
//    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(android.R.layout.simple_list_item_1, parent, false);
//        return new RecipeViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
//        holder.recipeTextView.setText(recipeList.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return recipeList.size();
//    }
//
//    static class RecipeViewHolder extends RecyclerView.ViewHolder {
//        TextView recipeTextView;
//
//        public RecipeViewHolder(@NonNull View itemView) {
//            super(itemView);
//            recipeTextView = itemView.findViewById(android.R.id.text1);
//        }
//    }
//}

package com.example.recipereach;
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.CheckBox;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
//
//    private List<Recipe> recipeList;
//    private List<Recipe> selectedRecipes = new ArrayList<>();
//    private OnRecipeClickListener listener;
//
//    public interface OnRecipeClickListener {
//        void onRecipeClick(Recipe recipe);
//    }
//
//    public RecipeAdapter(List<Recipe> recipeList) {
//        this.recipeList = recipeList;
//    }
//
//    public void setOnRecipeClickListener(OnRecipeClickListener listener) {
//        this.listener = listener;
//    }
//
//    public List<Recipe> getSelectedRecipes() {
//        return selectedRecipes;
//    }
//
//    public void clearSelection() {
//        selectedRecipes.clear();
//    }
//
//    @NonNull
//    @Override
//    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
//        return new RecipeViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
//        Recipe recipe = recipeList.get(position);
//        holder.recipeName.setText(recipe.getName());
//
//        holder.cardView.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onRecipeClick(recipe);
//            }
//        });
//
//        holder.cardView.setOnLongClickListener(v -> {
//            if (selectedRecipes.contains(recipe)) {
//                selectedRecipes.remove(recipe);
//                holder.checkBox.setChecked(false);
//            } else {
//                selectedRecipes.add(recipe);
//                holder.checkBox.setChecked(true);
//            }
//            return true;
//        });
//
//        holder.checkBox.setChecked(selectedRecipes.contains(recipe));
//    }
//
//    @Override
//    public int getItemCount() {
//        return recipeList.size();
//    }
//
//    static class RecipeViewHolder extends RecyclerView.ViewHolder {
//        TextView recipeName;
//        CardView cardView;
//        CheckBox checkBox;
//
//        public RecipeViewHolder(@NonNull View itemView) {
//            super(itemView);
//            recipeName = itemView.findViewById(R.id.tvRecipeName);
//            cardView = itemView.findViewById(R.id.cardViewRecipe);
//            checkBox = itemView.findViewById(R.id.checkBox);
//        }
//    }
//}
//
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> recipes;
    private final OnRecipeClickListener listener;

    public interface OnRecipeClickListener {
        void onRecipeClick(String recipeName);
    }

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String recipe = recipes.get(position).getName();
        holder.recipeName.setText(recipe);
        holder.itemView.setOnClickListener(v -> listener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName;

        ViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
        }
    }

    public void updateList(List<Recipe> newList) {
        recipes = newList;
        notifyDataSetChanged();
    }

}

