package com.example.p1;

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

    public String getName() {
        return name;
    }

    public String getDescription(){
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUid() {
        return uid;
    }
}