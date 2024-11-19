package com.example.p1.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.p1.Database.Recipe;
import com.example.p1.Adapter.*;
import com.example.p1.R;

import com.example.p1.Database.*;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class MealPlanFragment extends Fragment {

    private RecipeDao recipeDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        // Inicjalizacja bazy danych
        recipeDao = AppDatabase.getInstance(requireContext()).recipeDao();

        MaterialButton addBreakfastButton = view.findViewById(R.id.addBreakfastButton);
        MaterialButton addLunchButton = view.findViewById(R.id.addLunchButton);
        MaterialButton addDinnerButton = view.findViewById(R.id.addDinnerButton);

        // Obsługa kliknięć dla przycisków
        addBreakfastButton.setOnClickListener(v -> showRecipeDialog("Breakfast"));
        addLunchButton.setOnClickListener(v -> showRecipeDialog("Lunch"));
        addDinnerButton.setOnClickListener(v -> showRecipeDialog("Dinner"));

        return view;
    }

    private void showRecipeDialog(String mealType) {
        // Run the database query on a background thread
        new Thread(() -> {
            // Pobranie przepisów z bazy danych w tle
            List<Recipe> recipes = recipeDao.getAllRecipes();

            // Jeśli nie ma przepisów, wyświetl komunikat
            if (recipes.isEmpty()) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "No recipes found!", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            // Stworzenie widoku dla dialogu
            requireActivity().runOnUiThread(() -> {
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_recipes, null, false);
                RecyclerView recyclerView = dialogView.findViewById(R.id.recipeRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                // Adapter do wyświetlenia przepisów
                RecipeAdapter adapter = new RecipeAdapter(recipes, selectedRecipe -> {
                    // Działania po wybraniu przepisu
                    Toast.makeText(requireContext(), "Added " + selectedRecipe.getName() + " to " + mealType, Toast.LENGTH_SHORT).show();
                });

                recyclerView.setAdapter(adapter);

                // Utworzenie i wyświetlenie dialogu
                new AlertDialog.Builder(requireContext())
                        .setTitle("Select a Recipe for " + mealType)
                        .setView(dialogView)
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }).start();
    }

}
