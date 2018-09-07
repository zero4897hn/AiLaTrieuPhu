package com.example.zero.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.zero.model.CauHoi;

import java.util.ArrayList;
import java.util.Random;

public class GamingDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Milionare1806.sqlite";
    private Context context;
    private SQLiteDatabase database;
    private ArrayList<Integer> cauHoiDeDaLay, cauHoiThuongDaLay, cauHoiKhoDaLay;

    public ArrayList<Integer> getCauHoiDeDaLay() {
        return cauHoiDeDaLay;
    }

    public ArrayList<Integer> getCauHoiThuongDaLay() {
        return cauHoiThuongDaLay;
    }

    public ArrayList<Integer> getCauHoiKhoDaLay() {
        return cauHoiKhoDaLay;
    }

    public GamingDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        cauHoiDeDaLay = new ArrayList<>();
        cauHoiThuongDaLay = new ArrayList<>();
        cauHoiKhoDaLay = new ArrayList<>();
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

    public CauHoi getCauHoiDe() {
        try {
            CauHoi cauHoi = null;
            openDatabase();
            Random random = new Random();
            int soLuong = getSoLuongCauHoi("CauHoiDe");
            int id = random.nextInt(soLuong) + 1;
            while (cauHoiDeDaLay.contains(id)) {
                id = random.nextInt(soLuong) + 1;
            }
            cauHoiDeDaLay.add(id);
            Cursor cursor = database.rawQuery("select * from CauHoiDe where id = " + id, null);
            if (cursor.moveToNext()) {
                cauHoi = new CauHoi();
                cauHoi.setId(cursor.getInt(0));
                cauHoi.setNoiDung(cursor.getString(1));
                cauHoi.setDapAnA("A. " + cursor.getString(2));
                cauHoi.setDapAnB("B. " + cursor.getString(3));
                cauHoi.setDapAnC("C. " + cursor.getString(4));
                cauHoi.setDapAnD("D. " + cursor.getString(5));
                cauHoi.setCauTraLoi(cursor.getString(6));
            }
            cursor.close();
            closeDatabase();
            return cauHoi;
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
        return null;
    }

    public CauHoi getCauHoiThuong() {
        try {
            CauHoi cauHoi = null;
            openDatabase();
            Random random = new Random();
            int soLuong = getSoLuongCauHoi("CauHoiThuong");
            int id = random.nextInt(soLuong) + 1;
            while (cauHoiThuongDaLay.contains(id)) {
                id = random.nextInt(soLuong) + 1;
            }
            cauHoiThuongDaLay.add(id);
            Cursor cursor = database.rawQuery("select * from CauHoiThuong where id = " + id, null);
            if (cursor.moveToNext()) {
                cauHoi = new CauHoi();
                cauHoi.setId(cursor.getInt(0));
                cauHoi.setNoiDung(cursor.getString(1));
                cauHoi.setDapAnA("A. " + cursor.getString(2));
                cauHoi.setDapAnB("B. " + cursor.getString(3));
                cauHoi.setDapAnC("C. " + cursor.getString(4));
                cauHoi.setDapAnD("D. " + cursor.getString(5));
                cauHoi.setCauTraLoi(cursor.getString(6));
            }
            cursor.close();
            closeDatabase();
            return cauHoi;
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
        return null;
    }

    public CauHoi getCauHoiKho() {
        try {
            CauHoi cauHoi = null;
            openDatabase();
            Random random = new Random();
            int soLuong = getSoLuongCauHoi("CauHoiKho");
            int id = random.nextInt(soLuong) + 1;
            while (cauHoiKhoDaLay.contains(id)) {
                id = random.nextInt(soLuong) + 1;
            }
            cauHoiKhoDaLay.add(id);
            Cursor cursor = database.rawQuery("select * from CauHoiKho where id = " + id, null);
            if (cursor.moveToNext()) {
                cauHoi = new CauHoi();
                cauHoi.setId(cursor.getInt(0));
                cauHoi.setNoiDung(cursor.getString(1));
                cauHoi.setDapAnA("A. " + cursor.getString(2));
                cauHoi.setDapAnB("B. " + cursor.getString(3));
                cauHoi.setDapAnC("C. " + cursor.getString(4));
                cauHoi.setDapAnD("D. " + cursor.getString(5));
                cauHoi.setCauTraLoi(cursor.getString(6));
            }
            cursor.close();
            closeDatabase();
            return cauHoi;
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
        return null;
    }

    private int getSoLuongCauHoi(String table) {
        int soLuong = 0;
        try {
            Cursor cursor = database.rawQuery("select count(*) from " + table, null);
            if (cursor.moveToNext()) soLuong = cursor.getInt(0);
            cursor.close();
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
        return soLuong;
    }

    public double getDiem(int cauHoi) {
        double diem = 0;
        try {
            openDatabase();
            Cursor cursor = database.rawQuery("select diem from MucCauHoi where thuTu = " + cauHoi, null);
            if (cursor.moveToNext()) diem = cursor.getDouble(0);
            cursor.close();
            closeDatabase();
        } catch (Exception ex) {
            Log.e("ERROR", ex.toString());
        }
        return diem;
    }
}
