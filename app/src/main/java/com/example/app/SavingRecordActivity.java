package com.example.app;  // 여기에 패키지 이름을 맞춰주어야 합니다.

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

class SavingRecordsActivity extends AppCompatActivity {

    private ListView savingRecordsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_records);  // activity_saving_records.xml 파일을 사용

        savingRecordsList = findViewById(R.id.savingRecordsList);  // ListView ID 참조
        List<String> records = getIntent().getStringArrayListExtra("savingRecords");

        // ArrayAdapter를 사용하여 리스트 항목을 표시
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, records);
        savingRecordsList.setAdapter(adapter);  // ListView에 어댑터 설정
    }
}
