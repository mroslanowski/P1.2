package com.example.p1.Database.RecipeDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM Ingredient WHERE recipeId = :recipeId")
    List<Ingredient> getIngredientsByRecipeId(int recipeId);

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

    @Insert
    long insert(Recipe recipe);

    @Update
    void update(Recipe recipe);  // Używamy update, aby zaktualizować wartości odżywcze

    @Query("SELECT * FROM recipe WHERE name = :name LIMIT 1")
    Recipe getRecipeByName(String name);

    @Delete
    void delete(Recipe recipe); // Metoda do usuwania przepisu

}
