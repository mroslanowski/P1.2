package com.example.p1;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {

    private final List<Recipe> recipeList;
    private final DeleteRecipeCallback deleteCallback;
    private final EditRecipeCallback editCallback;

    public RecipeListAdapter(List<Recipe> recipeList, DeleteRecipeCallback deleteCallback, EditRecipeCallback editCallback) {
        this.recipeList = recipeList;
        this.deleteCallback = deleteCallback;
        this.editCallback = editCallback;
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

        // Usuwanie przepisu
        holder.deleteButton.setOnClickListener(v -> deleteCallback.onDelete(recipe));

        // Edytowanie przepisu
        holder.editButton.setOnClickListener(v -> {
            String newName = holder.editRecipeEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(newName)) {
                editCallback.onEdit(recipe, newName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recipeNameTextView;
        EditText editRecipeEditText;
        Button deleteButton, editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            editRecipeEditText = itemView.findViewById(R.id.editRecipeEditText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }

    public interface DeleteRecipeCallback {
        void onDelete(Recipe recipe);
    }

    public interface EditRecipeCallback {
        void onEdit(Recipe recipe, String newName);
    }
}
