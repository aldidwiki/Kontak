package com.aldidwikip.kontak.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aldidwikip.kontak.EditActivity;
import com.aldidwikip.kontak.Model.Kontak;
import com.aldidwikip.kontak.R;
import com.aldidwikip.kontak.Utils.CustomDialog;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class KontakAdapter extends RecyclerView.Adapter<KontakAdapter.MyViewHolder> {
    private List<Kontak> mKontakList;
    private Context mContext;

    public KontakAdapter(Context context, List<Kontak> KontakList) {
        mKontakList = KontakList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.kontak_list, parent, false);
        MyViewHolder mViewHolder = new MyViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String idKontak = mKontakList.get(position).getId();
        final String namaKontak = mKontakList.get(position).getNama();
        final String nomorKontak = mKontakList.get(position).getNomor();
        final String alamatKontak = mKontakList.get(position).getAlamat();
        final String avatarKontak = mKontakList.get(position).getAvatar();

        holder.mTextViewNama.setText(namaKontak);
        holder.mTextViewNomor.setText(nomorKontak);
        holder.mTextViewAlamat.setText(alamatKontak);
        Glide
                .with(mContext)
                .load(avatarKontak)
                .placeholder(R.drawable.ic_person_24dp)
                .into(holder.mImgavatar);

        holder.mKontakParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(view.getContext(), EditActivity.class);
                mIntent.putExtra("Id", idKontak);
                mIntent.putExtra("Nama", namaKontak);
                mIntent.putExtra("Nomor", nomorKontak);
                mIntent.putExtra("Alamat", alamatKontak);
                mIntent.putExtra("Avatar", avatarKontak);
                view.getContext().startActivity(mIntent);
            }
        });
        holder.mImgavatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) mContext;
                FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                CustomDialog customDialog = CustomDialog.newInstance(
                        idKontak,
                        namaKontak,
                        nomorKontak,
                        alamatKontak,
                        avatarKontak
                );
                customDialog.show(fragmentManager, "Dialog Fragment");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mKontakList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextViewNama, mTextViewNomor, mTextViewAlamat;
        CircleImageView mImgavatar;
        RelativeLayout mKontakParent;

        public MyViewHolder(View itemView) {
            super(itemView);
            mKontakParent = itemView.findViewById(R.id.kontakParent);
            mTextViewNama = itemView.findViewById(R.id.tvNama);
            mTextViewNomor = itemView.findViewById(R.id.tvNomor);
            mTextViewAlamat = itemView.findViewById(R.id.tvAlamat);
            mImgavatar = itemView.findViewById(R.id.avatarView);
        }
    }
}