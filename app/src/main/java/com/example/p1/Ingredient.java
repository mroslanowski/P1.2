package com.example.p1;
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

    @ColumnInfo(name = "recipeId")
    public int recipeId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "quantity")
    public String quantity;

    @ColumnInfo(name = "unit")
    public String unit;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @ColumnInfo(name = "category")
    public String category;

    // Getters
    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    // Optionally, setters can be added if needed
    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setRecipeId(int id) {
        this.recipeId = id;
    }
}
