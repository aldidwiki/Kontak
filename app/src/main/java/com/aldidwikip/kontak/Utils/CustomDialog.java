package com.aldidwikip.kontak.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.aldidwikip.kontak.MainActivity;
import com.aldidwikip.kontak.R;
import com.bumptech.glide.Glide;

public class CustomDialog extends Dialog implements android.view.View.OnClickListener {
    private Context context;
    private String imgUrl, idKontak;

    public CustomDialog(Context context, String imgUrl, String idKontak) {
        super(context);
        this.context = context;
        this.imgUrl = imgUrl;
        this.idKontak = idKontak;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        ImageView avatarImg, iconDelete, iconEdit;
        avatarImg = findViewById(R.id.fullAvatar);
        Glide.with(context)
                .load(imgUrl)
                .error(R.drawable.ic_person_24dp)
                .into(avatarImg);

        iconDelete = findViewById(R.id.iconDelete);
        iconEdit = findViewById(R.id.iconEdit);
        iconDelete.setOnClickListener(this);
        iconEdit.setOnClickListener(this);
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
                Toast.makeText(context, "Icon Edit", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        dismiss();
    }
}
