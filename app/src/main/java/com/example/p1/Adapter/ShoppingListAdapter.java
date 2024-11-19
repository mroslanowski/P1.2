package com.example.p1.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1.R;
import com.example.p1.Database.RecipeDatabase.ShoppingListItem;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private final List<ShoppingListItem> shoppingList;
    private final DeleteItemCallback deleteCallback;

    public ShoppingListAdapter(List<ShoppingListItem> shoppingList, DeleteItemCallback deleteCallback) {
        this.shoppingList = shoppingList;
        this.deleteCallback = deleteCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingListItem item = shoppingList.get(position);
        holder.itemNameTextView.setText(item.name);

        holder.deleteButton.setOnClickListener(v -> deleteCallback.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface DeleteItemCallback {
        void onDelete(ShoppingListItem item);
    }
}
