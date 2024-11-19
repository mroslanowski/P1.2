package com.example.p1.pp;

import android.app.Application;

import com.example.p1.Database.RecipeDatabase.AppDatabase;

public class MyApp extends Application {
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicjalizacja bazy danych
        database = AppDatabase.getInstance(this);
        AppDatabase db = AppDatabase.getInstance(this);
        new Thread(() -> {
            db.recipeDao().getAllRecipes();
        }).start();
    }

    // Metoda do uzyskania bazy danych
    public static AppDatabase getDatabase() {
        return database;
    }
}
