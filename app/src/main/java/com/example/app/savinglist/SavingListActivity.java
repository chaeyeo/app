package com.example.app.savinglist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.SavingsDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class SavingListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavingGoalAdapter adapter;
    private List<SavingGoal> savingGoalList;
    private SavingsDatabaseHelper dbHelper;
    private TextView totalSavingsCountTextView;
    private ImageView logImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_list);

        // UI 초기화
        recyclerView = findViewById(R.id.recyclerView_savings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        totalSavingsCountTextView = findViewById(R.id.total_savings_count);

        // Adapter 초기화
        dbHelper = new SavingsDatabaseHelper(this);
        adapter = new SavingGoalAdapter(this, savingGoalList, dbHelper);
        recyclerView.setAdapter(adapter);


        // 절약 목표 가져오기
        savingGoalList = fetchSavingGoalsFromDatabase();

        // 총 절약 기록 수 표시
        int totalGoalsCount = dbHelper.getSavingsGoalsCount();
        totalSavingsCountTextView.setText("총 절약 기록 수: " + totalGoalsCount);

        // RecyclerView Adapter 설정
        adapter = new SavingGoalAdapter(this, savingGoalList, dbHelper);
        recyclerView.setAdapter(adapter);

        // 상단 로고 클릭 시 메인 화면으로 이동
        logImageView = findViewById(R.id.now_image);
        logImageView.setOnClickListener(view -> {
            Intent intent = new Intent(SavingListActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private List<SavingGoal> fetchSavingGoalsFromDatabase() {
        List<SavingGoal> goals = new ArrayList<>();
        Cursor cursor = dbHelper.getAllSavingsGoals();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("saving_id"));
                String goalName = cursor.getString(cursor.getColumnIndex("goal_name"));

                goals.add(new SavingGoal(id, goalName));
            }
            cursor.close();
        }

        return goals;
    }
}
