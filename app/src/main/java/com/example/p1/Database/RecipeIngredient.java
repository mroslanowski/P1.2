package com.example.p1.Database;

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
    public int recipeId;
    public int ingredientId;

    public RecipeIngredient(int recipeId, int ingredientId) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
    }
}
