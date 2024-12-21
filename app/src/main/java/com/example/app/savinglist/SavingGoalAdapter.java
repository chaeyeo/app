package com.example.app.savinglist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;

import java.util.List;

public class SavingGoalAdapter extends RecyclerView.Adapter<SavingGoalAdapter.ViewHolder> {

    private Context context;
    private List<SavingGoal> savingGoals;

    public SavingGoalAdapter(Context context, List<SavingGoal> savingGoals) {
        this.context = context;
        this.savingGoals = savingGoals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saving_goal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavingGoal goal = savingGoals.get(position);
        String goalName = goal.getGoalName();
        holder.textGoalName.setText(goalName);
    }

    @Override
    public int getItemCount() {
        return savingGoals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textGoalName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textGoalName = itemView.findViewById(R.id.text_goal_name);
        }
    }
}
