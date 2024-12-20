package com.example.app;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class savingActivity extends AppCompatActivity {

    private TextView savingTitleTextView;
    private TextView goalAmountTextView;
    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 텍스트뷰와 버튼 초기화
        savingTitleTextView = findViewById(R.id.saving_title);
        goalAmountTextView = findViewById(R.id.goal_amount_text);
        settingsButton = findViewById(R.id.settings_button);

        // 톱니 바퀴 버튼 클릭 시 다이얼로그 표시
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsDialog();
            }
        });
    }

    // 다이얼로그를 사용하여 절약 제목과 목표 금액을 변경하는 함수
    private void showSettingsDialog() {
        // 다이얼로그에 표시할 뷰를 생성
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_settings, null);

        final EditText titleEditText = dialogView.findViewById(R.id.edit_saving_title);
        final EditText goalAmountEditText = dialogView.findViewById(R.id.edit_goal_amount);

        // 현재 값으로 초기화
        titleEditText.setText(savingTitleTextView.getText().toString());
        goalAmountEditText.setText(goalAmountTextView.getText().toString());

        // 다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("목표 수정")
                .setView(dialogView)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 입력된 값을 텍스트뷰에 반영
                        savingTitleTextView.setText(titleEditText.getText().toString());
                        goalAmountTextView.setText(goalAmountEditText.getText().toString());
                    }
                })
                .setNegativeButton("취소", null);

        builder.create().show();
    }
}
