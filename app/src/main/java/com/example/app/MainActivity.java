package com.example.app;

import android.os.Bundle;
import android.widget.ImageView;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.RelativeLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.graphics.Color;

import com.example.app.savinglist.SavingListActivity;

public class MainActivity extends AppCompatActivity {

    private TextView savingTitle, savingPurpose, dDayText, goalAmountText, todayExpenseText, expenseDetailsText;
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
        savingPurpose = findViewById(R.id.saving_purpose);
        dDayText = findViewById(R.id.d_day_text);
        goalAmountText = findViewById(R.id.goal_amount_text);
        todayExpenseText = findViewById(R.id.today_expense_text);
        expenseDetailsText = findViewById(R.id.expense_details_text);
        inputGoalAmount = findViewById(R.id.input_goal_amount);
        inputGoalDate = findViewById(R.id.input_goal_date);
        foodButton = findViewById(R.id.food_button);
        livingButton = findViewById(R.id.living_button);
        etcButton = findViewById(R.id.etc_button);
        settingsButton = findViewById(R.id.settings_button);

        // 숫자 입력 키보드 설정
        inputGoalAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        inputGoalDate.setInputType(android.text.InputType.TYPE_CLASS_DATETIME);

        updateSavingTitle();

        // 절약 목표 클릭 시 텍스트 수정 붛가능
        findViewById(R.id.end_image).setOnClickListener(view -> showConfirmationDialog());


        // 버튼 리스너 설정
        findViewById(R.id.update_button).setOnClickListener(view -> updateGoal());
        foodButton.setOnClickListener(view -> recordExpense("식비"));
        livingButton.setOnClickListener(view -> recordExpense("생활비"));
        etcButton.setOnClickListener(view -> recordExpense("기타"));
        settingsButton.setOnClickListener(view -> showSettingsDialog());

        ImageView logImageView = findViewById(R.id.log_image);
        logImageView.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SavingListActivity.class);
            startActivity(intent);
        });

    }



    private void updateSavingTitle() {
        savingTitle.setText("<< 나의 " + savingCount + "번째 절약 >>");
    }

    private void updateGoal() {
        String goalAmountInput = inputGoalAmount.getText().toString().trim();
        String goalDateInput = inputGoalDate.getText().toString().trim();

        if (goalAmountInput.isEmpty() || goalDateInput.isEmpty()) {
            Toast.makeText(this, "목표 금액과 날짜를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        goalAmount = goalAmountInput;

        try {
            // 입력된 날짜를 yyyyMMdd 형식으로 파싱
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date goalDate = inputDateFormat.parse(goalDateInput);

            // 목표 날짜와 현재 날짜의 차이 계산
            long remainingDays = (goalDate.getTime() - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);

            dDayText.setText("D-" + remainingDays);
            goalAmountText.setText("목표 금액: " + goalAmount + " 원");

            Toast.makeText(this, "목표가 설정되었습니다!", Toast.LENGTH_LONG).show();

        } catch (ParseException e) {
            Toast.makeText(this, "잘못된 날짜 형식입니다. yyyyMMdd 형식으로 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void recordExpense(String category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(category + " 항목 입력");

        final View expenseInputView = getLayoutInflater().inflate(R.layout.dialog_expense_input, null);
        builder.setView(expenseInputView);

        final EditText categoryInput = expenseInputView.findViewById(R.id.expense_category);
        final EditText amountInput = expenseInputView.findViewById(R.id.expense_amount);

        // 숫자 입력 키보드 설정
        amountInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

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

                if (!expenseDetails.containsKey(category)) {
                    expenseDetails.put(category, new StringBuilder());
                }
                expenseDetails.get(category).append(enteredCategory)
                        .append(": ")
                        .append(enteredAmount)
                        .append(" 원\n");

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

        for (Map.Entry<String, StringBuilder> entry : expenseDetails.entrySet()) {
            String icon = "";

            if (entry.getKey().equals("식비")) {
                icon = "■ ";
            } else if (entry.getKey().equals("생활비")) {
                icon = "● ";
            } else if(entry.getKey().equals("기타")) {
                icon = "★ ";
            }

            expenseSummary.append(icon)
                    .append(entry.getKey())
                    .append(" 소비 내역:\n")
                    .append(entry.getValue().toString())
                    .append("\n");
        }

        expenseDetailsText.setText(expenseSummary.toString());
    }



    private void showSettingsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_settings, null);

        final EditText titleEditText = dialogView.findViewById(R.id.edit_saving_title);
        final EditText goalAmountEditText = dialogView.findViewById(R.id.edit_goal_amount);

        titleEditText.setText(savingPurpose.getText().toString());
        goalAmountEditText.setText(goalAmount);

        new AlertDialog.Builder(this)
                .setTitle("목표 수정")
                .setView(dialogView)
                .setPositiveButton("확인", (dialog, which) -> {
                    savingPurpose.setText(titleEditText.getText().toString());
                    goalAmount = goalAmountEditText.getText().toString();
                    goalAmountText.setText("목표 금액: " + goalAmount + " 원");
                    inputGoalAmount.setText(goalAmount);
                    Toast.makeText(this, "목표가 수정되었습니다!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("취소", null)
                .create()
                .show();
    }
    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("절약 종료")
                .setMessage("절약을 종료하시겠습니까?")
                .setPositiveButton("네", (dialog, which) -> resetFields()) // "네"를 눌렀을 때 리셋 실행
                .setNegativeButton("아니오", null) // "아니오"를 누르면 아무 작업도 하지 않음
                .create()
                .show();
    }

    // 리셋 버튼 클릭 시 리셋 처리
    private void resetFields() {
        savingCount++;
        savingTitle.setText("<<나의 " + savingCount + "번째 절약>>");
        savingPurpose.setText("<<절약 목표>>");
        dDayText.setText("D-Day");
        goalAmountText.setText("목표 금액");
        todayExpenseText.setText("오늘의 소비 금액");
        expenseDetails.clear();
        expenseDetailsText.setText("소비 내역");

        inputGoalAmount.setText("");
        inputGoalDate.setText("");

        // 다른 리셋 로직 추가 가능
        Toast.makeText(this, "리셋되었습니다.", Toast.LENGTH_SHORT).show();
    }
}

