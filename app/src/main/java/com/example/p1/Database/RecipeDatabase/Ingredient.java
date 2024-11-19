package com.example.p1.Database.RecipeDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "uid",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE
        )
)
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "recipeId", defaultValue = "NULL")
    public Integer recipeId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "quantity")
    public double quantity;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "kcal")
    public double kcal;

    @ColumnInfo(name = "protein")
    public double protein;

    @ColumnInfo(name = "carb")
    public double carb;

    @ColumnInfo(name = "fat")
    public double fat;

    @ColumnInfo(name = "isPurchased")
    public Boolean isPurchased;

    public Ingredient(String name, int recipeId) {
        this.name = name;
        this.recipeId = recipeId;
    }

    public Ingredient() {

    }

    public Boolean getPurchased() {
        return isPurchased;
    }

    public void setPurchased(Boolean purchased) {
        this.isPurchased = purchased;
    }

    public String getCategory() {
        return category;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarb() {
        return carb;
    }

    public void setCarb(double carb) {
        this.carb = carb;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getKcal() {
        return kcal;
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getId() {
        return id;
    }
}
