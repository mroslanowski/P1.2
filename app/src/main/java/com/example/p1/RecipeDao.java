package com.example.p1;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecipeDao {

    // Wstawianie nowego przepisu do bazy danych
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertRecipe(Recipe recipe);

    // Wstawianie listy składników do bazy danych
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertIngredients(List<Ingredient> ingredients);

    // Pobieranie przepisu na podstawie jego id wraz ze składnikami
    @Transaction
    @Query("SELECT * FROM Recipe WHERE uid = :recipeId")
    RecipeWithIngredients getRecipeWithIngredients(int recipeId);

    // Pobieranie wszystkich przepisów
    @Query("SELECT * FROM Recipe")
    List<Recipe> getAllRecipes();

    // Usuwanie przepisu na podstawie jego id
    @Query("DELETE FROM Recipe WHERE uid = :recipeId")
    void deleteRecipe(int recipeId);

    // Usuwanie wszystkich przepisów
    @Query("DELETE FROM Recipe")
    void deleteAllRecipes();

    @Transaction
    @Query("SELECT * FROM Recipe WHERE name = :recipeName LIMIT 1")
    RecipeWithIngredients getRecipeWithIngredientsByName(String recipeName);

    @Query("SELECT * FROM Recipe WHERE uid = :id")
    Recipe getRecipeById(int id);

    @Insert
    void insertIngredient(Ingredient ingredient);

    @Update
    void updateRecipe(Recipe recipe);

    @Query("SELECT * FROM recipe WHERE name = :name LIMIT 1")
    Recipe getRecipeByName(String name);


}
