package com.example.p1.Database;

import androidx.room.Dao;
import androidx.room.Delete;
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

    // Pobieranie wszystkich przepisów
    @Query("SELECT * FROM Recipe")
    List<Recipe> getAllRecipes();

    // Usuwanie przepisu na podstawie jego id
    @Query("DELETE FROM Recipe WHERE uid = :recipeId")
    void deleteRecipe(int recipeId);

    // Usuwanie wszystkich przepisów
    @Query("DELETE FROM Recipe")
    void deleteAllRecipes();


    @Query("SELECT * FROM Recipe WHERE uid = :id")
    Recipe getRecipeById(int id);

    @Insert
    void insertIngredient(Ingredient ingredient);

    @Update
    void updateRecipe(Recipe recipe);

    @Query("SELECT * FROM recipe WHERE name = :name LIMIT 1")
    Recipe getRecipeByName(String name);


    @Insert
    long insert(Recipe recipe);
    @Delete
    void delete(Recipe recipe); // Metoda do usuwania przepisu

}
