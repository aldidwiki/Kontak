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

import java.io.File;

public class CustomBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private ItemClickListener mListener;

    public static CustomBottomSheetDialog newInstance() {
        return new CustomBottomSheetDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout removePhoto = view.findViewById(R.id.removePhoto);
        LinearLayout gallery = view.findViewById(R.id.gallery);
        LinearLayout camera = view.findViewById(R.id.camera);
        removePhoto.setOnClickListener(this);
        gallery.setOnClickListener(this);
        camera.setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
        String pathName = "/" + "temp_Compressed Image";
        File rootPath = new File(String.valueOf(v.getContext().getExternalFilesDir(pathName)));
        switch (v.getId()) {
            case R.id.removePhoto:
                mListener.onItemClick(R.id.removePhoto);
                break;
            case R.id.gallery:
                ImagePicker.Companion.with(this)
                        .galleryOnly()
                        .compress(100)
                        .saveDir(rootPath)
                        .start();
                break;
            case R.id.camera:
                ImagePicker.Companion.with(this)
                        .cameraOnly()
                        .compress(100)
                        .saveDir(rootPath)
                        .start();
            default:
                break;
        }
        dismiss();
    }

    public interface ItemClickListener {
        void onItemClick(int item);
    }
}
