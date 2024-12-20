package com.example.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView savingTitle, dDayText, goalAmountText, todayExpenseText, expenseDetailsText;
    private EditText inputGoalAmount, inputGoalDate;
    private Button foodButton, livingButton, etcButton, settingsButton;
    private int savingCount = 1;
    private int totalExpense = 0;
    private String goalAmount = "0";

    // 카테고리별 소비 내역을 저장할 Map
    private Map<String, StringBuilder> expenseDetails = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 요소 초기화
        savingTitle = findViewById(R.id.saving_title);
        dDayText = findViewById(R.id.d_day_text);
        goalAmountText = findViewById(R.id.goal_amount_text);
        todayExpenseText = findViewById(R.id.today_expense_text);
        expenseDetailsText = findViewById(R.id.expense_details_text);
        inputGoalAmount = findViewById(R.id.input_goal_amount);
        inputGoalDate = findViewById(R.id.input_goal_date);
        foodButton = findViewById(R.id.food_button);
        livingButton = findViewById(R.id.living_button);
        etcButton = findViewById(R.id.etc_button);
        settingsButton = findViewById(R.id.settings_button);  // 톱니바퀴 버튼 참조


        updateSavingTitle();

        // 리셋 이미지 뷰 클릭 리스너 설정
        findViewById(R.id.reset_image).setOnClickListener(view -> resetFields());


        // 버튼 리스너 설정
        findViewById(R.id.update_button).setOnClickListener(view -> updateGoal());
        foodButton.setOnClickListener(view -> recordExpense("식비"));
        livingButton.setOnClickListener(view -> recordExpense("생활비"));
        etcButton.setOnClickListener(view -> recordExpense("기타"));
        settingsButton.setOnClickListener(view -> showSettingsDialog());
    }

    private void updateSavingTitle() {
        savingTitle.setText("나의 " + savingCount + "번째 절약");
    }

    private void updateGoal() {
        String goalAmountInput = inputGoalAmount.getText().toString().trim();
        String goalDate = inputGoalDate.getText().toString().trim();

        if (goalAmountInput.isEmpty() || goalDate.isEmpty()) {
            Toast.makeText(this, "목표 금액과 날짜를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        goalAmount = goalAmountInput;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar goalCalendar = Calendar.getInstance();
            goalCalendar.setTime(sdf.parse(goalDate));
            long remainingDays = (goalCalendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / (1000 * 60 * 60 * 24);

            dDayText.setText("D-" + remainingDays);
            goalAmountText.setText("목표 금액: " + goalAmount + " 원");

            Toast.makeText(this, "목표가 설정되었습니다!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "잘못된 날짜 형식입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void recordExpense(String category) {
        // 다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(category + " 항목 입력");

        // 항목과 금액을 입력받을 수 있는 다이얼로그 레이아웃 설정
        final View expenseInputView = getLayoutInflater().inflate(R.layout.dialog_expense_input, null);
        builder.setView(expenseInputView);

        final EditText categoryInput = expenseInputView.findViewById(R.id.expense_category);
        final EditText amountInput = expenseInputView.findViewById(R.id.expense_amount);

        builder.setPositiveButton("확인", (dialogInterface, i) -> {
            String enteredCategory = categoryInput.getText().toString();
            String enteredAmount = amountInput.getText().toString();

            if (enteredCategory.isEmpty() || enteredAmount.isEmpty()) {
                Toast.makeText(MainActivity.this, "항목과 금액을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int amount = Integer.parseInt(enteredAmount);
                totalExpense += amount;

                // 해당 카테고리에 소비 내역 추가
                if (!expenseDetails.containsKey(category)) {
                    expenseDetails.put(category, new StringBuilder());
                }
                expenseDetails.get(category).append(enteredCategory)
                        .append(": ")
                        .append(enteredAmount)
                        .append(" 원\n");

                // 소비 금액 표시 업데이트
                todayExpenseText.setText("오늘 소비 금액: " + totalExpense + " 원");
                updateExpenseDetails();

                Toast.makeText(MainActivity.this, category + " " + enteredCategory + " " + amount + " 원 입력 완료", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(MainActivity.this, "금액을 올바르게 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void updateExpenseDetails() {
        StringBuilder expenseSummary = new StringBuilder();

        // 각 카테고리별로 아이콘 설정
        for (Map.Entry<String, StringBuilder> entry : expenseDetails.entrySet()) {
            String icon = "";

            if (entry.getKey().equals("식비")) {
                icon = "■ ";  // 네모 아이콘
            } else if (entry.getKey().equals("생활비")) {
                icon = "● ";  // 동그라미 아이콘
            } else if (entry.getKey().equals("기타")) {
                icon = "★ ";  // 별 아이콘
            }

            expenseSummary.append(icon)
                    .append(entry.getKey())
                    .append(" 소비 내역:\n")
                    .append(entry.getValue().toString())
                    .append("\n");
        }

        expenseDetailsText.setText(expenseSummary.toString());
    }

    // 목표 수정 다이얼로그 생성 메서드
    private void showSettingsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_settings, null);

        final EditText titleEditText = dialogView.findViewById(R.id.edit_saving_title);
        final EditText goalAmountEditText = dialogView.findViewById(R.id.edit_goal_amount);

        titleEditText.setText(savingTitle.getText().toString());
        goalAmountEditText.setText(goalAmount);

        new AlertDialog.Builder(this)
                .setTitle("목표 수정")
                .setView(dialogView)
                .setPositiveButton("확인", (dialog, which) -> {
                    savingTitle.setText(titleEditText.getText().toString());
                    goalAmount = goalAmountEditText.getText().toString();
                    goalAmountText.setText("목표 금액: " + goalAmount + " 원");
                    Toast.makeText(this, "목표가 수정되었습니다!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }

    // 리셋 버튼 클릭 시 리셋 처리
    private void resetFields() {
        savingTitle.setText("나의 " + savingCount + "번째 절약");
        dDayText.setText("");
        goalAmountText.setText("목표 금액: 0 원");
        todayExpenseText.setText("오늘 소비 금액: 0 원");
        expenseDetails.clear();
        expenseDetailsText.setText("");

        inputGoalAmount.setText("");
        inputGoalDate.setText("");

        // 다른 리셋 로직 추가 가능
        Toast.makeText(this, "리셋되었습니다.", Toast.LENGTH_SHORT).show();
    }
}

