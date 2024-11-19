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

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private final List<Ingredient> shoppingList;
    private final DeleteItemCallback deleteCallback;
    private final EditItemCallback editCallback;

    public ShoppingListAdapter(List<Ingredient> shoppingList, DeleteItemCallback deleteCallback, EditItemCallback editCallback) {
        this.shoppingList = shoppingList;
        this.deleteCallback = deleteCallback;
        this.editCallback = editCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = shoppingList.get(position);
        holder.ingredientNameTextView.setText(ingredient.getName() + " (" + ingredient.getQuantity() + " " + ingredient.getUnit() + ")");

        // Obsługa usuwania składnika
        holder.deleteButton.setOnClickListener(v -> deleteCallback.onDelete(ingredient));

        // Obsługa edycji składnika
        holder.editButton.setOnClickListener(v -> {
            String newName = holder.editIngredientEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(newName)) {
                editCallback.onEdit(ingredient, newName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ingredientNameTextView;
        EditText editIngredientEditText;
        Button deleteButton, editButton, addItemButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientNameTextView = itemView.findViewById(R.id.ingredientNameTextView);
            editIngredientEditText = itemView.findViewById(R.id.editIngredientEditText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            editButton = itemView.findViewById(R.id.editButton);
            addItemButton = itemView.findViewById(R.id.addItemButton);
        }
    }

    public interface DeleteItemCallback {
        void onDelete(Ingredient ingredient);
    }

    public interface EditItemCallback {
        void onEdit(Ingredient ingredient, String newName);
    }
}
