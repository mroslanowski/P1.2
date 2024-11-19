package com.example.p1.Database;

public class MealItem {
    private String name;
    private int kcal;
    private double protein;
    private double carbs;
    private double fat;

    public MealItem(String name, int kcal, double protein, double carbs, double fat) {
        this.name = name;
        this.kcal = kcal;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
    }

    public String getName() {
        return name;
    }

    public int getKcal() {
        return kcal;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getFat() {
        return fat;
    }
}
