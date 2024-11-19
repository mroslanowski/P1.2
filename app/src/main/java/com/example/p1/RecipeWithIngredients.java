package com.example.p1;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RecipeWithIngredients {
    @Embedded
    public Recipe recipe;

    @Relation(
            parentColumn = "uid",
            entityColumn = "recipeId"
    )
    public List<Ingredient> ingredients;
}
