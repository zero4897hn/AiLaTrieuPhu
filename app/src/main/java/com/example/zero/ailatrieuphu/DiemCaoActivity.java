package com.example.zero.ailatrieuphu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.zero.adapter.DiemCaoAdapter;
import com.example.zero.media.SoundTrackPlayer;
import com.example.zero.model.NguoiChoi;
import com.example.zero.sqlite.ScoreDatabaseHelper;

import java.util.ArrayList;

public class DiemCaoActivity extends AppCompatActivity {
    ListView lvDiemCao;
    ArrayList<NguoiChoi> dsNguoiChoi;
    DiemCaoAdapter adapter;
    ScoreDatabaseHelper helper;
    Button btnXoaHet;
    SoundTrackPlayer playerTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diem_cao);
        addControls();
        addEvents();
        playerTheme.startPlayerNonStop(R.raw.high_score);
    }

    private void addEvents() {
       btnXoaHet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.xoaHetDiemCao();
                dsNguoiChoi.removeAll(dsNguoiChoi);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void addControls() {
        helper = new ScoreDatabaseHelper(DiemCaoActivity.this);
        btnXoaHet = findViewById(R.id.btnXoaHet);
        lvDiemCao = findViewById(R.id.lvDiemCao);
        dsNguoiChoi = helper.getDanhSachNguoiChoi();
        adapter = new DiemCaoAdapter(DiemCaoActivity.this, R.layout.item_diem_cao, dsNguoiChoi);
        lvDiemCao.setAdapter(adapter);
        playerTheme = new SoundTrackPlayer(DiemCaoActivity.this);
    }

    @Override
    protected void onPause() {
        playerTheme.stopPlayer();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerTheme.startPlayerNonStop(R.raw.high_score);
    }
}
