package com.example.p1.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.p1.Database.RecipeDatabase.Recipe;
import com.example.p1.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SelectedRecipeAdapter extends RecyclerView.Adapter<SelectedRecipeAdapter.ViewHolder> {

    private final List<Recipe> selectedRecipes;
    private final RecipeClickListener listener;

    public SelectedRecipeAdapter(List<Recipe> selectedRecipes, RecipeClickListener listener) {
        this.selectedRecipes = selectedRecipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = selectedRecipes.get(position);
        holder.recipeName.setText(recipe.getName());

        holder.deleteButton.setOnClickListener(v -> {
            listener.onRecipeClick(recipe, position);
        });
    }

    @Override
    public int getItemCount() {
        return selectedRecipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName;
        MaterialButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface RecipeClickListener {
        void onRecipeClick(Recipe recipe, int position);
    }
}
