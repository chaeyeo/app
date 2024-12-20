package com.example.app.savinglist;

public class SavingGoal {
    private int id;
    private String goalName;

    public SavingGoal(int id, String goalName) {
        this.id = id;
        this.goalName = goalName;
    }

    public int getId() {
        return id;
    }

    public String getGoalName() {
        return goalName;
    }
}
