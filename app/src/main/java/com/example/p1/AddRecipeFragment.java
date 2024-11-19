package com.example.p1;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddRecipeFragment extends Fragment {

    private LinearLayout ingredientsContainer;
    private TextInputLayout recipeNameInput;
    private TextInputLayout descriptionInput;
    private AppDatabase db;
    private View view;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_recipe_activity, container, false);

        db = AppDatabase.getInstance(getContext()); // Inicjacja AppDatabase

        ingredientsContainer = view.findViewById(R.id.ingredientsContainer);
        MaterialButton addIngredientButton = view.findViewById(R.id.addIngredientButton);
        MaterialButton removeIngredientButton = view.findViewById(R.id.removeIngredientButton);
        recipeNameInput = view.findViewById(R.id.textFieldLayout);
        descriptionInput = view.findViewById(R.id.descriptionFieldLayout);

        // Set click listeners
        addIngredientButton.setOnClickListener(v -> addIngredientField());
        removeIngredientButton.setOnClickListener(v -> removeIngredientField());

        MaterialButton addRecipeButton = view.findViewById(R.id.addRecipeButton);

        addRecipeButton.setOnClickListener(v -> {
            if (validateRecipeFields()) {
                String recipeName = Objects.requireNonNull(recipeNameInput.getEditText()).getText().toString().trim();
                String description = Objects.requireNonNull(descriptionInput.getEditText()).getText().toString().trim();
                List<Ingredient> ingredients = collectIngredients();

                // Tworzymy obiekt Recipe
                Recipe recipe = new Recipe();
                recipe.name = recipeName;
                recipe.description = description;

                // Zapisujemy przepis w bazie Room w osobnym wątku
                new Thread(() -> {

                    long recipeId = db.recipeDao().insertRecipe(recipe);

                    // Dodajemy składniki do przepisu
                    for (Ingredient ingredient : ingredients) {
                        ingredient.recipeId = (int) recipeId;
                    }
                    db.recipeDao().insertIngredients(ingredients);

                    // Pokażemy użytkownikowi informację, że przepis został dodany
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Recipe added successfully!", Toast.LENGTH_SHORT).show()
                    );
                }).start();
            }
        });

        return view;
    }

    // Dodaje nowe pole dla składnika
    private void addIngredientField() {
        LinearLayout ingredientLayout = new LinearLayout(requireContext());
        ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Ingredient Name
        TextInputLayout nameLayout = new TextInputLayout(requireContext());
        nameLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        nameLayout.setHint("Ingredient");
        TextInputEditText nameEditText = new TextInputEditText(requireContext());
        nameLayout.addView(nameEditText);

        // Quantity
        TextInputLayout quantityLayout = new TextInputLayout(requireContext());
        quantityLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        quantityLayout.setHint("Quantity");
        TextInputEditText quantityEditText = new TextInputEditText(requireContext());
        quantityEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        quantityLayout.addView(quantityEditText);

        // Unit
        TextInputLayout unitLayout = new TextInputLayout(requireContext());
        unitLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        unitLayout.setHint("Unit");
        TextInputEditText unitEditText = new TextInputEditText(requireContext());
        unitLayout.addView(unitEditText);

        // Add all components to the container layout
        ingredientLayout.addView(nameLayout);
        ingredientLayout.addView(quantityLayout);
        ingredientLayout.addView(unitLayout);
        ingredientsContainer.addView(ingredientLayout);
    }

    // Usuwa ostatnie dodane pole składnika
    private void removeIngredientField() {
        int childCount = ingredientsContainer.getChildCount();
        if (childCount > 0) {
            ingredientsContainer.removeViewAt(childCount - 1);
        }
    }

    // Walidacja pól nazwy przepisu, składników i instrukcji
    private boolean validateRecipeFields() {
        boolean isValid = true;

        // Validate recipe name
        if (recipeNameInput.getEditText() != null && recipeNameInput.getEditText().getText().toString().trim().isEmpty()) {
            recipeNameInput.setError("Recipe name cannot be empty");
            isValid = false;
        } else {
            recipeNameInput.setError(null);
        }

        // Validate each ingredient input field
        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            LinearLayout ingredientLayout = (LinearLayout) ingredientsContainer.getChildAt(i);

            TextInputLayout nameLayout = (TextInputLayout) ingredientLayout.getChildAt(0);
            TextInputLayout quantityLayout = (TextInputLayout) ingredientLayout.getChildAt(1);
            TextInputLayout unitLayout = (TextInputLayout) ingredientLayout.getChildAt(2);

            if (nameLayout.getEditText() != null && nameLayout.getEditText().getText().toString().trim().isEmpty()) {
                nameLayout.setError("Ingredient name cannot be empty");
                isValid = false;
            } else {
                nameLayout.setError(null);
            }

            if (quantityLayout.getEditText() != null && quantityLayout.getEditText().getText().toString().trim().isEmpty()) {
                quantityLayout.setError("Quantity cannot be empty");
                isValid = false;
            } else {
                quantityLayout.setError(null);
            }

            if (unitLayout.getEditText() != null && unitLayout.getEditText().getText().toString().trim().isEmpty()) {
                unitLayout.setError("Unit cannot be empty");
                isValid = false;
            } else {
                unitLayout.setError(null);
            }
        }

        // Validate instructions
        if (descriptionInput.getEditText() != null && descriptionInput.getEditText().getText().toString().trim().isEmpty()) {
            descriptionInput.setError("Instructions cannot be empty");
            isValid = false;
        } else {
            descriptionInput.setError(null);
        }

        return isValid;
    }

    // Pobiera listę składników z dynamicznie dodanych pól
    private List<Ingredient> collectIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();

        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            LinearLayout ingredientLayout = (LinearLayout) ingredientsContainer.getChildAt(i);

            TextInputEditText nameEditText = (TextInputEditText) ((TextInputLayout) ingredientLayout.getChildAt(0)).getEditText();
            TextInputEditText quantityEditText = (TextInputEditText) ((TextInputLayout) ingredientLayout.getChildAt(1)).getEditText();
            TextInputEditText unitEditText = (TextInputEditText) ((TextInputLayout) ingredientLayout.getChildAt(2)).getEditText();

            String name = nameEditText != null ? Objects.requireNonNull(nameEditText.getText()).toString().trim() : "";
            double quantity = Double.parseDouble(quantityEditText.getText().toString().trim());
            String unit = unitEditText != null ? Objects.requireNonNull(unitEditText.getText()).toString().trim() : "";

            Ingredient ingredient = new Ingredient();
            ingredient.name = name;
            ingredient.quantity = quantity;
            ingredients.add(ingredient);
        }

        return ingredients;
    }
}
