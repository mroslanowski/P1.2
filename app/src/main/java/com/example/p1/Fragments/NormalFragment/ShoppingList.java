package com.example.p1.Fragments.NormalFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1.Adapter.ShoppingListAdapter;
import com.example.p1.Database.RecipeDatabase.AppDatabase;
import com.example.p1.Database.RecipeDatabase.Ingredient;
import com.example.p1.Database.RecipeDatabase.ShoppingListItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.p1.R;

import java.util.ArrayList;
import java.util.List;

public class ShoppingList extends Fragment {

    private Button addItemButton;
    private RecyclerView shoppingListRecyclerView;
    private ShoppingListAdapter shoppingListAdapter;
    private List<ShoppingListItem> shoppingList;

    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        // Initialize views
        addItemButton = view.findViewById(R.id.addItemButton);
        shoppingListRecyclerView = view.findViewById(R.id.shoppingListRecyclerView);

        // Initialize shopping list
        shoppingList = new ArrayList<>();

        // Set up RecyclerView
        shoppingListAdapter = new ShoppingListAdapter(
                shoppingList,
                this::deleteShoppingListItem
        );
        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        shoppingListRecyclerView.setAdapter(shoppingListAdapter);

        // Initialize database
        db = AppDatabase.getInstance(requireContext());

        // Fetch shopping list items from the database
        fetchShoppingListItemsFromDatabase();

        // Open dialog to show all available ingredients
        addItemButton.setOnClickListener(v -> showIngredientDialog());

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchShoppingListItemsFromDatabase() {
        new Thread(() -> {
            List<ShoppingListItem> items = db.shoppingListDao().getAllItems();

            requireActivity().runOnUiThread(() -> {
                shoppingList.clear();
                shoppingList.addAll(items);
                shoppingListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void deleteShoppingListItem(ShoppingListItem item) {
        new Thread(() -> {
            db.shoppingListDao().deleteItem(item);
            fetchShoppingListItemsFromDatabase();
        }).start();
    }

    private void showIngredientDialog() {
        new Thread(() -> {
            // Fetch all ingredients from the database
            List<Ingredient> ingredients = db.ingredientDao().getAllIngredients();

            // If there are no ingredients, show a message
            if (ingredients.isEmpty()) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "No ingredients found!", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            // Create the dialog view
            requireActivity().runOnUiThread(() -> {
                // Get the ingredient names for the list
                String[] ingredientNames = new String[ingredients.size()];
                for (int i = 0; i < ingredients.size(); i++) {
                    ingredientNames[i] = ingredients.get(i).getName();
                }

                // Show the dialog
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Select an Ingredient to Shopping List")
                        .setItems(ingredientNames, (dialog, which) -> {
                            // Add the selected ingredient to the shopping list
                            String selectedIngredientName = ingredientNames[which];
                            addShoppingListItem(selectedIngredientName);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            });
        }).start(); // Execute on a background thread
    }

    private void addShoppingListItem(String ingredientName) {
        new Thread(() -> {
            // Create a new shopping list item with the selected ingredient name and mark it as not purchased (false)
            ShoppingListItem newItem = new ShoppingListItem(ingredientName, false);
            // Insert the new shopping list item into the database
            db.shoppingListDao().insert(newItem);
            // Refresh the shopping list
            fetchShoppingListItemsFromDatabase();
        }).start();
    }
}
