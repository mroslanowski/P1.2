package com.example.p1;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddIngredientDialogFragment extends DialogFragment {

    private EditText nameEditText, quantityEditText, proteinEditText, fatEditText, carbEditText, kcalEditText;
    private Spinner categorySpinner;
    private Button addButton;
    private OnIngredientAddedListener ingredientAddedListener;

    // Interfejs do przekazania danych o nowym składniku
    public interface OnIngredientAddedListener {
        void onIngredientAdded(Ingredient ingredient);
    }

    public AddIngredientDialogFragment(OnIngredientAddedListener listener) {
        this.ingredientAddedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_ingredient, container, false);

        // Powiązanie widoków
        nameEditText = view.findViewById(R.id.nameEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        proteinEditText = view.findViewById(R.id.proteinEditText);
        fatEditText = view.findViewById(R.id.fatEditText);
        carbEditText = view.findViewById(R.id.carbEditText);
        kcalEditText = view.findViewById(R.id.kcalEditText);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        addButton = view.findViewById(R.id.addButton);

        // Automatyczne obliczanie kalorii po zmianie makroskładników
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

        // Obsługa przycisku "Dodaj"
        addButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            String quantityStr = quantityEditText.getText().toString().trim();
            String proteinStr = proteinEditText.getText().toString().trim();
            String fatStr = fatEditText.getText().toString().trim();
            String carbStr = carbEditText.getText().toString().trim();
            String kcalStr = kcalEditText.getText().toString().trim();

            // Walidacja danych
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(kcalStr)) {
                Toast.makeText(getContext(), "Proszę wypełnić wszystkie wymagane pola", Toast.LENGTH_SHORT).show();
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

                // Przekazanie danych do interfejsu
                if (ingredientAddedListener != null) {
                    ingredientAddedListener.onIngredientAdded(ingredient);
                }

                dismiss(); // Zamknięcie dialogu
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Nieprawidłowe dane liczbowe", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Funkcja do obliczania kalorii na podstawie białek, tłuszczy i węglowodanów
    private void calculateKcal() {
        try {
            double protein = Double.parseDouble(proteinEditText.getText().toString().trim());
            double fat = Double.parseDouble(fatEditText.getText().toString().trim());
            double carb = Double.parseDouble(carbEditText.getText().toString().trim());
            double quantity = Double.parseDouble(quantityEditText.getText().toString().trim());

            // Obliczanie kalorii na 100 g i na podaną ilość
            double kcalPer100g = (protein * 4) + (fat * 9) + (carb * 4);
            double totalKcal = (kcalPer100g / 100) * quantity;

            kcalEditText.setText(String.format("%.2f", totalKcal)); // Ustawienie wyniku w polu
        } catch (NumberFormatException e) {
            kcalEditText.setText(""); // Wyczyść pole, jeśli dane są nieprawidłowe
        }
    }
}
