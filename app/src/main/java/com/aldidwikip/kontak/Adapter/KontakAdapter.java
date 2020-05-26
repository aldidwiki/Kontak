package com.aldidwikip.kontak.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.aldidwikip.kontak.EditActivity;
import com.aldidwikip.kontak.Model.Kontak;
import com.aldidwikip.kontak.R;
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.mTextViewNama.setText("Nama = " + mKontakList.get(position).getNama());
        holder.mTextViewNomor.setText("Nomor = " + mKontakList.get(position).getNomor());
        holder.mTextViewAlamat.setText("Alamat = " + mKontakList.get(position).getAlamat());
        Glide
                .with(mContext)
                .load(mKontakList.get(position).getAvatar())
                .error(R.drawable.ic_person_24dp)
                .into(holder.mImgavatar);

        holder.mKontakParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(view.getContext(), EditActivity.class);
                mIntent.putExtra("Id", mKontakList.get(position).getId());
                mIntent.putExtra("Nama", mKontakList.get(position).getNama());
                mIntent.putExtra("Nomor", mKontakList.get(position).getNomor());
                mIntent.putExtra("Alamat", mKontakList.get(position).getAlamat());
                mIntent.putExtra("Avatar", mKontakList.get(position).getAvatar());
                view.getContext().startActivity(mIntent);
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
        LinearLayout mKontakParent;

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