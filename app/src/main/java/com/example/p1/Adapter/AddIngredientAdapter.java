package com.example.p1.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1.Database.RecipeDatabase.Ingredient;
import com.example.p1.R;

import java.util.List;
public class AddIngredientAdapter extends RecyclerView.Adapter<AddIngredientAdapter.ViewHolder> {

    private final List<Ingredient> ingredientList;
    private final DeleteItemCallback deleteCallback;
    private final OnItemClickListener itemClickListener;

    public AddIngredientAdapter(List<Ingredient> ingredientList, DeleteItemCallback deleteCallback, EditItemCallback editCallback, OnItemClickListener itemClickListener) {
        this.ingredientList = ingredientList;
        this.deleteCallback = deleteCallback;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);
        holder.ingredientNameTextView.setText(ingredient.getName());

        holder.deleteButton.setOnClickListener(v -> deleteCallback.onDelete(ingredient));

        // Handle item click
        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(ingredient));
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientNameTextView;
        EditText editIngredientEditText;
        Button deleteButton, editButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientNameTextView = itemView.findViewById(R.id.itemNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            //editButton = itemView.findViewById(R.id.editButton);
        }
    }

    public interface DeleteItemCallback {
        void onDelete(Ingredient ingredient);
    }

    public interface EditItemCallback {
        void onEdit(Ingredient ingredient, String newName);
    }

    public interface OnItemClickListener {
        void onItemClick(Ingredient ingredient);
    }
}
