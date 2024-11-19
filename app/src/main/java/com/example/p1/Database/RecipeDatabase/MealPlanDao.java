package com.example.p1.Database.RecipeDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MealPlanDao {

    @Insert
    long insertMealPlan(MealPlan mealPlan);

    @Query("SELECT * FROM meal_plan WHERE meal_date = :mealDate")
    MealPlan getMealPlanByDate(String mealDate);

    @Update
    void updateMealPlan(MealPlan mealPlan);

    @Delete
    void deleteMealPlan(MealPlan mealPlan);
}
