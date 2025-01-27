package com.example.recipereach;

public class Recipe {
    private String userId;
    private String name;
    private String ingredients;
    private String instructions;
    private String notes;

    public Recipe(String name, String ingredients, String instructions, String notes,String userId) {
        this.userId=userId;
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", instructions='" + instructions + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getNotes() {
        return notes;
    }

    public String getFullRecipe(){
        String notesToAdd=notes!=null?"\n הערות נוספות: \n"+notes:"";
        return name+'\n'+"מצרכים:\n"+ingredients+"אופן ההכנה: \n"+instructions+notesToAdd;
    }
}

