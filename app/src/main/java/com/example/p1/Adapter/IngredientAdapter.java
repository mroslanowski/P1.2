package com.example.p1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p1.R;

import java.util.List;
public class IngredientAdapter extends RecyclerView.Adapter<IngredientViewHolder> {

    private Context context;
    private List<String> ingredientList;

    public IngredientAdapter(Context context, List<String> ingredientList) {
        this.context = context;
        this.ingredientList = ingredientList;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ingredient_in_recipe_item, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        String ingredient = ingredientList.get(position);
        holder.itemNameTextView.setText(ingredient);

        // Handle delete button
        holder.deleteButton.setOnClickListener(v -> {
            ingredientList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, ingredientList.size());
        });
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }
}
