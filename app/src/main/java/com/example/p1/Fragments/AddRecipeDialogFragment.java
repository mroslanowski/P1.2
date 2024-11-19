package com.example.p1.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1.Database.*;
import com.example.p1.Adapter.IngredientAdapter;
import com.example.p1.R;
import com.example.p1.Database.RecipeIngredient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddRecipeDialogFragment extends BottomSheetDialogFragment {

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
        private void addIngredientToRecyclerView(String ingredientName) {
            selectedIngredients.add(ingredientName);
            ingredientAdapter.notifyDataSetChanged();
        }


        private void addRecipe() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || selectedIngredients.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Używamy ExecutorService do obsługi operacji na bazie danych w tle
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Tworzymy obiekt przepisu i zapisujemy go w bazie
            Recipe recipe = new Recipe(name, description);
            long recipeId = db.recipeDao().insert(recipe);  // Zakładając, że insert() zwraca ID nowo dodanego przepisu

            // Teraz dodajemy składniki do tabeli łączącej RecipeIngredient
            for (String ingredientName : selectedIngredients) {
                // Sprawdzamy, czy składnik już istnieje w bazie
                Ingredient ingredient = db.ingredientDao().getIngredientByName(ingredientName);

                if (ingredient != null) {
                    // Tworzymy rekord w tabeli RecipeIngredient
                    RecipeIngredient recipeIngredient = new RecipeIngredient((int) recipeId, ingredient.getId());
                    db.recipeIngredientDao().insertRecipeIngredient(recipeIngredient);
                }
            }

            // Po zakończeniu operacji, aktualizujemy UI na głównym wątku
            requireActivity().runOnUiThread(() -> {
                // Po dodaniu przepisu i składników, wyczyść listę składników
                selectedIngredients.clear();
                ingredientAdapter.notifyDataSetChanged();  // Odświeżamy RecyclerView

                Toast.makeText(getContext(), "Recipe added successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        });
    }


}
