package com.example.p1.Database.PersonDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class Person {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "date")
    @NotNull
    private String date;

    @ColumnInfo(name = "weight")
    private double weight;

    @ColumnInfo(name = "abdomen")
    private Double abdomen;

    @ColumnInfo(name = "neck")
    private Double neck;

    @ColumnInfo(name = "hips")
    private Double hips;

    @ColumnInfo(name = "thigh")
    private Double thigh;

    @ColumnInfo(name = "chest")
    private Double chest;

    @ColumnInfo(name = "calf")
    private Double calf;

    @ColumnInfo(name = "arm")
    private Double arm;

    @ColumnInfo(name = "waist")
    private Double waist;

    // Constructor
    public Person(@NotNull String date, double weight) {
        this.date = date;
        this.weight = weight;
    }

    public Person() {}

    // Getters and Setters
    // (Include the setters and getters for all new fields)

    public Double getAbdomen() {
        return abdomen;
    }

    public void setAbdomen(Double abdomen) {
        this.abdomen = abdomen;
    }

    public Double getNeck() {
        return neck;
    }

    public void setNeck(Double neck) {
        this.neck = neck;
    }

    public Double getHips() {
        return hips;
    }

    public void setHips(Double hips) {
        this.hips = hips;
    }

    public Double getThigh() {
        return thigh;
    }

    public void setThigh(Double thigh) {
        this.thigh = thigh;
    }

    public Double getChest() {
        return chest;
    }

    public void setChest(Double chest) {
        this.chest = chest;
    }

    public Double getCalf() {
        return calf;
    }

    public void setCalf(Double calf) {
        this.calf = calf;
    }

    public Double getArm() {
        return arm;
    }

    public void setArm(Double arm) {
        this.arm = arm;
    }

    public Double getWaist() {
        return waist;
    }

    public void setWaist(Double waist) {
        this.waist = waist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public String getDate() {
        return date;
    }

    public void setDate(@NotNull String date) {
        this.date = date;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
