package com.aldidwikip.kontak.presenter;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aldidwikip.kontak.model.PostPutDelKontak;
import com.aldidwikip.kontak.rest.ApiInterface;
import com.aldidwikip.kontak.view.InsertView;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertPresenter {
    private InsertView view;
    private ApiInterface mApiInterface;
    private File file;
    private Context context;

    public InsertPresenter(Context context, InsertView view, ApiInterface mApiInterface) {
        this.context = context;
        this.view = view;
        this.mApiInterface = mApiInterface;
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

    public void insertKontak(String strNama, String strNomor, String strAlamat, final String mediaPath) {
        view.showLoadingAnimation();
        String strAvatar;

        if (mediaPath != null) {
            uploadImage(mediaPath);
            strAvatar = file.getName();
        } else {
            strAvatar = "";
        }

        Call<PostPutDelKontak> callPostKontak = mApiInterface.postKontak(strNama, strNomor, strAlamat, strAvatar);
        callPostKontak.enqueue(new Callback<PostPutDelKontak>() {
            @Override
            public void onResponse(@NonNull Call<PostPutDelKontak> call, @NonNull Response<PostPutDelKontak> response) {
                Toast.makeText(context, "Inserted", Toast.LENGTH_SHORT).show();
                if (mediaPath == null) {
                    view.hideLoadingAnimation();
                    view.isInsertResponsed();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostPutDelKontak> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error Insert", Toast.LENGTH_SHORT).show();
                if (mediaPath == null) {
                    view.hideLoadingAnimation();
                }
            }
        });
    }
}
