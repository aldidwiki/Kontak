package com.aldidwikip.kontak.presenter;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aldidwikip.kontak.model.PostPutDelKontak;
import com.aldidwikip.kontak.rest.ApiInterface;
import com.aldidwikip.kontak.view.EditView;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPresenter {
    private ApiInterface mApiInterface;
    private Context context;
    private EditView view;
    private File file;

    public EditPresenter(Context context, EditView view, ApiInterface mApiInterface) {
        this.mApiInterface = mApiInterface;
        this.context = context;
        this.view = view;
    }

    public void uploadImage(String mediaPath) {
        view.showLoadingAnimation();
        file = new File(mediaPath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part imageToUpload = MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);

        Call<PostPutDelKontak> callUploadImage = mApiInterface.uploadFile(imageToUpload);
        callUploadImage.enqueue(new Callback<PostPutDelKontak>() {
            @Override
            public void onResponse(@NonNull Call<PostPutDelKontak> call, @NonNull Response<PostPutDelKontak> response) {
                Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show();
                view.hideLoadingAnimation();
                view.isUploadResponsed();
            }

            @Override
            public void onFailure(@NonNull Call<PostPutDelKontak> call, @NonNull Throwable t) {
                Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
                view.hideLoadingAnimation();
            }
        });
    }

    public void editKontak(String strIDKontak, String strNama, String strNomor, String strAlamat, String fieldAvatar, Boolean imgRemoved, final String mediaPath) {
        view.showLoadingAnimation();
        String strAvatar;

        if (mediaPath != null) {
            uploadImage(mediaPath);
            strAvatar = file.getName();
        } else if (imgRemoved) {
            strAvatar = "Image Removed";
        } else {
            strAvatar = fieldAvatar.substring(fieldAvatar.lastIndexOf("/") + 1);
        }

        Call<PostPutDelKontak> callEditKontak = mApiInterface.putKontak(strIDKontak, strNama, strNomor, strAlamat, strAvatar);
        callEditKontak.enqueue(new Callback<PostPutDelKontak>() {
            @Override
            public void onResponse(@NonNull Call<PostPutDelKontak> call, @NonNull Response<PostPutDelKontak> response) {
                Toast.makeText(context, "Edited", Toast.LENGTH_SHORT).show();
                if (mediaPath == null) {
                    view.hideLoadingAnimation();
                    view.isEditResponsed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostPutDelKontak> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error Update", Toast.LENGTH_SHORT).show();
                if (mediaPath == null) {
                    view.hideLoadingAnimation();
                }
            }
        });
    }
}
