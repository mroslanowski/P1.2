package com.example.p1.Fragments.BottomSheetDialogFragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.p1.Database.PersonDatabase.Person;
import com.example.p1.Database.PersonDatabase.WeightTrackingDatabase;
import com.example.p1.R;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddWeight extends BottomSheetDialogFragment {

    private EditText weightEditText;
    private EditText dateEditText;  // Dodajemy EditText dla daty

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_weight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weightEditText = view.findViewById(R.id.weightEditText);
        dateEditText = view.findViewById(R.id.dateEditText);  // Inicjalizujemy dateEditText
        Button saveButton = view.findViewById(R.id.saveButton);

        // Ustawiamy dzisiejszą datę w polu EditText
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateEditText.setText(currentDate);

        // Obsługa kliknięcia przycisku "Save"
        saveButton.setOnClickListener(v -> savePersonToDatabase());
    }

    private void savePersonToDatabase() {
        String weightText = weightEditText.getText().toString().trim();
        String dateText = dateEditText.getText().toString().trim();

        // Sprawdzamy, czy waga jest pusta
        if (TextUtils.isEmpty(weightText)) {
            Toast.makeText(requireContext(), "Please enter a weight!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sprawdzamy, czy data jest pusta
        if (TextUtils.isEmpty(dateText)) {
            Toast.makeText(requireContext(), "Please enter a date!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double weight = Double.parseDouble(weightText);

            // Utwórz obiekt Person z wprowadzoną datą i wagą
            Person person = new Person(dateText, weight);

            // Zapisz do bazy w wątku roboczym
            new Thread(() -> {
                WeightTrackingDatabase database = WeightTrackingDatabase.getInstance(requireContext());
                database.personDao().insertPerson(person);

                // Wyświetl toast po zapisaniu danych
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Person saved successfully!", Toast.LENGTH_SHORT).show()
                );
            }).start();
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid weight format!", Toast.LENGTH_SHORT).show();
        }
    }
}
