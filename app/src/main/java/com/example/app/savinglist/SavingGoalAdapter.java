package com.example.app.savinglist;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.SavingsDatabaseHelper;

import java.util.List;

public class SavingGoalAdapter extends RecyclerView.Adapter<SavingGoalAdapter.ViewHolder> {

    private Context context;
    private List<SavingGoal> savingGoals;
    private SavingsDatabaseHelper dbHelper;

    // 생성자에서 dbHelper 주입
    public SavingGoalAdapter(Context context, List<SavingGoal> savingGoals, SavingsDatabaseHelper dbHelper) {
        this.context = context;
        this.savingGoals = savingGoals;
        this.dbHelper = dbHelper;
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

        holder.textGoalName.setText(goal.getGoalName());

        holder.itemView.setOnClickListener(view -> {
            showGoalDetailsPopup(goal.getId());
        });
    }

    // 팝업 다이얼로그를 표시하는 메서드
    private void showGoalDetailsPopup(int savingId) {
        // 데이터베이스에서 세부 정보를 가져옵니다.
        Cursor cursor = dbHelper.getSavingsGoal(savingId);

        if (cursor == null || !cursor.moveToFirst()) {
            Toast.makeText(context, "데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 데이터 가져오기
        String title = "<< 나의 " + cursor.getInt(cursor.getColumnIndex("saving_id")) + "번째 절약 >>";
        String amount = cursor.getString(cursor.getColumnIndex("goal_amount")) + " 원";
        String date = cursor.getString(cursor.getColumnIndex("goal_date"));
        String total = cursor.getString(cursor.getColumnIndex("today_total")) + " 원";

        cursor.close();

        // 팝업 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        // 팝업 내용을 문자열로 구성
        StringBuilder message = new StringBuilder();
        message.append("목표 금액: ").append(amount).append("\n");
        message.append("목표 날짜: ").append(date).append("\n");
        message.append("총 소비 금액: ").append(total);

        builder.setMessage(message.toString());

        // 확인 버튼 추가
        builder.setPositiveButton("닫기", (dialog, which) -> dialog.dismiss());

        // 팝업 표시
        builder.create().show();
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
