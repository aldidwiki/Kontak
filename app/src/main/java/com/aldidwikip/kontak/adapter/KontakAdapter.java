package com.aldidwikip.kontak.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aldidwikip.kontak.R;
import com.aldidwikip.kontak.listener.KontakListener;
import com.aldidwikip.kontak.model.GetKontak;
import com.aldidwikip.kontak.model.Kontak;
import com.aldidwikip.kontak.utils.CustomDialogFragment;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class KontakAdapter extends RecyclerView.Adapter<KontakAdapter.MyViewHolder> {
    private GetKontak mKontakList;
    private Context mContext;
    private KontakListener listener;

    public KontakAdapter(Context context, GetKontak mKontakList, KontakListener listener) {
        this.mKontakList = mKontakList;
        this.mContext = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.kontak_list, parent, false);
        return new MyViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final int adapterPosition = holder.getAdapterPosition();

        holder.bindKontak(mKontakList.getListDataKontak().get(position));

        holder.mKontakParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onKontakClicked(mKontakList.getListDataKontak().get(adapterPosition));
            }
        });

        holder.mImgavatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) mContext;
                FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                CustomDialogFragment customDialogFragment = CustomDialogFragment.newInstance(
                        mKontakList.getListDataKontak().get(adapterPosition)
                );
                customDialogFragment.getKontakForEdit(mKontakList, listener, adapterPosition);
                customDialogFragment.show(fragmentManager, "DIALOG FRAGMENT");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mKontakList.getListDataKontak().size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewNama, mTextViewNomor, mTextViewAlamat;
        private CircleImageView mImgavatar;
        private RelativeLayout mKontakParent;

        public MyViewHolder(View itemView) {
            super(itemView);
            mKontakParent = itemView.findViewById(R.id.kontakParent);
            mTextViewNama = itemView.findViewById(R.id.tvNama);
            mTextViewNomor = itemView.findViewById(R.id.tvNomor);
            mTextViewAlamat = itemView.findViewById(R.id.tvAlamat);
            mImgavatar = itemView.findViewById(R.id.avatarView);
        }

        void bindKontak(@NonNull Kontak model) {
            mTextViewNama.setText(model.getNama());
            mTextViewNomor.setText(model.getNomor());
            mTextViewAlamat.setText(model.getAlamat());
            Glide.with(itemView.getContext())
                    .load(model.getAvatar())
                    .placeholder(R.drawable.ic_person_24dp)
                    .into(mImgavatar);
        }
    }
}