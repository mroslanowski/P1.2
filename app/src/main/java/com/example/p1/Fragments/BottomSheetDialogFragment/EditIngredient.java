package com.example.p1.Fragments.BottomSheetDialogFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.p1.Database.RecipeDatabase.AppDatabase;
import com.example.p1.Database.RecipeDatabase.Ingredient;
import com.example.p1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EditIngredient extends BottomSheetDialogFragment {

    private Ingredient ingredient;
    private UpdateCallback updateCallback;

    private EditText nameEditText, proteinEditText, fatEditText, carbEditText, quantityEditText, kcalEditText;
    private Button categoryButton;

    public EditIngredient(Ingredient ingredient, UpdateCallback updateCallback) {
        this.ingredient = ingredient;
        this.updateCallback = updateCallback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_ingredient, container, false);

        // Bind views
        nameEditText = view.findViewById(R.id.nameEditText);
        proteinEditText = view.findViewById(R.id.proteinEditText);
        fatEditText = view.findViewById(R.id.fatEditText);
        carbEditText = view.findViewById(R.id.carbEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        kcalEditText = view.findViewById(R.id.kcalEditText);
        categoryButton = view.findViewById(R.id.categoryEditText);

        Button updateButton = view.findViewById(R.id.addButton);

        // Populate fields with current data
        nameEditText.setText(ingredient.getName());
        proteinEditText.setText(String.valueOf(ingredient.getProtein()));
        fatEditText.setText(String.valueOf(ingredient.getFat()));
        carbEditText.setText(String.valueOf(ingredient.getCarb()));
        quantityEditText.setText(String.valueOf(ingredient.getQuantity()));
        kcalEditText.setText(String.valueOf(ingredient.getKcal()));
        categoryButton.setText(ingredient.getCategory());


        // Add dynamic calorie calculation
        TextWatcher kcalCalculatorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                calculateKcal();
            }
        };

        proteinEditText.addTextChangedListener(kcalCalculatorWatcher);
        fatEditText.addTextChangedListener(kcalCalculatorWatcher);
        carbEditText.addTextChangedListener(kcalCalculatorWatcher);
        quantityEditText.addTextChangedListener(kcalCalculatorWatcher);

        // Handle category selection
        categoryButton.setOnClickListener(v -> showCategorySelectionDialog());

        // Handle Update button
        updateButton.setText("Save");
        updateButton.setOnClickListener(v -> {
            ingredient.setName(nameEditText.getText().toString().trim());
            ingredient.setProtein(parseDouble(proteinEditText));
            ingredient.setFat(parseDouble(fatEditText));
            ingredient.setCarb(parseDouble(carbEditText));
            ingredient.setQuantity(parseDouble(quantityEditText));
            ingredient.setCategory(categoryButton.getText().toString().trim());

            new Thread(() -> {
                AppDatabase.getInstance(requireContext()).ingredientDao().updateIngredient(ingredient);
                updateCallback.onIngredientUpdated(ingredient);
                dismiss();
            }).start();
        });

        return view;
    }

    private void showCategorySelectionDialog() {
        String[] categories = {"Vegetables", "Fruits", "Dairy", "Protein", "Grains", "Snacks", "Liquid"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select Category")
                .setItems(categories, (dialog, which) -> categoryButton.setText(categories[which]))
                .show();
    }

    @SuppressLint("DefaultLocale")
    private void calculateKcal() {
        try {
            double protein = parseDouble(proteinEditText);
            double fat = parseDouble(fatEditText);
            double carb = parseDouble(carbEditText);
            double quantity = parseDouble(quantityEditText);

            double kcalPer100g = (protein * 4) + (fat * 9) + (carb * 4);
            double totalKcal = (kcalPer100g / 100) * quantity;

            kcalEditText.setText(String.format("%.2f", totalKcal));
        } catch (NumberFormatException e) {
            kcalEditText.setText("");
        }
    }

    private double parseDouble(EditText editText) {
        String text = editText.getText().toString().trim();
        return text.isEmpty() ? 0 : Double.parseDouble(text);
    }

    public interface UpdateCallback {
        void onIngredientUpdated(Ingredient updatedIngredient);
    }
}
