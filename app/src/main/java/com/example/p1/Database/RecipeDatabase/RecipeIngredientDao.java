package com.example.p1.Database.RecipeDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecipeIngredientDao {

    @Insert
    void insertRecipeIngredient(RecipeIngredient recipeIngredient);
    @Query("DELETE FROM RecipeIngredient WHERE recipeId = :recipeId")
    void deleteRecipeIngredientsByRecipeId(int recipeId);

    @Query("SELECT i.* FROM Ingredient i " +
            "INNER JOIN RecipeIngredient ri ON i.id = ri.ingredientId " +
            "WHERE ri.recipeId = :recipeId")
    List<Ingredient> getIngredientsByRecipeId(int recipeId);

    @Query("SELECT * FROM RecipeIngredient WHERE recipeId = :recipeId")
    List<RecipeIngredient> getIngredientsForRecipe(int recipeId); // Metoda, która zwraca składniki powiązane z przepisem
}
