package com.example.p1.Fragments.NormalFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1.Adapter.SelectedRecipeAdapter;
import com.example.p1.Adapter.RecipeAdapter;
import com.example.p1.Database.RecipeDatabase.AppDatabase;
import com.example.p1.Database.RecipeDatabase.Ingredient;
import com.example.p1.Database.RecipeDatabase.MealPlanDao;
import com.example.p1.Database.RecipeDatabase.MealPlanRecipe;
import com.example.p1.Database.RecipeDatabase.MealPlanRecipeDao;
import com.example.p1.Database.RecipeDatabase.Recipe;
import com.example.p1.Database.RecipeDatabase.RecipeDao;
import com.example.p1.Database.RecipeDatabase.RecipeIngredient;
import com.example.p1.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MealPlan extends Fragment {

    private MealPlanDao mealPlanDao;
    private MealPlanRecipeDao mealPlanRecipeDao;
    private RecipeDao recipeDao;

    private SelectedRecipeAdapter selectedRecipeAdapter;

    private EditText totalCalories, totalProtein, totalCarbs, totalFats;
    private RecyclerView mealRecyclerView;
    private final List<Recipe> selectedRecipes = new ArrayList<>();
    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        // Inicjalizacja DAO
        AppDatabase db = AppDatabase.getInstance(requireContext());
        mealPlanDao = db.mealPlanDao();
        mealPlanRecipeDao = db.mealPlanRecipeDao();
        recipeDao = db.recipeDao();

        MaterialButton addBreakfastButton = view.findViewById(R.id.addBreakfastButton);
        MaterialButton addLunchButton = view.findViewById(R.id.addLunchButton);
        MaterialButton addDinnerButton = view.findViewById(R.id.addDinnerButton);

        addBreakfastButton.setOnClickListener(v -> showRecipeDialog("Breakfast"));
        addLunchButton.setOnClickListener(v -> showRecipeDialog("Lunch"));
        addDinnerButton.setOnClickListener(v -> showRecipeDialog("Dinner"));

        // Elementy UI
        totalCalories = view.findViewById(R.id.totalCalories);
        totalProtein = view.findViewById(R.id.totalProtein);
        totalCarbs = view.findViewById(R.id.totalCarbs);
        totalFats = view.findViewById(R.id.totalFats);

        mealRecyclerView = view.findViewById(R.id.mealRecyclerView);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Inicjalizacja adaptera z obsługą kliknięcia przycisku usuwania przepisu
        selectedRecipeAdapter = new SelectedRecipeAdapter(selectedRecipes, (recipe, position) -> {
            // Usunięcie przepisu z listy
            selectedRecipes.remove(position);
            selectedRecipeAdapter.notifyItemRemoved(position); // Odświeżenie RecyclerView po usunięciu przepisu

            // Aktualizacja wartości odżywczych po usunięciu przepisu
            updateNutritionValuesAfterDelete(recipe);
        });

        mealRecyclerView.setAdapter(selectedRecipeAdapter);

        // Obsługa kalendarza
        CalendarView calendarView = view.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            loadMealPlanForDate(selectedDate);
        });

        // Obsługa przycisku zapisu
        MaterialButton saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveMealPlanToDatabase());

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadMealPlanForDate(String date) {
        new Thread(() -> {
            // Pobranie planu dnia
            com.example.p1.Database.RecipeDatabase.MealPlan mealPlan = mealPlanDao.getMealPlanByDate(date);

            if (mealPlan != null) {
                // Pobranie powiązanych przepisów
                List<Integer> recipeIds = mealPlanRecipeDao.getRecipeIdsForMealPlan(mealPlan.id);
                List<Recipe> recipes = new ArrayList<>();
                for (int recipeId : recipeIds) {
                    Recipe recipe = recipeDao.getRecipeById(recipeId);
                    if (recipe != null) {
                        recipes.add(recipe);
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    // Zaktualizuj UI
                    selectedRecipes.clear();
                    selectedRecipes.addAll(recipes);
                    selectedRecipeAdapter.notifyDataSetChanged();

                    totalCalories.setText(String.valueOf(mealPlan.totalCalories));
                    totalProtein.setText(String.valueOf(mealPlan.totalProtein));
                    totalCarbs.setText(String.valueOf(mealPlan.totalCarbs));
                    totalFats.setText(String.valueOf(mealPlan.totalFats));
                });
            } else {
                // Jeśli brak planu na daną datę
                requireActivity().runOnUiThread(() -> {
                    selectedRecipes.clear();
                    selectedRecipeAdapter.notifyDataSetChanged();
                    totalCalories.setText("0");
                    totalProtein.setText("0");
                    totalCarbs.setText("0");
                    totalFats.setText("0");
                    Toast.makeText(requireContext(), "No meal plan found for " + date, Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void saveMealPlanToDatabase() {
        new Thread(() -> {
            // Zapis planu dnia
            double calories = parseDoubleOrZero(totalCalories.getText().toString());
            double protein = parseDoubleOrZero(totalProtein.getText().toString());
            double carbs = parseDoubleOrZero(totalCarbs.getText().toString());
            double fats = parseDoubleOrZero(totalFats.getText().toString());

            com.example.p1.Database.RecipeDatabase.MealPlan mealPlan = new com.example.p1.Database.RecipeDatabase.MealPlan(selectedDate, calories, protein, carbs, fats);
            long mealPlanId = mealPlanDao.insertMealPlan(mealPlan);

            // Zapis powiązanych przepisów
            for (Recipe recipe : selectedRecipes) {
                MealPlanRecipe mealPlanRecipe = new MealPlanRecipe((int) mealPlanId, recipe.getUid());
                mealPlanRecipeDao.insertMealPlanRecipe(mealPlanRecipe);
            }

            requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), "Meal plan saved for " + selectedDate, Toast.LENGTH_SHORT).show()
            );
        }).start();
    }

    private double parseDoubleOrZero(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void updateNutritionValuesAfterDelete(Recipe recipe) {
        // Pobranie wartości odżywczych z usuniętego przepisu
        double deletedCalories = recipe.getCalories();
        double deletedProtein = recipe.getProtein();
        double deletedCarbs = recipe.getCarbs();
        double deletedFats = recipe.getFats();

        // Pobranie aktualnych wartości odżywczych
        double currentCalories = parseDoubleOrZero(totalCalories.getText().toString());
        double currentProtein = parseDoubleOrZero(totalProtein.getText().toString());
        double currentCarbs = parseDoubleOrZero(totalCarbs.getText().toString());
        double currentFats = parseDoubleOrZero(totalFats.getText().toString());

        // Zaktualizowanie wartości po usunięciu przepisu
        totalCalories.setText(String.valueOf(currentCalories - deletedCalories));
        totalProtein.setText(String.valueOf(currentProtein - deletedProtein));
        totalCarbs.setText(String.valueOf(currentCarbs - deletedCarbs));
        totalFats.setText(String.valueOf(currentFats - deletedFats));
    }

    private void showRecipeDialog(String mealType) {
        new Thread(() -> {
            List<Recipe> recipes = recipeDao.getAllRecipes();

            if (recipes.isEmpty()) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "No recipes found!", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            requireActivity().runOnUiThread(() -> {
                View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_recipes, null, false);
                RecyclerView recyclerView = dialogView.findViewById(R.id.recipeRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                RecipeAdapter adapter = new RecipeAdapter(recipes, selectedRecipe -> {
                    addRecipeToList(selectedRecipe, mealType);
                    Toast.makeText(requireContext(), "Added " + selectedRecipe.getName() + " to " + mealType, Toast.LENGTH_SHORT).show();
                });

                recyclerView.setAdapter(adapter);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Select a Recipe for " + mealType)
                        .setView(dialogView)
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addRecipeToList(Recipe recipe, String mealType) {
        new Thread(() -> {
            // Pobierz składniki powiązane z tym przepisem
            List<RecipeIngredient> recipeIngredients = AppDatabase.getInstance(requireContext())
                    .recipeIngredientDao()
                    .getIngredientsForRecipe(recipe.getUid());

            // Inicjalizujemy zmienne do sumowania wartości odżywczych
            double totalCalories = 0;
            double totalProtein = 0;
            double totalCarbs = 0;
            double totalFats = 0;

            // Sumujemy wartości odżywcze dla każdego składnika
            for (RecipeIngredient recipeIngredient : recipeIngredients) {
                Ingredient ingredient = AppDatabase.getInstance(requireContext())
                        .ingredientDao()
                        .getIngredientById(recipeIngredient.getIngredientId());

                if (ingredient != null) {
                    totalCalories += ingredient.getKcal();
                    totalProtein += ingredient.getProtein();
                    totalCarbs += ingredient.getCarb();
                    totalFats += ingredient.getFat();
                }
            }

            // Zaktualizuj obiekt przepisu
            recipe.setCalories(totalCalories);
            recipe.setProtein(totalProtein);
            recipe.setCarbs(totalCarbs);
            recipe.setFats(totalFats);

            // Aktualizacja UI
            double finalTotalCalories = totalCalories;
            double finalTotalProtein = totalProtein;
            double finalTotalCarbs = totalCarbs;
            double finalTotalFats = totalFats;
            requireActivity().runOnUiThread(() -> {
                selectedRecipes.add(recipe); // Dodanie przepisu do listy
                selectedRecipeAdapter.notifyDataSetChanged(); // Odświeżenie RecyclerView

                // Przekazanie mealType do metody updateNutritionSummary
                updateNutritionSummary(finalTotalCalories, finalTotalProtein, finalTotalCarbs, finalTotalFats);
            });
        }).start();
    }

    private void updateNutritionSummary(double calories, double protein, double carbs, double fats) {
        double currentCalories = parseDoubleOrZero(totalCalories.getText().toString());
        double currentProtein = parseDoubleOrZero(totalProtein.getText().toString());
        double currentCarbs = parseDoubleOrZero(totalCarbs.getText().toString());
        double currentFats = parseDoubleOrZero(totalFats.getText().toString());

        // Aktualizacja podsumowania wartości odżywczych na dole fragmentu
        totalCalories.setText(String.valueOf(currentCalories + calories));
        totalProtein.setText(String.valueOf(currentProtein + protein));
        totalCarbs.setText(String.valueOf(currentCarbs + carbs));
        totalFats.setText(String.valueOf(currentFats + fats));
    }

}
