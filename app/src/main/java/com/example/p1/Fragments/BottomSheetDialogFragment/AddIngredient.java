package com.example.p1.Fragments.BottomSheetDialogFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.p1.Database.RecipeDatabase.AppDatabase;
import com.example.p1.Database.RecipeDatabase.Ingredient;
import com.example.p1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AddIngredient extends BottomSheetDialogFragment {

    private EditText nameEditText, quantityEditText, proteinEditText, fatEditText, carbEditText, kcalEditText;
    private Button categoryButton;
    private final OnIngredientAddedListener ingredientAddedListener;
    private AppDatabase db;

    // Interface to pass ingredient data back to the host
    public interface OnIngredientAddedListener {
        void onIngredientAdded(Ingredient ingredient);
    }

    public AddIngredient(OnIngredientAddedListener listener) {
        this.ingredientAddedListener = listener;
    }

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_ingredient, container, false);

        // Initialize database
        db = AppDatabase.getInstance(requireContext());

        // Bind views
        nameEditText = view.findViewById(R.id.nameEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        proteinEditText = view.findViewById(R.id.proteinEditText);
        fatEditText = view.findViewById(R.id.fatEditText);
        carbEditText = view.findViewById(R.id.carbEditText);
        kcalEditText = view.findViewById(R.id.kcalEditText);
        categoryButton = view.findViewById(R.id.categoryEditText);
        Button addButton = view.findViewById(R.id.addButton);

        // Watcher for automatic calorie calculation
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

        // Handle Add button
        addButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String category = categoryButton.getText().toString().trim();
            String quantityStr = quantityEditText.getText().toString().trim();
            String proteinStr = proteinEditText.getText().toString().trim();
            String fatStr = fatEditText.getText().toString().trim();
            String carbStr = carbEditText.getText().toString().trim();
            String kcalStr = kcalEditText.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(category) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(kcalStr)) {
                Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(name);
                ingredient.setCategory(category);
                ingredient.setQuantity(Double.parseDouble(quantityStr));
                ingredient.setProtein(!TextUtils.isEmpty(proteinStr) ? Double.parseDouble(proteinStr) : 0);
                ingredient.setFat(!TextUtils.isEmpty(fatStr) ? Double.parseDouble(fatStr) : 0);
                ingredient.setCarb(!TextUtils.isEmpty(carbStr) ? Double.parseDouble(carbStr) : 0);
                ingredient.setKcal(Double.parseDouble(kcalStr));

                saveIngredient(ingredient);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid numeric input", Toast.LENGTH_SHORT).show();
            }
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
            double protein = Double.parseDouble(proteinEditText.getText().toString().trim());
            double fat = Double.parseDouble(fatEditText.getText().toString().trim());
            double carb = Double.parseDouble(carbEditText.getText().toString().trim());
            double quantity = Double.parseDouble(quantityEditText.getText().toString().trim());

            double kcalPer100g = (protein * 4) + (fat * 9) + (carb * 4);
            double totalKcal = (kcalPer100g / 100) * quantity;

            kcalEditText.setText(String.format("%.2f", totalKcal));
        } catch (NumberFormatException e) {
            kcalEditText.setText("");
        }
    }

    private void saveIngredient(Ingredient ingredient) {
        new Thread(() -> {
            db.ingredientDao().insertIngredient(ingredient);

            requireActivity().runOnUiThread(() -> {
                if (ingredientAddedListener != null) {
                    ingredientAddedListener.onIngredientAdded(ingredient);
                }
                dismiss();
            });
        }).start();
    }
}
