package com.example.p1;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecipeListFragment extends Fragment {

    private EditText addRecipeEditText;
    private Button addRecipeButton;
    private RecyclerView recipeListRecyclerView;
    private RecipeListAdapter recipeListAdapter;
    private List<Recipe> recipeList;

    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        // Inicjalizacja widoków
        addRecipeEditText = view.findViewById(R.id.addRecipeEditText);
        addRecipeButton = view.findViewById(R.id.addRecipeButton);
        recipeListRecyclerView = view.findViewById(R.id.recipeListRecyclerView);

        // Inicjalizacja RecyclerView
        recipeList = new ArrayList<>();
        recipeListAdapter = new RecipeListAdapter(recipeList, this::deleteRecipe, this::editRecipe);
        recipeListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recipeListRecyclerView.setAdapter(recipeListAdapter);

        // Inicjalizacja bazy danych
        db = AppDatabase.getInstance(requireContext());

        // Pobranie przepisów z bazy
        fetchRecipesFromDatabase();

        // Dodawanie nowego przepisu
        addRecipeButton.setOnClickListener(v -> {
            String recipeName = addRecipeEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(recipeName)) {
                new Thread(() -> {
                    Recipe recipe = new Recipe();
                    recipe.setName(recipeName);
                    db.recipeDao().insertRecipe(recipe);
                    fetchRecipesFromDatabase(); // Aktualizacja listy
                }).start();
                addRecipeEditText.setText("");
            } else {
                Toast.makeText(requireContext(), "Recipe name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Pobieranie przepisów z bazy
    private void fetchRecipesFromDatabase() {
        new Thread(() -> {
            List<Recipe> recipes = db.recipeDao().getAllRecipes();
            requireActivity().runOnUiThread(() -> {
                recipeList.clear();
                recipeList.addAll(recipes);
                recipeListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    // Usuwanie przepisu
    private void deleteRecipe(Recipe recipe) {
        new Thread(() -> {
            db.recipeDao().deleteRecipe(recipe.getUid());
            fetchRecipesFromDatabase();
        }).start();
    }

    // Edytowanie przepisu
    private void editRecipe(Recipe recipe, String newName) {
        new Thread(() -> {
            recipe.setName(newName);
            db.recipeDao().updateRecipe(recipe);
            fetchRecipesFromDatabase();
        }).start();
        addRecipeEditText.setText("");
    }
}
