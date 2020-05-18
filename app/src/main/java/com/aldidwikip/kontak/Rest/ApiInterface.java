package com.aldidwikip.kontak.Rest;

import com.aldidwikip.kontak.Model.GetKontak;
import com.aldidwikip.kontak.Model.PostPutDelKontak;

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
    @GET("Kontak_android")
    Call<GetKontak> getKontak();

    @Multipart
    @POST("Kontak")
    Call<PostPutDelKontak> uploadFile(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("Kontak")
    Call<PostPutDelKontak> postKontak(@Field("nama") String nama,
                                      @Field("nomor") String nomor,
                                      @Field("alamat") String alamat,
                                      @Field("avatar") String avatar);

    @FormUrlEncoded
    @PUT("Kontak")
    Call<PostPutDelKontak> putKontak(@Field("id") String id,
                                     @Field("nama") String nama,
                                     @Field("nomor") String nomor,
                                     @Field("alamat") String alamat,
                                     @Field("avatar") String avatar);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "Kontak", hasBody = true)
    Call<PostPutDelKontak> deleteKontak(@Field("id") String id);


}