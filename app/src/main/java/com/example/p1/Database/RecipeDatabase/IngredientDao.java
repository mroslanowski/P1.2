package com.example.p1.Database.RecipeDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IngredientDao {

    // Pobiera wszystkie składniki
    @Query("SELECT * FROM Ingredient")
    List<Ingredient> getAllIngredients();

    // Wstawia nowy składnik do bazy danych
    @Insert
    void insertIngredient(Ingredient ingredient);

    // Usuwa składnik z bazy danych (po obiekcie Ingredient)
    @Delete
    void deleteIngredient(Ingredient ingredient);

    // Możesz także dodać metodę usuwania po ID składnika
    @Query("DELETE FROM Ingredient WHERE id = :ingredientId")
    void deleteIngredientById(int ingredientId);

    @Query("SELECT * FROM Ingredient WHERE id = :ingredientId")
    Ingredient getIngredientById(int ingredientId);

    @Query("SELECT * FROM Ingredient WHERE name = :ingredientName")
    Ingredient getIngredientByName(String ingredientName);

    @Update
    void updateIngredient(Ingredient ingredient);


    @Query("SELECT i.* FROM Ingredient i " +
            "INNER JOIN RecipeIngredient ri ON i.id = ri.ingredientId " +
            "WHERE ri.recipeId = :recipeId")
    List<Ingredient> getIngredientsByRecipeId(int recipeId);

    @Query("SELECT * FROM Ingredient WHERE recipeId = :recipeId")
    List<Ingredient> getIngredientsForRecipe(int recipeId);
}
