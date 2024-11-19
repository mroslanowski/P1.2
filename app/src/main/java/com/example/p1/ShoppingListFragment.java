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

public class ShoppingListFragment extends Fragment {

    private Button addItemButton;
    private RecyclerView shoppingListRecyclerView;
    private ShoppingListAdapter shoppingListAdapter;
    private List<Ingredient> shoppingList;

    private AppDatabase db; // Reference to the database
    private int defaultRecipeId = -1; // Default recipe ID

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        // Initialize views
        addItemButton = view.findViewById(R.id.addItemButton);
        shoppingListRecyclerView = view.findViewById(R.id.shoppingListRecyclerView);

        // Initialize shopping list
        shoppingList = new ArrayList<>();

        // Set up RecyclerView
        shoppingListAdapter = new ShoppingListAdapter(
                shoppingList,
                ingredient -> deleteIngredient(ingredient), // Handle delete
                (ingredient, newName) -> editIngredient(ingredient, newName) // Handle edit
        );
        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        shoppingListRecyclerView.setAdapter(shoppingListAdapter);

        // Initialize database instance
        db = AppDatabase.getInstance(requireContext());

        // Fetch the ingredients from the database when the fragment is created
        fetchIngredientsFromDatabase();

        // Open dialog to add item
        addItemButton.setOnClickListener(v -> {
            AddIngredientDialogFragment dialog = new AddIngredientDialogFragment(ingredient -> {
                addIngredientToDatabase(ingredient);
            });
            dialog.show(getParentFragmentManager(), "AddIngredientDialog");
        });

        return view;
    }

    // Ensure that the default Recipe exists only once
    private void ensureDefaultRecipeExists() {
        new Thread(() -> {
            // Check if a recipe with the name "Default Recipe" exists
            Recipe defaultRecipe = db.recipeDao().getRecipeByName("Default Recipe");
            if (defaultRecipe == null) {
                // Create the default recipe if it doesn't exist
                Recipe newRecipe = new Recipe();
                newRecipe.setName("Default Recipe");
                db.recipeDao().insertRecipe(newRecipe);

                // Retrieve the newly inserted recipe's ID
                defaultRecipe = db.recipeDao().getRecipeByName("Default Recipe");
            }

            // Store the default recipe ID for future operations
            if (defaultRecipe != null) {
                defaultRecipeId = defaultRecipe.getUid();
            }
        }).start();
    }

    // Add an ingredient to the database and update the list
    private void addIngredientToDatabase(Ingredient ingredient) {
        ensureDefaultRecipeExists();

        // Add ingredient to database in a background thread
        new Thread(() -> {
            if (defaultRecipeId != -1) {
                ingredient.setRecipeId(defaultRecipeId);

                db.ingredientDao().insertIngredient(ingredient);

                // Update UI on the main thread
                requireActivity().runOnUiThread(() -> {
                    fetchIngredientsFromDatabase();
                    Toast.makeText(requireContext(), "Ingredient added successfully", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // Fetch ingredients from the database and update the RecyclerView
    private void fetchIngredientsFromDatabase() {
        new Thread(() -> {
            List<Ingredient> ingredients = db.ingredientDao().getAllIngredients();

            requireActivity().runOnUiThread(() -> {
                shoppingList.clear();
                shoppingList.addAll(ingredients);
                shoppingListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    // Delete an ingredient from the database
    private void deleteIngredient(Ingredient ingredient) {
        new Thread(() -> {
            db.ingredientDao().deleteIngredient(ingredient);
            fetchIngredientsFromDatabase();
        }).start();
    }

    // Edit an ingredient in the database
    private void editIngredient(Ingredient ingredient, String newName) {
        new Thread(() -> {
            ingredient.setName(newName);
            db.ingredientDao().updateIngredient(ingredient);
            fetchIngredientsFromDatabase();
        }).start();
    }
}
