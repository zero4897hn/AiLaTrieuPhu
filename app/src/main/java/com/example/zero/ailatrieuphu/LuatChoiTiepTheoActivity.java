package com.example.zero.ailatrieuphu;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.zero.media.EffectPlayer;
import com.example.zero.media.SoundTrackPlayer;
import com.example.zero.setting.GameSetting;
import com.example.zero.sqlite.GamingDatabaseHelper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LuatChoiTiepTheoActivity extends AppCompatActivity {
    TextView txtCau14, txtCau13, txtCau12, txtCau11, txtCau10, txtCau09, txtCau08, txtCau07, txtCau06, txtCau05;
    TextView txtNoiDung;
    ImageButton btn5050, btnGoiDien, btnKhanGia, btnTuVan;
    Button btnChuyenTiep, btnTiepTucChoi;
    GamingDatabaseHelper helper;
    int page = 1;
    int thuTuCauHoi;
    EffectPlayer playerTheme, playerMC;
    Timer timer;
    Animation animation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luat_choi_tiep_theo);
        addControls();
        addEvents();
    }

    private void addEvents() {
        Intent intent = getIntent();
        btn5050.setEnabled(intent.getBooleanExtra("50_50", true));
        btnGoiDien.setEnabled(intent.getBooleanExtra("GOI_DIEN", true));
        btnKhanGia.setEnabled(intent.getBooleanExtra("KHAN_GIA", true));
        btnTuVan.setEnabled(intent.getBooleanExtra("TU_VAN", true));
        if (!btn5050.isEnabled()) btn5050.setImageResource(R.drawable.support_1_used);
        if (!btnGoiDien.isEnabled()) btnGoiDien.setImageResource(R.drawable.support_2_used);
        if (!btnKhanGia.isEnabled()) btnKhanGia.setImageResource(R.drawable.support_3_used);
        if (!btnTuVan.isEnabled()) btnTuVan.setImageResource(R.drawable.support_4_used);
        final ArrayList<Boolean> dsTroGiup = new ArrayList<>();
        dsTroGiup.add(btn5050.isEnabled());
        dsTroGiup.add(btnGoiDien.isEnabled());
        dsTroGiup.add(btnKhanGia.isEnabled());
        thuTuCauHoi = intent.getIntExtra("CAU_HOI", 0);
        if (thuTuCauHoi != 5) dsTroGiup.add(btnTuVan.isEnabled());
        pointQuestion(thuTuCauHoi);
        if (thuTuCauHoi == 5) {
            btnTuVan.setVisibility(View.GONE);
        }
        else btnTuVan.setVisibility(View.VISIBLE);
        if (thuTuCauHoi == 5 || thuTuCauHoi == 10) playerTheme.startPlayerAtOnce(R.raw.before_play_next_question_6_and_11);
        else playerTheme.startPlayerAtOnce(R.raw.before_play_next_question_7_to_10_and_12_to_15);
        final double tienThuong = helper.getDiem(thuTuCauHoi);
        final double tienThuongCauHoiTiep = helper.getDiem(thuTuCauHoi + 1);
        txtNoiDung.setText("Như vậy là bạn đã trả lời xong câu số " + thuTuCauHoi + " với mức tiền thưởng là " + tienThuong);
        btnChuyenTiep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page++;
                if (page == 1) {
                    if (thuTuCauHoi == 5) btnTuVan.setVisibility(View.GONE);
                    else btnTuVan.setVisibility(View.VISIBLE);
                    playerMC.stopPlayer();
                    if (timer != null) timer.cancel();
                    txtNoiDung.setText("Như vậy là bạn đã trả lời xong câu số " + thuTuCauHoi + " với mức tiền thưởng là " + tienThuong);
                }
                else if (page == 2) {
                    if (thuTuCauHoi == 5) btnTuVan.setVisibility(View.GONE);
                    else btnTuVan.setVisibility(View.VISIBLE);
                    playerMC.stopPlayer();
                    if (timer != null) timer.cancel();
                    txtNoiDung.setText("Trả lời đúng câu số " + (thuTuCauHoi + 1) + ", mức tiền thưởng sẽ là " + tienThuongCauHoiTiep);
                }
                else if (page == 3) {
                    if (timer != null) timer.cancel();
                    int tongTroGiup = 0;
                    for (Boolean x : dsTroGiup) if (x.booleanValue()) tongTroGiup++;
                    if (thuTuCauHoi == 5) btnTuVan.setVisibility(View.GONE);
                    else btnTuVan.setVisibility(View.VISIBLE);
                    playerMC.stopPlayer();
                    if (tongTroGiup > 0) txtNoiDung.setText("Bạn vẫn còn " + tongTroGiup + " sự trợ giúp.");
                    else txtNoiDung.setText("Bạn đã hết sự trợ giúp.");
                }
                else {
                    if (thuTuCauHoi == 5) btnTuVan.setVisibility(View.GONE);
                    else btnTuVan.setVisibility(View.VISIBLE);
                    playerMC.stopPlayer();
                    if (timer != null) timer.cancel();
                    if (thuTuCauHoi == 5) {
                        playerMC.startPlayerAtOnce(R.raw.mc_voices_question_6);
                        if (GameSetting.amThanhHieuUng) {
                            animation = AnimationUtils.loadAnimation(LuatChoiTiepTheoActivity.this, R.anim.scale_button);
                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            btnTuVan.setVisibility(View.VISIBLE);
                                            btnTuVan.startAnimation(animation);
                                            timer.cancel();
                                        }
                                    });
                                }
                            }, 2604, 6000);
                        }
                        else btnTuVan.setVisibility(View.VISIBLE);
                        txtNoiDung.setText("Và bắt đầu từ câu số 6, bạn sẽ có thêm một sự trợ giúp nữa, là tư vấn tại chỗ.");
                        page = 0;
                    }
                    else {
                        if (timer != null) timer.cancel();
                        page = 1;
                        txtNoiDung.setText("Như vậy là bạn đã trả lời xong câu số " + thuTuCauHoi + " với mức tiền thưởng là " + tienThuong);
                    }
                }
            }
        });
        btnTiepTucChoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void pointQuestion(int thuTuCauHoi) {
        switch (thuTuCauHoi) {
            case 5: txtCau05.setBackgroundResource(R.drawable.selected_option); break;
            case 6:
                txtCau06.setTextColor(Color.BLACK);
                txtCau06.setBackgroundResource(R.drawable.selected_option);
                break;
            case 7:
                txtCau07.setTextColor(Color.BLACK);
                txtCau07.setBackgroundResource(R.drawable.selected_option);
                break;
            case 8:
                txtCau08.setTextColor(Color.BLACK);
                txtCau08.setBackgroundResource(R.drawable.selected_option);
                break;
            case 9:
                txtCau09.setTextColor(Color.BLACK);
                txtCau09.setBackgroundResource(R.drawable.selected_option);
                break;
            case 10: txtCau10.setBackgroundResource(R.drawable.selected_option); break;
            case 11:
                txtCau11.setTextColor(Color.BLACK);
                txtCau11.setBackgroundResource(R.drawable.selected_option);
                break;
            case 12:
                txtCau12.setTextColor(Color.BLACK);
                txtCau12.setBackgroundResource(R.drawable.selected_option);
                break;
            case 13:
                txtCau13.setTextColor(Color.BLACK);
                txtCau13.setBackgroundResource(R.drawable.selected_option);
                break;
            case 14:
                txtCau14.setTextColor(Color.BLACK);
                txtCau14.setBackgroundResource(R.drawable.selected_option);
                break;
        }
    }

    @Override
    protected void onPause() {
        playerMC.stopPlayer();
        playerTheme.stopPlayer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        playerMC.stopPlayer();
        super.onBackPressed();
    }

    private void addControls() {
        btn5050 = findViewById(R.id.btn5050);
        btnGoiDien = findViewById(R.id.btnGoiDien);
        btnKhanGia = findViewById(R.id.btnKhanGia);
        btnTuVan = findViewById(R.id.btnTuVan);
        txtCau05 = findViewById(R.id.txtCau05);
        txtCau06 = findViewById(R.id.txtCau06);
        txtCau07 = findViewById(R.id.txtCau07);
        txtCau08 = findViewById(R.id.txtCau08);
        txtCau09 = findViewById(R.id.txtCau09);
        txtCau10 = findViewById(R.id.txtCau10);
        txtCau11 = findViewById(R.id.txtCau11);
        txtCau12 = findViewById(R.id.txtCau12);
        txtCau13 = findViewById(R.id.txtCau13);
        txtCau14 = findViewById(R.id.txtCau14);
        txtNoiDung = findViewById(R.id.txtNoiDung);
        btnChuyenTiep = findViewById(R.id.btnChuyenTiep);
        btnTiepTucChoi = findViewById(R.id.btnTiepTucChoi);
        helper = new GamingDatabaseHelper(LuatChoiTiepTheoActivity.this);
        playerTheme = new EffectPlayer(LuatChoiTiepTheoActivity.this);
        playerMC = new EffectPlayer(LuatChoiTiepTheoActivity.this);
    }
}
