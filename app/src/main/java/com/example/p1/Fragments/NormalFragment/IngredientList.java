package com.example.p1.Fragments.NormalFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.p1.Adapter.AddIngredientAdapter;
import com.example.p1.Database.RecipeDatabase.AppDatabase;
import com.example.p1.Database.RecipeDatabase.Ingredient;
import com.example.p1.Fragments.BottomSheetDialogFragment.AddIngredient;
import com.example.p1.Fragments.BottomSheetDialogFragment.EditIngredient;
import com.example.p1.R;

import java.util.ArrayList;
import java.util.List;public class IngredientList extends Fragment {

    private Button addItemButton;
    private RecyclerView ingredientListRecyclerView;
    private AddIngredientAdapter addIngredientAdapter;
    private List<Ingredient> ingredientList;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_ingredient, container, false);

        addItemButton = view.findViewById(R.id.addItemButton);
        ingredientListRecyclerView = view.findViewById(R.id.ingredientListRecyclerView);
        ingredientList = new ArrayList<>();

        db = AppDatabase.getInstance(requireContext());

        addIngredientAdapter = new AddIngredientAdapter(
                ingredientList,
                this::deleteIngredient,
                this::editIngredient,
                this::onIngredientClick // Handle click on an item
        );

        ingredientListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ingredientListRecyclerView.setAdapter(addIngredientAdapter);

        addItemButton.setOnClickListener(v -> {
            AddIngredient dialog = new AddIngredient(ingredient -> {
                fetchIngredientsFromDatabase();
            });
            dialog.show(getParentFragmentManager(), "AddIngredientDialog");
        });

        fetchIngredientsFromDatabase();
        return view;
    }

    private void fetchIngredientsFromDatabase() {
        new Thread(() -> {
            List<Ingredient> ingredients = db.ingredientDao().getAllIngredients();
            requireActivity().runOnUiThread(() -> {
                ingredientList.clear();
                ingredientList.addAll(ingredients);
                addIngredientAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void deleteIngredient(Ingredient ingredient) {
        new Thread(() -> {
            db.ingredientDao().deleteIngredient(ingredient);
            fetchIngredientsFromDatabase();
        }).start();
    }

    private void editIngredient(Ingredient ingredient, String newName) {
        new Thread(() -> {
            ingredient.setName(newName);
            db.ingredientDao().updateIngredient(ingredient);
            fetchIngredientsFromDatabase();
        }).start();
    }

    private void onIngredientClick(Ingredient ingredient) {
        EditIngredient dialog = new EditIngredient(ingredient, updatedIngredient -> {
            fetchIngredientsFromDatabase();
        });
        dialog.show(getParentFragmentManager(), "EditIngredientDialog");
    }
}
