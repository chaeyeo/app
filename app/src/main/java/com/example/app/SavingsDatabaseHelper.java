package com.example.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SavingsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Savings.db";
    private static final int DATABASE_VERSION = 1;

    // Savings 테이블
    private static final String TABLE_SAVINGS = "Savings";
    private static final String COLUMN_SAVING_ID = "saving_id";
    private static final String COLUMN_GOAL_NAME = "goal_name";
    private static final String COLUMN_GOAL_DATE = "goal_date";
    private static final String COLUMN_GOAL_AMOUNT = "goal_amount";
    private static final String COLUMN_TODAY_TOTAL = "today_total";

    // Expenses 테이블
    private static final String TABLE_EXPENSES = "Expenses";
    private static final String COLUMN_EXPENSE_ID = "expense_id";
    private static final String COLUMN_EXPENSE_CATEGORY = "category";
    private static final String COLUMN_EXPENSE_PRICE = "price";
    private static final String COLUMN_EXPENSE_DATE = "expense_date";

    // Foreign Key
    private static final String COLUMN_SAVING_REF_ID = "saving_id";

    public SavingsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Savings 테이블 생성
        String CREATE_SAVINGS_TABLE = "CREATE TABLE " + TABLE_SAVINGS + "(" +
                COLUMN_SAVING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GOAL_NAME + " TEXT, " +
                COLUMN_GOAL_DATE + " TEXT, " +
                COLUMN_GOAL_AMOUNT + " INTEGER, " +
                COLUMN_TODAY_TOTAL + " INTEGER)";
        db.execSQL(CREATE_SAVINGS_TABLE);

        // Expenses 테이블 생성
        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "(" +
                COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EXPENSE_CATEGORY + " TEXT, " +
                COLUMN_EXPENSE_PRICE + " INTEGER, " +
                COLUMN_EXPENSE_DATE + " TEXT, " +
                COLUMN_SAVING_REF_ID + " INTEGER, " +
                "FOREIGN KEY (" + COLUMN_SAVING_REF_ID + ") REFERENCES " + TABLE_SAVINGS + "(" + COLUMN_SAVING_ID + "))";
        db.execSQL(CREATE_EXPENSES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVINGS);
        onCreate(db);
    }

    // 절약 목표 추가
    public long addSavingsGoal(String goalName, String goalDate, int goalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL_NAME, goalName);
        values.put(COLUMN_GOAL_DATE, goalDate);
        values.put(COLUMN_GOAL_AMOUNT, goalAmount);
        values.put(COLUMN_TODAY_TOTAL, 0);
        long id = db.insert(TABLE_SAVINGS, null, values);
        db.close();
        return id;
    }

    // 소비 내역 추가
    public void addExpense(int savingId, String category, int price, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SAVING_REF_ID, savingId);
        values.put(COLUMN_EXPENSE_CATEGORY, category);
        values.put(COLUMN_EXPENSE_PRICE, price);
        values.put(COLUMN_EXPENSE_DATE, date);
        db.insert(TABLE_EXPENSES, null, values);

        // 오늘의 소비 금액 합산 업데이트
        updateTodayTotal(savingId, price);
        db.close();
    }

    // 오늘 소비 금액 합산 업데이트
    private void updateTodayTotal(int savingId, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_SAVINGS +
                " SET " + COLUMN_TODAY_TOTAL + " = " + COLUMN_TODAY_TOTAL + " + " + price +
                " WHERE " + COLUMN_SAVING_ID + " = " + savingId);
        db.close();
    }

    // 특정 절약 목표 조회
    public Cursor getSavingsGoal(int savingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SAVINGS + " WHERE " + COLUMN_SAVING_ID + "=?", new String[]{String.valueOf(savingId)});
    }

    // 특정 절약 목표의 소비 내역 조회
    public Cursor getExpensesBySavingId(int savingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_EXPENSES + " WHERE " + COLUMN_SAVING_REF_ID + "=?", new String[]{String.valueOf(savingId)});
    }

    // [절약 기록 리스트] 조회
    public Cursor getAllSavingsGoals() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_SAVINGS,
                null,                        // 조회할 컬럼 (null이면 모든 컬럼)
                null,                        // WHERE 조건 (필요 없으면 null)
                null,                        // WHERE 조건 값
                null,                        // GROUP BY
                null,                        // HAVING
                COLUMN_SAVING_ID + " ASC"   // 순서 정렬
        );
    }

    // [절약 기록 리스트] 총 데이터 개수 조회
    public int getSavingsGoalsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_SAVINGS;
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }
}
