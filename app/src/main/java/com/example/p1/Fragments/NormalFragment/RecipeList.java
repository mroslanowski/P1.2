package com.example.p1.Fragments.NormalFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1.Adapter.RecipeListAdapter;
import com.example.p1.Database.RecipeDatabase.AppDatabase;
import com.example.p1.Database.RecipeDatabase.Recipe;
import com.example.p1.Fragments.BottomSheetDialogFragment.AddRecipe;
import com.example.p1.Fragments.BottomSheetDialogFragment.EditRecipe;
import com.example.p1.R;

import java.util.List;

public class RecipeList extends Fragment implements RecipeListAdapter.OnRecipeClickListener, RecipeListAdapter.OnRecipeDeleteListener {

    private RecyclerView recipeRecyclerView;
    private RecipeListAdapter recipeAdapter;
    private Button addRecipeButton;
    private static AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        recipeRecyclerView = view.findViewById(R.id.recipeListRecyclerView);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = AppDatabase.getInstance(requireContext());
        loadRecipes();

        addRecipeButton = view.findViewById(R.id.addItemButton);
        addRecipeButton.setOnClickListener(v -> {
            AddRecipe dialog = new AddRecipe();
            dialog.show(getParentFragmentManager(), "AddRecipeDialog");
        });

        return view;
    }

    public void loadRecipes() {
        new Thread(() -> {
            List<Recipe> recipes = db.recipeDao().getAllRecipes();
            requireActivity().runOnUiThread(() -> {
                recipeAdapter = new RecipeListAdapter(recipes, this, this);  // Pass both listeners
                recipeRecyclerView.setAdapter(recipeAdapter);
            });
        }).start();
    }

    @Override
    public void onDelete(Recipe recipe) {
        new Thread(() -> {
            db.recipeDao().delete(recipe);
            loadRecipes();  // Reload the list after deletion
        }).start();
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        // Handle recipe item click
        EditRecipe dialog = new EditRecipe(recipe, updatedRecipe -> {
            loadRecipes();  // Reload the list after updating the recipe
        });
        dialog.show(getParentFragmentManager(), "EditRecipeDialog");
    }
}
