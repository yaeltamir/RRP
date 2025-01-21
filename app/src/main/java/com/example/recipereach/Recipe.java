package com.example.recipereach;

public class Recipe {
    private String userId;
    private String name;
    private String ingredients;
    private String instructions;
    private String notes;

    public Recipe(String name, String ingredients, String instructions, String notes){
                                                                                //,String userId) {
        this.userId="need to update this field";
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
}

