package com.aldidwikip.kontak.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aldidwikip.kontak.MainActivity;
import com.aldidwikip.kontak.R;
import com.labo.kaji.fragmentanimations.PushPullAnimation;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class NothingToShowFragment extends Fragment {

    CircularProgressButton btnRetry;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.nothing_to_show, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnRetry = view.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRetry.startAnimation();
                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;
                    mainActivity.refresh();
                }
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
