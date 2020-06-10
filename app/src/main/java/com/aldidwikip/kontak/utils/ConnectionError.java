package com.aldidwikip.kontak.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aldidwikip.kontak.R;
import com.aldidwikip.kontak.presenter.MainPresenter;
import com.aldidwikip.kontak.rest.ApiClient;
import com.aldidwikip.kontak.rest.ApiInterface;
import com.aldidwikip.kontak.view.MainView;
import com.labo.kaji.fragmentanimations.PushPullAnimation;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class ConnectionError extends Fragment {

    CircularProgressButton btnRetry;
    private MainPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.connection_error, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        presenter = new MainPresenter(getContext(), (MainView) getContext(), mApiInterface);

        btnRetry = view.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRetry.startAnimation();
                presenter.refreshKontak();
            }
        });
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return PushPullAnimation.create(PushPullAnimation.DOWN, enter, 500);
    }

    @Override
    public void onDestroy() {
        btnRetry.dispose();
        super.onDestroy();
    }
}
