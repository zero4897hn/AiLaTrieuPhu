package com.example.zero.ailatrieuphu;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zero.media.EffectPlayer;
import com.example.zero.media.SoundTrackPlayer;
import com.example.zero.setting.GameSetting;

import java.util.Timer;
import java.util.TimerTask;

public class LuatChoiActivity extends AppCompatActivity {
    TextView txtCau15, txtCau10, txtCau05;
    TextView txtNoiDung;
    ImageButton btn5050, btnGoiDien, btnKhanGia;
    Button btnChuyenTiep, btnChoiLuon;
    SoundTrackPlayer playerTheme;
    EffectPlayer playerVoices, playerMain;
    Timer timer;
    int page = 1;
    int imagePosition;
    boolean isStarted = false;
    Animation animation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luat_choi);
        addControls();
        addEvents();
        playerTheme.startPlayerNonStop(R.raw.explain_the_rules);
        setTextOnChatBox(1);
    }

    private void setTextOnChatBox(int page) {
        switch (page) {
            case 1:
                txtCau05.setBackgroundResource(0);
                txtCau10.setBackgroundResource(0);
                txtCau15.setBackgroundResource(0);
                if (timer != null) timer.cancel();
                playerVoices.stopPlayer();
                playerVoices.startPlayerAtOnce(R.raw.explain_the_rules_page_1_voices);
                btnChuyenTiep.setText("Chuyển tiếp");
                btnChoiLuon.setText("Chơi luôn");
                txtNoiDung.setText("Có tất cả 15 câu hỏi đang chờ đợi bạn.");
                break;
            case 2:
                txtCau05.setBackgroundResource(0);
                txtCau10.setBackgroundResource(0);
                txtCau15.setBackgroundResource(0);
                if (timer != null) timer.cancel();
                playerVoices.stopPlayer();
                playerVoices.startPlayerAtOnce(R.raw.explain_the_rules_page_2_voices);
                btnChuyenTiep.setText("Chuyển tiếp");
                btnChoiLuon.setText("Chơi luôn");
                txtNoiDung.setText("Và có 3 mốc quan trọng cần phải vượt qua, đó là 5, 10 và 15.");
                if (GameSetting.amThanhHieuUng) {
                    timer = new Timer();
                    imagePosition = 0;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imagePosition++;
                                    if (imagePosition == 1) {
                                        txtCau05.setBackgroundResource(R.drawable.selected_option);
                                        txtCau10.setBackgroundResource(0);
                                        txtCau15.setBackgroundResource(0);
                                    }
                                    else if (imagePosition == 2) {
                                        txtCau05.setBackgroundResource(0);
                                        txtCau10.setBackgroundResource(R.drawable.selected_option);
                                        txtCau15.setBackgroundResource(0);
                                    }
                                    else {
                                        txtCau05.setBackgroundResource(0);
                                        txtCau10.setBackgroundResource(0);
                                        txtCau15.setBackgroundResource(R.drawable.selected_option);
                                        imagePosition = 0;
                                        timer.cancel();
                                    }
                                }
                            });
                        }
                    }, 1800, 300);
                }
                else {
                    txtCau05.setBackgroundResource(R.drawable.selected_option);
                    txtCau10.setBackgroundResource(R.drawable.selected_option);
                    txtCau15.setBackgroundResource(R.drawable.selected_option);
                }
                break;
            case 3:
                txtCau05.setBackgroundResource(0);
                txtCau10.setBackgroundResource(0);
                txtCau15.setBackgroundResource(0);
                if (timer != null) timer.cancel();
                playerVoices.stopPlayer();
                playerVoices.startPlayerAtOnce(R.raw.explain_the_rules_page_3_voices);
                btnChuyenTiep.setText("Chuyển tiếp");
                btnChoiLuon.setText("Chơi luôn");
                txtNoiDung.setText("Bạn cũng như những người chơi khác đến với chúng tôi, có 3 sự trợ giúp, đó là 50 : 50, Gọi điện thoại cho người thân, hoặc Hỏi ý kiến khán giả trong trường quay.");
                if (GameSetting.amThanhHieuUng) {
                    animation = AnimationUtils.loadAnimation(LuatChoiActivity.this, R.anim.scale_button);
                    timer = new Timer();
                    imagePosition = 0;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imagePosition++;
                                    if (imagePosition == 1) btn5050.startAnimation(animation);
                                    else if (imagePosition == 2) btnGoiDien.startAnimation(animation);
                                    else {
                                        btnKhanGia.startAnimation(animation);
                                        imagePosition = 0;
                                        timer.cancel();
                                    }
                                }
                            });
                        }
                    }, 3200, 1300);
                }
                break;
            case 4:
                txtCau05.setBackgroundResource(0);
                txtCau10.setBackgroundResource(0);
                txtCau15.setBackgroundResource(0);
                if (timer != null) timer.cancel();
                playerVoices.stopPlayer();
                playerVoices.startPlayerAtOnce(R.raw.explain_the_rules_page_4_voices);
                btnChuyenTiep.setText("Chưa rõ");
                btnChoiLuon.setText("Tôi đã rõ");
                txtNoiDung.setText("Bạn đã nắm rõ luật của chương trình chưa?");
                break;
        }
    }

    private void addEvents() {
        btnChuyenTiep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                if (page > 4) page = 1;
                setTextOnChatBox(page);
            }
        });
        btnChoiLuon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null) timer.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(LuatChoiActivity.this);
                builder.setTitle("Xác nhận chơi");
                builder.setMessage("Bạn đã sẵn sàng chơi với chúng tôi chưa?");
                builder.setPositiveButton("Đã sẵn sàng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        playerVoices.stopPlayer();
                        playerTheme.stopPlayer();
                        if (GameSetting.amThanhHieuUng) {
                            btnChoiLuon.setEnabled(false);
                            btnChuyenTiep.setEnabled(false);
                            txtNoiDung.setText("Người chơi đã sẵn sàng, và chúng ta bắt đầu đi tìm Ai là triệu phú.");
                            isStarted = true;
                            playerMain.startPlayerWithOnCompleteListener(R.raw.ready_to_play, new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    playerMain.stopPlayer();
                                    onBackPressed();
                                    Intent intent = new Intent(LuatChoiActivity.this, CauHoiActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(LuatChoiActivity.this, "Người chơi đã sẵn sàng, và chúng ta bắt đầu đi tìm Ai là triệu phú.", Toast.LENGTH_LONG).show();
                            onBackPressed();
                            Intent intent = new Intent(LuatChoiActivity.this, CauHoiActivity.class);
                            startActivity(intent);
                        }
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("Chưa sẵn sàng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private void addControls() {
        btn5050 = findViewById(R.id.btn5050);
        btnGoiDien = findViewById(R.id.btnGoiDien);
        btnKhanGia = findViewById(R.id.btnKhanGia);
        txtCau05 = findViewById(R.id.txtCau05);
        txtCau10 = findViewById(R.id.txtCau10);
        txtCau15 = findViewById(R.id.txtCau15);
        txtNoiDung = findViewById(R.id.txtNoiDung);
        btnChuyenTiep = findViewById(R.id.btnChuyenTiep);
        btnChoiLuon = findViewById(R.id.btnChoiLuon);
        playerTheme = new SoundTrackPlayer(LuatChoiActivity.this);
        playerVoices = new EffectPlayer(LuatChoiActivity.this);
        playerMain = new EffectPlayer(LuatChoiActivity.this);
    }

    @Override
    protected void onPause() {
        playerTheme.stopPlayer();
        playerVoices.stopPlayer();
        playerMain.pausePlayer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerTheme.startPlayerNonStop(R.raw.explain_the_rules);
        playerMain.continuePlayer();
    }
}
