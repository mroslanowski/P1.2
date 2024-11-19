package com.example.p1.Database.RecipeDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        primaryKeys = {"recipeId", "ingredientId"},
        foreignKeys = {
                @ForeignKey(entity = Recipe.class,
                        parentColumns = "uid",
                        childColumns = "recipeId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Ingredient.class,
                        parentColumns = "id",
                        childColumns = "ingredientId",
                        onDelete = ForeignKey.CASCADE)
        }
)
public class RecipeIngredient {

    @ColumnInfo(name = "recipeId")
    public int recipeId;

    @ColumnInfo(name = "ingredientId")
    public int ingredientId;

    // Nowe kolumny dla wartości odżywczych
    @ColumnInfo(name = "calories")
    public double calories;

    @ColumnInfo(name = "protein")
    public double protein;

    @ColumnInfo(name = "carbs")
    public double carbs;

    @ColumnInfo(name = "fats")
    public double fats;

    // Konstruktor
    public RecipeIngredient(int recipeId, int ingredientId, double calories, double protein, double carbs, double fats) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }

    public RecipeIngredient(int uid, int id) {
        this.recipeId = uid;
        this.ingredientId = id;
    }

    // Gettery i settery
    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }
}
