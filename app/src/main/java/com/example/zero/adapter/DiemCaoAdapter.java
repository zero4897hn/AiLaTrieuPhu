package com.example.zero.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.zero.ailatrieuphu.R;
import com.example.zero.model.NguoiChoi;

import java.util.List;

public class DiemCaoAdapter extends ArrayAdapter<NguoiChoi> {
    Activity context;
    int resource;
    List<NguoiChoi> objects;

    public DiemCaoAdapter(@NonNull Activity context, int resource, @NonNull List<NguoiChoi> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(resource, null);
        TextView txtThuTu = convertView.findViewById(R.id.txtThuTu);
        TextView txtTen = convertView.findViewById(R.id.txtTen);
        TextView txtDiem = convertView.findViewById(R.id.txtDiem);
        TextView txtCauHoi = convertView.findViewById(R.id.txtCauHoi);
        TextView txtThoiGian = convertView.findViewById(R.id.txtThoiGian);
        NguoiChoi nguoiChoi = objects.get(position);
        txtThuTu.setText((position + 1) + "");
        txtThoiGian.setText(nguoiChoi.getThoiGian());
        txtTen.setText("Họ và tên: " + nguoiChoi.getTen());
        txtDiem.setText("Điểm: " + nguoiChoi.getDiem());
        txtCauHoi.setText("Câu hỏi: " + nguoiChoi.getThuTuCauHoi());
        return convertView;
    }
}
