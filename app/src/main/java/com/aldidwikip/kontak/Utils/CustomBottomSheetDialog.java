package com.aldidwikip.kontak.Utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aldidwikip.kontak.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CustomBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private ItemClickListener mListener;

    public static CustomBottomSheetDialog newInstance() {
        return new CustomBottomSheetDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(com.aldidwikip.kontak.R.layout.bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout removePhoto = view.findViewById(R.id.removePhoto);
        LinearLayout gallery = view.findViewById(R.id.gallery);
        removePhoto.setOnClickListener(this);
        gallery.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            mListener = (ItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.removePhoto:
                mListener.onItemClick(R.id.removePhoto);
                break;
            case R.id.gallery:
                ImagePicker.Companion.with(this).galleryOnly().compress(1024).start();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface ItemClickListener {
        void onItemClick(int item);
    }
}
