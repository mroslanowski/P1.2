package com.example.p1;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.p1.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ReplaceFragment(new HomeFragment());
        database = AppDatabase.getInstance(this);
        new Thread(() -> {
            database.recipeDao().getAllRecipes(); // Wykonanie zapytania (nawet jeśli nie używasz wyniku)
        }).start();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item ->{
            if(item.getItemId() == R.id.home){
                ReplaceFragment(new HomeFragment());
            } else if(item.getItemId() == R.id.plan) {
                ReplaceFragment(new MealPlanFragment());
            }else if(item.getItemId() == R.id.addrecipe){
                ReplaceFragment(new AddRecipeFragment());
            }
            else if(item.getItemId() == R.id.recipelist){
                ReplaceFragment(new RecipeListFragment());
            }
            else if(item.getItemId() == R.id.shoppinglist){
                ReplaceFragment(new ShoppingListFragment());
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