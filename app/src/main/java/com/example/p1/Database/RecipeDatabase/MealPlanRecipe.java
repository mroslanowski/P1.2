package com.example.p1.Database.RecipeDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "meal_plan_recipe",
        foreignKeys = {
                @ForeignKey(entity = MealPlan.class,
                        parentColumns = "id",
                        childColumns = "mealPlanId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Recipe.class,
                        parentColumns = "uid",
                        childColumns = "recipeId",
                        onDelete = ForeignKey.CASCADE)
        }
)
public class MealPlanRecipe {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "mealPlanId")
    public int mealPlanId;

    @ColumnInfo(name = "recipeId")
    public int recipeId;

    public MealPlanRecipe(int mealPlanId, int recipeId) {
        this.mealPlanId = mealPlanId;
        this.recipeId = recipeId;
    }
}
