package com.aldidwikip.kontak.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aldidwikip.kontak.model.GetKontak;
import com.aldidwikip.kontak.model.PostPutDelKontak;
import com.aldidwikip.kontak.rest.ApiInterface;
import com.aldidwikip.kontak.view.MainView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPresenter {
    private MainView view;
    private ApiInterface mApiInterface;
    private Context context;

    public MainPresenter(Context context, MainView view, ApiInterface mApiInterface) {
        this.context = context;
        this.view = view;
        this.mApiInterface = mApiInterface;
    }

    public void refreshKontak() {
        view.hidetvNothingToShow();
        view.showSkeleton();
        final Call<GetKontak> kontakCall = mApiInterface.getKontak();
        kontakCall.enqueue(new Callback<GetKontak>() {
            @Override
            public void onResponse(@NonNull Call<GetKontak> call, @NonNull Response<GetKontak> response) {
                view.hideConnectionError();
                view.hideSkeleton();
                assert response.body() != null;
                GetKontak kontakList = response.body();
                view.onKontakLoaded(context, kontakList);
                view.hideSwipeRefreshLayout();
                Log.d("Retrofit Get", "Jumlah data Kontak: " + kontakList.getListDataKontak().size());
                if (kontakList.getListDataKontak().size() == 0) {
                    view.showtvNothingToShow();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetKontak> call, @NonNull Throwable t) {
                Log.e("Retrofit Get", t.toString());
                view.showConnectionError();
//                view.showSkeleton();
            }
        });
    }

    public void deleteKontak(final String delId) {
        Call<PostPutDelKontak> delKontakCall = mApiInterface.deleteKontak(delId);
        delKontakCall.enqueue(new Callback<PostPutDelKontak>() {
            @Override
            public void onResponse(@NonNull Call<PostPutDelKontak> call, @NonNull Response<PostPutDelKontak> response) {
                Toast.makeText(context, "Deleted from Database", Toast.LENGTH_SHORT).show();
                Log.d("Delete Kontak", delId + "Deleted from DB");
            }

            @Override
            public void onFailure(@NonNull Call<PostPutDelKontak> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error Delete", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void searchKontak(String keywords) {
        view.hidetvNothingToShow();
        view.hideSwipeRefreshLayout();
        view.hideConnectionError();
        Call<GetKontak> searchKontak = mApiInterface.searchKontak(keywords);
        searchKontak.enqueue(new Callback<GetKontak>() {
            @Override
            public void onResponse(@NonNull Call<GetKontak> call, @NonNull Response<GetKontak> response) {
                assert response.body() != null;
                GetKontak getSearchResult = response.body();
                if (getSearchResult.getListDataKontak().size() == 0) {
                    view.showtvNothingToShow();
                }
                view.showRecyclerView();
                view.onKontakSearchResult(context, getSearchResult);
            }

            @Override
            public void onFailure(@NonNull Call<GetKontak> call, @NonNull Throwable t) {
//                view.showtvNothingToShow();
            }
        });
    }
}
