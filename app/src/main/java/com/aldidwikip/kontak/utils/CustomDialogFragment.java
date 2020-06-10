package com.aldidwikip.kontak.utils;

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

import com.aldidwikip.kontak.R;
import com.aldidwikip.kontak.listener.KontakListener;
import com.aldidwikip.kontak.model.GetKontak;
import com.aldidwikip.kontak.model.Kontak;
import com.aldidwikip.kontak.presenter.MainPresenter;
import com.aldidwikip.kontak.rest.ApiClient;
import com.aldidwikip.kontak.rest.ApiInterface;
import com.aldidwikip.kontak.view.MainView;
import com.bumptech.glide.Glide;

import java.util.Objects;

public class CustomDialogFragment extends DialogFragment implements View.OnClickListener {

    String idKontak, namaKontak, alamatKontak, nomorKontak, avatarKontak;
    private MainPresenter presenter;
    private int adapterPosition;
    private GetKontak mKontak;
    private KontakListener listener;

    public static CustomDialogFragment newInstance(Kontak kontak) {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
        Bundle args = new Bundle();
        args.putString("idKontak", kontak.getId());
        args.putString("namaKontak", kontak.getNama());
        args.putString("nomorKontak", kontak.getNomor());
        args.putString("alamatKontak", kontak.getAlamat());
        args.putString("avatarKontak", kontak.getAvatar());

        customDialogFragment.setArguments(args);

        return customDialogFragment;
    }

    public void getKontakForEdit(GetKontak getKontak, KontakListener listener, int adapterPosition) {
        this.mKontak = getKontak;
        this.listener = listener;
        this.adapterPosition = adapterPosition;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawableResource(R.drawable.rounded_bg);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        presenter = new MainPresenter(getContext(), (MainView) getContext(), mApiInterface);

        assert getArguments() != null;
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
                presenter.deleteKontak(idKontak);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        presenter.refreshKontak();
                    }
                }, 1000);
                break;
            case R.id.iconEdit:
                listener.onKontakClicked(mKontak.getListDataKontak().get(adapterPosition));
                break;
            default:
                break;
        }
        dismiss();
    }
}
