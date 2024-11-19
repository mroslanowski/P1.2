package com.example.p1;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping_list_items")
public class ShoppingListItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public boolean purchased;

    public ShoppingListItem(String name, boolean purchased) {
        this.name = name;
        this.purchased = purchased;
    }
}
