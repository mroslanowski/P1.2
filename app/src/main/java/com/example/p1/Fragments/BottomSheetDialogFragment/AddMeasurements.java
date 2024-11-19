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
import androidx.room.Room;

import com.example.p1.Database.PersonDatabase.Person;
import com.example.p1.Database.PersonDatabase.WeightTrackingDatabase;
import com.example.p1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMeasurements extends BottomSheetDialogFragment {

    private EditText abdomenEditText, neckEditText, hipsEditText, thighEditText, chestEditText, calfEditText, armEditText, waistEditText, weightEditText;
    private WeightTrackingDatabase personDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_measurements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database
        personDatabase = Room.databaseBuilder(requireContext(), WeightTrackingDatabase.class, "person_db").build();

        // Bind EditText views
        abdomenEditText = view.findViewById(R.id.abdomenEditText);
        neckEditText = view.findViewById(R.id.neckEditText);
        hipsEditText = view.findViewById(R.id.hipsEditText);
        thighEditText = view.findViewById(R.id.thighEditText);
        chestEditText = view.findViewById(R.id.chestEditText);
        calfEditText = view.findViewById(R.id.calfEditText);
        armEditText = view.findViewById(R.id.armEditText);
        waistEditText = view.findViewById(R.id.waistEditText);
        weightEditText = view.findViewById(R.id.weightEditText);

        // Save button listener
        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveMeasurementData());
    }

    private void saveMeasurementData() {
        String currentDate = getCurrentDate();
        String abdomen = abdomenEditText.getText().toString().trim();
        String neck = neckEditText.getText().toString().trim();
        String hips = hipsEditText.getText().toString().trim();
        String thigh = thighEditText.getText().toString().trim();
        String chest = chestEditText.getText().toString().trim();
        String calf = calfEditText.getText().toString().trim();
        String arm = armEditText.getText().toString().trim();
        String waist = waistEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();

        if (TextUtils.isEmpty(weight)) {
            Toast.makeText(requireContext(), "Weight is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Person object
        Person person = new Person(currentDate, Double.parseDouble(weight));

        // Add optional measurements if provided
        if (!abdomen.isEmpty()) person.setAbdomen(Double.parseDouble(abdomen));
        if (!neck.isEmpty()) person.setNeck(Double.parseDouble(neck));
        if (!hips.isEmpty()) person.setHips(Double.parseDouble(hips));
        if (!thigh.isEmpty()) person.setThigh(Double.parseDouble(thigh));
        if (!chest.isEmpty()) person.setChest(Double.parseDouble(chest));
        if (!calf.isEmpty()) person.setCalf(Double.parseDouble(calf));
        if (!arm.isEmpty()) person.setArm(Double.parseDouble(arm));
        if (!waist.isEmpty()) person.setWaist(Double.parseDouble(waist));

        // Save to database in a background thread
        new Thread(() -> {
            personDatabase.personDao().insertPerson(person);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Measurements saved successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            });
        }).start();
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
