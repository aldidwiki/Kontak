package com.aldidwikip.kontak.view;

import android.content.Context;

import com.aldidwikip.kontak.model.GetKontak;

public interface MainView {

    void showConnectionError();

    void hideConnectionError();

    void showSkeleton();

    void hideSkeleton();

    void onKontakLoaded(Context context, GetKontak getKontak);

    void hideSwipeRefreshLayout();

    void onKontakSearchResult(Context context, GetKontak getSearchResult);

    void showRecyclerView();

    void showtvNothingToShow();

    void hidetvNothingToShow();
}
