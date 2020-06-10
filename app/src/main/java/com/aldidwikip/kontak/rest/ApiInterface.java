package com.aldidwikip.kontak.rest;

import com.aldidwikip.kontak.model.GetKontak;
import com.aldidwikip.kontak.model.PostPutDelKontak;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface ApiInterface {
    @GET("kontak_android")
    Call<GetKontak> getKontak();

    @FormUrlEncoded
    @POST("kontak_android")
    Call<GetKontak> searchKontak(@Field("keyword") String keyword);

    @Multipart
    @POST("kontak")
    Call<PostPutDelKontak> uploadFile(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("kontak")
    Call<PostPutDelKontak> postKontak(@Field("nama") String nama,
                                      @Field("nomor") String nomor,
                                      @Field("alamat") String alamat,
                                      @Field("avatar") String avatar);

    @FormUrlEncoded
    @PUT("kontak")
    Call<PostPutDelKontak> putKontak(@Field("id") String id,
                                     @Field("nama") String nama,
                                     @Field("nomor") String nomor,
                                     @Field("alamat") String alamat,
                                     @Field("avatar") String avatar);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "kontak", hasBody = true)
    Call<PostPutDelKontak> deleteKontak(@Field("id") String id);

}