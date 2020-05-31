package com.aldidwikip.kontak.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aldidwikip.kontak.EditActivity;
import com.aldidwikip.kontak.MainActivity;
import com.aldidwikip.kontak.R;
import com.bumptech.glide.Glide;

public class CustomDialog extends DialogFragment implements View.OnClickListener {

    private String idKontak, namaKontak, alamatKontak, nomorKontak, avatarKontak;

    public static CustomDialog newInstance(String idKontak, String namaKontak, String nomorKontak,
                                           String alamatKontak, String avatarKontak) {

        CustomDialog customDialog = new CustomDialog();
        Bundle args = new Bundle();
        args.putString("idKontak", idKontak);
        args.putString("namaKontak", namaKontak);
        args.putString("nomorKontak", nomorKontak);
        args.putString("alamatKontak", alamatKontak);
        args.putString("avatarKontak", avatarKontak);
        customDialog.setArguments(args);

        return customDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.custom_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idKontak = getArguments().getString("idKontak");
        namaKontak = getArguments().getString("namaKontak");
        nomorKontak = getArguments().getString("nomorKontak");
        alamatKontak = getArguments().getString("alamatKontak");
        avatarKontak = getArguments().getString("avatarKontak");

        ImageView avatarImg, iconDelete, iconEdit;
        avatarImg = view.findViewById(R.id.avatarDialog);
        Glide.with(this)
                .load(avatarKontak)
                .centerCrop()
                .placeholder(R.drawable.ic_person_24dp)
                .into(avatarImg);

        iconDelete = view.findViewById(R.id.iconDelete);
        iconEdit = view.findViewById(R.id.iconEdit);
        iconDelete.setOnClickListener(this);
        iconEdit.setOnClickListener(this);

        TextView tvNamaDialog = view.findViewById(R.id.tvNamaDialog);
        tvNamaDialog.setText(namaKontak);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iconDelete:
                MainActivity.ma.deleteKontak(idKontak);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.ma.refresh();
                    }
                }, 1000);
                break;
            case R.id.iconEdit:
                Intent intent = new Intent(v.getContext(), EditActivity.class);
                intent.putExtra("Id", idKontak);
                intent.putExtra("Nama", namaKontak);
                intent.putExtra("Nomor", nomorKontak);
                intent.putExtra("Alamat", alamatKontak);
                intent.putExtra("Avatar", avatarKontak);
                v.getContext().startActivity(intent);
                break;
            default:
                break;
        }
        dismiss();
    }
}
