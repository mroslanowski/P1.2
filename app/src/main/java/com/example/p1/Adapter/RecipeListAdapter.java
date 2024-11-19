package com.example.p1.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1.Database.RecipeDatabase.Recipe;
import com.example.p1.R;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    private final List<Recipe> recipeList;
    private final OnRecipeClickListener onRecipeClickListener;
    private final OnRecipeDeleteListener onRecipeDeleteListener;

    public RecipeListAdapter(List<Recipe> recipeList, OnRecipeClickListener onRecipeClickListener, OnRecipeDeleteListener onRecipeDeleteListener) {
        this.recipeList = recipeList;
        this.onRecipeClickListener = onRecipeClickListener;
        this.onRecipeDeleteListener = onRecipeDeleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.recipeNameTextView.setText(recipe.getName());

        // Handle the delete button click
        holder.deleteButton.setOnClickListener(v -> onRecipeDeleteListener.onDelete(recipe));

        // Handle item click to open recipe details
        holder.itemView.setOnClickListener(v -> onRecipeClickListener.onRecipeClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recipeNameTextView;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public interface OnRecipeDeleteListener {
        void onDelete(Recipe recipe);
    }
}
