package com.example.p1;

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

    @Update
    void updateIngredient(Ingredient ingredient);
}
