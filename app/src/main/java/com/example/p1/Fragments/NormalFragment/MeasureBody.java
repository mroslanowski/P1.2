package com.example.p1.Fragments.NormalFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.p1.Database.PersonDatabase.Person;
import com.example.p1.Database.PersonDatabase.PersonDao;
import com.example.p1.Fragments.BottomSheetDialogFragment.*;

import com.example.p1.Database.PersonDatabase.WeightTrackingDatabase;
import com.example.p1.R;
import com.example.p1.Fragments.BottomSheetDialogFragment.*;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeasureBody extends Fragment {

    private LineChart lineChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_measure_body, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lineChart = view.findViewById(R.id.lineChart);

        // Load data from the database
        new Thread(() -> {
            List<Person> personList = WeightTrackingDatabase.getInstance(requireContext()).personDao().getAllPersons();
            List<Entry> entries = new ArrayList<>();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Person person : personList) {
                String dateString = person.getDate();
                if (dateString != null && !dateString.isEmpty()) {
                    try {
                        Date date = dateFormat.parse(dateString);
                        if (date != null) {
                            float xValue = date.getTime();
                            float yValue = (float) person.getWeight();
                            entries.add(new Entry(xValue, yValue));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Skipping entry with null or empty date: " + person.getId());
                }
            }

            Collections.sort(entries, Comparator.comparingDouble(Entry::getX));

            requireActivity().runOnUiThread(() -> updateChart(entries));
        }).start();

        Button bmiButton = view.findViewById(R.id.bmiButton);
        bmiButton.setOnClickListener(v -> {
            new BmiCalculator().show(getChildFragmentManager(), "BMI Dialog");
        });

        Button measurementsButton = view.findViewById(R.id.measurementsButton);
        measurementsButton.setOnClickListener(v -> {
            new AddMeasurements().show(getChildFragmentManager(), "Measurements Dialog");
        });

        Button activityButton = view.findViewById(R.id.activityButton);
        activityButton.setOnClickListener(v -> {
            new Activity().show(getChildFragmentManager(), "Activity Dialog");
        });

        Button addButton = view.findViewById(R.id.addItemButton);
        addButton.setOnClickListener(v -> {
            new AddWeight().show(getChildFragmentManager(), "Add Weight Dialog");
        });

        Button trainingButton = view.findViewById(R.id.trainingButton);
        trainingButton.setOnClickListener(v -> showTrainingOptions());
    }

    private void showTrainingOptions() {
        // Example URLs for training plans
        String[] trainingUrls = {
                "https://pacjent.gov.pl/",
                "https://example.com/plan2"
        };

        String[] trainingNames = {"8 tygodni do zdrowia", "Plan 2"};

        // Create an AlertDialog to select a plan
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Choose a Training Plan")
                .setItems(trainingNames, (dialog, which) -> {
                    String selectedUrl = trainingUrls[which];
                    openTrainingPlan(selectedUrl);
                })
                .show();
    }

    private void openTrainingPlan(String url) {
        Intent intent = new Intent(requireContext(), WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void updateChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Weight Over Time");
        dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Format x-axis as date
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @SuppressLint("SimpleDateFormat")
            private final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");

            @Override
            public String getFormattedValue(float value) {
                return formatter.format(new Date((long) value));
            }
        });
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // Set y-axis limits dynamically
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        lineChart.getAxisRight().setEnabled(false); // Disable right y-axis
        lineChart.getDescription().setEnabled(false);
        lineChart.invalidate(); // Refresh chart
    }
}
