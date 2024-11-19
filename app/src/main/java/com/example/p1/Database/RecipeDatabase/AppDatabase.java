package com.example.p1.Database.RecipeDatabase;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Recipe.class, Ingredient.class, RecipeIngredient.class, ShoppingListItem.class, MealPlan.class, MealPlanRecipe.class}, version = 1) // Zwiększ numer wersji
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
    public abstract IngredientDao ingredientDao();
    public abstract ShoppingListDao shoppingListDao();
    public abstract RecipeIngredientDao recipeIngredientDao();
    public abstract MealPlanDao mealPlanDao();
    public abstract MealPlanRecipeDao mealPlanRecipeDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "BAZA")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

