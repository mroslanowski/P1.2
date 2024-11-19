package com.example.p1.Database.RecipeDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_plan")
public class MealPlan {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "meal_date")
    public String mealDate;

    @ColumnInfo(name = "total_calories")
    public double totalCalories;

    @ColumnInfo(name = "total_protein")
    public double totalProtein;

    @ColumnInfo(name = "total_carbs")
    public double totalCarbs;

    @ColumnInfo(name = "total_fats")
    public double totalFats;

    public MealPlan(String mealDate, double totalCalories, double totalProtein, double totalCarbs, double totalFats) {
        this.mealDate = mealDate;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFats = totalFats;
    }

    public MealPlan() {
    }
}
