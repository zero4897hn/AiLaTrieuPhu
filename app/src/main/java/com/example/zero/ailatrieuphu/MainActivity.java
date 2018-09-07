package com.example.zero.ailatrieuphu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.zero.media.SoundTrackPlayer;
import com.example.zero.model.CauHoi;
import com.example.zero.setting.GameSetting;
import com.example.zero.sqlite.GamingDatabaseHelper;
import com.example.zero.sqlite.ScoreDatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    SoundTrackPlayer playerTheme;
    GamingDatabaseHelper helperGame;
    ScoreDatabaseHelper helperScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            playerTheme = new SoundTrackPlayer(MainActivity.this);
            connectToDatabase();

            SharedPreferences preferences = getSharedPreferences(GameSetting.tenLuuTru, MODE_PRIVATE);
            GameSetting.amThanhNen = preferences.getBoolean("AM_THANH_NEN", true);
            GameSetting.amThanhHieuUng = preferences.getBoolean("AM_THANH_HIEU_UNG", true);
            playThemeMusic();
        }
        catch (Exception ex) {
            Log.e("LOI_VAO_MAN_HINH_CHINH", ex.toString());
        }
    }

    private void playThemeMusic() {
        playerTheme.startPlayerNonStop(R.raw.main);
    }

    private void connectToDatabase() {
        helperGame = new GamingDatabaseHelper(MainActivity.this);
        helperScore = new ScoreDatabaseHelper(MainActivity.this);
        removeOldDataFiles("Milionare.sqlite");
        removeOldDataFiles("Milionare260718.sqlite");
        removeOldDataFiles("Milionare1800.sqlite");
        removeOldDataFiles("Milionare1801.sqlite");
        removeOldDataFiles("Milionare1802.sqlite");
        removeOldDataFiles("Milionare1803.sqlite");
        removeOldDataFiles("Milionare1804.sqlite");
        removeOldDataFiles("Milionare1805.sqlite");
        removeOldDataFiles("Score.sqlite");
        File databaseGame = getApplicationContext().getDatabasePath(GamingDatabaseHelper.DATABASE_NAME);
        if (!databaseGame.exists()) {
            helperGame.getReadableDatabase();
            copyDatabase(MainActivity.this, GamingDatabaseHelper.DATABASE_NAME);
        }
        File databaseScore = getApplicationContext().getDatabasePath(ScoreDatabaseHelper.DATABASE_NAME);
        if (!databaseScore.exists()) {
            helperScore.getReadableDatabase();
            copyDatabase(MainActivity.this, ScoreDatabaseHelper.DATABASE_NAME);
        }
    }

    private void removeOldDataFiles(String dataFileName) {
        File file = getApplicationContext().getDatabasePath(dataFileName);
        if (file.exists()) file.delete();
    }

    private void copyDatabase(Context context, String databaseName) {
        try {
            InputStream inputStream = context.getAssets().open(databaseName);
            OutputStream outputStream = new FileOutputStream(context.getDatabasePath(databaseName));
            byte[] buff = new byte[1024];
            int length;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            Log.w("MainActivity", "Database copied");
        } catch (Exception ex) {
            Log.e("LOI_SAO_CHEP_CSDL", ex.toString());
        }
    }

    public void thucHienThoat(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Xác nhận thoát?");
        builder.setMessage("Bạn có chắc chắn muốn thoát?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                finish();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onPause() {
        playerTheme.stopPlayer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playThemeMusic();
    }

    public void thucHienXemDiemCao(View view) {
        Intent intent = new Intent(MainActivity.this, DiemCaoActivity.class);
        startActivity(intent);
    }

    public void thucHienTuyChinh(View view) {
        Intent intent = new Intent(MainActivity.this, TuyChinhActivity.class);
        startActivity(intent);
    }

    public void thucHienBatDauChoi(View view) {
        final SharedPreferences preferences = getSharedPreferences(CauHoiActivity.tenLuuTru, MODE_PRIVATE);
        final int thuTuCauHoi = preferences.getInt("CAU_HOI", 0);
        if (thuTuCauHoi == 0) {
            Intent intent = new Intent(MainActivity.this, LuatChoiActivity.class);
            startActivity(intent);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Tiếp tục chơi?");
            builder.setMessage("Lần chơi trước bạn vẫn còn chơi dở. Bạn có muốn tiếp tục?");
            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.setNeutralButton("Chơi mới", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this, LuatChoiActivity.class);
                    startActivity(intent);
                }
            });
            builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(MainActivity.this, CauHoiActivity.class);
                    CauHoi cauHoi = new CauHoi();
                    cauHoi.setId(preferences.getInt("ID", 0));
                    cauHoi.setNoiDung(preferences.getString("NOI_DUNG", ""));
                    cauHoi.setDapAnA(preferences.getString("DAP_AN_A", ""));
                    cauHoi.setDapAnB(preferences.getString("DAP_AN_B", ""));
                    cauHoi.setDapAnC(preferences.getString("DAP_AN_C", ""));
                    cauHoi.setDapAnD(preferences.getString("DAP_AN_D", ""));
                    cauHoi.setCauTraLoi(preferences.getString("CAU_TRA_LOI", ""));
                    intent.putExtra("CAU_HOI", thuTuCauHoi);
                    intent.putExtra("GOI_CAU_HOI", cauHoi);
                    intent.putExtra("50_50", preferences.getBoolean("50_50", true));
                    intent.putExtra("GOI_DIEN", preferences.getBoolean("GOI_DIEN", true));
                    intent.putExtra("KHAN_GIA", preferences.getBoolean("KHAN_GIA", true));
                    intent.putExtra("TU_VAN", preferences.getBoolean("TU_VAN", true));
                    intent.putExtra("AN_NUT_TRO_GIUP", preferences.getString("AN_NUT_TRO_GIUP", ""));
                    intent.putExtra("NGUOI_GOI", preferences.getString("NGUOI_GOI", ""));
                    intent.putExtra("CHUOI_CAU_HOI", preferences.getString("CHUOI_CAU_HOI", ""));
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR, preferences.getInt("HOUR", 0));
                    calendar.set(Calendar.MINUTE, preferences.getInt("MINUTE", 0));
                    calendar.set(Calendar.SECOND, preferences.getInt("SECOND", 0));
                    calendar.set(Calendar.MILLISECOND, preferences.getInt("MILLISECOND", 0));
                    calendar.set(Calendar.AM_PM, preferences.getInt("AM_PM", Calendar.AM));
                    intent.putExtra("CALENDAR", calendar);
                    startActivity(intent);
                }
            });
            builder.show();
        }
    }
}
