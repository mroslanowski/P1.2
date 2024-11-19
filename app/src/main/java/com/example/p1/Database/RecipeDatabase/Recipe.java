package com.example.p1.Database.RecipeDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    // Nowe pola dla wartości odżywczych
    @ColumnInfo(name = "calories")
    public double calories;

    @ColumnInfo(name = "protein")
    public double protein;

    @ColumnInfo(name = "carbs")
    public double carbs;

    @ColumnInfo(name = "fats")
    public double fats;

    // Konstruktor
    public Recipe(String name, String description, double calories, double protein, double carbs, double fats) {
        this.name = name;
        this.description = description;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }

    public Recipe(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Konstruktor bez parametrów
    public Recipe() {}

    // Gettery i settery
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getUid() {
        return uid;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
