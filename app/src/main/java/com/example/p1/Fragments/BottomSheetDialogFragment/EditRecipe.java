package com.example.p1.Fragments.BottomSheetDialogFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.p1.Database.RecipeDatabase.RecipeIngredient;
import com.example.p1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditRecipe extends BottomSheetDialogFragment {

    private Recipe recipe;
    private EditText nameEditText, descriptionEditText;
    private EditText totalKcalEditText, totalProteinEditText, totalFatEditText, totalCarbEditText;
    private IngredientAdapter ingredientAdapter;
    private final List<String> selectedIngredients = new ArrayList<>();
    private final RecipeUpdateListener recipeUpdateListener;
    private AppDatabase db;

    public EditRecipe(Recipe recipe, RecipeUpdateListener recipeUpdateListener) {
        this.recipe = recipe;
        this.recipeUpdateListener = recipeUpdateListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_recipe, container, false);

        // Bind views
        nameEditText = view.findViewById(R.id.nameEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        totalKcalEditText = view.findViewById(R.id.kcalEditText);
        totalProteinEditText = view.findViewById(R.id.proteinEditText);
        totalFatEditText = view.findViewById(R.id.fatEditText);
        totalCarbEditText = view.findViewById(R.id.carbEditText);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button updateButton = view.findViewById(R.id.addButton);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button addIngredientButton = view.findViewById(R.id.addIngredient);

        RecyclerView ingredientRecyclerView = view.findViewById(R.id.ingredientRecyclerView);
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientAdapter = new IngredientAdapter(getContext(), selectedIngredients);
        ingredientRecyclerView.setAdapter(ingredientAdapter);

        // Pre-fill fields with recipe data
        nameEditText.setText(recipe.getName());
        descriptionEditText.setText(recipe.getDescription());

        // Initialize database instance
        db = AppDatabase.getInstance(getContext());

        // Load existing ingredients linked to the recipe
        loadRecipeIngredients(recipe.getUid());

        // Add ingredient dialog
        addIngredientButton.setOnClickListener(v -> showAllIngredientsDialog());

        // Save updated recipe
        updateButton.setOnClickListener(v -> updateRecipe());

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadRecipeIngredients(int recipeId) {
        new Thread(() -> {
            List<Ingredient> ingredients = db.recipeIngredientDao().getIngredientsByRecipeId(recipeId);
            requireActivity().runOnUiThread(() -> {
                selectedIngredients.clear();
                for (Ingredient ingredient : ingredients) {
                    selectedIngredients.add(ingredient.getName());
                }
                ingredientAdapter.notifyDataSetChanged();
                updateNutritionalTotals();
            });
        }).start();
    }

    private void showAllIngredientsDialog() {
        new Thread(() -> {
            List<Ingredient> allIngredients = db.ingredientDao().getAllIngredients();
            String[] ingredientNames = allIngredients.stream()
                    .map(Ingredient::getName)
                    .toArray(String[]::new);

            requireActivity().runOnUiThread(() -> {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Available Ingredients")
                        .setItems(ingredientNames, (dialog, which) -> {
                            String selectedIngredientName = ingredientNames[which];
                            addIngredientToRecyclerView(selectedIngredientName);
                        })
                        .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                        .show();
            });
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addIngredientToRecyclerView(String ingredientName) {
        if (!selectedIngredients.contains(ingredientName)) {
            selectedIngredients.add(ingredientName);
            ingredientAdapter.notifyDataSetChanged();
            updateNutritionalTotals();
        } else {
            Toast.makeText(getContext(), "Ingredient already added", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateNutritionalTotals() {
        new Thread(() -> {
            double totalKcal = 0, totalProtein = 0, totalFat = 0, totalCarb = 0;

            for (String ingredientName : selectedIngredients) {
                Ingredient ingredient = db.ingredientDao().getIngredientByName(ingredientName);
                if (ingredient != null) {
                    totalKcal += ingredient.getKcal();
                    totalProtein += ingredient.getProtein();
                    totalFat += ingredient.getFat();
                    totalCarb += ingredient.getCarb();
                }
            }

            double finalTotalKcal = totalKcal;
            double finalTotalProtein = totalProtein;
            double finalTotalFat = totalFat;
            double finalTotalCarb = totalCarb;

            requireActivity().runOnUiThread(() -> {
                totalKcalEditText.setText(String.valueOf(finalTotalKcal));
                totalProteinEditText.setText(String.valueOf(finalTotalProtein));
                totalFatEditText.setText(String.valueOf(finalTotalFat));
                totalCarbEditText.setText(String.valueOf(finalTotalCarb));
            });
        }).start();
    }

    private void updateRecipe() {
        String updatedName = nameEditText.getText().toString().trim();
        String updatedDescription = descriptionEditText.getText().toString().trim();

        if (updatedName.isEmpty() || updatedDescription.isEmpty() || selectedIngredients.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            recipe.setName(updatedName);
            recipe.setDescription(updatedDescription);
            db.recipeDao().updateRecipe(recipe);

            db.recipeIngredientDao().deleteRecipeIngredientsByRecipeId(recipe.getUid());
            for (String ingredientName : selectedIngredients) {
                Ingredient ingredient = db.ingredientDao().getIngredientByName(ingredientName);
                if (ingredient != null) {
                    RecipeIngredient recipeIngredient = new RecipeIngredient(recipe.getUid(), ingredient.getId());
                    db.recipeIngredientDao().insertRecipeIngredient(recipeIngredient);
                }
            }

            requireActivity().runOnUiThread(() -> {
                recipeUpdateListener.onRecipeUpdated(recipe);
                Toast.makeText(getContext(), "Recipe updated successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        });
    }

    public interface RecipeUpdateListener {
        void onRecipeUpdated(Recipe updatedRecipe);
    }
}
