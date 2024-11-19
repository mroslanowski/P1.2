package com.example.p1.Fragments.BottomSheetDialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.p1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BmiCalculator extends BottomSheetDialogFragment {

    private EditText weightEditText, heightEditText, bmiEditText;
    private Button sexButton;
    private String selectedSex = "";  // By default, no sex is selected

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_bmi, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weightEditText = view.findViewById(R.id.weightEditText);
        heightEditText = view.findViewById(R.id.heightEditText);
        bmiEditText = view.findViewById(R.id.bmiEditText);
        sexButton = view.findViewById(R.id.sexButton);

        // Sex button listener
        sexButton.setOnClickListener(v -> showSexSelectionDialog());

        // Text watcher for automatic BMI calculation
        weightEditText.addTextChangedListener(bmiTextWatcher);
        heightEditText.addTextChangedListener(bmiTextWatcher);
    }

    private void showSexSelectionDialog() {
        String[] sexes = {"MALE", "FEMALE"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Gender")
                .setItems(sexes, (dialog, which) -> {
                    selectedSex = sexes[which];
                    Toast.makeText(requireContext(), "Selected: " + selectedSex, Toast.LENGTH_SHORT).show();
                    calculateBMI(); // Trigger BMI calculation after gender selection
                })
                .show();
    }

    // TextWatcher to calculate BMI automatically when weight or height is entered
    private final TextWatcher bmiTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            calculateBMI();
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    @SuppressLint("DefaultLocale")
    private void calculateBMI() {
        String weightText = weightEditText.getText().toString().trim();
        String heightText = heightEditText.getText().toString().trim();

        if (!weightText.isEmpty() && !heightText.isEmpty() && !selectedSex.isEmpty()) {
            try {
                double weight = Double.parseDouble(weightText);
                double height = Double.parseDouble(heightText) / 100;  // Convert cm to meters
                double bmi = weight / (height * height);

                // Interpret BMI based on gender
                String interpretation = interpretBmi(bmi, selectedSex);

                // Display calculated BMI and interpretation
                bmiEditText.setText(String.format("%.2f (%s)", bmi, interpretation));
            } catch (NumberFormatException e) {
                bmiEditText.setText("Invalid input");
            }
        } else if (selectedSex.isEmpty()) {
            bmiEditText.setText("Please select your gender");
        } else {
            bmiEditText.setText("");
        }
    }

    private String interpretBmi(double bmi, String gender) {
        if (gender.equals("MALE")) {
            if (bmi < 18.5) return "Underweight";
            else if (bmi < 24.9) return "Normal weight";
            else if (bmi < 29.9) return "Overweight";
            else return "Obesity";
        } else if (gender.equals("FEMALE")) {
            if (bmi < 18.5) return "Underweight";
            else if (bmi < 24.9) return "Normal weight";
            else if (bmi < 29.9) return "Overweight";
            else return "Obesity";
        }
        return "Unknown";
    }
}
