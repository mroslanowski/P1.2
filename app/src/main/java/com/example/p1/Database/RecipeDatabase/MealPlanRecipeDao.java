package com.example.p1.Database.RecipeDatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MealPlanRecipeDao {

    @Insert
    void insertMealPlanRecipe(MealPlanRecipe mealPlanRecipe);

    @Query("SELECT recipeId FROM meal_plan_recipe WHERE mealPlanId = :mealPlanId")
    List<Integer> getRecipeIdsForMealPlan(int mealPlanId);

    @Query("DELETE FROM meal_plan_recipe WHERE mealPlanId = :mealPlanId")
    void deleteRecipesForMealPlan(int mealPlanId);
}
