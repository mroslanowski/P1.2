package com.example.p1;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Recipe.class, Ingredient.class, ShoppingListItem.class}, version = 4) // Zwiększ numer wersji
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
    public abstract IngredientDao ingredientDao();
    public abstract ShoppingListDao shoppingListDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "meal_plan_database1")
                            .fallbackToDestructiveMigration() // Dodaj to, jeśli chcesz, by migracja niszczyła bazę przy niekompatybilności wersji
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

