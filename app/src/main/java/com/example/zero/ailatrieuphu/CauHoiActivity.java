package com.example.zero.ailatrieuphu;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zero.media.EffectPlayer;
import com.example.zero.media.SoundTrackPlayer;
import com.example.zero.model.CauHoi;
import com.example.zero.setting.GameSetting;
import com.example.zero.sqlite.GamingDatabaseHelper;
import com.example.zero.sqlite.ScoreDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class CauHoiActivity extends AppCompatActivity {
    ImageButton btn5050, btnGoiDien, btnKhanGia, btnTuVan, btnDungCuocChoi;
    TextView txtCauHoi, txtNoiDungCauHoi, txtThoiGian;
    Button btnDapAnA, btnDapAnB, btnDapAnC, btnDapAnD;
    SoundTrackPlayer playerTheme;
    EffectPlayer playerSoundEffect, playerEffect;

    Calendar calendar;
    Timer timerPlay;
    SimpleDateFormat simpleDateFormat;

    GamingDatabaseHelper helperGame;
    ScoreDatabaseHelper helperScore;
    CauHoi cauHoi;
    Random random = new Random();

    Button selectedButton;
    ImageButton selectedSupport;
    boolean isKetThuc = false;
    public final static String tenLuuTru = "DuLieuChoi";
    int thuTuCauHoi;
    String nguoiCanGoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cau_hoi);
            addControls();
            addEvents();
            layDuLieuCu();
        }
        catch (Exception ex) {
            Log.e("LOI_VAO_CHOI", ex.toString());
        }
    }

    private void startCounting() {
        if (timerPlay == null) {
            timerPlay = new Timer();
            timerPlay.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtThoiGian.setText(simpleDateFormat.format(calendar.getTime()));
                            calendar.add(Calendar.MILLISECOND, 20);
                        }
                    });
                }
            }, 0, 20);
        }
    }

    private void stopCounting() {
        if (timerPlay != null) {
            timerPlay.cancel();
            timerPlay = null;
        }
    }

    private void layDuLieuCu() {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.AM_PM, Calendar.AM);
        Intent intent = getIntent();
        thuTuCauHoi = intent.getIntExtra("CAU_HOI", 0);
        if (thuTuCauHoi == 0) {
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            btnTuVan.setVisibility(View.GONE);
            thuTuCauHoi++;
            playerTheme.startPlayerNonStop(R.raw.question_1_to_5);
            cauHoi = helperGame.getCauHoiDe();
            setCauHoi();
            startCounting();
            return;
        }
        playSelectedThemeMusic();
        cauHoi = (CauHoi) intent.getSerializableExtra("GOI_CAU_HOI");
        setCauHoi();

        String chuoiCauHoi = intent.getStringExtra("CHUOI_CAU_HOI");
        if (!chuoiCauHoi.equals("")) {
            String[] dsChuoiCauHoi = chuoiCauHoi.split("\\s+");
            if (thuTuCauHoi <= 5) {
                btnTuVan.setVisibility(View.GONE);
                for (String x : dsChuoiCauHoi) helperGame.getCauHoiDeDaLay().add(Integer.parseInt(x));
            }
            else if (thuTuCauHoi <= 10) {
                for (String x : dsChuoiCauHoi) helperGame.getCauHoiThuongDaLay().add(Integer.parseInt(x));
            }
            else {
                for (String x : dsChuoiCauHoi) helperGame.getCauHoiKhoDaLay().add(Integer.parseInt(x));
            }
        }

        boolean troGiup5050 = intent.getBooleanExtra("50_50", true);
        boolean troGiupGoiDien = intent.getBooleanExtra("GOI_DIEN", true);
        boolean troGiupKhanGia = intent.getBooleanExtra("KHAN_GIA", true);
        boolean troGiupTuVan = intent.getBooleanExtra("TU_VAN", true);

        String troGiupDaAn = intent.getStringExtra("AN_NUT_TRO_GIUP");
        if (troGiupDaAn.equals("50_50")) {
            xuLyTroGiup5050();
            troGiup5050 = false;
        }
        else if (troGiupDaAn.equals("GOI_DIEN")) {
            xuLyTroGiupGoiDien(intent.getStringExtra("NGUOI_GOI"));
            troGiupGoiDien = false;
        }
        else if (troGiupDaAn.equals("KHAN_GIA")) {
            xuLyTroGiupKhanGia();
            troGiupKhanGia = false;
        }
        else if (troGiupDaAn.equals("TU_VAN")) {
            xuLyTroGiupTuVan();
            troGiupTuVan = false;
        }

        if (!troGiup5050) {
            btn5050.setEnabled(false);
            btn5050.setImageResource(R.drawable.support_1_used);
        }
        if (!troGiupGoiDien) {
            btnGoiDien.setEnabled(false);
            btnGoiDien.setImageResource(R.drawable.support_2_used);
        }
        if (!troGiupKhanGia) {
            btnKhanGia.setEnabled(false);
            btnKhanGia.setImageResource(R.drawable.support_3_used);
        }
        if (!troGiupTuVan) {
            btnTuVan.setEnabled(false);
            btnTuVan.setImageResource(R.drawable.support_4_used);
        }

        calendar = (Calendar) intent.getSerializableExtra("CALENDAR");
        startCounting();
    }

    private void setCauHoi() {
        setOriginalOption(btnDapAnA);
        setOriginalOption(btnDapAnB);
        setOriginalOption(btnDapAnC);
        setOriginalOption(btnDapAnD);
        txtCauHoi.setText("Câu hỏi số " + thuTuCauHoi + ":");
        txtNoiDungCauHoi.setText(cauHoi.getNoiDung());
        btnDapAnA.setText(cauHoi.getDapAnA());
        btnDapAnB.setText(cauHoi.getDapAnB());
        btnDapAnC.setText(cauHoi.getDapAnC());
        btnDapAnD.setText(cauHoi.getDapAnD());
    }

    private void setOriginalOption(Button button) {
        button.setBackgroundResource(R.drawable.option);
        button.setTextColor(Color.WHITE);
    }

    private void addEvents() {
        btn5050.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(CauHoiActivity.this);
                    dialog.setTitle("Sử dụng trợ giúp");
                    dialog.setMessage("Bạn muốn sử dụng trợ giúp 50 : 50?");
                    dialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            if (GameSetting.amThanhHieuUng) {
                                selectedSupport = btn5050;
                                stopCounting();
                                playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.set_5050_voices, new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        startCounting();
                                        selectedSupport = null;
                                        playerSoundEffect.stopPlayer();
                                        playerEffect.startPlayerAtOnce(R.raw.set_5050);
                                        xuLyTroGiup5050();
                                    }
                                });
                            }
                            else {
                                xuLyTroGiup5050();
                            }
                            btn5050.setEnabled(false);
                            btn5050.setImageResource(R.drawable.support_1_used);
                            dialogInterface.cancel();
                        }
                    });
                    dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    dialog.show();
                }
                catch (Exception ex) {
                    Log.e("LOI_DUNG_50_50", ex.toString());
                }
            }
        });
        btnGoiDien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder dialogXacNhan = new AlertDialog.Builder(CauHoiActivity.this);
                    dialogXacNhan.setTitle("Sử dụng trợ giúp");
                    dialogXacNhan.setMessage("Bạn muốn sử dụng trợ giúp gọi điện thoại cho người thân?");
                    dialogXacNhan.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            final String[] dsNguoiCanGoi = {"Phan Quân", "Black Obama", "Donald Trump", "Conan", "Bao Công"};
                            AlertDialog.Builder dialogChon = new AlertDialog.Builder(CauHoiActivity.this);
                            dialogChon.setTitle("Chọn người cần gọi");
                            dialogChon.setItems(dsNguoiCanGoi, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface1, int which) {
                                    nguoiCanGoi = dsNguoiCanGoi[which];
                                    if (GameSetting.amThanhHieuUng) {
                                        selectedSupport = btnGoiDien;
                                        stopCounting();
                                        playerTheme.stopPlayer();
                                        final ProgressDialog progressDialog = new ProgressDialog(CauHoiActivity.this);
                                        progressDialog.setTitle("Thông báo");
                                        progressDialog.setMessage("Đang gọi điện cho " + nguoiCanGoi + ", vui lòng chờ...");
                                        progressDialog.setCanceledOnTouchOutside(false);
                                        progressDialog.setButton("Bỏ qua", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                progressDialog.cancel();
                                            }
                                        });
                                        progressDialog.show();
                                        playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.phone_a_friend, new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                progressDialog.cancel();
                                            }
                                        });
                                        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialogInterface) {
                                                selectedSupport = null;
                                                startCounting();
                                                playerSoundEffect.stopPlayer();
                                                playSelectedThemeMusic();
                                                xuLyTroGiupGoiDien(nguoiCanGoi);
                                            }
                                        });
                                    }
                                    else {
                                        xuLyTroGiupGoiDien(nguoiCanGoi);
                                    }
                                    btnGoiDien.setEnabled(false);
                                    btnGoiDien.setImageResource(R.drawable.support_2_used);
                                    dialogInterface1.cancel();
                                }
                            });
                            dialogChon.show();
                            dialogInterface.cancel();
                        }
                    });
                    dialogXacNhan.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    dialogXacNhan.show();
                }
                catch (Exception ex) {
                    Log.e("LOI_GOI_DIEN", ex.toString());
                }
            }
        });
        btnKhanGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder dialogXacNhan = new AlertDialog.Builder(CauHoiActivity.this);
                    dialogXacNhan.setTitle("Sử dụng trợ giúp");
                    dialogXacNhan.setMessage("Bạn muốn sử dụng trợ giúp hỏi ý kiến khán giả trong trường quay?");
                    dialogXacNhan.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (GameSetting.amThanhHieuUng) {
                                selectedSupport = btnKhanGia;
                                stopCounting();
                                playerTheme.stopPlayer();
                                final ProgressDialog progressDialog = new ProgressDialog(CauHoiActivity.this);
                                progressDialog.setTitle("Thông báo");
                                progressDialog.setMessage("Đang khảo sát, vui lòng chờ...");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setButton("Bỏ qua", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.cancel();
                                    }
                                });
                                progressDialog.show();
                                playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.ask_the_audience_processing, new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        progressDialog.cancel();
                                    }
                                });
                                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        selectedSupport = null;
                                        startCounting();
                                        playerSoundEffect.stopPlayer();
                                        playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.ask_the_audience_done, new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer) {
                                                playerSoundEffect.stopPlayer();
                                                playSelectedThemeMusic();
                                            }
                                        });
                                        xuLyTroGiupKhanGia();
                                    }
                                });
                            }
                            else {
                                xuLyTroGiupKhanGia();
                            }
                            btnKhanGia.setEnabled(false);
                            btnKhanGia.setImageResource(R.drawable.support_3_used);
                        }
                    });
                    dialogXacNhan.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    dialogXacNhan.show();
                }
                catch (Exception ex) {
                    Log.e("LOI_KHAN_GIA", ex.toString());
                }
            }
        });
        btnTuVan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder dialogXacNhan = new AlertDialog.Builder(CauHoiActivity.this);
                    dialogXacNhan.setTitle("Sử dụng trợ giúp");
                    dialogXacNhan.setMessage("Bạn muốn sử dụng trợ giúp tư vấn tại chỗ?");
                    dialogXacNhan.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (GameSetting.amThanhHieuUng) {
                                selectedSupport = btnTuVan;
                                stopCounting();
                                playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.consulting_voices, new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        startCounting();
                                        selectedSupport = null;
                                        playerSoundEffect.stopPlayer();
                                        xuLyTroGiupTuVan();
                                    }
                                });
                            }
                            else {
                                xuLyTroGiupTuVan();
                            }
                            btnTuVan.setEnabled(false);
                            btnTuVan.setImageResource(R.drawable.support_4_used);
                            dialog.cancel();
                        }
                    });
                    dialogXacNhan.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    dialogXacNhan.show();
                }
                catch (Exception ex) {
                    Log.e("LOI_TU_VAN", ex.toString());
                }
            }
        });
        btnDapAnA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    xuLyTraLoiCauHoi(btnDapAnA);
                }
                catch (Exception ex) {
                    Log.e("LOI_CAU_ID_" + cauHoi.getId(), ex.toString());
                }
            }
        });
        btnDapAnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    xuLyTraLoiCauHoi(btnDapAnB);
                }
                catch (Exception ex) {
                    Log.e("LOI_CAU_ID_" + cauHoi.getId(), ex.toString());
                }
            }
        });
        btnDapAnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    xuLyTraLoiCauHoi(btnDapAnC);
                }
                catch (Exception ex) {
                    Log.e("LOI_CAU_ID_" + cauHoi.getId(), ex.toString());
                }
            }
        });
        btnDapAnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    xuLyTraLoiCauHoi(btnDapAnD);
                }
                catch (Exception ex) {
                    Log.e("LOI_CAU_ID_" + cauHoi.getId(), ex.toString());
                }
            }
        });
        btnDungCuocChoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CauHoiActivity.this);
                    builder.setTitle("Xác nhận dừng cuộc chơi");
                    builder.setMessage("Bạn có chắc chắn muốn dừng cuộc chơi?");
                    builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopCounting();
                            playerTheme.stopPlayer();
                            hideOptions();
                            Button btnDapAnDung = getButtonDapAnDung();
                            setCorrectOption(btnDapAnDung);
                            AlertDialog.Builder builder = new AlertDialog.Builder(CauHoiActivity.this);
                            builder.setCancelable(false);
                            View view = getLayoutInflater().inflate(R.layout.dialog_ket_thuc, null);
                            final EditText txtHoTen = view.findViewById(R.id.txtHoTen);
                            TextView txtPhanThuong = view.findViewById(R.id.txtPhanThuong);
                            TextView txtDapAnDung = view.findViewById(R.id.txtDapAnDung);
                            Button btnThoat = view.findViewById(R.id.btnThoat);
                            Button btnXacNhan = view.findViewById(R.id.btnXacNhan);
                            final double diemThuong = helperGame.getDiem(thuTuCauHoi - 1);
                            txtPhanThuong.setText("Phần thưởng: " + diemThuong);
                            txtDapAnDung.setText("Đáp án đúng: " + btnDapAnDung.getText().toString());
                            isKetThuc = true;
                            builder.setView(view);
                            final AlertDialog dialog = builder.create();
                            btnThoat.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
                            btnXacNhan.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    helperScore.insertDiem(txtHoTen.getText().toString(), diemThuong, thuTuCauHoi, simpleDateFormat.format(calendar.getTime()));
                                    Toast.makeText(CauHoiActivity.this, "Lưu điểm thành công.", Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                    finish();
                                }
                            });
                            dialog.show();
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }
                catch (Exception ex) {
                    Log.e("LOI_DUNG_CUOC_CHOI", ex.toString());
                }
            }
        });
    }

    private void setCorrectOption(Button button) {
        button.setBackgroundResource(R.drawable.correct_option);
        button.setTextColor(Color.BLACK);
    }

    private void xuLyTroGiupGoiDien(String nguoiCanGoi) {
        Toast.makeText(CauHoiActivity.this, nguoiCanGoi + " xin trợ giúp cho bạn, đáp án của tôi là " + getButtonDapAnDung().getText().toString().charAt(0), Toast.LENGTH_LONG).show();
    }

    private void xuLyTroGiupKhanGia() {
        char dapAn = getButtonDapAnDung().getText().toString().charAt(0);
        int[] dsPhanTramDapAn = new int[4];
        if (thuTuCauHoi <= 5) {
            dsPhanTramDapAn[0] = dapAn == 'A'? 10000 : 0;
            dsPhanTramDapAn[1] = dapAn == 'B'? 10000 : 0;
            dsPhanTramDapAn[2] = dapAn == 'C'? 10000 : 0;
            dsPhanTramDapAn[3] = dapAn == 'D'? 10000 : 0;
        }
        else {
            List<Integer> dsPhanVan = new ArrayList<>();
            dsPhanVan.add(random.nextInt(101));
            dsPhanVan.add(random.nextInt(51));
            if (btnDapAnA.isEnabled() & btnDapAnB.isEnabled() & btnDapAnC.isEnabled()) {
                dsPhanVan.add(random.nextInt(31));
                dsPhanVan.add(random.nextInt(21));
            }
            Collections.sort(dsPhanVan);
            Collections.reverse(dsPhanVan);
            int tong = 0;
            for (Integer x : dsPhanVan) tong += x;
            double max = dsPhanVan.get(0);
            List<Integer> dsViTriDaLay = new ArrayList<>();
            dsViTriDaLay.add(0);
            if (btnDapAnA.isEnabled()) {
                if (dapAn == 'A') dsPhanTramDapAn[0] = (int) Math.round(max/tong*10000.0);
                else {
                    int viTriLay = random.nextInt(dsPhanVan.size());
                    while (dsViTriDaLay.contains(viTriLay)) viTriLay = random.nextInt(dsPhanVan.size());
                    double phanTuLay = (double)dsPhanVan.get(viTriLay);
                    dsPhanTramDapAn[0] = (int) Math.round(phanTuLay/tong*10000.0);
                    dsViTriDaLay.add(viTriLay);
                }
            }
            else dsPhanTramDapAn[0] = 0;
            if (btnDapAnB.isEnabled()) {
                if (dapAn == 'B') dsPhanTramDapAn[1] = (int) Math.round(max/tong*10000.0);
                else {
                    int viTriLay = random.nextInt(dsPhanVan.size());
                    while (dsViTriDaLay.contains(viTriLay)) viTriLay = random.nextInt(dsPhanVan.size());
                    double phanTuLay = (double)dsPhanVan.get(viTriLay);
                    dsPhanTramDapAn[1] = (int) Math.round(phanTuLay/tong*10000.0);
                    dsViTriDaLay.add(viTriLay);
                }
            }
            else dsPhanTramDapAn[1] = 0;
            if (btnDapAnC.isEnabled()) {
                if (dapAn == 'C') dsPhanTramDapAn[2] = (int) Math.round(max/tong*10000.0);
                else {
                    int viTriLay = random.nextInt(dsPhanVan.size());
                    while (dsViTriDaLay.contains(viTriLay)) viTriLay = random.nextInt(dsPhanVan.size());
                    double phanTuLay = (double)dsPhanVan.get(viTriLay);
                    dsPhanTramDapAn[2] = (int) Math.round(phanTuLay/tong*10000.0);
                    dsViTriDaLay.add(viTriLay);
                }
            }
            else dsPhanTramDapAn[2] = 0;
            if (btnDapAnD.isEnabled()) {
                if (dapAn == 'D') dsPhanTramDapAn[3] = (int) Math.round(max/tong*10000.0);
                else {
                    int viTriLay = random.nextInt(dsPhanVan.size());
                    while (dsViTriDaLay.contains(viTriLay)) viTriLay = random.nextInt(dsPhanVan.size());
                    double phanTuLay = (double)dsPhanVan.get(viTriLay);
                    dsPhanTramDapAn[3] = (int) Math.round(phanTuLay/tong*10000.0);
                }
            }
            else dsPhanTramDapAn[3] = 0;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(CauHoiActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Kết quả khảo sát");
        View view = getLayoutInflater().inflate(R.layout.dialog_phan_tram, null);
        ProgressBar progressBarA = view.findViewById(R.id.progressBarA);
        ProgressBar progressBarB = view.findViewById(R.id.progressBarB);
        ProgressBar progressBarC = view.findViewById(R.id.progressBarC);
        ProgressBar progressBarD = view.findViewById(R.id.progressBarD);
        TextView txtDapAnA = view.findViewById(R.id.txtDapAnA);
        TextView txtDapAnB = view.findViewById(R.id.txtDapAnB);
        TextView txtDapAnC = view.findViewById(R.id.txtDapAnC);
        TextView txtDapAnD = view.findViewById(R.id.txtDapAnD);
        txtDapAnA.setText("Đáp án A: " + ((double)dsPhanTramDapAn[0]/100) + "%");
        txtDapAnB.setText("Đáp án B: " + ((double)dsPhanTramDapAn[1]/100) + "%");
        txtDapAnC.setText("Đáp án C: " + ((double)dsPhanTramDapAn[2]/100) + "%");
        txtDapAnD.setText("Đáp án D: " + ((double)dsPhanTramDapAn[3]/100) + "%");
        progressBarA.setProgress(dsPhanTramDapAn[0]);
        progressBarB.setProgress(dsPhanTramDapAn[1]);
        progressBarC.setProgress(dsPhanTramDapAn[2]);
        progressBarD.setProgress(dsPhanTramDapAn[3]);
        builder.setView(view);
        builder.setNegativeButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void xuLyTroGiupTuVan() {
        char dapAn = getButtonDapAnDung().getText().toString().charAt(0);
        List<Character> cacDapAn = new ArrayList<>();
        cacDapAn.add('A');
        cacDapAn.add('B');
        cacDapAn.add('C');
        cacDapAn.add('D');
        List<Character> cacDapAnSaiKoTheLay = new ArrayList<>();
        cacDapAnSaiKoTheLay.add(dapAn);
        if (!btnDapAnA.isEnabled()) cacDapAnSaiKoTheLay.add('A');
        if (!btnDapAnB.isEnabled()) cacDapAnSaiKoTheLay.add('B');
        if (!btnDapAnC.isEnabled()) cacDapAnSaiKoTheLay.add('C');
        if (!btnDapAnD.isEnabled()) cacDapAnSaiKoTheLay.add('D');
        String[] dsTuVan = new String[3];
        if (thuTuCauHoi <= 5) {
            dsTuVan[0] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAn;
            dsTuVan[1] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAn;
            dsTuVan[2] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAn;
        }
        else if (thuTuCauHoi <= 10) {
            if (random.nextInt(100) < 90) dsTuVan[0] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAn;
            else {
                char dapAnSai = cacDapAn.get(random.nextInt(4));
                while (cacDapAnSaiKoTheLay.contains(dapAnSai)) dapAnSai = cacDapAn.get(random.nextInt(4));
                dsTuVan[0] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAnSai;
            }
            if (random.nextInt(100) < 90) dsTuVan[1] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAn;
            else {
                char dapAnSai = cacDapAn.get(random.nextInt(4));
                while (cacDapAnSaiKoTheLay.contains(dapAnSai)) dapAnSai = cacDapAn.get(random.nextInt(4));
                dsTuVan[1] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAnSai;
            }
            if (random.nextInt(100) < 90) dsTuVan[2] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAn;
            else {
                char dapAnSai = cacDapAn.get(random.nextInt(4));
                while (cacDapAnSaiKoTheLay.contains(dapAnSai)) dapAnSai = cacDapAn.get(random.nextInt(4));
                dsTuVan[2] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAnSai;
            }
        }
        else {
            if (random.nextInt(100) < 70) dsTuVan[0] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAn;
            else {
                char dapAnSai = cacDapAn.get(random.nextInt(4));
                while (cacDapAnSaiKoTheLay.contains(dapAnSai)) dapAnSai = cacDapAn.get(random.nextInt(4));
                dsTuVan[0] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAnSai;
            }
            if (random.nextInt(100) < 70) dsTuVan[1] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAn;
            else {
                char dapAnSai = cacDapAn.get(random.nextInt(4));
                while (cacDapAnSaiKoTheLay.contains(dapAnSai)) dapAnSai = cacDapAn.get(random.nextInt(4));
                dsTuVan[1] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAnSai;
            }
            if (random.nextInt(100) < 70) dsTuVan[2] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAn;
            else {
                char dapAnSai = cacDapAn.get(random.nextInt(4));
                while (cacDapAnSaiKoTheLay.contains(dapAnSai)) dapAnSai = cacDapAn.get(random.nextInt(4));
                dsTuVan[2] = "Tôi xin tư vấn cho bạn, đáp án của tôi là " + dapAnSai;
            }
        }
        AlertDialog.Builder dialogTuVan = new AlertDialog.Builder(CauHoiActivity.this);
        dialogTuVan.setTitle("Kết quả tư vấn");
        dialogTuVan.setItems(dsTuVan, null);
        dialogTuVan.show();
    }

    private void xuLyTroGiup5050() {
        List<Integer> dapAnLoaiTru = new ArrayList<>();
        while (dapAnLoaiTru.size() != 2) {
            int dapAnChon = random.nextInt(4);
            while (dapAnLoaiTru.contains(dapAnChon)) dapAnChon = random.nextInt(4);
            Button nutLoaiTru = getButtonDapAn(dapAnChon);
            if (nutLoaiTru.getText().toString().substring(3).equals(cauHoi.getCauTraLoi())) continue;
            dapAnLoaiTru.add(dapAnChon);
            nutLoaiTru.setEnabled(false);
            nutLoaiTru.setText("");
            if (dapAnChon == 0) cauHoi.setDapAnA("");
            else if (dapAnChon == 1) cauHoi.setDapAnB("");
            else if (dapAnChon == 2) cauHoi.setDapAnC("");
            else cauHoi.setDapAnD("");
        }
    }

    private Button getButtonDapAn(int dapAnChon) {
        switch (dapAnChon) {
            case 0: return btnDapAnA;
            case 1: return btnDapAnB;
            case 2: return btnDapAnC;
            case 3: return btnDapAnD;
            default: return null;
        }
    }

    private void xuLyTraLoiCauHoi(final Button button) {
        setSelectedOption(button);
        AlertDialog.Builder builder = new AlertDialog.Builder(CauHoiActivity.this);
        builder.setTitle("Xác nhận câu trả lời");
        builder.setMessage("Bạn đã lựa chọn đáp án " + button.getText() + "\r\nBạn đã chắc chắn với câu trả lời của mình chưa?");
        builder.setPositiveButton("Chắc chắn", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final boolean cauTraLoiDung = button.getText().toString().substring(3).equals(cauHoi.getCauTraLoi());
                final char cauTraLoi = button.getText().toString().charAt(0);
                if (GameSetting.amThanhHieuUng) {
                    selectedButton = button;
                    hideOptions();
                    btnDungCuocChoi.setVisibility(View.GONE);
                    hideAllSupports();
                    if (thuTuCauHoi < 5) {
                        selectedButton = null;
                        stopCounting();
                        if (cauTraLoiDung) {
                            nhapNhayKhiTraLoiDung(button);
                            Toast.makeText(CauHoiActivity.this, cauTraLoi + " là câu trả lời đúng.\r\nBạn được " + helperGame.getDiem(thuTuCauHoi), Toast.LENGTH_SHORT).show();
                            thuTuCauHoi++;
                            cauHoi = helperGame.getCauHoiDe();
                            setCorrectOption(button);
                            playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.question_1_to_4_win, new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    playerSoundEffect.stopPlayer();
                                    showOptions();
                                    show3Supports();
                                    setCauHoi();
                                    btnDungCuocChoi.setVisibility(View.VISIBLE);
                                    startCounting();
                                }
                            });
                        }
                        else {
                            playerTheme.stopPlayer();
                            xuLyThuaCuocVoiNhac(R.raw.question_1_to_5_lose, 0);
                        }
                    }
                    else if (thuTuCauHoi == 5) {
                        stopCounting();
                        playerTheme.stopPlayer();
                        selectedButton = null;
                        if (cauTraLoiDung) {
                            nhapNhayKhiTraLoiDung(button);
                            Toast.makeText(CauHoiActivity.this, cauTraLoi + " là câu trả lời đúng.\r\nBạn được " + helperGame.getDiem(thuTuCauHoi), Toast.LENGTH_SHORT).show();
                            thuTuCauHoi++;
                            cauHoi = helperGame.getCauHoiThuong();
                            setCorrectOption(button);
                            xuLyTraLoiDungVoiNhac(R.raw.question_5_win);
                        }
                        else {
                            xuLyThuaCuocVoiNhac(R.raw.question_1_to_5_lose, 0);
                        }
                    }
                    else if (thuTuCauHoi < 10) {
                        stopCounting();
                        playerTheme.stopPlayer();
                        playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.question_6_to_10_final_answer, new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                playerSoundEffect.stopPlayer();
                                selectedButton = null;
                                if (cauTraLoiDung) {
                                    nhapNhayKhiTraLoiDung(button);
                                    Toast.makeText(CauHoiActivity.this, cauTraLoi + " là câu trả lời đúng.\r\nBạn được " + helperGame.getDiem(thuTuCauHoi), Toast.LENGTH_SHORT).show();
                                    thuTuCauHoi++;
                                    cauHoi = helperGame.getCauHoiThuong();
                                    setCorrectOption(button);
                                    xuLyTraLoiDungVoiNhac(R.raw.question_6_to_9_win);
                                }
                                else {
                                    xuLyThuaCuocVoiNhac(R.raw.question_6_to_10_lose, 5);
                                }
                            }
                        });
                    }
                    else if (thuTuCauHoi == 10) {
                        stopCounting();
                        playerTheme.stopPlayer();
                        playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.question_6_to_10_final_answer, new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                playerSoundEffect.stopPlayer();
                                selectedButton = null;
                                if (cauTraLoiDung) {
                                    nhapNhayKhiTraLoiDung(button);
                                    Toast.makeText(CauHoiActivity.this, cauTraLoi + " là câu trả lời đúng.\r\nBạn được " + helperGame.getDiem(thuTuCauHoi), Toast.LENGTH_SHORT).show();
                                    thuTuCauHoi++;
                                    cauHoi = helperGame.getCauHoiKho();
                                    setCorrectOption(button);
                                    xuLyTraLoiDungVoiNhac(R.raw.question_10_win);
                                }
                                else {
                                    xuLyThuaCuocVoiNhac(R.raw.question_6_to_10_lose, 5);
                                }
                            }
                        });
                    }
                    else if (thuTuCauHoi < 15) {
                        stopCounting();
                        playerTheme.stopPlayer();
                        playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.question_11_to_14_final_answer, new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                playerSoundEffect.stopPlayer();
                                selectedButton = null;
                                if (cauTraLoiDung) {
                                    nhapNhayKhiTraLoiDung(button);
                                    Toast.makeText(CauHoiActivity.this, cauTraLoi + " là câu trả lời đúng.\r\nBạn được " + helperGame.getDiem(thuTuCauHoi), Toast.LENGTH_SHORT).show();
                                    thuTuCauHoi++;
                                    cauHoi = helperGame.getCauHoiKho();
                                    setCorrectOption(button);
                                    xuLyTraLoiDungVoiNhac(R.raw.question_11_to_14_win);
                                }
                                else {
                                    xuLyThuaCuocVoiNhac(R.raw.question_11_to_14_lose, 10);
                                }
                            }
                        });
                    }
                    else if (thuTuCauHoi == 15) {
                        playerTheme.stopPlayer();
                        stopCounting();
                        playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.question_15_final_answer, new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                isKetThuc = true;
                                playerSoundEffect.stopPlayer();
                                selectedButton = null;
                                if (cauTraLoiDung) {
                                    playerTheme.stopPlayer();
                                    final int[] position = new int[1];
                                    final Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    position[0]++;
                                                    if (position[0] % 2 == 1) setCorrectOption(button);
                                                    else setSelectedOption(button);
                                                }
                                            });
                                        }
                                    }, 0, 117);
                                    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
                                        @Override
                                        public void onCompletion(MediaPlayer mediaPlayer) {
                                            timer.cancel();
                                            setCorrectOption(button);
                                            xuLyThangCuoc();
                                        }
                                    };
                                    playerSoundEffect.startPlayerWithOnCompleteListener(R.raw.question_15_win, completionListener);
                                }
                                else {
                                    xuLyThuaCuocVoiNhac(R.raw.question_15_lose, 10);
                                }
                            }
                        });
                    }
                }
                else {
                    if (cauTraLoiDung) {
                        showOptions();
                        if (thuTuCauHoi < 5) {
                            Toast.makeText(CauHoiActivity.this, cauTraLoi + " là câu trả lời đúng.\r\nBạn được " + helperGame.getDiem(thuTuCauHoi), Toast.LENGTH_SHORT).show();
                            thuTuCauHoi++;
                            cauHoi = helperGame.getCauHoiDe();
                            setCauHoi();
                        }
                        else if (thuTuCauHoi < 15) {
                            stopCounting();
                            if (thuTuCauHoi == 5) btnTuVan.setVisibility(View.VISIBLE);
                            Toast.makeText(CauHoiActivity.this, cauTraLoi + " là câu trả lời đúng.\r\nBạn được " + helperGame.getDiem(thuTuCauHoi), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CauHoiActivity.this, LuatChoiTiepTheoActivity.class);
                            intent.putExtra("CAU_HOI", thuTuCauHoi);
                            intent.putExtra("50_50", btn5050.isEnabled());
                            intent.putExtra("GOI_DIEN", btnGoiDien.isEnabled());
                            intent.putExtra("KHAN_GIA", btnKhanGia.isEnabled());
                            intent.putExtra("TU_VAN", btnTuVan.isEnabled());
                            startActivity(intent);
                            thuTuCauHoi++;
                            if (thuTuCauHoi <= 10) cauHoi = helperGame.getCauHoiThuong();
                            else cauHoi = helperGame.getCauHoiKho();
                            setCauHoi();
                        }
                        else {
                            stopCounting();
                            isKetThuc = true;
                            setCorrectOption(button);
                            xuLyThangCuoc();
                        }
                    }
                    else {
                        stopCounting();
                        isKetThuc = true;
                        int mocCauHoi;
                        playerTheme.stopPlayer();
                        if (thuTuCauHoi > 10) mocCauHoi = 10;
                        else if (thuTuCauHoi > 5) mocCauHoi = 5;
                        else mocCauHoi = 0;
                        Button btnDapAnDung = getButtonDapAnDung();
                        setCorrectOption(btnDapAnDung);
                        xuLyThuaCuoc(mocCauHoi);
                        Toast.makeText(CauHoiActivity.this, "Rất tiếc, bạn đã trả lời không chính xác.", Toast.LENGTH_LONG).show();
                    }
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setOriginalOption(button);
                dialogInterface.cancel();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                setOriginalOption(button);
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

    private void nhapNhayKhiTraLoiDung(final Button button) {
        final int[] position = new int[1];
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        position[0]++;
                        if (position[0] % 2 == 1) setCorrectOption(button);
                        else setSelectedOption(button);
                        if (position[0] == 15) {
                            setCorrectOption(button);
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, 117);
    }

    private void setSelectedOption(Button button) {
        button.setBackgroundResource(R.drawable.selected_option);
        button.setTextColor(Color.BLACK);
    }

    private void show3Supports() {
        btn5050.setVisibility(View.VISIBLE);
        btnGoiDien.setVisibility(View.VISIBLE);
        btnKhanGia.setVisibility(View.VISIBLE);
    }

    private void hideAllSupports() {
        btn5050.setVisibility(View.GONE);
        btnGoiDien.setVisibility(View.GONE);
        btnKhanGia.setVisibility(View.GONE);
        btnTuVan.setVisibility(View.GONE);
    }

    private void xuLyTraLoiDungVoiNhac(int musicEffectResource) {
        playerSoundEffect.startPlayerWithOnCompleteListener(musicEffectResource, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playerSoundEffect.stopPlayer();
                Intent intent = new Intent(CauHoiActivity.this, LuatChoiTiepTheoActivity.class);
                intent.putExtra("CAU_HOI", thuTuCauHoi - 1);
                intent.putExtra("50_50", btn5050.isEnabled());
                intent.putExtra("GOI_DIEN", btnGoiDien.isEnabled());
                intent.putExtra("KHAN_GIA", btnKhanGia.isEnabled());
                intent.putExtra("TU_VAN", btnTuVan.isEnabled());
                startActivity(intent);
                btnDungCuocChoi.setVisibility(View.VISIBLE);
                showAllSupports();
                showOptions();
                setCauHoi();
            }
        });
    }

    private void showAllSupports() {
        btn5050.setVisibility(View.VISIBLE);
        btnGoiDien.setVisibility(View.VISIBLE);
        btnKhanGia.setVisibility(View.VISIBLE);
        btnTuVan.setVisibility(View.VISIBLE);
    }

    private void xuLyThuaCuocVoiNhac(int musicEffectResource, final int mocCauHoi) {
        stopCounting();
        selectedButton = null;
        isKetThuc = true;
        final Button btnDapAnDung = getButtonDapAnDung();
        nhapNhayKhiTraLoiSai(btnDapAnDung);
        setCorrectOption(btnDapAnDung);
        playerSoundEffect.startPlayerWithOnCompleteListener(musicEffectResource, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playerSoundEffect.stopPlayer();
                xuLyThuaCuoc(mocCauHoi);
            }
        });
    }

    private void nhapNhayKhiTraLoiSai(final Button button) {
        final Timer timer = new Timer();
        final int[] position = {0};
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        position[0]++;
                        if (position[0] % 2 == 1) setCorrectOption(button);
                        else setOriginalOption(button);
                        if (position[0] == 13) {
                            setCorrectOption(button);
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, 117);
    }

    private void xuLyThuaCuoc(int mocCauHoi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CauHoiActivity.this);
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.dialog_ket_thuc, null);
        final EditText txtHoTen = view.findViewById(R.id.txtHoTen);
        TextView txtPhanThuong = view.findViewById(R.id.txtPhanThuong);
        TextView txtDapAnDung = view.findViewById(R.id.txtDapAnDung);
        Button btnThoat = view.findViewById(R.id.btnThoat);
        Button btnXacNhan = view.findViewById(R.id.btnXacNhan);
        final double diemThuong = helperGame.getDiem(mocCauHoi);
        txtPhanThuong.setText("Phần thưởng: " + diemThuong);
        txtDapAnDung.setText("Đáp án đúng: " + getButtonDapAnDung().getText().toString());
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                finish();
            }
        });
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helperScore.insertDiem(txtHoTen.getText().toString(), diemThuong, thuTuCauHoi, simpleDateFormat.format(calendar.getTime()));
                Toast.makeText(CauHoiActivity.this, "Lưu điểm thành công.", Toast.LENGTH_LONG).show();
                dialog.cancel();
                finish();
            }
        });
        dialog.show();
    }

    private Button getButtonDapAnDung() {
        if (!btnDapAnA.getText().toString().equals("")) if (btnDapAnA.getText().toString().substring(3).equals(cauHoi.getCauTraLoi())) return btnDapAnA;
        if (!btnDapAnB.getText().toString().equals("")) if (btnDapAnB.getText().toString().substring(3).equals(cauHoi.getCauTraLoi())) return btnDapAnB;
        if (!btnDapAnC.getText().toString().equals("")) if (btnDapAnC.getText().toString().substring(3).equals(cauHoi.getCauTraLoi())) return btnDapAnC;
        if (!btnDapAnD.getText().toString().equals("")) if (btnDapAnD.getText().toString().substring(3).equals(cauHoi.getCauTraLoi())) return btnDapAnD;
        return null;
    }

    private void xuLyThangCuoc() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CauHoiActivity.this);
        builder.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.dialog_thang_cuoc, null);
        final EditText txtHoTen = view.findViewById(R.id.txtHoTen);
        TextView txtPhanThuong = view.findViewById(R.id.txtPhanThuong);
        Button btnThoat = view.findViewById(R.id.btnThoat);
        Button btnXacNhan = view.findViewById(R.id.btnXacNhan);
        final double diemThuong = helperGame.getDiem(15);
        txtPhanThuong.setText("Phần thưởng: " + diemThuong);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                finish();
            }
        });
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helperScore.insertDiem(txtHoTen.getText().toString(), diemThuong, 15, simpleDateFormat.format(calendar.getTime()));
                Toast.makeText(CauHoiActivity.this, "Lưu điểm thành công.", Toast.LENGTH_LONG).show();
                dialog.cancel();
                finish();
            }
        });
        dialog.show();
    }
    
    private void showOptions() {
        if (!btnDapAnA.isEnabled()) btnDapAnA.setEnabled(true);
        if (!btnDapAnB.isEnabled()) btnDapAnB.setEnabled(true);
        if (!btnDapAnC.isEnabled()) btnDapAnC.setEnabled(true);
        if (!btnDapAnD.isEnabled()) btnDapAnD.setEnabled(true);
    }

    private void hideOptions() {
        if (btnDapAnA.isEnabled()) btnDapAnA.setEnabled(false);
        if (btnDapAnB.isEnabled()) btnDapAnB.setEnabled(false);
        if (btnDapAnC.isEnabled()) btnDapAnC.setEnabled(false);
        if (btnDapAnD.isEnabled()) btnDapAnD.setEnabled(false);
    }

    private void addControls() {
        btn5050 = findViewById(R.id.btn5050);
        btnGoiDien = findViewById(R.id.btnGoiDien);
        btnKhanGia = findViewById(R.id.btnKhanGia);
        btnTuVan = findViewById(R.id.btnTuVan);
        txtThoiGian = findViewById(R.id.txtThoiGian);
        txtCauHoi = findViewById(R.id.txtCauHoi);
        txtNoiDungCauHoi = findViewById(R.id.txtNoiDungCauHoi);
        btnDapAnA = findViewById(R.id.btnDapAnA);
        btnDapAnB = findViewById(R.id.btnDapAnB);
        btnDapAnC = findViewById(R.id.btnDapAnC);
        btnDapAnD = findViewById(R.id.btnDapAnD);
        btnDungCuocChoi = findViewById(R.id.btnDungCuocChoi);
        helperGame = new GamingDatabaseHelper(CauHoiActivity.this);
        helperScore = new ScoreDatabaseHelper(CauHoiActivity.this);
        playerTheme = new SoundTrackPlayer(CauHoiActivity.this);
        playerSoundEffect = new EffectPlayer(CauHoiActivity.this);
        playerEffect = new EffectPlayer(CauHoiActivity.this);
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    }

    @Override
    protected void onPause() {
        stopCounting();
        playerTheme.stopPlayer();
        playerEffect.stopPlayer();
        playerSoundEffect.pausePlayer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCounting();
        if (selectedSupport != btnGoiDien && selectedSupport != btnKhanGia) playSelectedThemeMusic();
        playerSoundEffect.continuePlayer();
    }

    private void playSelectedThemeMusic() {
        if (thuTuCauHoi <= 5) playerTheme.startPlayerNonStop(R.raw.question_1_to_5);
        else if (thuTuCauHoi <= 10) playerTheme.startPlayerNonStop(R.raw.question_6_to_10);
        else if (thuTuCauHoi < 15) playerTheme.startPlayerNonStop(R.raw.question_11_to_14);
        else if (thuTuCauHoi == 15) playerTheme.startPlayerNonStop(R.raw.question_15);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopCounting();
    }

    @Override
    protected void onDestroy() {
        stopCounting();
        if (selectedButton != null) {
            if (selectedButton.getText().toString().substring(3).equals(cauHoi.getCauTraLoi())) {
                thuTuCauHoi++;
                if (thuTuCauHoi <= 5) cauHoi = helperGame.getCauHoiDe();
                else if (thuTuCauHoi <= 10) cauHoi = helperGame.getCauHoiThuong();
                else if (thuTuCauHoi <= 15) cauHoi = helperGame.getCauHoiKho();
                else isKetThuc = true;
            } else {
                isKetThuc = true;
            }
        }

        if (isKetThuc) {
            SharedPreferences preferences = getSharedPreferences(tenLuuTru, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("CAU_HOI");
            editor.remove("ID");
            editor.remove("NOI_DUNG");
            editor.remove("DAP_AN_A");
            editor.remove("DAP_AN_B");
            editor.remove("DAP_AN_C");
            editor.remove("DAP_AN_D");
            editor.remove("CAU_TRA_LOI");
            editor.remove("50_50");
            editor.remove("GOI_DIEN");
            editor.remove("KHAN_GIA");
            editor.remove("TU_VAN");
            editor.remove("NGUOI_GOI");
            editor.remove("AN_NUT_TRO_GIUP");
            editor.commit();
        }
        else {
            SharedPreferences preferences = getSharedPreferences(tenLuuTru, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("CAU_HOI", thuTuCauHoi);
            editor.putInt("ID", cauHoi.getId());
            editor.putString("NOI_DUNG", cauHoi.getNoiDung());
            editor.putString("DAP_AN_A", cauHoi.getDapAnA());
            editor.putString("DAP_AN_B", cauHoi.getDapAnB());
            editor.putString("DAP_AN_C", cauHoi.getDapAnC());
            editor.putString("DAP_AN_D", cauHoi.getDapAnD());
            editor.putString("CAU_TRA_LOI", cauHoi.getCauTraLoi());
            editor.putBoolean("50_50", btn5050.isEnabled());
            editor.putBoolean("GOI_DIEN", btnGoiDien.isEnabled());
            editor.putBoolean("KHAN_GIA", btnKhanGia.isEnabled());
            editor.putBoolean("TU_VAN", btnTuVan.isEnabled());
            if (selectedSupport == btn5050) {
                editor.remove("NGUOI_GOI");
                editor.putString("AN_NUT_TRO_GIUP", "50_50");
            }
            else if (selectedSupport == btnGoiDien) {
                editor.putString("NGUOI_GOI", nguoiCanGoi);
                editor.putString("AN_NUT_TRO_GIUP", "GOI_DIEN");
            }
            else if (selectedSupport == btnKhanGia) {
                editor.remove("NGUOI_GOI");
                editor.putString("AN_NUT_TRO_GIUP", "KHAN_GIA");
            }
            else if (selectedSupport == btnTuVan) {
                editor.remove("NGUOI_GOI");
                editor.putString("AN_NUT_TRO_GIUP", "TU_VAN");
            }
            else {
                editor.remove("NGUOI_GOI");
                editor.remove("AN_NUT_TRO_GIUP");
            }
            StringBuilder chuoiCauHoi = new StringBuilder("");
            if (thuTuCauHoi <= 5) {
                for (Integer x : helperGame.getCauHoiDeDaLay()) chuoiCauHoi.append(x + " ");
            }
            else if (thuTuCauHoi <= 10) {
                for (Integer x : helperGame.getCauHoiThuongDaLay()) chuoiCauHoi.append(x + " ");
            }
            else {
                for (Integer x : helperGame.getCauHoiKhoDaLay()) chuoiCauHoi.append(x + " ");
            }
            editor.putString("CHUOI_CAU_HOI", chuoiCauHoi.toString().trim());
            editor.putInt("HOUR", calendar.get(Calendar.HOUR));
            editor.putInt("MINUTE", calendar.get(Calendar.MINUTE));
            editor.putInt("SECOND", calendar.get(Calendar.SECOND));
            editor.putInt("MILLISECOND", calendar.get(Calendar.MILLISECOND));
            editor.putInt("AM_PM", calendar.get(Calendar.AM_PM));
            editor.commit();
        }
        super.onDestroy();
    }
}