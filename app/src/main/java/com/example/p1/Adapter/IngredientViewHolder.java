package com.example.p1.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.p1.R;
import com.google.android.material.button.MaterialButton;

public class IngredientViewHolder extends RecyclerView.ViewHolder {
    TextView itemNameTextView;
    MaterialButton deleteButton;

    public IngredientViewHolder(View itemView) {
        super(itemView);
        itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }
}