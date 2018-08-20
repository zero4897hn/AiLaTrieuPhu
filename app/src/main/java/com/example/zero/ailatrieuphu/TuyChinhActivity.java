package com.example.zero.ailatrieuphu;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.zero.media.SoundTrackPlayer;
import com.example.zero.setting.GameSetting;

public class TuyChinhActivity extends AppCompatActivity {
    Switch aSwitchAmThanhNen, aSwitchAmThanhHieuUng;
    SoundTrackPlayer playerTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuy_chinh);
        addControls();
        addEvents();
        playThemeMusic();
    }

    private void addEvents() {
        aSwitchAmThanhNen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    GameSetting.amThanhNen = true;
                    playThemeMusic();
                }
                else {
                    GameSetting.amThanhNen = false;
                    stopThemeMusic();
                }
            }
        });
    }

    private void playThemeMusic() {
        playerTheme.startPlayerNonStop(R.raw.setting);
    }

    private void addControls() {
        aSwitchAmThanhNen = findViewById(R.id.switchAmThanhNen);
        aSwitchAmThanhHieuUng = findViewById(R.id.switchAmThanhHieuUng);
        aSwitchAmThanhHieuUng.setChecked(GameSetting.amThanhHieuUng);
        aSwitchAmThanhNen.setChecked(GameSetting.amThanhNen);
        playerTheme = new SoundTrackPlayer(TuyChinhActivity.this);
    }

    @Override
    public void onBackPressed() {
        boolean amThanhNen = aSwitchAmThanhNen.isChecked();
        boolean amThanhHieuUng = aSwitchAmThanhHieuUng.isChecked();
        GameSetting.amThanhNen = amThanhNen;
        GameSetting.amThanhHieuUng = amThanhHieuUng;

        SharedPreferences preferences = getSharedPreferences(GameSetting.tenLuuTru, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("AM_THANH_NEN", amThanhNen);
        editor.putBoolean("AM_THANH_HIEU_UNG", amThanhHieuUng);
        editor.commit();

        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        stopThemeMusic();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playThemeMusic();
    }

    private void stopThemeMusic() {
        playerTheme.stopPlayer();
    }
}
