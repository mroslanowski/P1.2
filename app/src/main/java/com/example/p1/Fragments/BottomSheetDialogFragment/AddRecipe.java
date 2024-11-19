package com.example.p1.Fragments.BottomSheetDialogFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1.Adapter.IngredientAdapter;
import com.example.p1.Database.RecipeDatabase.AppDatabase;
import com.example.p1.Database.RecipeDatabase.Ingredient;
import com.example.p1.Database.RecipeDatabase.Recipe;
import com.example.p1.R;
import com.example.p1.Database.RecipeDatabase.RecipeIngredient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddRecipe extends BottomSheetDialogFragment {

    private EditText nameEditText, descriptionEditText;
    private RecyclerView ingredientRecyclerView;
    private IngredientAdapter ingredientAdapter;
    private List<String> selectedIngredients = new ArrayList<>();
    private AppDatabase db;  // Baza danych

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_recipe, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        ingredientRecyclerView = view.findViewById(R.id.ingredientRecyclerView);

        // RecyclerView setup
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientAdapter = new IngredientAdapter(getContext(), selectedIngredients);
        ingredientRecyclerView.setAdapter(ingredientAdapter);

        // Handling Add Ingredient Button
        view.findViewById(R.id.addIngredient).setOnClickListener(v -> showAllIngredientsDialog());

        // Handling Add Recipe Button
        view.findViewById(R.id.addButton).setOnClickListener(v -> addRecipe());

        // Initialize the database instance
        db = AppDatabase.getInstance(getContext());

        return view;
    }

    // Show dialog to select ingredients from the database
    private void showAllIngredientsDialog() {
        new Thread(() -> {
            // Fetch all ingredients from the database
            List<Ingredient> allIngredients = db.ingredientDao().getAllIngredients();
            String[] ingredientNames = allIngredients.stream()
                    .map(Ingredient::getName)
                    .toArray(String[]::new);

            requireActivity().runOnUiThread(() -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Available Ingredients")
                        .setItems(ingredientNames, (dialog, which) -> {
                            // Add the selected ingredient's name to the shopping list
                            String selectedIngredientName = ingredientNames[which];
                            addIngredientToRecyclerView(selectedIngredientName);
                        })
                        .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                        .show();
            });
        }).start();
    }

    // Add selected ingredient to RecyclerView list
    @SuppressLint("NotifyDataSetChanged")
    private void addIngredientToRecyclerView(String ingredientName) {
        selectedIngredients.add(ingredientName);
        ingredientAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addRecipe() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || selectedIngredients.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // ExecutorService to handle database operations in the background
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Check if the recipe already exists
            Recipe existingRecipe = db.recipeDao().getRecipeByName(name);
            if (existingRecipe != null) {
                // If the recipe already exists, show a message and stop further operations
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Recipe already exists", Toast.LENGTH_SHORT).show();
                });
                return;  // Stop method execution if the recipe already exists
            }

            // Create a new recipe object
            Recipe recipe = new Recipe(name, description);
            long recipeId = db.recipeDao().insert(recipe);  // Insert the recipe into the database

            // Initialize nutritional values for the recipe
            double totalCalories = 0;
            double totalProtein = 0;
            double totalCarbs = 0;
            double totalFats = 0;

            // Add ingredients to the RecipeIngredient table
            for (String ingredientName : selectedIngredients) {
                Ingredient ingredient = db.ingredientDao().getIngredientByName(ingredientName);

                if (ingredient != null) {
                    // Calculate the nutritional values for the ingredient
                    double ingredientCalories = ingredient.getKcal();
                    double ingredientProtein = ingredient.getProtein();
                    double ingredientCarbs = ingredient.getCarb();
                    double ingredientFats = ingredient.getFat();

                    // Sum the nutritional values for the recipe
                    totalCalories += ingredientCalories;
                    totalProtein += ingredientProtein;
                    totalCarbs += ingredientCarbs;
                    totalFats += ingredientFats;

                    // Create an entry in the RecipeIngredient table (link the recipe with the ingredient)
                    RecipeIngredient recipeIngredient = new RecipeIngredient(
                            (int) recipeId, // Recipe ID
                            ingredient.getId(), // Ingredient ID
                            0, // We don't need nutritional values here
                            0, // We don't need nutritional values here
                            0, // We don't need nutritional values here
                            0 // We don't need nutritional values here
                    );
                    db.recipeIngredientDao().insertRecipeIngredient(recipeIngredient);
                }
            }

            // After finishing, update the nutritional values for the recipe
            recipe.setCalories(totalCalories);
            recipe.setProtein(totalProtein);
            recipe.setCarbs(totalCarbs);
            recipe.setFats(totalFats);

            // Instead of calling insert() again, use update() to update the recipe with its nutritional values
            db.recipeDao().update(recipe); // Update the existing recipe with nutritional values

            // Once the operation is finished, update the UI on the main thread
            requireActivity().runOnUiThread(() -> {
                selectedIngredients.clear();
                ingredientAdapter.notifyDataSetChanged();  // Refresh the RecyclerView

                Toast.makeText(getContext(), "Recipe added successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        });
    }

}
