package com.example.p1.pp;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.p1.Fragments.NormalFragment.IngredientList;
import com.example.p1.Fragments.NormalFragment.MealPlan;
import com.example.p1.Fragments.NormalFragment.MeasureBody;
import com.example.p1.Fragments.NormalFragment.RecipeList;
import com.example.p1.Fragments.NormalFragment.ShoppingList;
import com.example.p1.R;
import com.example.p1.databinding.ActivityMainBinding;
import com.example.p1.Database.RecipeDatabase.AppDatabase;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ReplaceFragment(new MealPlan());
        database = AppDatabase.getInstance(this);
        new Thread(() -> {
            database.recipeDao().getAllRecipes(); // Wykonanie zapytania (nawet jeśli nie używasz wyniku)
        }).start();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item ->{
            if(item.getItemId() == R.id.plan){
                ReplaceFragment(new MealPlan());
            }
            else if(item.getItemId() == R.id.addRecipe) {
                ReplaceFragment(new RecipeList());
            }
            else if(item.getItemId() == R.id.addIngredient){
                ReplaceFragment(new IngredientList());
            }
            else if(item.getItemId() == R.id.shoppingList){
                ReplaceFragment(new ShoppingList());
            }
            else if(item.getItemId() == R.id.measurements){
                ReplaceFragment(new MeasureBody());
            };
            return true;
        });
    }
    private void ReplaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
    public AppDatabase getDatabase() {
        return database;
    }
}