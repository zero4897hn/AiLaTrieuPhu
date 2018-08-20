package com.example.zero.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.zero.model.NguoiChoi;

import java.util.ArrayList;

public class ScoreDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Scores.sqlite";
    private Context context;
    private SQLiteDatabase database;

    public ScoreDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void openDatabase() {
        String DATABASE_PATH = context.getDatabasePath(DATABASE_NAME).getPath();
        if (database != null && database.isOpen()) {
            return;
        }
        database = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase() {
        if (database != null) database.close();
    }

    public void insertDiem(String ten, double diem, int cauHoi, String thoiGian) {
        try {
            openDatabase();
            database.execSQL("insert into DiemCao values (null, ?, ?, ?, ?)", new Object[] {ten, diem, cauHoi, thoiGian});
            closeDatabase();
        }
        catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
    }

    public ArrayList<NguoiChoi> getDanhSachNguoiChoi() {
        ArrayList<NguoiChoi> dsNguoiChoi = new ArrayList<>();
        try {
            openDatabase();
            Cursor cursor = database.rawQuery("select * from DiemCao order by diemCao desc, thoiGian asc limit 6", null);
            while (cursor.moveToNext()) {
                NguoiChoi nguoiChoi = new NguoiChoi();
                nguoiChoi.setThuTu(cursor.getInt(0));
                nguoiChoi.setTen(cursor.getString(1));
                nguoiChoi.setDiem(cursor.getDouble(2));
                nguoiChoi.setThuTuCauHoi(cursor.getInt(3));
                nguoiChoi.setThoiGian(cursor.getString(4));
                dsNguoiChoi.add(nguoiChoi);
            }
            cursor.close();
            closeDatabase();
        }
        catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
        return dsNguoiChoi;
    }

    public void xoaHetDiemCao() {
        try {
            openDatabase();
            database.execSQL("delete from DiemCao");
            closeDatabase();
        }
        catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
    }
}
