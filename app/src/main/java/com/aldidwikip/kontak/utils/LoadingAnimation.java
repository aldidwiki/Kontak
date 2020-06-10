package com.aldidwikip.kontak.utils;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

public class LoadingAnimation {
    FrameLayout mFlLoading;
    SpinKitView mSpinKitView;
    boolean mEnabled;

    public LoadingAnimation(FrameLayout flLoadingInsert, SpinKitView spinKitView) {
        this.mFlLoading = flLoadingInsert;
        this.mSpinKitView = spinKitView;
    }


    public void show(boolean enabled) {
        ProgressBar progressBar = mSpinKitView;
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);

        if (enabled) {
            mFlLoading.setVisibility(View.VISIBLE);
            mSpinKitView.setVisibility(View.VISIBLE);
            mEnabled = true;
        } else {
            mFlLoading.setVisibility(View.GONE);
            mSpinKitView.setVisibility(View.GONE);
            mEnabled = false;
        }
    }

    public boolean isEnabled() {
        return mEnabled;
    }
}
