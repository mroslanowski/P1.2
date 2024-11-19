package com.example.p1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MealPlanFragment extends Fragment {

    private CalendarView calendarView;
    private TextView selectedDateText;
    private TextView mealSummaryText;
    private String selectedDate;
    private Map<String, Map<String, String>> mealPlanData; // Map<Date, Map<MealType, Recipe>>
    private AppDatabase db;
    private LinearLayout ingredientsContainer;

    public MealPlanFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_plan_activity, container, false);
        db = AppDatabase.getInstance(requireContext());

        // Initialize views
        calendarView = view.findViewById(R.id.calendarView);
        selectedDateText = view.findViewById(R.id.selectedDateText);
        mealSummaryText = view.findViewById(R.id.mealSummaryText);


        Button addBreakfastButton = view.findViewById(R.id.addBreakfastButton);
        Button addLunchButton = view.findViewById(R.id.addLunchButton);
        Button addDinnerButton = view.findViewById(R.id.addDinnerButton);


        // Initialize the meal plan data map
        mealPlanData = new HashMap<>();

        // Set the current date as selected by default
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        selectedDateText.setText("Selected Date: " + selectedDate);

        // Display meals for the selected date
        displayMealSummary();

        // Calendar date selection listener
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            selectedDateText.setText("Selected Date: " + selectedDate);
            displayMealSummary(); // Refresh meal summary for the newly selected date
        });

        // Set up click listeners for meal buttons
        addBreakfastButton.setOnClickListener(v -> showRecipeSelectionDialog("Breakfast"));
        addLunchButton.setOnClickListener(v -> showRecipeSelectionDialog("Lunch"));
        addDinnerButton.setOnClickListener(v -> showRecipeSelectionDialog("Dinner"));



        return view;
    }

    private void showRecipeSelectionDialog(String mealType) {
        new Thread(() -> {
            // Pobierz listę przepisów z bazy danych
            List<Recipe> recipeList = db.recipeDao().getAllRecipes();

            // Utwórz tablicę stringów o długości równej liczbie przepisów
            String[] recipes = new String[recipeList.size()];

            // Dodaj nazwy przepisów do tablicy
            for (int i = 0; i < recipeList.size(); i++) {
                recipes[i] = recipeList.get(i).name;
            }

            // Zaktualizuj UI, gdy dane są gotowe
            requireActivity().runOnUiThread(() -> {
                // Stwórz i wyświetl dialog wyboru przepisów
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Select a recipe for " + mealType + " on " + selectedDate)
                        .setItems(recipes, (dialog, which) -> {
                            String selectedRecipe = recipes[which];
                            saveMeal(selectedDate, mealType, selectedRecipe);
                            displayMealSummary(); // Aktualizuje podsumowanie posiłków
                            dialog.dismiss();
                        })
                        .show();
            });
        }).start();
    }

    private void saveMeal(String date, String mealType, String recipe) {
        // Retrieve or create the meal plan for the selected date
        Map<String, String> dailyPlan = mealPlanData.getOrDefault(date, new HashMap<>());
        dailyPlan.put(mealType, recipe);
        mealPlanData.put(date, dailyPlan); // Save updated plan
    }

    private void displayMealSummary() {
        // Retrieve the meal plan for the selected date
        Map<String, String> dailyPlan = mealPlanData.get(selectedDate);

        if (dailyPlan != null && !dailyPlan.isEmpty()) {
            StringBuilder summary = new StringBuilder("Meal Plan for " + selectedDate + ":\n");

            // Append each meal's plan to the summary
            if (dailyPlan.containsKey("Breakfast")) {
                summary.append("Breakfast: ").append(dailyPlan.get("Breakfast")).append("\n");
            }
            if (dailyPlan.containsKey("Lunch")) {
                summary.append("Lunch: ").append(dailyPlan.get("Lunch")).append("\n");
            }
            if (dailyPlan.containsKey("Dinner")) {
                summary.append("Dinner: ").append(dailyPlan.get("Dinner")).append("\n");
            }

            mealSummaryText.setText(summary.toString());
        } else {
            mealSummaryText.setText("No meals planned for " + selectedDate);
        }
    }

    // Method to fetch ingredients from the database and display them
    private void fetchAndDisplayIngredients() {
        new Thread(() -> {
            // Fetch ingredients from the database
            List<Ingredient> ingredients = db.ingredientDao().getAllIngredients();

            // Run UI updates on the main thread
            requireActivity().runOnUiThread(() -> {
                for (Ingredient ingredient : ingredients) {
                    // Dynamically create views to display each ingredient
                    addIngredientToLayout(ingredient.getName(), ingredient.getQuantity(), ingredient.getUnit());
                }
                Toast.makeText(getContext(), "Ingredients loaded successfully!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }


    // Method to dynamically add ingredient views to the layout
    private void addIngredientToLayout(String name, String quantity, String unit) {
        // Create a new LinearLayout to hold the ingredient data
        LinearLayout ingredientLayout = new LinearLayout(getContext());
        ingredientLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Create TextViews for ingredient details
        TextView ingredientName = new TextView(getContext());
        ingredientName.setText("Name: " + name);
        ingredientName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView ingredientQuantity = new TextView(getContext());
        ingredientQuantity.setText("Quantity: " + quantity);
        ingredientQuantity.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView ingredientUnit = new TextView(getContext());
        ingredientUnit.setText("Unit: " + unit);
        ingredientUnit.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // Add the TextViews to the LinearLayout
        ingredientLayout.addView(ingredientName);
        ingredientLayout.addView(ingredientQuantity);
        ingredientLayout.addView(ingredientUnit);

        // Add the ingredient layout to the container
        ingredientsContainer.addView(ingredientLayout);
    }
}
