package com.example.p1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ShoppingListDao {
    @Insert
    void insert(ShoppingListItem item);

    @Query("SELECT * FROM shopping_list_items")
    List<ShoppingListItem> getAllItems();

    @Query("DELETE FROM shopping_list_items")
    void deleteAllItems();

    @Delete
    void deleteItem(ShoppingListItem item);
}
