package com.aldidwikip.kontak.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.aldidwikip.kontak.EditActivity;
import com.aldidwikip.kontak.MainActivity;
import com.aldidwikip.kontak.R;
import com.bumptech.glide.Glide;

public class CustomDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private String idKontak, namaKontak, alamatKontak, nomorKontak, avatarKontak;

    public CustomDialog(Context context, String idKontak, String namaKontak, String nomorKontak,
                        String alamatKontak, String avatarKontak) {
        super(context);
        this.context = context;
        this.idKontak = idKontak;
        this.namaKontak = namaKontak;
        this.nomorKontak = nomorKontak;
        this.alamatKontak = alamatKontak;
        this.avatarKontak = avatarKontak;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        ImageView avatarImg, iconDelete, iconEdit;
        avatarImg = findViewById(R.id.avatarDialog);
        Glide.with(context)
                .load(avatarKontak)
                .centerCrop()
                .placeholder(R.drawable.ic_person_24dp)
                .into(avatarImg);

        iconDelete = findViewById(R.id.iconDelete);
        iconEdit = findViewById(R.id.iconEdit);
        iconDelete.setOnClickListener(this);
        iconEdit.setOnClickListener(this);

        TextView tvNamaDialog = findViewById(R.id.tvNamaDialog);
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
